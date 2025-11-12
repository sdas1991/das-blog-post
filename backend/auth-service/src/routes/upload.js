const express = require('express')
const router = express.Router()
const multer = require('multer')
const path = require('path')
const authMiddleware = require('../middleware/auth')

// Configure multer for file upload
const storage = multer.diskStorage({
  destination: (req, file, cb) => {
    cb(null, 'uploads/')
  },
  filename: (req, file, cb) => {
    const uniqueSuffix = Date.now() + '-' + Math.round(Math.random() * 1E9)
    cb(null, uniqueSuffix + path.extname(file.originalname))
  }
})

const fileFilter = (req, file, cb) => {
  const allowedTypes = /jpeg|jpg|png|gif|pdf|doc|docx/
  const extname = allowedTypes.test(path.extname(file.originalname).toLowerCase())
  const mimetype = allowedTypes.test(file.mimetype)

  if (extname && mimetype) {
    cb(null, true)
  } else {
    cb(new Error('Invalid file type'))
  }
}

const upload = multer({
  storage,
  fileFilter,
  limits: {
    fileSize: parseInt(process.env.MAX_FILE_SIZE) || 5 * 1024 * 1024 // 5MB default
  }
})

router.post('/', authMiddleware, upload.single('file'), (req, res) => {
  try {
    if (!req.file) {
      return res.status(400).json({ message: 'No file uploaded' })
    }

    const fileUrl = `/uploads/${req.file.filename}`

    res.json({
      message: 'File uploaded successfully',
      url: fileUrl,
      filename: req.file.filename,
      size: req.file.size
    })
  } catch (error) {
    console.error('Upload error:', error)
    res.status(500).json({ message: 'File upload failed' })
  }
})

module.exports = router
