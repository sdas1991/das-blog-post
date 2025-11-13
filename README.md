# DAS Blog & Portfolio Platform 🚀

A comprehensive, production-ready blog and portfolio platform with microservices architecture. Features include blog modules, reactions, guest posts, trending algorithms, full-text search, and more.

**Built with:** Vue.js 3, Node.js, Java Spring Boot, Kotlin Spring Boot

## 🌟 Key Features

### Blog Features
- ✅ **Module System**: Organize content into modules (AI, Travel, Dev Tools, Tech Spotlight, etc.)
- ✅ **Reactions**: Likes, upvotes, and emoji reactions (👍 ❤️ 🔥)
- ✅ **Trending Algorithm**: Smart trending based on weights, reactions, views, and recency
- ✅ **Guest Posts**: Community contributions with admin approval workflow
- ✅ **Full-Text Search**: MongoDB text search with weighted indexing
- ✅ **Rich Text Editor**: TipTap editor with tables, code blocks, images, and more
- ✅ **Comments**: Threaded discussions on blog posts
- ✅ **Tags & Categories**: Flexible content organization
- ✅ **Post Weighting**: Admin-defined priority (1-10) for featured content

### Portfolio Features
- ✅ **Image Upload**: Easy portfolio image management
- ✅ **Project Showcase**: Display projects with live demos and GitHub links
- ✅ **Skills Management**: Track skills with proficiency levels (0-100)
- ✅ **Technology Tags**: Highlight tech stack for each project

### User & Community Features
- ✅ **User Profiles**: Bio, avatar, social links
- ✅ **Guest Author System**: Apply and contribute as guest author
- ✅ **Email Notifications**: Automated emails when guest posts are published
- ✅ **Role-Based Access**: User, guest author, admin roles

### Admin Features
- ✅ **Admin Dashboard**: Centralized management interface
- ✅ **Content Moderation**: Approve/reject guest posts
- ✅ **Analytics**: View counts, reaction metrics
- ✅ **No Backend Access Needed**: All features accessible from admin UI

### Homepage Dashboard
- 📊 **Widget-Based Layout**: Modular sections
- 🎯 **Portfolio Widget**: Top section showcase
- 🔥 **Trending Section**: Dynamically calculated trending posts
- 📚 **Module Widgets**: AI, Travel, Dev Tools, Tech Spotlight, Weekly Check-ins
- 💰 **Donation Section**: Placeholder for future payment integration

## 📋 Table of Contents

