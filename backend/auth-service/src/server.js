require('dotenv').config()
const express = require('express')
const cors = require('cors')
const { initDatabase } = require('./config/database')
const authRoutes = require('./routes/auth')
const commentRoutes = require('./routes/comments')
const uploadRoutes = require('./routes/upload')

const app = express()
const PORT = process.env.PORT || 3001

// Middleware
app.use(cors())
app.use(express.json())
app.use(express.urlencoded({ extended: true }))

// Static files
app.use('/uploads', express.static('uploads'))

// Health check
app.get('/health', (req, res) => {
  res.json({ status: 'ok', service: 'auth-service' })
})

// Routes
app.use('/api/auth', authRoutes)
app.use('/api/auth/comments', commentRoutes)
app.use('/api/auth/upload', uploadRoutes)

// Error handling
app.use((err, req, res, next) => {
  console.error(err.stack)
  res.status(err.status || 500).json({
    message: err.message || 'Internal server error',
    ...(process.env.NODE_ENV === 'development' && { stack: err.stack })
  })
})

// Initialize database and start server
initDatabase()
  .then(() => {
    app.listen(PORT, () => {
      console.log(`Auth service running on port ${PORT}`)
    })
  })
  .catch(err => {
    console.error('Failed to initialize database:', err)
    process.exit(1)
  })
