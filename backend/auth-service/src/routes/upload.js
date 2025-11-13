const express = require('express')
const router = express.Router()
const multer = require('multer')
const path = require('path')
const authMiddleware = require('../middleware/auth')
const StorageFactory = require('../services/storage/StorageFactory')

// Initialize storage service (GridFS for dev, S3 for release)
const storageService = StorageFactory.createStorage()

// Configure multer for memory storage (files handled by storage service)
const storage = multer.memoryStorage()

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

// Upload file endpoint
router.post('/', authMiddleware, upload.single('file'), async (req, res) => {
  try {
    if (!req.file) {
      return res.status(400).json({ message: 'No file uploaded' })
    }

    // Upload file using storage service
    const result = await storageService.uploadFile(
      req.file.buffer,
      req.file.originalname,
      req.file.mimetype
    )

    res.json({
      message: 'File uploaded successfully',
      url: result.url,
      filename: result.filename,
      fileId: result.fileId,
      size: result.size
    })
  } catch (error) {
    console.error('Upload error:', error)
    res.status(500).json({ message: 'File upload failed', error: error.message })
  }
})

// Download file endpoint (primarily for dev mode with GridFS)
router.get('/files/:fileId', async (req, res) => {
  try {
    const { fileId } = req.params

    const file = await storageService.downloadFile(fileId)

    res.setHeader('Content-Type', file.mimetype)
    res.setHeader('Content-Disposition', `inline; filename="${file.filename}"`)
    res.send(file.buffer)
  } catch (error) {
    console.error('Download error:', error)
    res.status(404).json({ message: 'File not found', error: error.message })
  }
})

// Delete file endpoint
router.delete('/files/:fileId', authMiddleware, async (req, res) => {
  try {
    const { fileId } = req.params

    const success = await storageService.deleteFile(fileId)

    if (success) {
      res.json({ message: 'File deleted successfully' })
    } else {
      res.status(404).json({ message: 'File not found or already deleted' })
    }
  } catch (error) {
    console.error('Delete error:', error)
    res.status(500).json({ message: 'File deletion failed', error: error.message })
  }
})

module.exports = router
