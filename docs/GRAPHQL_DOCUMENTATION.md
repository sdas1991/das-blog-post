# GraphQL API Documentation

Complete GraphQL API documentation for the Blog Service.

**Endpoint:** `http://localhost:3002/graphql`
**GraphiQL Playground:** `http://localhost:3002/graphiql`

## Table of Contents

1. [Schema Overview](#schema-overview)
2. [Queries](#queries)
3. [Mutations](#mutations)
4. [Types](#types)
5. [Input Types](#input-types)
6. [Example Queries](#example-queries)
7. [Introspection Query](#introspection-query)

---

## Schema Overview

The Blog Service provides a GraphQL API for managing blog posts with support for:
- Full-text search
- Categories and tags
- Modules (AI, Travel, Dev Tools, etc.)
- Reactions (likes, upvotes, emojis)
- Guest post submissions
- Trending posts algorithm
- View tracking

---

## Queries

### posts

Get a list of posts with optional filters.

```graphql
posts(filters: PostFilters): [Post!]!
```

**Arguments:**
- `filters` (PostFilters): Optional filters for the query

**Example:**
```graphql
query {
  posts(filters: {
    published: true
    module: "AI"
    limit: 10
  }) {
    id
    title
    excerpt
    publishedAt
    module
    category
    tags
    reactions {
      likes
      upvotes
    }
  }
}
```

### post

Get a single post by ID. Automatically increments view count.

```graphql
post(id: ID!): Post
```

**Arguments:**
- `id` (ID!): Post ID

**Example:**
```graphql
query {
  post(id: "507f1f77bcf86cd799439011") {
    id
    title
    slug
    content
    author {
      id
      name
      email
    }
    tags
    category
    module
    views
    reactions {
      likes
      upvotes
      emojis {
        emoji
        count
      }
    }
  }
}
```

### categories

Get all unique categories.

```graphql
categories: [String!]!
```

**Example:**
```graphql
query {
  categories
}
```

**Response:**
```json
{
  "data": {
    "categories": ["Technology", "Tutorial", "News", "Opinion"]
  }
}
```

### tags

Get all unique tags.

```graphql
tags: [String!]!
```

**Example:**
```graphql
query {
  tags
}
```

**Response:**
```json
{
  "data": {
    "tags": ["react", "nodejs", "graphql", "ai", "travel"]
  }
}
```

### modules

Get all unique modules.

```graphql
modules: [String!]!
```

**Example:**
```graphql
query {
  modules
}
```

**Response:**
```json
{
  "data": {
    "modules": ["AI", "Travel", "Dev Tools", "Tech Spotlight", "Weekly Check-ins"]
  }
}
```

### trendingPosts

Get trending posts based on weight, reactions, views, and recency.

```graphql
trendingPosts(limit: Int): [Post!]!
```

**Arguments:**
- `limit` (Int): Maximum number of trending posts to return (default: 10)

**Trending Score Algorithm:**
```
score = (weight * 100) + (reactions * 10) + (views * 0.5) * recency_factor
where recency_factor = 1.0 / (1.0 + days_old / 7.0)
```

**Example:**
```graphql
query {
  trendingPosts(limit: 5) {
    id
    title
    excerpt
    module
    weight
    views
    reactions {
      likes
      upvotes
    }
    trendingScore
  }
}
```

### guestPosts

Get guest post submissions with optional status filter.

```graphql
guestPosts(status: String): [Post!]!
```

**Arguments:**
- `status` (String): Filter by status ("draft", "pending", "approved", "rejected")

**Example:**
```graphql
query {
  guestPosts(status: "pending") {
    id
    title
    excerpt
    isGuestPost
    guestAuthorEmail
    guestAuthorStatus
    createdAt
  }
}
```

---

## Mutations

### createPost

Create a new blog post.

```graphql
createPost(input: PostInput!): Post!
```

**Arguments:**
- `input` (PostInput!): Post data

**Example:**
```graphql
mutation {
  createPost(input: {
    title: "Getting Started with GraphQL"
    slug: "getting-started-with-graphql"
    excerpt: "Learn the basics of GraphQL"
    content: "<h1>Introduction</h1><p>GraphQL is...</p>"
    tags: ["graphql", "tutorial"]
    category: "Tutorial"
    module: "Dev Tools"
    published: true
    weight: 5
  }) {
    id
    title
    slug
    publishedAt
  }
}
```

### updatePost

Update an existing post.

```graphql
updatePost(id: ID!, input: PostInput!): Post!
```

**Arguments:**
- `id` (ID!): Post ID
- `input` (PostInput!): Updated post data

**Example:**
```graphql
mutation {
  updatePost(
    id: "507f1f77bcf86cd799439011"
    input: {
      title: "Updated Title"
      weight: 8
      published: true
    }
  ) {
    id
    title
    weight
    updatedAt
  }
}
```

### deletePost

Delete a post.

```graphql
deletePost(id: ID!): Boolean!
```

**Arguments:**
- `id` (ID!): Post ID

**Example:**
```graphql
mutation {
  deletePost(id: "507f1f77bcf86cd799439011")
}
```

**Response:**
```json
{
  "data": {
    "deletePost": true
  }
}
```

### reactToPost

Add or remove a reaction to a post.

```graphql
reactToPost(
  postId: ID!
  reactionType: String!
  emoji: String
  userId: Int!
): Post!
```

**Arguments:**
- `postId` (ID!): Post ID
- `reactionType` (String!): "like", "upvote", or "emoji"
- `emoji` (String): Emoji character (required if reactionType is "emoji")
- `userId` (Int!): User ID (from authentication)

**Reaction Types:**
- `like`: Toggle like (adds or removes)
- `upvote`: Toggle upvote (adds or removes)
- `emoji`: Add emoji reaction (👍, ❤️, 🔥, etc.)

**Example - Like:**
```graphql
mutation {
  reactToPost(
    postId: "507f1f77bcf86cd799439011"
    reactionType: "like"
    userId: 1
  ) {
    id
    reactions {
      likes
      userLikes
    }
  }
}
```

**Example - Emoji:**
```graphql
mutation {
  reactToPost(
    postId: "507f1f77bcf86cd799439011"
    reactionType: "emoji"
    emoji: "🔥"
    userId: 1
  ) {
    id
    reactions {
      emojis {
        emoji
        count
      }
    }
  }
}
```

### approveGuestPost

Approve a guest post submission (publishes it).

```graphql
approveGuestPost(postId: ID!): Post!
```

**Arguments:**
- `postId` (ID!): Post ID

**Example:**
```graphql
mutation {
  approveGuestPost(postId: "507f1f77bcf86cd799439011") {
    id
    title
    published
    guestAuthorStatus
    publishedAt
  }
}
```

**Note:** Sends email notification to guest author upon approval.

### rejectGuestPost

Reject a guest post submission.

```graphql
rejectGuestPost(postId: ID!): Post!
```

**Arguments:**
- `postId` (ID!): Post ID

**Example:**
```graphql
mutation {
  rejectGuestPost(postId: "507f1f77bcf86cd799439011") {
    id
    title
    guestAuthorStatus
  }
}
```

---

## Types

### Post

```graphql
type Post {
  id: ID!
  title: String!
  slug: String!
  excerpt: String
  content: String!
  author: Author!
  tags: [String!]
  category: String
  module: String
  published: Boolean!
  publishedAt: String
  createdAt: String!
  updatedAt: String!
  views: Int
  weight: Int
  reactions: Reactions
  isGuestPost: Boolean
  guestAuthorEmail: String
  guestAuthorStatus: String
  trendingScore: Float
}
```

**Fields:**
- `id`: MongoDB ObjectId
- `title`: Post title
- `slug`: URL-friendly slug
- `excerpt`: Short description/preview
- `content`: Full HTML content
- `author`: Author information
- `tags`: Array of tags
- `category`: Category name
- `module`: Module/section (AI, Travel, Dev Tools, etc.)
- `published`: Publication status
- `publishedAt`: Publication timestamp (ISO 8601)
- `createdAt`: Creation timestamp
- `updatedAt`: Last update timestamp
- `views`: View count
- `weight`: Admin-defined priority (1-10, default: 1)
- `reactions`: Reaction data
- `isGuestPost`: Whether this is a guest post
- `guestAuthorEmail`: Email of guest author
- `guestAuthorStatus`: Status ("draft", "pending", "approved", "rejected")
- `trendingScore`: Calculated trending score (only in trendingPosts query)

### Author

```graphql
type Author {
  id: Int!
  name: String!
  email: String!
}
```

### Reactions

```graphql
type Reactions {
  likes: Int!
  upvotes: Int!
  emojis: [EmojiCount!]
  userLikes: [Int!]
  userUpvotes: [Int!]
}
```

**Fields:**
- `likes`: Total like count
- `upvotes`: Total upvote count
- `emojis`: Array of emoji reactions with counts
- `userLikes`: Array of user IDs who liked
- `userUpvotes`: Array of user IDs who upvoted

### EmojiCount

```graphql
type EmojiCount {
  emoji: String!
  count: Int!
}
```

---

## Input Types

### PostInput

```graphql
input PostInput {
  title: String!
  slug: String
  excerpt: String
  content: String!
  tags: [String!]
  category: String
  module: String
  published: Boolean
  weight: Int
  isGuestPost: Boolean
  guestAuthorEmail: String
}
```

**Fields:**
- `title` (required): Post title
- `slug` (optional): Custom slug (auto-generated from title if not provided)
- `excerpt` (optional): Short description
- `content` (required): Full HTML content
- `tags` (optional): Array of tags
- `category` (optional): Category name
- `module` (optional): Module/section name
- `published` (optional): Publication status (default: false)
- `weight` (optional): Priority weight 1-10 (default: 1)
- `isGuestPost` (optional): Mark as guest post
- `guestAuthorEmail` (optional): Guest author email

### PostFilters

```graphql
input PostFilters {
  published: Boolean
  category: String
  tag: String
  module: String
  search: String
  limit: Int
  sortBy: String
  isGuestPost: Boolean
  guestAuthorStatus: String
}
```

**Fields:**
- `published`: Filter by publication status
- `category`: Filter by category
- `tag`: Filter by tag
- `module`: Filter by module
- `search`: Full-text search (searches title, excerpt, content)
- `limit`: Maximum number of results
- `sortBy`: Sort order (not yet implemented)
- `isGuestPost`: Filter guest posts
- `guestAuthorStatus`: Filter by guest post status

---

## Example Queries

### Get All Published Posts with Full Details

```graphql
query GetPublishedPosts {
  posts(filters: { published: true, limit: 20 }) {
    id
    title
    slug
    excerpt
    author {
      id
      name
    }
    tags
    category
    module
    publishedAt
    views
    weight
    reactions {
      likes
      upvotes
      emojis {
        emoji
        count
      }
    }
  }
}
```

### Search Posts

```graphql
query SearchPosts($searchTerm: String!) {
  posts(filters: { search: $searchTerm, published: true }) {
    id
    title
    excerpt
    module
    category
  }
}
```

**Variables:**
```json
{
  "searchTerm": "GraphQL tutorial"
}
```

### Get Posts by Module

```graphql
query GetAIPosts {
  posts(filters: { module: "AI", published: true, limit: 10 }) {
    id
    title
    excerpt
    publishedAt
    views
  }
}
```

### Create Guest Post

```graphql
mutation SubmitGuestPost {
  createPost(input: {
    title: "My Guest Post About AI"
    content: "<p>Content here...</p>"
    excerpt: "An interesting perspective on AI"
    module: "AI"
    tags: ["ai", "machine-learning"]
    isGuestPost: true
    guestAuthorEmail: "guest@example.com"
  }) {
    id
    title
    isGuestPost
    guestAuthorStatus
  }
}
```

### Get Trending Posts

```graphql
query GetTrending {
  trendingPosts(limit: 5) {
    id
    title
    excerpt
    module
    weight
    views
    reactions {
      likes
      upvotes
    }
    publishedAt
  }
}
```

### React to Post

```graphql
mutation LikePost {
  reactToPost(
    postId: "507f1f77bcf86cd799439011"
    reactionType: "like"
    userId: 1
  ) {
    id
    reactions {
      likes
      upvotes
      userLikes
    }
  }
}
```

### Complete Post CRUD Example

```graphql
# Create
mutation {
  createPost(input: {
    title: "New Post"
    content: "<p>Content</p>"
    published: false
  }) {
    id
  }
}

# Read
query {
  post(id: "507f1f77bcf86cd799439011") {
    title
    content
  }
}

# Update
mutation {
  updatePost(
    id: "507f1f77bcf86cd799439011"
    input: {
      title: "Updated Post"
      published: true
    }
  ) {
    id
    title
    published
  }
}

# Delete
mutation {
  deletePost(id: "507f1f77bcf86cd799439011")
}
```

---

## Introspection Query

Use this query to get the complete schema:

```graphql
query IntrospectionQuery {
  __schema {
    queryType {
      name
      fields {
        name
        description
        args {
          name
          type {
            name
            kind
          }
        }
        type {
          name
          kind
        }
      }
    }
    mutationType {
      name
      fields {
        name
        description
        args {
          name
          type {
            name
            kind
          }
        }
        type {
          name
          kind
        }
      }
    }
    types {
      name
      kind
      description
      fields {
        name
        description
        type {
          name
          kind
          ofType {
            name
            kind
          }
        }
      }
      inputFields {
        name
        description
        type {
          name
          kind
        }
      }
    }
  }
}
```

### Simplified Introspection for Schema Export

```graphql
{
  __schema {
    types {
      name
      kind
      fields {
        name
        type {
          name
          kind
        }
      }
    }
  }
}
```

---

## Testing with GraphiQL

Access the interactive GraphiQL playground at:
```
http://localhost:3002/graphiql
```

Features:
- **Auto-completion**: Press Ctrl+Space for suggestions
- **Documentation Explorer**: Click "Docs" to browse schema
- **Query History**: Access previous queries
- **Variables Panel**: Test queries with variables
- **Prettify**: Format your queries

---

## Error Handling

GraphQL errors follow this format:

```json
{
  "errors": [
    {
      "message": "Post not found",
      "locations": [{ "line": 2, "column": 3 }],
      "path": ["post"]
    }
  ],
  "data": {
    "post": null
  }
}
```

Common errors:
- **ValidationError**: Invalid input data
- **Not Found**: Resource doesn't exist
- **Unauthorized**: Authentication required (future implementation)

---

## Performance Considerations

1. **Pagination**: Use `limit` filter to control result size
2. **Field Selection**: Only request fields you need
3. **Caching**: Consider implementing DataLoader for N+1 queries
4. **Indexing**: MongoDB indexes on:
   - `slug` (unique)
   - `published` + `publishedAt`
   - Text index on `title`, `excerpt`, `content`

---

## Future Enhancements

- [ ] Pagination with cursor-based approach
- [ ] Subscriptions for real-time updates
- [ ] Advanced sorting options
- [ ] Field-level authentication
- [ ] Query complexity limits
- [ ] Response caching with Redis

---

## Additional Resources

- [GraphQL Official Docs](https://graphql.org/)
- [Spring Boot GraphQL Guide](https://spring.io/guides/gs/graphql-server/)
- [API Documentation](./API_DOCUMENTATION.md)
- [AWS Deployment Guide](./AWS_DEPLOYMENT_GUIDE.md)
