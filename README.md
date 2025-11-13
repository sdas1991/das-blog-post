# DAS Blog & Portfolio Platform

A full-stack blog and portfolio platform with microservices architecture, built with Vue.js, Node.js, Java Spring Boot, and Kotlin Spring Boot.

## Architecture

This platform consists of:

```
┌─────────┐     ┌──────────────┐     ┌─────────────┐
│ Client  │────▶│  Frontend    │────▶│ API Gateway │
│(Browser)│     │   (Vue.js)   │     │  (Python)   │
└─────────┘     │  Port: 3000  │     │  Port: 8000 │
                └──────────────┘     └──────┬──────┘
                                            │
                                     ┌──────▼──────┐
                                     │    Redis    │
                                     │(Rate Limit) │
                                     └─────────────┘
                                            │
                        ┌───────────────────┼───────────────────┐
                        │                   │                   │
                   ┌────▼──────┐     ┌─────▼────┐     ┌───────▼────┐
                   │   Auth    │     │   Blog   │     │ Portfolio  │
                   │  Service  │     │  Service │     │  Service   │
                   │  (Node.js)│     │  (Java)  │     │  (Kotlin)  │
                   │Port: 3001 │     │Port: 3002│     │Port: 3003  │
                   └─────┬─────┘     └─────┬────┘     └──────┬─────┘
                         │                 │                  │
                   ┌─────▼─────┐     ┌────▼─────┐     ┌──────▼──────┐
                   │PostgreSQL │     │ MongoDB  │     │   MySQL     │
                   └───────────┘     └──────────┘     └─────────────┘
```

### API Gateway (Python FastAPI)
- **Port**: 8000
- **Technology**: Python + FastAPI + Redis
- **Responsibilities**:
  - Request routing to microservices
  - Rate limiting (100 req/60s per IP)
  - Request/Response logging
  - Error handling and timeouts
  - Health monitoring

### Frontend
- **Technology**: Vue.js 3 with Vite
- **Features**:
  - Blog listing, reading, and commenting
  - Portfolio projects display
  - Admin dashboard with rich text editor (TipTap)
  - Authentication UI
  - Responsive design
- **Hosting**: AWS S3 + CloudFront (Free Tier ready)

### Microservice 1: Authentication Service (Node.js)
- **Port**: 3001
- **Technology**: Node.js + Express
- **Database**: PostgreSQL
- **Build Tool**: npm
- **Responsibilities**:
  - User registration and login (JWT)
  - Comment system for blog posts
  - File uploads (GridFS/S3 based on mode)
  - User management

### Microservice 2: Blog Service (Java Spring Boot)
- **Port**: 3002
- **Technology**: Java 17 + Spring Boot + GraphQL
- **Database**: MongoDB
- **Build Tool**: Gradle (Groovy DSL)
- **Responsibilities**:
  - Blog post CRUD operations
  - Content search and filtering
  - Tags and categories management
  - Blog analytics (views)

### Microservice 3: Portfolio Service (Kotlin Spring Boot)
- **Port**: 3003
- **Technology**: Kotlin + Spring Boot
- **Database**: MySQL
- **Build Tool**: Gradle (Groovy DSL)
- **Responsibilities**:
  - Project showcase CRUD
  - Skills and experience data
  - Contact form handling
  - Resume/CV data

## Prerequisites

- Docker and Docker Compose
- Node.js 20+ (for local frontend development)
- Java 17+ (for local backend development)
- Gradle 8.5+ (for building Java/Kotlin services)
- Python 3.11+ (for API Gateway development)

## Application Modes

This application supports two deployment modes:

### 🔧 Dev Mode (Default)
- **Purpose**: Local development with zero AWS dependencies
- **File Storage**: MongoDB GridFS (stored in local Docker container)
- **Databases**: All databases run in local Docker containers
- **Benefits**:
  - No AWS account required
  - No cloud costs
  - Fully isolated local environment
  - Fast iteration and testing

### 🚀 Release Mode
- **Purpose**: Production deployment with AWS services
- **File Storage**: AWS S3
- **Databases**: Can use AWS RDS, MongoDB Atlas, or self-hosted
- **Benefits**:
  - Scalable cloud infrastructure
  - Production-grade file storage
  - CDN integration available

## Quick Start

### 1. Clone the Repository

```bash
git clone <repository-url>
cd das-blog-post
```

### 2. Configure Environment Variables