- [Architecture](#architecture)
- [Quick Start](#quick-start)
- [Documentation](#documentation)
- [API Reference](#api-reference)
- [Features Deep Dive](#features-deep-dive)
- [Development](#development)
- [Deployment](#deployment)
- [Environment Configuration](#environment-configuration)

---

## 🏗 Architecture

### Microservices Architecture

```
┌─────────────────────────────────────────┐
│          Frontend (Vue.js 3)            │
│              Port: 3000                 │
└────────────┬────────────────────────────┘
             │
     ┌───────┴───────┐
     │               │
┌────▼──────┐  ┌────▼──────┐  ┌──────────┐
│   Auth    │  │   Blog    │  │Portfolio │
│  Service  │  │  Service  │  │ Service  │
│ (Node.js) │  │  (Java)   │  │ (Kotlin) │
│   :3001   │  │   :3002   │  │  :3003   │
└─────┬─────┘  └─────┬─────┘  └────┬─────┘
      │              │               │
┌─────▼─────┐  ┌────▼────┐  ┌──────▼──────┐
│PostgreSQL │  │ MongoDB │  │    MySQL    │
│   :5432   │  │  :27017 │  │    :3306    │
└───────────┘  └─────────┘  └─────────────┘
```

### Service Responsibilities

#### **Frontend Service** (Vue.js 3)
- Modern SPA with Vite
- Pinia state management
- TipTap rich text editor
- Responsive design
- Widget-based homepage

#### **Auth Service** (Node.js + PostgreSQL)
- User authentication (JWT)
- User profiles with social links
- Guest author management
- Comments system
- File uploads
- **Port**: 3001

#### **Blog Service** (Java + Spring Boot + MongoDB)
- Blog post CRUD
- GraphQL API
- Module system (AI, Travel, etc.)
- Reactions (likes, upvotes, emojis)
- Trending algorithm
- Guest post workflow
- Full-text search
- Email notifications
- **Port**: 3002

#### **Portfolio Service** (Kotlin + Spring Boot + MySQL)
- Project showcase
- Skills management
- Contact form
- Image upload
- **Port**: 3003

---

## 🚀 Quick Start

### Prerequisites

- Docker & Docker Compose
- Node.js 20+ (for local development)
- Git

### 1. Clone & Setup

```bash
git clone <repository-url>
cd das-blog-post

# Copy environment template
cp .env.example .env

# Edit .env with your configuration
nano .env
```

### 2. Start with Docker Compose

```bash
# Build and start all services
docker-compose up --build

# Or run in background
docker-compose up -d --build
```

### 3. Access the Application

- **Frontend**: http://localhost:3000
- **Admin Dashboard**: http://localhost:3000/admin
- **GraphiQL Playground**: http://localhost:3002/graphiql
- **Auth API**: http://localhost:3001/api/auth
- **Portfolio API**: http://localhost:3003/api/portfolio

### 4. Create Admin User

```bash
# Register via API
curl -X POST http://localhost:3001/api/auth/register \
  -H "Content-Type: application/json" \
  -d '{
    "name": "Admin",
    "email": "admin@example.com",
    "password": "admin123"
  }'
```

---

## 📚 Documentation

Comprehensive documentation available in the `/docs` directory:

### 📖 [API Documentation](./docs/API_DOCUMENTATION.md)
Complete REST API reference for Auth and Portfolio services.
- Authentication endpoints
- User profile management
- Portfolio CRUD operations
- File upload endpoints
- Request/response examples

### 📖 [GraphQL Documentation](./docs/GRAPHQL_DOCUMENTATION.md)
Complete GraphQL API reference for Blog service.
- Schema overview
- Queries and mutations
- Reactions system
- Trending algorithm
- Guest post workflow
- Introspection query

### 📖 [AWS Deployment Guide](./docs/AWS_DEPLOYMENT_GUIDE.md)
Step-by-step guide for deploying to AWS Free Tier.
- EC2 instance setup
- Docker deployment
- SSL configuration
- Database backups
- Monitoring setup
- Cost optimization

---

## 🔌 API Reference

### Auth Service (REST)

**Base URL**: `http://localhost:3001/api/auth`

```bash
# Register
POST /register
{
  "name": "John Doe",
  "email": "john@example.com",
  "password": "password123"
}

# Login
POST /login
{
  "email": "john@example.com",
  "password": "password123"
}

# Get current user
GET /me
Authorization: Bearer <token>

# Update profile
PUT /profile
Authorization: Bearer <token>
{
  "bio": "Full-stack developer",
  "social_links": {
    "github": "https://github.com/username"
  }
}

# Apply for guest author
POST /apply-guest-author
Authorization: Bearer <token>
```

### Blog Service (GraphQL)

**Base URL**: `http://localhost:3002/graphql`

```graphql
# Get posts by module
query {
  posts(filters: {
    module: "AI"
    published: true
    limit: 10
  }) {
    id
    title
    excerpt
    reactions {
      likes
      upvotes
    }
  }
}

# Get trending posts
query {
  trendingPosts(limit: 5) {
    id
    title
    weight
    views
    reactions {
      likes
      upvotes
    }
  }
}

# Create post
mutation {
  createPost(input: {
    title: "Getting Started with AI"
    content: "<p>Content here...</p>"
    module: "AI"
    tags: ["ai", "tutorial"]
    weight: 5
    published: true
  }) {
    id
    title
  }
}

# React to post
mutation {
  reactToPost(
    postId: "123"
    reactionType: "like"
    userId: 1
  ) {
    id
    reactions {
      likes
    }
  }
}

# Approve guest post
mutation {
  approveGuestPost(postId: "123") {
    id
    published
    guestAuthorStatus
  }
}
```

### Portfolio Service (REST)

**Base URL**: `http://localhost:3003/api/portfolio`

```bash
# Get all projects
GET /projects

# Create project
POST /projects
{
  "title": "My Project",
  "description": "Description",
  "technologies": ["React", "Node.js"],
  "imageUrl": "/uploads/portfolio/image.jpg"
}

# Upload image
POST /upload
Content-Type: multipart/form-data
file: <binary>

# Get all skills
GET /skills

# Create skill
POST /skills
{
  "name": "React",
  "category": "Frontend",
  "level": 90
}
```

---

## 🎯 Features Deep Dive

### Trending Algorithm

Posts are ranked by a composite score:

```
score = (weight × 100) + (reactions × 10) + (views × 0.5) × recency_factor

where:
  weight = Admin-defined priority (1-10)
  reactions = likes + (upvotes × 2) + emoji_count
  recency_factor = 1.0 / (1.0 + days_old / 7.0)
```

**Usage**:
```graphql
query {
  trendingPosts(limit: 5) {
    title
    weight
    views
    reactions { likes upvotes }
  }
}
```

### Module System

Organize blog posts into thematic modules:

- **AI**: AI and machine learning content
- **Travel**: Travel stories and photography
- **Dev Tools**: Developer tools and libraries
- **Tech Spotlight**: Monthly deep dives
- **Weekly Check-ins**: Short-form updates
- **Reading Log**: Book reviews and recommendations
- **Community Questions**: Q&A section

**Usage**:
```graphql
query {
  modules  # Get all modules
  posts(filters: { module: "AI" }) {
    title
    module
  }
}
```

### Reactions System

Users can interact with posts via:
- **Likes**: Quick appreciation
- **Upvotes**: More meaningful endorsement (counts 2× in trending)
- **Emojis**: Expressive reactions (👍, ❤️, 🔥, 😂, 😍, etc.)

**Usage**:
```graphql
mutation {
  reactToPost(
    postId: "123"
    reactionType: "emoji"
    emoji: "🔥"
    userId: 1
  ) {
    reactions {
      emojis {
        emoji
        count
      }
    }
  }
}
```

### Guest Post Workflow

1. User applies for guest author status
2. User creates post (automatically set to `pending`)
3. Admin reviews in admin dashboard
4. Admin approves or rejects
5. On approval:
   - Post is published
   - Email notification sent to guest author

**Statuses**: `draft`, `pending`, `approved`, `rejected`

### Full-Text Search

MongoDB text search with weighted fields:
- Title (weight: 3)
- Excerpt (weight: 2)
- Content (weight: 1)
- Tags (weight: 1)

**Usage**:
```graphql
query {
  posts(filters: {
    search: "GraphQL tutorial"
    published: true
  }) {
    title
    excerpt
  }
}
```

---

## 💻 Development

### Local Development Setup

#### Frontend Development

```bash
cd frontend
npm install
npm run dev
```

Access at: http://localhost:5173 (Vite dev server)

#### Backend Services

**Auth Service**:
```bash
cd backend/auth-service
npm install
npm run dev
```

**Blog Service**:
```bash
cd backend/blog-service
mvn spring-boot:run
```

**Portfolio Service**:
```bash
cd backend/portfolio-service
mvn spring-boot:run
```

### Project Structure

```
das-blog-post/
├── frontend/                 # Vue.js 3 frontend
│   ├── src/
│   │   ├── components/       # Reusable components
│   │   ├── views/            # Page components
│   │   │   ├── admin/        # Admin dashboard
│   │   │   └── ...
│   │   ├── stores/           # Pinia stores
│   │   ├── services/         # API services
│   │   └── router/           # Vue Router
│   └── package.json
│
├── backend/
│   ├── auth-service/         # Node.js + Express + PostgreSQL
│   │   ├── src/
│   │   │   ├── controllers/
│   │   │   ├── routes/
│   │   │   ├── middleware/
│   │   │   └── config/
│   │   └── package.json
│   │
│   ├── blog-service/         # Java + Spring Boot + MongoDB
│   │   ├── src/main/
│   │   │   ├── java/.../
│   │   │   │   ├── controller/
│   │   │   │   ├── service/
│   │   │   │   ├── model/
│   │   │   │   └── repository/
│   │   │   └── resources/
│   │   │       └── graphql/
│   │   └── pom.xml
│   │
│   └── portfolio-service/    # Kotlin + Spring Boot + MySQL
│       ├── src/main/kotlin/
│       │   └── .../
│       │       ├── controller/
│       │       ├── service/
│       │       ├── model/
│       │       └── repository/
│       └── pom.xml
│
├── docs/                     # Comprehensive documentation
│   ├── API_DOCUMENTATION.md
│   ├── GRAPHQL_DOCUMENTATION.md
│   └── AWS_DEPLOYMENT_GUIDE.md
│
├── docker-compose.yml        # Multi-service orchestration
├── .env.example              # Environment template
└── README.md
```

---

## 🌐 Deployment

### Docker Compose (Development/Staging)

```bash
# Start all services
docker-compose up -d --build

# View logs
docker-compose logs -f

# Stop services
docker-compose down

# Clean volumes (reset databases)
docker-compose down -v
```

### AWS Free Tier Deployment

Detailed deployment guide: [AWS_DEPLOYMENT_GUIDE.md](./docs/AWS_DEPLOYMENT_GUIDE.md)

**Quick Overview**:

1. **EC2 Instance** (t2.micro)
   - Ubuntu 22.04 LTS
   - Docker & Docker Compose
   - 30 GB EBS storage

2. **Databases**
   - PostgreSQL (Docker container)
   - MongoDB (Docker container)
   - MySQL (Docker container)
   - OR use RDS/Atlas (Free Tier)

3. **Nginx Reverse Proxy**
   - Route traffic to services
   - SSL with Let's Encrypt

4. **Backups**
   - Daily automated backups
   - S3 storage (5 GB free)

5. **Monitoring**
   - CloudWatch logs
   - Health check scripts

**Estimated Cost**: $0-15/month (depends on traffic)

### Production Checklist

- [ ] Update `JWT_SECRET` to a strong random value
- [ ] Configure production database credentials
- [ ] Setup email SMTP (Gmail app password or AWS SES)
- [ ] Enable SSL/TLS certificates
- [ ] Configure CORS allowed origins
- [ ] Setup automated backups
- [ ] Enable logging and monitoring
- [ ] Configure rate limiting
- [ ] Setup CI/CD pipeline
- [ ] Enable security headers
- [ ] Configure firewall rules
- [ ] Setup domain and DNS

---

## ⚙️ Environment Configuration

### Required Variables

Create `.env` from `.env.example`:

```bash
cp .env.example .env
```

**Essential variables**:

```env
# JWT Configuration
JWT_SECRET=your_very_secure_jwt_secret_key_minimum_32_characters_long
JWT_EXPIRES_IN=7d

# Database Credentials
DB_USER=postgres
DB_PASSWORD=your_secure_password

MYSQL_PASSWORD=your_mysql_password

# Email (for guest author notifications)
MAIL_USERNAME=your_email@gmail.com
MAIL_PASSWORD=your_gmail_app_password
```

See [.env.example](./.env.example) for complete configuration.

---

## 🔧 Troubleshooting

### Services Won't Start

```bash
# Check if ports are in use
sudo lsof -i :3000 -i :3001 -i :3002 -i :3003

# Clean Docker system
docker system prune -a
docker volume prune

# Restart from scratch
docker-compose down -v
docker-compose up --build
```

### Database Connection Errors

```bash
# Check database logs
docker-compose logs postgres
docker-compose logs mongodb
docker-compose logs mysql

# Restart databases
docker-compose restart postgres mongodb mysql

# Wait for initialization (can take 30-60 seconds)
```

### Frontend Can't Reach Backend

```bash
# Check service status
docker-compose ps

# Verify network
docker network inspect das-blog-post_app-network

# Check CORS configuration in backend services
```

---

## 📊 Database Schema

### PostgreSQL (Auth Service)

**users**:
- id, name, email, password, avatar
- bio, role, is_guest_author
- social_links (JSONB)
- created_at, updated_at

**comments**:
- id, post_id, user_id, content
- created_at, updated_at

### MongoDB (Blog Service)

**posts**:
```javascript
{
  title, slug, excerpt, content,
  author: { id, name, email },
  tags: [],
  category,
  module,  // AI, Travel, Dev Tools, etc.
  published,
  publishedAt,
  views,
  weight,  // 1-10 for trending
  reactions: {
    likes, upvotes,
    emojis: { emoji: count },
    userLikes: [], userUpvotes: []
  },
  isGuestPost,
  guestAuthorEmail,
  guestAuthorStatus  // pending, approved, rejected
}
```

### MySQL (Portfolio Service)

**projects**:
- id, title, description
- image_url, live_url, github_url
- technologies (ManyToMany)
- created_at, updated_at

**skills**:
- id, name, category, level (0-100)
- description

---

## 🤝 Contributing

1. Fork the repository
2. Create feature branch: `git checkout -b feature/amazing-feature`
3. Commit changes: `git commit -m 'Add amazing feature'`
4. Push to branch: `git push origin feature/amazing-feature`
5. Open Pull Request

---

## 📄 License

MIT License - see LICENSE file for details

---

## 👤 Author

**DAS** - Full Stack Developer

---

## 🛠 Tech Stack

### Frontend
- Vue.js 3 (Composition API)
- Vite 5
- Pinia (State Management)
- Vue Router 4
- TipTap 2 (Rich Text Editor)
- Axios

### Backend
- **Auth**: Node.js, Express, PostgreSQL, JWT, Bcrypt
- **Blog**: Java 17, Spring Boot, GraphQL, MongoDB, Spring Mail
- **Portfolio**: Kotlin, Spring Boot, MySQL, JPA

### DevOps
- Docker & Docker Compose
- Nginx (Reverse Proxy)
- Let's Encrypt (SSL)

### Cloud
- AWS EC2, RDS, S3, CloudWatch
- MongoDB Atlas (optional)

---

## 🎉 Acknowledgments

- TipTap for the amazing rich text editor
- Spring GraphQL for the excellent GraphQL implementation
- Vue.js team for the incredible framework
- All open-source contributors

---

## 📞 Support

For questions or issues:
- Check [API Documentation](./docs/API_DOCUMENTATION.md)
- Review [GraphQL Documentation](./docs/GRAPHQL_DOCUMENTATION.md)
- See [AWS Deployment Guide](./docs/AWS_DEPLOYMENT_GUIDE.md)
- Open an issue on GitHub

**Happy Coding! 🚀**
