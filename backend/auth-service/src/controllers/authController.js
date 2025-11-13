const bcrypt = require('bcryptjs')
const { pool } = require('../config/database')
const { generateToken } = require('../utils/jwt')

const register = async (req, res) => {
  try {
    const { name, email, password } = req.body

    // Validate input
    if (!name || !email || !password) {
      return res.status(400).json({ message: 'All fields are required' })
    }

    if (password.length < 6) {
      return res.status(400).json({ message: 'Password must be at least 6 characters' })
    }

    // Check if user exists
    const existingUser = await pool.query(
      'SELECT * FROM users WHERE email = $1',
      [email]
    )

    if (existingUser.rows.length > 0) {
      return res.status(400).json({ message: 'User already exists' })
    }

    // Hash password
    const hashedPassword = await bcrypt.hash(password, 10)

    // Create user
    const result = await pool.query(
      'INSERT INTO users (name, email, password) VALUES ($1, $2, $3) RETURNING id, name, email, created_at',
      [name, email, hashedPassword]
    )

    const user = result.rows[0]

    // Generate token
    const token = generateToken(user.id, user.email)

    res.status(201).json({
      token,
      user: {
        id: user.id,
        name: user.name,
        email: user.email,
        createdAt: user.created_at
      }
    })
  } catch (error) {
    console.error('Registration error:', error)
    res.status(500).json({ message: 'Registration failed' })
  }
}

const login = async (req, res) => {
  try {
    const { email, password } = req.body

    // Validate input
    if (!email || !password) {
      return res.status(400).json({ message: 'Email and password are required' })
    }

    // Find user
    const result = await pool.query(
      'SELECT * FROM users WHERE email = $1',
      [email]
    )

    if (result.rows.length === 0) {
      return res.status(401).json({ message: 'Invalid credentials' })
    }

    const user = result.rows[0]

    // Check password
    const isPasswordValid = await bcrypt.compare(password, user.password)

    if (!isPasswordValid) {
      return res.status(401).json({ message: 'Invalid credentials' })
    }

    // Generate token
    const token = generateToken(user.id, user.email)

    res.json({
      token,
      user: {
        id: user.id,
        name: user.name,
        email: user.email,
        avatar: user.avatar
      }
    })
  } catch (error) {
    console.error('Login error:', error)
    res.status(500).json({ message: 'Login failed' })
  }
}

const getCurrentUser = async (req, res) => {
  try {
    const result = await pool.query(
      'SELECT id, name, email, avatar, bio, role, is_guest_author, social_links, created_at FROM users WHERE id = $1',
      [req.user.userId]
    )

    if (result.rows.length === 0) {
      return res.status(404).json({ message: 'User not found' })
    }

    res.json(result.rows[0])
  } catch (error) {
    console.error('Get user error:', error)
    res.status(500).json({ message: 'Failed to get user' })
  }
}

const updateProfile = async (req, res) => {
  try {
    const { name, bio, avatar, social_links } = req.body
    const userId = req.user.userId

    // Build dynamic update query
    const updates = []
    const values = []
    let paramCount = 1

    if (name !== undefined) {
      updates.push(`name = $${paramCount}`)
      values.push(name)
      paramCount++
    }

    if (bio !== undefined) {
      updates.push(`bio = $${paramCount}`)
      values.push(bio)
      paramCount++
    }

    if (avatar !== undefined) {
      updates.push(`avatar = $${paramCount}`)
      values.push(avatar)
      paramCount++
    }

    if (social_links !== undefined) {
      updates.push(`social_links = $${paramCount}`)
      values.push(JSON.stringify(social_links))
      paramCount++
    }

    if (updates.length === 0) {
      return res.status(400).json({ message: 'No fields to update' })
    }

    updates.push(`updated_at = NOW()`)
    values.push(userId)

    const query = `
      UPDATE users
      SET ${updates.join(', ')}
      WHERE id = $${paramCount}
      RETURNING id, name, email, avatar, bio, role, is_guest_author, social_links, created_at, updated_at
    `

    const result = await pool.query(query, values)

    if (result.rows.length === 0) {
      return res.status(404).json({ message: 'User not found' })
    }

    res.json(result.rows[0])
  } catch (error) {
    console.error('Update profile error:', error)
    res.status(500).json({ message: 'Failed to update profile' })
  }
}

const getUserProfile = async (req, res) => {
  try {
    const userId = req.params.userId

    const result = await pool.query(
      'SELECT id, name, email, avatar, bio, is_guest_author, social_links, created_at FROM users WHERE id = $1',
      [userId]
    )

    if (result.rows.length === 0) {
      return res.status(404).json({ message: 'User not found' })
    }

    res.json(result.rows[0])
  } catch (error) {
    console.error('Get user profile error:', error)
    res.status(500).json({ message: 'Failed to get user profile' })
  }
}

const applyForGuestAuthor = async (req, res) => {
  try {
    const userId = req.user.userId

    const result = await pool.query(
      'UPDATE users SET is_guest_author = TRUE WHERE id = $1 RETURNING id, name, email, is_guest_author',
      [userId]
    )

    if (result.rows.length === 0) {
      return res.status(404).json({ message: 'User not found' })
    }

    res.json({
      message: 'Guest author status granted',
      user: result.rows[0]
    })
  } catch (error) {
    console.error('Apply for guest author error:', error)
    res.status(500).json({ message: 'Failed to apply for guest author status' })
  }
}

module.exports = {
  register,
  login,
  getCurrentUser,
  updateProfile,
  getUserProfile,
  applyForGuestAuthor
}