```bash
# Copy the example environment file
cp .env.example .env

# Edit .env and ensure APP_MODE is set to 'dev' for local development
# APP_MODE=dev
```

### 3. Start All Services with Docker Compose (Dev Mode)

```bash
docker-compose up --build
```

This will start:
- **API Gateway**: http://localhost:8000 (Rate limiting & routing)
  - Health check: http://localhost:8000/health
- **Frontend**: http://localhost:3000
- **Auth Service**: http://localhost:3001
- **Blog Service**: http://localhost:3002
  - GraphiQL: http://localhost:3002/graphiql
- **Portfolio Service**: http://localhost:3003
- **PostgreSQL**: localhost:5432
- **MongoDB**: localhost:27017 (Blog + File storage in dev mode)
- **MySQL**: localhost:3306
- **Redis**: localhost:6379 (Rate limiting)

### 4. Access the Application

- **Frontend**: http://localhost:3000
- **API Gateway**: http://localhost:8000 (all API requests route through here)
- **Admin Panel**: http://localhost:3000/admin (after login)
- **GraphiQL Playground**: http://localhost:3002/graphiql (direct access)

**Note**: All frontend API requests automatically go through the API Gateway for rate limiting and routing.

### Running in Release Mode

To run the application in release mode with AWS services:

1. **Configure AWS credentials in `.env`**:
```env
APP_MODE=release
AWS_REGION=us-east-1
AWS_S3_BUCKET=your-s3-bucket-name
AWS_ACCESS_KEY_ID=your-access-key
AWS_SECRET_ACCESS_KEY=your-secret-key
```

2. **Ensure AWS S3 bucket exists and is configured**:
```bash
# Create S3 bucket (if not exists)
aws s3 mb s3://your-s3-bucket-name --region us-east-1

# Configure bucket for public read access (optional)
aws s3api put-bucket-cors --bucket your-s3-bucket-name --cors-configuration file://cors.json
```

3. **Start services with release mode**:
```bash
docker-compose up --build
```

The application will automatically use AWS S3 for file uploads instead of MongoDB GridFS.

## Local Development

### Frontend Development

```bash
cd frontend
npm install
npm run dev
```

The frontend will be available at http://localhost:3000 with hot reload.

### Backend Services Development

#### Auth Service (Node.js)

```bash
cd backend/auth-service
npm install
cp .env.example .env
# Edit .env with your configuration
npm run dev
```

#### Blog Service (Java)

```bash
cd backend/blog-service
mvn spring-boot:run
```

#### Portfolio Service (Kotlin)

```bash
cd backend/portfolio-service
mvn spring-boot:run
```

## API Documentation

### Auth Service (REST API)

**Base URL**: http://localhost:3001/api/auth

- `POST /register` - Register a new user
- `POST /login` - Login user
- `GET /me` - Get current user (requires authentication)
- `GET /comments/:postId` - Get comments for a post
- `POST /comments` - Create a comment (requires authentication)
- `DELETE /comments/:commentId` - Delete a comment (requires authentication)
- `POST /upload` - Upload a file (requires authentication)
  - **Dev Mode**: Stores in MongoDB GridFS
  - **Release Mode**: Stores in AWS S3
- `GET /files/:fileId` - Download a file (dev mode with GridFS)
- `DELETE /files/:fileId` - Delete a file (requires authentication)

### Blog Service (GraphQL API)

**Base URL**: http://localhost:3002/graphql

**Queries**:
```graphql
query {
  posts(filters: { published: true }) {
    id
    title
    content
    author {
      name
    }
  }

  post(id: "123") {
    id
    title
    content
  }

  categories
  tags
}
```

**Mutations**:
```graphql
mutation {
  createPost(input: {
    title: "My First Post"
    content: "Hello World"
    published: true
  }) {
    id
    title
  }

  updatePost(id: "123", input: {
    title: "Updated Title"
  }) {
    id
    title
  }

  deletePost(id: "123")
}
```

### Portfolio Service (REST API)

**Base URL**: http://localhost:3003/api/portfolio

- `GET /projects` - Get all projects
- `GET /projects/:id` - Get project by ID
- `POST /projects` - Create a project
- `PUT /projects/:id` - Update a project
- `DELETE /projects/:id` - Delete a project
- `GET /skills` - Get all skills
- `POST /skills` - Create a skill
- `POST /contact` - Submit contact form

