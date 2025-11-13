const express = require('express')
const router = express.Router()
const authMiddleware = require('../middleware/auth')
const {
  register,
  login,
  getCurrentUser,
  updateProfile,
  getUserProfile,
  applyForGuestAuthor
} = require('../controllers/authController')

router.post('/register', register)
router.post('/login', login)
router.get('/me', authMiddleware, getCurrentUser)
router.put('/profile', authMiddleware, updateProfile)
router.get('/profile/:userId', getUserProfile)
router.post('/apply-guest-author', authMiddleware, applyForGuestAuthor)

module.exports = router
