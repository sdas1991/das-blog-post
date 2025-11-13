"""
API Gateway with Rate Limiting
Handles request routing and throttling for all microservices
"""
import os
import time
from typing import Optional
from fastapi import FastAPI, Request, HTTPException, Response
from fastapi.responses import JSONResponse, StreamingResponse
import httpx
import redis
from redis import Redis
from contextlib import asynccontextmanager
import logging

# Configure logging
logging.basicConfig(level=logging.INFO)
logger = logging.getLogger(__name__)

# Configuration
APP_MODE = os.getenv("APP_MODE", "dev")
REDIS_HOST = os.getenv("REDIS_HOST", "redis")
REDIS_PORT = int(os.getenv("REDIS_PORT", "6379"))
RATE_LIMIT_REQUESTS = int(os.getenv("RATE_LIMIT_REQUESTS", "100"))  # requests
RATE_LIMIT_WINDOW = int(os.getenv("RATE_LIMIT_WINDOW", "60"))  # seconds

# Backend service URLs
AUTH_SERVICE_URL = os.getenv("AUTH_SERVICE_URL", "http://auth-service:3001")
BLOG_SERVICE_URL = os.getenv("BLOG_SERVICE_URL", "http://blog-service:3002")
PORTFOLIO_SERVICE_URL = os.getenv("PORTFOLIO_SERVICE_URL", "http://portfolio-service:3003")

# Redis client
redis_client: Optional[Redis] = None


@asynccontextmanager
async def lifespan(app: FastAPI):
    """Initialize and cleanup resources"""
    global redis_client

    # Startup
    logger.info(f"🚀 Starting API Gateway in {APP_MODE.upper()} mode")
    logger.info(f"   Rate Limit: {RATE_LIMIT_REQUESTS} requests per {RATE_LIMIT_WINDOW}s")

    try:
        redis_client = redis.Redis(
            host=REDIS_HOST,
            port=REDIS_PORT,
            decode_responses=True,
            socket_connect_timeout=5
        )
        redis_client.ping()
        logger.info(f"✓ Connected to Redis at {REDIS_HOST}:{REDIS_PORT}")
    except Exception as e:
        logger.warning(f"⚠ Redis connection failed: {e}")
        logger.warning("  Rate limiting will be disabled")
        redis_client = None

    yield

    # Shutdown
    if redis_client:
        redis_client.close()
        logger.info("✓ Closed Redis connection")


app = FastAPI(
    title="DAS API Gateway",
    description="API Gateway with rate limiting and request routing",
    version="1.0.0",
    lifespan=lifespan
)


def get_client_ip(request: Request) -> str:
    """Extract client IP address from request"""
    forwarded = request.headers.get("X-Forwarded-For")
    if forwarded:
        return forwarded.split(",")[0].strip()
    return request.client.host if request.client else "unknown"


def check_rate_limit(client_ip: str) -> tuple[bool, dict]:
    """
    Check if client has exceeded rate limit
    Returns: (allowed, rate_limit_info)
    """
    if not redis_client:
        # Rate limiting disabled if Redis unavailable
        return True, {
            "limit": RATE_LIMIT_REQUESTS,
            "remaining": RATE_LIMIT_REQUESTS,
            "reset": int(time.time() + RATE_LIMIT_WINDOW)
        }

    try:
        key = f"rate_limit:{client_ip}"
        current_time = int(time.time())
        window_start = current_time - RATE_LIMIT_WINDOW

        # Remove old entries
        redis_client.zremrangebyscore(key, 0, window_start)

        # Count requests in current window
        request_count = redis_client.zcard(key)

        if request_count >= RATE_LIMIT_REQUESTS:
            # Rate limit exceeded
            reset_time = int(redis_client.zscore(key, redis_client.zrange(key, 0, 0)[0])) + RATE_LIMIT_WINDOW
            return False, {
                "limit": RATE_LIMIT_REQUESTS,
                "remaining": 0,
                "reset": reset_time
            }

        # Add current request
        redis_client.zadd(key, {str(current_time): current_time})
        redis_client.expire(key, RATE_LIMIT_WINDOW)

        remaining = RATE_LIMIT_REQUESTS - request_count - 1
        reset_time = current_time + RATE_LIMIT_WINDOW

        return True, {
            "limit": RATE_LIMIT_REQUESTS,
            "remaining": remaining,
            "reset": reset_time
        }

    except Exception as e:
        logger.error(f"Rate limit check failed: {e}")
        # Allow request if rate limiting fails
        return True, {
            "limit": RATE_LIMIT_REQUESTS,
            "remaining": RATE_LIMIT_REQUESTS,
            "reset": int(time.time() + RATE_LIMIT_WINDOW)
        }