## Environment Variables

### Global Configuration

```env
# Application mode: 'dev' or 'release'
APP_MODE=dev

# Service ports
AUTH_SERVICE_PORT=3001
BLOG_SERVICE_PORT=3002
PORTFOLIO_SERVICE_PORT=3003
FRONTEND_PORT=3000
```

### Auth Service

```env
PORT=3001
NODE_ENV=development
APP_MODE=dev

# PostgreSQL Database
DB_HOST=postgres
DB_PORT=5432
DB_NAME=auth_db
DB_USER=postgres
DB_PASSWORD=postgres

# JWT Configuration
JWT_SECRET=your-secret-key
JWT_EXPIRES_IN=7d

# File Upload Configuration
MAX_FILE_SIZE=5242880

# Dev Mode - MongoDB GridFS
MONGODB_HOST=mongodb
MONGODB_PORT=27017
MONGODB_DATABASE=auth_storage_db

# Release Mode - AWS S3 (only required when APP_MODE=release)
AWS_REGION=us-east-1
AWS_S3_BUCKET=your-bucket-name
AWS_ACCESS_KEY_ID=your-access-key
AWS_SECRET_ACCESS_KEY=your-secret-key
```

### Blog Service

```env
SPRING_DATA_MONGODB_HOST=mongodb
SPRING_DATA_MONGODB_PORT=27017
SPRING_DATA_MONGODB_DATABASE=blog_db
```

### Portfolio Service

```env
MYSQL_HOST=mysql
MYSQL_PORT=3306
MYSQL_DATABASE=portfolio_db
MYSQL_USER=root
MYSQL_PASSWORD=root
```

## Database Schema

### PostgreSQL (Auth Service)

**users** table:
- id (SERIAL PRIMARY KEY)
- name (VARCHAR)
- email (VARCHAR UNIQUE)
- password (VARCHAR)
- avatar (VARCHAR)
- created_at (TIMESTAMP)
- updated_at (TIMESTAMP)

**comments** table:
- id (SERIAL PRIMARY KEY)
- post_id (VARCHAR)
- user_id (INTEGER FK)
- content (TEXT)
- created_at (TIMESTAMP)
- updated_at (TIMESTAMP)

### MongoDB (Blog Service)

**posts** collection:
```javascript
{
  _id: ObjectId,
  title: String,
  slug: String,
  excerpt: String,
  content: String,
  author: {
    id: Number,
    name: String,
    email: String
  },
  tags: [String],
  category: String,
  published: Boolean,
  publishedAt: Date,
  createdAt: Date,
  updatedAt: Date,
  views: Number
}
```

### MySQL (Portfolio Service)

**projects** table:
- id (BIGINT PRIMARY KEY AUTO_INCREMENT)
- title (VARCHAR)
- description (TEXT)
- image_url (VARCHAR)
- live_url (VARCHAR)
- github_url (VARCHAR)
- created_at (DATETIME)
- updated_at (DATETIME)

**skills** table:
- id (BIGINT PRIMARY KEY AUTO_INCREMENT)
- name (VARCHAR)
- category (VARCHAR)
- level (INT) - 0-100
- description (VARCHAR)

**contacts** table:
- id (BIGINT PRIMARY KEY AUTO_INCREMENT)
- name (VARCHAR)
- email (VARCHAR)
- message (TEXT)
- created_at (DATETIME)

## Storage Architecture

### File Upload System

The application uses a **Storage Service Abstraction** pattern that automatically switches between storage backends based on the `APP_MODE` environment variable:

```
┌─────────────────────────────────────────┐
│         Upload Route Handler            │
│      (routes/upload.js)                 │
└─────────────────┬───────────────────────┘
                  │
                  ▼
┌─────────────────────────────────────────┐
│       Storage Factory                   │
│   (services/storage/StorageFactory.js)  │
│                                         │
│   if (APP_MODE === 'release')          │
│      return S3Storage                   │
│   else                                  │
│      return GridFSStorage               │
└─────────┬──────────────┬────────────────┘
          │              │
    ┌─────▼──────┐  ┌───▼──────────┐
    │  Dev Mode  │  │ Release Mode │
    │  GridFS    │  │   AWS S3     │
    │  Storage   │  │   Storage    │
    └────────────┘  └──────────────┘
         │                 │
    ┌────▼─────┐     ┌────▼─────┐
    │ MongoDB  │     │  AWS S3  │
    │  Docker  │     │  Bucket  │
    └──────────┘     └──────────┘
```

