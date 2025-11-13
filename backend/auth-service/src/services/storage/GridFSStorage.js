const { MongoClient, GridFSBucket, ObjectId } = require('mongodb')
const StorageService = require('./StorageService')
const path = require('path')

/**
 * MongoDB GridFS Storage Implementation for Dev Mode
 * Stores files in MongoDB GridFS instead of AWS S3
 */
class GridFSStorage extends StorageService {
  constructor() {
    super()
    this.client = null
    this.db = null
    this.bucket = null
    this.connected = false
  }

  /**
   * Connect to MongoDB and initialize GridFS bucket
   */
  async connect() {
    if (this.connected) return

    const mongoHost = process.env.MONGODB_HOST || 'mongodb'
    const mongoPort = process.env.MONGODB_PORT || 27017
    const mongoDatabase = process.env.MONGODB_DATABASE || 'auth_storage_db'

    const uri = `mongodb://${mongoHost}:${mongoPort}`

    try {
      this.client = new MongoClient(uri)
      await this.client.connect()
      this.db = this.client.db(mongoDatabase)
      this.bucket = new GridFSBucket(this.db, {
        bucketName: 'uploads'
      })
      this.connected = true
      console.log('✓ Connected to MongoDB GridFS for file storage (Dev Mode)')
    } catch (error) {
      console.error('Failed to connect to MongoDB GridFS:', error)
      throw error
    }
  }

  /**
   * Ensure connection before operations
   */
  async ensureConnection() {
    if (!this.connected) {
      await this.connect()
    }
  }

  /**
   * Upload a file to GridFS
   * @param {Buffer} fileBuffer - File content as buffer
   * @param {string} filename - Original filename
   * @param {string} mimetype - File MIME type
   * @returns {Promise<{url: string, filename: string, size: number}>}
   */
  async uploadFile(fileBuffer, filename, mimetype) {
    await this.ensureConnection()

    return new Promise((resolve, reject) => {
      // Generate unique filename
      const uniqueSuffix = Date.now() + '-' + Math.round(Math.random() * 1E9)
      const storedFilename = uniqueSuffix + path.extname(filename)

      const uploadStream = this.bucket.openUploadStream(storedFilename, {
        metadata: {
          originalName: filename,
          contentType: mimetype,
          uploadDate: new Date()
        }
      })

      uploadStream.on('finish', (file) => {
        resolve({
          url: `/api/auth/files/${file._id}`,
          filename: storedFilename,
          fileId: file._id.toString(),
          size: fileBuffer.length
        })
      })

      uploadStream.on('error', (error) => {
        console.error('GridFS upload error:', error)
        reject(error)
      })

      uploadStream.end(fileBuffer)
    })
  }

  /**
   * Download a file from GridFS
   * @param {string} fileId - File identifier (GridFS ObjectId)
   * @returns {Promise<{buffer: Buffer, filename: string, mimetype: string}>}
   */
  async downloadFile(fileId) {
    await this.ensureConnection()

    try {
      const _id = new ObjectId(fileId)

      // Get file metadata
      const files = await this.db.collection('uploads.files').findOne({ _id })

      if (!files) {
        throw new Error('File not found')
      }

      // Download file
      const chunks = []
      const downloadStream = this.bucket.openDownloadStream(_id)

      return new Promise((resolve, reject) => {
        downloadStream.on('data', (chunk) => {
          chunks.push(chunk)
        })

        downloadStream.on('end', () => {
          resolve({
            buffer: Buffer.concat(chunks),
            filename: files.filename,
            mimetype: files.metadata?.contentType || 'application/octet-stream'
          })
        })

        downloadStream.on('error', (error) => {
          console.error('GridFS download error:', error)
          reject(error)
        })
      })
    } catch (error) {
      console.error('Failed to download file:', error)
      throw error
    }
  }

  /**
   * Delete a file from GridFS
   * @param {string} fileId - File identifier (GridFS ObjectId)
   * @returns {Promise<boolean>}
   */
  async deleteFile(fileId) {
    await this.ensureConnection()

    try {
      const _id = new ObjectId(fileId)
      await this.bucket.delete(_id)
      console.log(`File deleted from GridFS: ${fileId}`)
      return true
    } catch (error) {
      console.error('Failed to delete file:', error)
      return false
    }
  }

  /**
   * Get file URL
   * @param {string} fileId - File identifier
   * @returns {string}
   */
  getFileUrl(fileId) {
    return `/api/auth/files/${fileId}`
  }

  /**
   * Close MongoDB connection
   */
  async disconnect() {
    if (this.client) {
      await this.client.close()
      this.connected = false
      console.log('Disconnected from MongoDB GridFS')
    }
  }
}

module.exports = GridFSStorage
