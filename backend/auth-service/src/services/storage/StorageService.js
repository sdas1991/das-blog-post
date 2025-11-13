/**
 * Base Storage Service Interface
 * Provides abstract methods for file storage operations
 */
class StorageService {
  /**
   * Upload a file to storage
   * @param {Buffer} fileBuffer - File content as buffer
   * @param {string} filename - Original filename
   * @param {string} mimetype - File MIME type
   * @returns {Promise<{url: string, filename: string, size: number}>}
   */
  async uploadFile(fileBuffer, filename, mimetype) {
    throw new Error('uploadFile method must be implemented')
  }

  /**
   * Download a file from storage
   * @param {string} fileId - File identifier
   * @returns {Promise<{buffer: Buffer, filename: string, mimetype: string}>}
   */
  async downloadFile(fileId) {
    throw new Error('downloadFile method must be implemented')
  }

  /**
   * Delete a file from storage
   * @param {string} fileId - File identifier
   * @returns {Promise<boolean>}
   */
  async deleteFile(fileId) {
    throw new Error('deleteFile method must be implemented')
  }

  /**
   * Get file URL
   * @param {string} fileId - File identifier
   * @returns {string}
   */
  getFileUrl(fileId) {
    throw new Error('getFileUrl method must be implemented')
  }
}

module.exports = StorageService