### Dev Mode Storage (MongoDB GridFS)

**Benefits**:
- No external dependencies
- No AWS account needed
- Zero cloud costs
- Files stored in MongoDB container
- Automatic cleanup when containers are removed
- Fast local development

**Technical Details**:
- Files stored in `uploads.files` and `uploads.chunks` collections
- Database: `auth_storage_db`
- Access via: `/api/auth/files/:fileId`
- Supports all file operations (upload, download, delete)

### Release Mode Storage (AWS S3)

**Benefits**:
- Scalable cloud storage
- CDN integration ready
- Persistent storage
- Production-grade reliability
- Global availability

**Technical Details**:
- Files stored in S3 bucket
- Region: Configurable (default: us-east-1)
- Access via: Public S3 URLs or pre-signed URLs
- IAM permissions required for access

### Switching Between Modes

Simply change the `APP_MODE` environment variable:

```bash
# For local development
APP_MODE=dev docker-compose up

# For production
APP_MODE=release docker-compose up
```

No code changes required - the application automatically adapts!

## Features

### Rich Text Editor

The admin panel includes a powerful rich text editor (TipTap) with:
- Text formatting (bold, italic, strikethrough)
- Headings (H1, H2, H3)
- Lists (bullet and ordered)
- Code blocks with syntax highlighting
- Links and images
- Tables
- Blockquotes
- Undo/Redo

### Authentication

- JWT-based authentication
- Secure password hashing with bcrypt
- Token expiration
- Protected routes

### Blog Management

- Create, edit, and delete blog posts
- Publish/unpublish posts
- Rich text content
- Tags and categories
- Search functionality
- View analytics

### Portfolio Management

- Add/edit/delete projects
- Showcase skills with proficiency levels
- Contact form submissions
- Technology tags

## Deployment to AWS Free Tier

### Frontend (S3 + CloudFront)

1. Build the frontend:
```bash
cd frontend
npm run build
```

2. Upload `dist/` folder to S3 bucket
3. Configure CloudFront distribution
4. Update API URLs in frontend environment variables

### Backend Services (EC2 or ECS)

1. Use AWS RDS for PostgreSQL and MySQL (Free Tier)
2. Use MongoDB Atlas (Free Tier) or self-hosted EC2
3. Deploy microservices to:
   - AWS EC2 (t2.micro Free Tier)
   - AWS ECS with Fargate
   - AWS Lambda (for serverless option)

### Environment Configuration

Update the following for production:
- JWT_SECRET (use a strong, random secret)
- Database credentials
- CORS origins
- API URLs

## Troubleshooting

### Services not starting

- Ensure Docker is running
- Check if ports 3000-3003, 5432, 27017, 3306 are available
- Run `docker-compose down -v` to clean up volumes

### Database connection errors

- Wait for databases to fully initialize (can take 30 seconds)
- Check database credentials in docker-compose.yml
- Verify network connectivity between services

### Frontend can't connect to backend

- Ensure all services are running
- Check proxy configuration in vite.config.js
- Verify CORS settings in backend services

## Contributing

1. Fork the repository
2. Create a feature branch
3. Commit your changes
4. Push to the branch
5. Create a Pull Request

## License

MIT License

## Author

DAS - Full Stack Developer

## Tech Stack Summary

- **Frontend**: Vue.js 3, Vite, Pinia, Vue Router, TipTap, Axios
- **API Gateway**: Python 3.11, FastAPI, Redis, httpx
- **Auth Service**: Node.js, Express, PostgreSQL, JWT, Bcrypt, MongoDB (GridFS)
- **Blog Service**: Java 17, Spring Boot, GraphQL, MongoDB, Gradle
- **Portfolio Service**: Kotlin, Spring Boot, MySQL, JPA, Gradle
- **Infrastructure**: Docker, Docker Compose, Redis
- **Cloud Ready**: AWS S3, CloudFront, RDS, EC2, ECS, ElastiCache

## Additional Documentation

- **[AWS Deployment Guide](AWS_DEPLOYMENT.md)** - Complete guide for deploying to AWS Free Tier
- **[Mode Configuration](MODE_CONFIGURATION.md)** - Detailed dev/release mode documentation
- **[API Gateway README](backend/api-gateway/README.md)** - API Gateway documentation
