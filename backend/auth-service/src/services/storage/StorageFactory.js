const GridFSStorage = require('./GridFSStorage')
const S3Storage = require('./S3Storage')

/**
 * Storage Factory
 * Returns the appropriate storage implementation based on APP_MODE
 */
class StorageFactory {
  /**
   * Create and return the appropriate storage service
   * @returns {StorageService}
   */
  static createStorage() {
    const appMode = process.env.APP_MODE || 'dev'

    console.log(`\n📦 Initializing Storage Service...`)
    console.log(`   Mode: ${appMode.toUpperCase()}`)

    if (appMode === 'release') {
      // Production mode - use AWS S3
      console.log(`   Provider: AWS S3`)
      return new S3Storage()
    } else {
      // Development mode - use MongoDB GridFS
      console.log(`   Provider: MongoDB GridFS (Local)`)
      return new GridFSStorage()
    }
  }
}

module.exports = StorageFactory
