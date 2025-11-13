# API Gateway Service

Python-based API Gateway with rate limiting and request routing.

## Features

- **Request Routing**: Routes requests to appropriate microservices
- **Rate Limiting**: Redis-based rate limiting (configurable)
- **Health Checks**: Monitors backend service availability
- **Error Handling**: Graceful error handling and timeouts
- **CORS Support**: Configurable CORS policies

## Configuration

### Environment Variables

- `PORT`: Gateway port (default: 8000)
- `APP_MODE`: Application mode (dev/release)
- `REDIS_HOST`: Redis server host (default: redis)
- `REDIS_PORT`: Redis server port (default: 6379)
- `RATE_LIMIT_REQUESTS`: Max requests per window (default: 100)
- `RATE_LIMIT_WINDOW`: Time window in seconds (default: 60)

### Rate Limiting

The gateway implements a sliding window rate limiter using Redis:
- Default: 100 requests per 60 seconds per IP
- Rate limit headers included in responses:
  - `X-RateLimit-Limit`: Request limit
  - `X-RateLimit-Remaining`: Remaining requests
  - `X-RateLimit-Reset`: Reset timestamp
- HTTP 429 returned when limit exceeded

## Endpoints

### Gateway Endpoints

- `GET /`: Gateway information
- `GET /health`: Health check

### Proxied Routes

- `/api/auth/*` → Auth Service (port 3001)
- `/api/blog/*` → Blog Service (port 3002)
- `/api/portfolio/*` → Portfolio Service (port 3003)

## Running Locally

```bash
# Install dependencies
pip install -r requirements.txt

# Run with uvicorn
uvicorn main:app --reload --port 8000
```

## Docker

```bash
# Build image
docker build -t api-gateway .

# Run container
docker run -p 8000:8000 \
  -e REDIS_HOST=redis \
  -e APP_MODE=dev \
  api-gateway
```

## Testing

### Test rate limiting
```bash
# Make requests to trigger rate limit
for i in {1..105}; do
  curl http://localhost:8000/api/auth/health
  echo "Request $i"
done
```

### Check headers
```bash
curl -I http://localhost:8000/api/auth/health
```

## Architecture

```
┌─────────┐      ┌──────────────┐      ┌──────────┐
│ Client  │─────▶│ API Gateway  │─────▶│  Redis   │
└─────────┘      │ (Port 8000)  │      │(Rate Limit)
                 └──────┬───────┘      └──────────┘
                        │
         ┌──────────────┼──────────────┐
         │              │              │
    ┌────▼───┐    ┌────▼───┐    ┌────▼─────┐
    │  Auth  │    │  Blog  │    │Portfolio │
    │Service │    │Service │    │ Service  │
    │ :3001  │    │ :3002  │    │  :3003   │
    └────────┘    └────────┘    └──────────┘
```

## Monitoring

The gateway logs:
- Request routing
- Rate limit violations
- Backend connection errors
- Timeout issues

Monitor logs:
```bash
docker logs -f api-gateway
```
