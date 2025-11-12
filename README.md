# DAS Blog & Portfolio Platform

A full-stack blog and portfolio platform with microservices architecture, built with Vue.js, Node.js, Java Spring Boot, and Kotlin Spring Boot.

## Architecture

This platform consists of:

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
- **Responsibilities**:
  - User registration and login (JWT)
  - Comment system for blog posts
  - File uploads (profile pics, assets)
  - User management

### Microservice 2: Blog Service (Java Spring Boot)
- **Port**: 3002
- **Technology**: Java 17 + Spring Boot + GraphQL
- **Database**: MongoDB
- **Responsibilities**:
  - Blog post CRUD operations
  - Content search and filtering
  - Tags and categories management
  - Blog analytics (views)

### Microservice 3: Portfolio Service (Kotlin Spring Boot)
- **Port**: 3003
- **Technology**: Kotlin + Spring Boot
- **Database**: MySQL
- **Responsibilities**:
  - Project showcase CRUD
  - Skills and experience data
  - Contact form handling
  - Resume/CV data

## Prerequisites

- Docker and Docker Compose
- Node.js 20+ (for local frontend development)
- Java 17+ (for local backend development)
- Maven 3.9+ (for building Java/Kotlin services)

## Quick Start

### 1. Clone the Repository

```bash
git clone <repository-url>
cd das-blog-post
```

### 2. Start All Services with Docker Compose

```bash
docker-compose up --build
```

This will start:
- Frontend: http://localhost:3000
- Auth Service: http://localhost:3001
- Blog Service: http://localhost:3002
  - GraphiQL: http://localhost:3002/graphiql
- Portfolio Service: http://localhost:3003
- PostgreSQL: localhost:5432
- MongoDB: localhost:27017
- MySQL: localhost:3306

### 3. Access the Application

- **Frontend**: http://localhost:3000
- **Admin Panel**: http://localhost:3000/admin (after login)
- **GraphiQL Playground**: http://localhost:3002/graphiql

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

### Auth Service

```env
PORT=3001
DB_HOST=postgres
DB_PORT=5432
DB_NAME=auth_db
DB_USER=postgres
DB_PASSWORD=postgres
JWT_SECRET=your-secret-key
JWT_EXPIRES_IN=7d
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
- **Auth Service**: Node.js, Express, PostgreSQL, JWT, Bcrypt, Multer
- **Blog Service**: Java 17, Spring Boot, GraphQL, MongoDB
- **Portfolio Service**: Kotlin, Spring Boot, MySQL, JPA
- **Infrastructure**: Docker, Docker Compose
- **Cloud Ready**: AWS S3, CloudFront, RDS, EC2, ECS
