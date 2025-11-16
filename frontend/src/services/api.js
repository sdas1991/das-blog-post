import axios from 'axios'

const API_BASE_URL = import.meta.env.VITE_API_BASE_URL || ''

// Auth Service API
export const authApi = axios.create({
  baseURL: `${API_BASE_URL}/api/auth`,
  headers: {
    'Content-Type': 'application/json'
  }
})

// Blog Service API (GraphQL)
export const blogApi = axios.create({
  baseURL: `${API_BASE_URL}/api/blog`,
  headers: {
    'Content-Type': 'application/json'
  }
})

// Portfolio Service API
export const portfolioApi = axios.create({
  baseURL: `${API_BASE_URL}/api/portfolio`,
  headers: {
    'Content-Type': 'application/json'
  }
})

// Add token to requests
const apis = [authApi, blogApi, portfolioApi]
apis.forEach(api => {
  api.interceptors.request.use(config => {
    const token = localStorage.getItem('token')
    if (token) {
      config.headers.Authorization = `Bearer ${token}`
    }
    return config
  })

  api.interceptors.response.use(
    response => response,
    error => {
      if (error.response?.status === 401) {
        localStorage.removeItem('token')
        window.location.href = '/login'
      }
      return Promise.reject(error)
    }
  )
})

// Auth API methods
export const authService = {
  async login(email, password) {
    const response = await authApi.post('/login', { email, password })
    return response.data
  },

  async register(userData) {
    const response = await authApi.post('/register', userData)
    return response.data
  },

  async getCurrentUser() {
    const response = await authApi.get('/me')
    return response.data
  },

  async uploadFile(file) {
    const formData = new FormData()
    formData.append('file', file)
    const response = await authApi.post('/upload', formData, {
      headers: { 'Content-Type': 'multipart/form-data' }
    })
    return response.data
  }
}

// Blog API methods (GraphQL)
export const blogService = {
  async getPosts(filters = {}) {
    const query = `
      query GetPosts($filters: PostFilters) {
        posts(filters: $filters) {
          id
          title
          slug
          excerpt
          content
          author {
            id
            name
            email
          }
          tags
          category
          published
          publishedAt
          createdAt
          updatedAt
          views
          theme
        }
      }
    `
    const response = await blogApi.post('/graphql', {
      query,
      variables: { filters }
    })
    return response.data.data.posts
  },

  async getPost(id) {
    const query = `
      query GetPost($id: ID!) {
        post(id: $id) {
          id
          title
          slug
          excerpt
          content
          author {
            id
            name
            email
          }
          tags
          category
          published
          publishedAt
          createdAt
          updatedAt
          views
          theme
        }
      }
    `
    const response = await blogApi.post('/graphql', {
      query,
      variables: { id }
    })
    return response.data.data.post
  },

  async createPost(postData) {
    const mutation = `
      mutation CreatePost($input: PostInput!) {
        createPost(input: $input) {
          id
          title
          slug
          excerpt
          content
          tags
          category
          published
          publishedAt
          createdAt
          theme
        }
      }
    `
    const response = await blogApi.post('/graphql', {
      query: mutation,
      variables: { input: postData }
    })
    return response.data.data.createPost
  },

  async updatePost(id, postData) {
    const mutation = `
      mutation UpdatePost($id: ID!, $input: PostInput!) {
        updatePost(id: $id, input: $input) {
          id
          title
          slug
          excerpt
          content
          tags
          category
          published
          publishedAt
          updatedAt
          theme
        }
      }
    `
    const response = await blogApi.post('/graphql', {
      query: mutation,
      variables: { id, input: postData }
    })
    return response.data.data.updatePost
  },

  async deletePost(id) {
    const mutation = `
      mutation DeletePost($id: ID!) {
        deletePost(id: $id)
      }
    `
    const response = await blogApi.post('/graphql', {
      query: mutation,
      variables: { id }
    })
    return response.data.data.deletePost
  },

  async getCategories() {
    const query = `
      query GetCategories {
        categories
      }
    `
    const response = await blogApi.post('/graphql', { query })
    return response.data.data.categories
  },

  async getTags() {
    const query = `
      query GetTags {
        tags
      }
    `
    const response = await blogApi.post('/graphql', { query })
    return response.data.data.tags
  }
}

// Comment API methods
export const commentService = {
  async getComments(postId) {
    const response = await authApi.get(`/comments/${postId}`)
    return response.data
  },

  async createComment(postId, content) {
    const response = await authApi.post('/comments', { postId, content })
    return response.data
  },

  async deleteComment(commentId) {
    const response = await authApi.delete(`/comments/${commentId}`)
    return response.data
  }
}

// Portfolio API methods
export const portfolioService = {
  async getProjects() {
    const response = await portfolioApi.get('/projects')
    return response.data
  },

  async getProject(id) {
    const response = await portfolioApi.get(`/projects/${id}`)
    return response.data
  },

  async createProject(projectData) {
    const response = await portfolioApi.post('/projects', projectData)
    return response.data
  },

  async updateProject(id, projectData) {
    const response = await portfolioApi.put(`/projects/${id}`, projectData)
    return response.data
  },

  async deleteProject(id) {
    const response = await portfolioApi.delete(`/projects/${id}`)
    return response.data
  },

  async getSkills() {
    const response = await portfolioApi.get('/skills')
    return response.data
  },

  async submitContact(contactData) {
    const response = await portfolioApi.post('/contact', contactData)
    return response.data
  }
}
