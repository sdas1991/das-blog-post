const express = require('express')
const router = express.Router()
const authMiddleware = require('../middleware/auth')
const { getCommentsByPost, createComment, deleteComment } = require('../controllers/commentController')

router.get('/:postId', getCommentsByPost)
router.post('/', authMiddleware, createComment)
router.delete('/:commentId', authMiddleware, deleteComment)

module.exports = router
