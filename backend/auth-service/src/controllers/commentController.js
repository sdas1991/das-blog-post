const { pool } = require('../config/database')

const getCommentsByPost = async (req, res) => {
  try {
    const { postId } = req.params

    const result = await pool.query(
      `SELECT c.id, c.post_id, c.content, c.created_at, c.updated_at,
              u.id as author_id, u.name as author_name, u.email as author_email, u.avatar as author_avatar
       FROM comments c
       JOIN users u ON c.user_id = u.id
       WHERE c.post_id = $1
       ORDER BY c.created_at DESC`,
      [postId]
    )

    const comments = result.rows.map(row => ({
      id: row.id,
      postId: row.post_id,
      content: row.content,
      author: {
        id: row.author_id,
        name: row.author_name,
        email: row.author_email,
        avatar: row.author_avatar
      },
      createdAt: row.created_at,
      updatedAt: row.updated_at
    }))

    res.json(comments)
  } catch (error) {
    console.error('Get comments error:', error)
    res.status(500).json({ message: 'Failed to get comments' })
  }
}

const createComment = async (req, res) => {
  try {
    const { postId, content } = req.body
    const userId = req.user.userId

    if (!postId || !content) {
      return res.status(400).json({ message: 'Post ID and content are required' })
    }

    const result = await pool.query(
      `INSERT INTO comments (post_id, user_id, content)
       VALUES ($1, $2, $3)
       RETURNING id, post_id, content, created_at, updated_at`,
      [postId, userId, content]
    )

    const comment = result.rows[0]

    // Get user info
    const userResult = await pool.query(
      'SELECT id, name, email, avatar FROM users WHERE id = $1',
      [userId]
    )

    const user = userResult.rows[0]

    res.status(201).json({
      id: comment.id,
      postId: comment.post_id,
      content: comment.content,
      author: {
        id: user.id,
        name: user.name,
        email: user.email,
        avatar: user.avatar
      },
      createdAt: comment.created_at,
      updatedAt: comment.updated_at
    })
  } catch (error) {
    console.error('Create comment error:', error)
    res.status(500).json({ message: 'Failed to create comment' })
  }
}

const deleteComment = async (req, res) => {
  try {
    const { commentId } = req.params
    const userId = req.user.userId

    // Check if comment exists and belongs to user
    const commentResult = await pool.query(
      'SELECT * FROM comments WHERE id = $1',
      [commentId]
    )

    if (commentResult.rows.length === 0) {
      return res.status(404).json({ message: 'Comment not found' })
    }

    if (commentResult.rows[0].user_id !== userId) {
      return res.status(403).json({ message: 'Unauthorized' })
    }

    await pool.query('DELETE FROM comments WHERE id = $1', [commentId])

    res.json({ message: 'Comment deleted successfully' })
  } catch (error) {
    console.error('Delete comment error:', error)
    res.status(500).json({ message: 'Failed to delete comment' })
  }
}

module.exports = { getCommentsByPost, createComment, deleteComment }
