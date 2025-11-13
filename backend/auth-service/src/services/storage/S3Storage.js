const AWS = require('aws-sdk')
const StorageService = require('./StorageService')
const path = require('path')

/**
 * AWS S3 Storage Implementation for Release Mode
 * Stores files in AWS S3
 */
class S3Storage extends StorageService {
  constructor() {
    super()

    // Configure AWS SDK
    AWS.config.update({
      region: process.env.AWS_REGION || 'us-east-1',
      accessKeyId: process.env.AWS_ACCESS_KEY_ID,
      secretAccessKey: process.env.AWS_SECRET_ACCESS_KEY
    })

    this.s3 = new AWS.S3()
    this.bucket = process.env.AWS_S3_BUCKET

    if (!this.bucket) {
      throw new Error('AWS_S3_BUCKET environment variable is required in release mode')
    }

    console.log(`✓ Initialized AWS S3 Storage (Bucket: ${this.bucket}, Region: ${process.env.AWS_REGION})`)
  }

  /**
   * Upload a file to S3
   * @param {Buffer} fileBuffer - File content as buffer
   * @param {string} filename - Original filename
   * @param {string} mimetype - File MIME type
   * @returns {Promise<{url: string, filename: string, size: number}>}
   */
  async uploadFile(fileBuffer, filename, mimetype) {
    try {
      // Generate unique filename
      const uniqueSuffix = Date.now() + '-' + Math.round(Math.random() * 1E9)
      const storedFilename = uniqueSuffix + path.extname(filename)
      const key = `uploads/${storedFilename}`

      const params = {
        Bucket: this.bucket,
        Key: key,
        Body: fileBuffer,
        ContentType: mimetype,
        Metadata: {
          originalName: filename,
          uploadDate: new Date().toISOString()
        }
      }

      const result = await this.s3.upload(params).promise()

      console.log(`File uploaded to S3: ${key}`)

      return {
        url: result.Location,
        filename: storedFilename,
        fileId: key,
        size: fileBuffer.length
      }
    } catch (error) {
      console.error('S3 upload error:', error)
      throw new Error(`Failed to upload file to S3: ${error.message}`)
    }
  }

  /**
   * Download a file from S3
   * @param {string} fileId - File identifier (S3 key)
   * @returns {Promise<{buffer: Buffer, filename: string, mimetype: string}>}
   */
  async downloadFile(fileId) {
    try {
      const params = {
        Bucket: this.bucket,
        Key: fileId
      }

      const result = await this.s3.getObject(params).promise()

      return {
        buffer: result.Body,
        filename: path.basename(fileId),
        mimetype: result.ContentType || 'application/octet-stream'
      }
    } catch (error) {
      console.error('S3 download error:', error)
      throw new Error(`Failed to download file from S3: ${error.message}`)
    }
  }

  /**
   * Delete a file from S3
   * @param {string} fileId - File identifier (S3 key)
   * @returns {Promise<boolean>}
   */
  async deleteFile(fileId) {
    try {
      const params = {
        Bucket: this.bucket,
        Key: fileId
      }

      await this.s3.deleteObject(params).promise()
      console.log(`File deleted from S3: ${fileId}`)
      return true
    } catch (error) {
      console.error('S3 delete error:', error)
      return false
    }
  }

  /**
   * Get file URL (public S3 URL)
   * @param {string} fileId - File identifier (S3 key)
   * @returns {string}
   */
  getFileUrl(fileId) {
    return `https://${this.bucket}.s3.${process.env.AWS_REGION || 'us-east-1'}.amazonaws.com/${fileId}`
  }

  /**
   * Generate a pre-signed URL for temporary access
   * @param {string} fileId - File identifier (S3 key)
   * @param {number} expiresIn - Expiration time in seconds (default: 1 hour)
   * @returns {string}
   */
  getSignedUrl(fileId, expiresIn = 3600) {
    const params = {
      Bucket: this.bucket,
      Key: fileId,
      Expires: expiresIn
    }

    return this.s3.getSignedUrl('getObject', params)
  }
}

module.exports = S3Storage
