# API Documentation

Complete API documentation for the DAS Blog & Portfolio Platform.

## Table of Contents

1. [Auth Service API](#auth-service-api)
2. [Portfolio Service API](#portfolio-service-api)
3. [Blog Service GraphQL API](#blog-service-graphql-api)

---

## Auth Service API

**Base URL:** `http://localhost:3001/api/auth`

### Authentication Endpoints

#### Register User
```http
POST /api/auth/register
Content-Type: application/json

{
  "name": "John Doe",
  "email": "john@example.com",
  "password": "password123"
}
```

**Response (201 Created):**
```json
{
  "token": "eyJhbGciOiJIUzI1NiIsInR5cCI6IkpXVCJ9...",
  "user": {
    "id": 1,
    "name": "John Doe",
    "email": "john@example.com",
    "createdAt": "2024-01-15T10:30:00.000Z"
  }
}
```

#### Login
```http
POST /api/auth/login
Content-Type: application/json

{
  "email": "john@example.com",
  "password": "password123"
}
```

**Response (200 OK):**
```json
{
  "token": "eyJhbGciOiJIUzI1NiIsInR5cCI6IkpXVCJ9...",
  "user": {
    "id": 1,
    "name": "John Doe",
    "email": "john@example.com",
    "avatar": "/uploads/avatar.jpg"
  }
}
```

#### Get Current User
```http
GET /api/auth/me
Authorization: Bearer <token>
```

**Response (200 OK):**
```json
{
  "id": 1,
  "name": "John Doe",
  "email": "john@example.com",
  "avatar": "/uploads/avatar.jpg",
  "bio": "Software developer and blogger",
  "role": "user",
  "is_guest_author": false,
  "social_links": {
    "twitter": "https://twitter.com/johndoe",
    "github": "https://github.com/johndoe"
  },
  "created_at": "2024-01-15T10:30:00.000Z"
}
```

#### Update Profile
```http
PUT /api/auth/profile
Authorization: Bearer <token>
Content-Type: application/json

{
  "name": "John Doe Updated",
  "bio": "Full-stack developer passionate about web technologies",
  "avatar": "/uploads/new-avatar.jpg",
  "social_links": {
    "twitter": "https://twitter.com/johndoe",
    "github": "https://github.com/johndoe",
    "linkedin": "https://linkedin.com/in/johndoe"
  }
}
```

**Response (200 OK):**
```json
{
  "id": 1,
  "name": "John Doe Updated",
  "email": "john@example.com",
  "avatar": "/uploads/new-avatar.jpg",
  "bio": "Full-stack developer passionate about web technologies",
  "role": "user",
  "is_guest_author": false,
  "social_links": {
    "twitter": "https://twitter.com/johndoe",
    "github": "https://github.com/johndoe",
    "linkedin": "https://linkedin.com/in/johndoe"
  },
  "created_at": "2024-01-15T10:30:00.000Z",
  "updated_at": "2024-01-16T14:20:00.000Z"
}
```

#### Get User Profile
```http
GET /api/auth/profile/:userId
```

**Response (200 OK):**
```json
{
  "id": 1,
  "name": "John Doe",
  "email": "john@example.com",
  "avatar": "/uploads/avatar.jpg",
  "bio": "Software developer and blogger",
  "is_guest_author": false,
  "social_links": {
    "twitter": "https://twitter.com/johndoe"
  },
  "created_at": "2024-01-15T10:30:00.000Z"
}
```

#### Apply for Guest Author Status
```http
POST /api/auth/apply-guest-author
Authorization: Bearer <token>
```

**Response (200 OK):**
```json
{
  "message": "Guest author status granted",
  "user": {
    "id": 1,
    "name": "John Doe",
    "email": "john@example.com",
    "is_guest_author": true
  }
}
```

### Comment Endpoints

#### Get Comments for a Post
```http
GET /api/auth/comments/:postId
```

**Response (200 OK):**
```json
[
  {
    "id": 1,
    "post_id": "507f1f77bcf86cd799439011",
    "user_id": 1,
    "content": "Great article!",
    "created_at": "2024-01-15T10:30:00.000Z",
    "updated_at": "2024-01-15T10:30:00.000Z",
    "user": {
      "id": 1,
      "name": "John Doe",
      "avatar": "/uploads/avatar.jpg"
    }
  }
]
```

#### Create Comment
```http
POST /api/auth/comments
Authorization: Bearer <token>
Content-Type: application/json

{
  "post_id": "507f1f77bcf86cd799439011",
  "content": "Great article!"
}
```

**Response (201 Created):**
```json
{
  "id": 1,
  "post_id": "507f1f77bcf86cd799439011",
  "user_id": 1,
  "content": "Great article!",
  "created_at": "2024-01-15T10:30:00.000Z",
  "updated_at": "2024-01-15T10:30:00.000Z"
}
```

#### Delete Comment
```http
DELETE /api/auth/comments/:commentId
Authorization: Bearer <token>
```

**Response (200 OK):**
```json
{
  "message": "Comment deleted successfully"
}
```

### Upload Endpoints

#### Upload File
```http
POST /api/auth/upload
Authorization: Bearer <token>
Content-Type: multipart/form-data

file: <binary file data>
```

**Allowed file types:** jpeg, jpg, png, gif, pdf, doc, docx
**Max file size:** 5MB (configurable)

**Response (200 OK):**
```json
{
  "message": "File uploaded successfully",
  "filename": "1234567890_image.jpg",
  "path": "/uploads/1234567890_image.jpg"
}
```

---

## Portfolio Service API

**Base URL:** `http://localhost:3003/api/portfolio`

### Project Endpoints

#### Get All Projects
```http
GET /api/portfolio/projects
```

**Response (200 OK):**
```json
[
  {
    "id": 1,
    "title": "E-commerce Platform",
    "description": "A full-featured e-commerce platform built with React and Node.js",
    "imageUrl": "/uploads/portfolio/project1.jpg",
    "liveUrl": "https://example-ecommerce.com",
    "githubUrl": "https://github.com/user/ecommerce",
    "technologies": ["React", "Node.js", "MongoDB", "Stripe"],
    "createdAt": "2024-01-10T10:00:00",
    "updatedAt": "2024-01-10T10:00:00"
  }
]
```

#### Get Project by ID
```http
GET /api/portfolio/projects/:id
```

**Response (200 OK):**
```json
{
  "id": 1,
  "title": "E-commerce Platform",
  "description": "A full-featured e-commerce platform built with React and Node.js",
  "imageUrl": "/uploads/portfolio/project1.jpg",
  "liveUrl": "https://example-ecommerce.com",
  "githubUrl": "https://github.com/user/ecommerce",
  "technologies": ["React", "Node.js", "MongoDB", "Stripe"],
  "createdAt": "2024-01-10T10:00:00",
  "updatedAt": "2024-01-10T10:00:00"
}
```

#### Create Project
```http
POST /api/portfolio/projects
Content-Type: application/json

{
  "title": "New Project",
  "description": "Project description",
  "imageUrl": "/uploads/portfolio/image.jpg",
  "liveUrl": "https://project.com",
  "githubUrl": "https://github.com/user/project",
  "technologies": ["React", "TypeScript", "TailwindCSS"]
}
```

**Response (201 Created):**
```json
{
  "id": 2,
  "title": "New Project",
  "description": "Project description",
  "imageUrl": "/uploads/portfolio/image.jpg",
  "liveUrl": "https://project.com",
  "githubUrl": "https://github.com/user/project",
  "technologies": ["React", "TypeScript", "TailwindCSS"],
  "createdAt": "2024-01-15T14:30:00",
  "updatedAt": "2024-01-15T14:30:00"
}
```

#### Update Project
```http
PUT /api/portfolio/projects/:id
Content-Type: application/json

{
  "title": "Updated Project Title",
  "description": "Updated description",
  "technologies": ["React", "TypeScript", "TailwindCSS", "Vite"]
}
```

**Response (200 OK):**
```json
{
  "id": 2,
  "title": "Updated Project Title",
  "description": "Updated description",
  "imageUrl": "/uploads/portfolio/image.jpg",
  "liveUrl": "https://project.com",
  "githubUrl": "https://github.com/user/project",
  "technologies": ["React", "TypeScript", "TailwindCSS", "Vite"],
  "createdAt": "2024-01-15T14:30:00",
  "updatedAt": "2024-01-15T15:45:00"
}
```

#### Delete Project
```http
DELETE /api/portfolio/projects/:id
```

**Response (200 OK):**
```json
{
  "message": "Project deleted successfully"
}
```

### Skill Endpoints

#### Get All Skills
```http
GET /api/portfolio/skills
```

**Response (200 OK):**
```json
[
  {
    "id": 1,
    "name": "React",
    "category": "Frontend",
    "level": 90,
    "description": "Expert in React development"
  },
  {
    "id": 2,
    "name": "Node.js",
    "category": "Backend",
    "level": 85,
    "description": "Proficient in Node.js backend development"
  }
]
```

#### Get Skills by Category
```http
GET /api/portfolio/skills/category/:category
```

**Response (200 OK):**
```json
[
  {
    "id": 1,
    "name": "React",
    "category": "Frontend",
    "level": 90,
    "description": "Expert in React development"
  }
]
```

#### Create Skill
```http
POST /api/portfolio/skills
Content-Type: application/json

{
  "name": "TypeScript",
  "category": "Frontend",
  "level": 88,
  "description": "Strong TypeScript skills"
}
```

#### Update Skill
```http
PUT /api/portfolio/skills/:id
Content-Type: application/json

{
  "level": 92,
  "description": "Advanced TypeScript development"
}
```

#### Delete Skill
```http
DELETE /api/portfolio/skills/:id
```

### Contact Endpoints

#### Submit Contact Form
```http
POST /api/portfolio/contact
Content-Type: application/json

{
  "name": "Jane Smith",
  "email": "jane@example.com",
  "message": "I'd like to discuss a project opportunity."
}
```

**Response (201 Created):**
```json
{
  "id": 1,
  "name": "Jane Smith",
  "email": "jane@example.com",
  "message": "I'd like to discuss a project opportunity.",
  "createdAt": "2024-01-15T16:20:00"
}
```

#### Get All Contact Submissions
```http
GET /api/portfolio/contact
```

**Response (200 OK):**
```json
[
  {
    "id": 1,
    "name": "Jane Smith",
    "email": "jane@example.com",
    "message": "I'd like to discuss a project opportunity.",
    "createdAt": "2024-01-15T16:20:00"
  }
]
```

### Upload Endpoints

#### Upload Portfolio Image
```http
POST /api/portfolio/upload
Content-Type: multipart/form-data

file: <binary file data>
```

**Allowed file types:** jpg, jpeg, png, gif, webp, svg
**Max file size:** 10MB

**Response (200 OK):**
```json
{
  "message": "File uploaded successfully",
  "filename": "uuid_timestamp.jpg",
  "url": "/uploads/portfolio/uuid_timestamp.jpg",
  "size": "1024567"
}
```

#### Delete Uploaded File
```http
DELETE /api/portfolio/upload/:filename
```

**Response (200 OK):**
```json
{
  "message": "File deleted successfully"
}
```

#### List Uploaded Files
```http
GET /api/portfolio/upload/list
```

**Response (200 OK):**
```json
[
  {
    "filename": "uuid_timestamp.jpg",
    "url": "/uploads/portfolio/uuid_timestamp.jpg",
    "size": "1024567",
    "lastModified": "1705334400000"
  }
]
```

---

## Blog Service GraphQL API

**Base URL:** `http://localhost:3002/graphql`
**GraphiQL Playground:** `http://localhost:3002/graphiql`

See [GRAPHQL_DOCUMENTATION.md](./GRAPHQL_DOCUMENTATION.md) for complete GraphQL API documentation.

---

## Error Responses

All APIs use standard HTTP status codes:

- **200 OK:** Request succeeded
- **201 Created:** Resource created successfully
- **400 Bad Request:** Invalid request data
- **401 Unauthorized:** Authentication required or failed
- **404 Not Found:** Resource not found
- **500 Internal Server Error:** Server error

**Error Response Format:**
```json
{
  "message": "Error description",
  "error": "Detailed error information (in development mode)"
}
```

---

## Authentication

Protected endpoints require a JWT token in the Authorization header:

```http
Authorization: Bearer <your_jwt_token>
```

Tokens are obtained from:
- `POST /api/auth/register`
- `POST /api/auth/login`

Tokens expire after **7 days** (configurable).

---

## CORS

All services have CORS enabled for cross-origin requests. In production, configure allowed origins in environment variables.

---

## Rate Limiting

Consider implementing rate limiting for production:
- Authentication endpoints: 5 requests/minute
- General API: 100 requests/minute
- Upload endpoints: 10 requests/minute

---

## Environment Variables

See [AWS_DEPLOYMENT_GUIDE.md](./AWS_DEPLOYMENT_GUIDE.md) for complete environment configuration.

---

## Additional Resources

- [GraphQL Schema Documentation](./GRAPHQL_DOCUMENTATION.md)
- [AWS Deployment Guide](./AWS_DEPLOYMENT_GUIDE.md)
- [OpenAPI/Swagger Specification](./swagger.yaml)