async def proxy_request(request: Request, target_url: str) -> Response:
    """Proxy request to backend service"""
    client_ip = get_client_ip(request)

    # Check rate limit
    allowed, rate_info = check_rate_limit(client_ip)

    if not allowed:
        logger.warning(f"Rate limit exceeded for {client_ip}")
        return JSONResponse(
            status_code=429,
            content={
                "error": "Rate limit exceeded",
                "message": f"Too many requests. Limit: {rate_info['limit']} per {RATE_LIMIT_WINDOW}s",
                "retry_after": rate_info['reset'] - int(time.time())
            },
            headers={
                "X-RateLimit-Limit": str(rate_info["limit"]),
                "X-RateLimit-Remaining": str(rate_info["remaining"]),
                "X-RateLimit-Reset": str(rate_info["reset"]),
                "Retry-After": str(rate_info['reset'] - int(time.time()))
            }
        )

    # Prepare request
    url = f"{target_url}{request.url.path}"
    if request.url.query:
        url = f"{url}?{request.url.query}"

    headers = dict(request.headers)
    headers.pop("host", None)  # Remove host header

    # Forward request to backend
    async with httpx.AsyncClient(timeout=30.0) as client:
        try:
            body = await request.body()

            response = await client.request(
                method=request.method,
                url=url,
                headers=headers,
                content=body,
            )

            # Add rate limit headers to response
            response_headers = dict(response.headers)
            response_headers["X-RateLimit-Limit"] = str(rate_info["limit"])
            response_headers["X-RateLimit-Remaining"] = str(rate_info["remaining"])
            response_headers["X-RateLimit-Reset"] = str(rate_info["reset"])

            return Response(
                content=response.content,
                status_code=response.status_code,
                headers=response_headers,
                media_type=response.headers.get("content-type")
            )

        except httpx.ConnectError:
            logger.error(f"Failed to connect to {target_url}")
            return JSONResponse(
                status_code=503,
                content={
                    "error": "Service Unavailable",
                    "message": "Backend service is unavailable"
                }
            )
        except httpx.TimeoutException:
            logger.error(f"Request timeout for {target_url}")
            return JSONResponse(
                status_code=504,
                content={
                    "error": "Gateway Timeout",
                    "message": "Request to backend service timed out"
                }
            )
        except Exception as e:
            logger.error(f"Proxy error: {e}")
            return JSONResponse(
                status_code=500,
                content={
                    "error": "Internal Server Error",
                    "message": "An error occurred while processing your request"
                }
            )


# Health check endpoint
@app.get("/health")
async def health_check():
    """API Gateway health check"""
    redis_status = "connected" if redis_client else "disconnected"

    return {
        "status": "healthy",
        "service": "api-gateway",
        "mode": APP_MODE,
        "redis": redis_status,
        "rate_limiting": "enabled" if redis_client else "disabled",
        "backends": {
            "auth": AUTH_SERVICE_URL,
            "blog": BLOG_SERVICE_URL,
            "portfolio": PORTFOLIO_SERVICE_URL
        }
    }


# Route: Auth Service
@app.api_route("/api/auth/{path:path}", methods=["GET", "POST", "PUT", "DELETE", "PATCH"])
async def route_auth(request: Request, path: str):
    """Route requests to Auth Service"""
    return await proxy_request(request, AUTH_SERVICE_URL)


# Route: Blog Service
@app.api_route("/api/blog/{path:path}", methods=["GET", "POST", "PUT", "DELETE", "PATCH"])
async def route_blog(request: Request, path: str):
    """Route requests to Blog Service"""
    return await proxy_request(request, BLOG_SERVICE_URL)


# Route: Portfolio Service
@app.api_route("/api/portfolio/{path:path}", methods=["GET", "POST", "PUT", "DELETE", "PATCH"])
async def route_portfolio(request: Request, path: str):
    """Route requests to Portfolio Service"""
    return await proxy_request(request, PORTFOLIO_SERVICE_URL)


# Root endpoint
@app.get("/")
async def root():
    """API Gateway information"""
    return {
        "service": "DAS API Gateway",
        "version": "1.0.0",
        "mode": APP_MODE,
        "endpoints": {
            "health": "/health",
            "auth": "/api/auth/*",
            "blog": "/api/blog/*",
            "portfolio": "/api/portfolio/*"
        }
    }


if __name__ == "__main__":
    import uvicorn
    port = int(os.getenv("PORT", "8000"))
    uvicorn.run(app, host="0.0.0.0", port=port)
