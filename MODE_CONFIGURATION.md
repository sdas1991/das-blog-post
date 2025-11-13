# Application Mode Configuration Guide

This document provides detailed information about the Dev and Release modes in the DAS Blog & Portfolio Platform.

## Overview

The application supports two operational modes:
- **Dev Mode**: For local development with no AWS dependencies
- **Release Mode**: For production deployment with AWS services

## Mode Switching

The application mode is controlled by a single environment variable:

```env
APP_MODE=dev   # or 'release'
```

## Dev Mode (Local Development)

### Purpose
- Local development and testing
- No external cloud dependencies
- Zero AWS costs
- Fully contained in Docker

### File Storage: MongoDB GridFS

In dev mode, all file uploads (profile pictures, assets, documents) are stored in **MongoDB GridFS** instead of AWS S3.

#### How It Works

1. **Upload Process**:
   ```
   User uploads file → Multer receives (memory) → StorageFactory → GridFSStorage
   → File stored in MongoDB GridFS → Returns GridFS file ID
   ```

2. **Storage Location**:
   - Database: `auth_storage_db`
   - Collections: `uploads.files` and `uploads.chunks`
   - Container: `mongodb` (Docker service)

3. **File Access**:
   - URL Pattern: `/api/auth/files/:fileId`
   - File ID: MongoDB ObjectId (e.g., `507f1f77bcf86cd799439011`)
   - Download: Stream from GridFS to client

4. **Benefits**:
   - No AWS account needed
   - Files automatically cleaned up when containers are removed
   - Fast local access
   - No internet connection required
   - Perfect for development and testing

#### Configuration

```env
# In .env or docker-compose.yml
APP_MODE=dev
MONGODB_HOST=mongodb
MONGODB_PORT=27017
MONGODB_DATABASE=auth_storage_db
```

#### Example URLs

- Upload: `POST /api/auth/upload`
- Download: `GET /api/auth/files/507f1f77bcf86cd799439011`
- Delete: `DELETE /api/auth/files/507f1f77bcf86cd799439011`

### Running in Dev Mode

```bash
# Ensure APP_MODE is set to 'dev' in .env
echo "APP_MODE=dev" >> .env

# Start all services
docker-compose up --build

# All services will use local databases and GridFS for file storage
```

### Database Services in Dev Mode

| Service | Database | Port | Purpose |
|---------|----------|------|---------|
| Auth Service | PostgreSQL | 5432 | User accounts, comments |
| Auth Service | MongoDB | 27017 | File storage (GridFS) |
| Blog Service | MongoDB | 27017 | Blog posts, content |
| Portfolio Service | MySQL | 3306 | Projects, skills |

---

## Release Mode (Production)

### Purpose
- Production deployment
- Scalable cloud infrastructure
- AWS service integration
- High availability

### File Storage: AWS S3

In release mode, all file uploads are stored in **AWS S3** buckets.

#### How It Works

1. **Upload Process**:
   ```
   User uploads file → Multer receives (memory) → StorageFactory → S3Storage
   → File uploaded to S3 → Returns S3 URL
   ```

2. **Storage Location**:
   - Service: AWS S3
   - Bucket: Configurable (e.g., `das-blog-uploads`)
   - Region: Configurable (default: `us-east-1`)
   - Access: Public or pre-signed URLs

3. **File Access**:
   - URL Pattern: `https://{bucket}.s3.{region}.amazonaws.com/{key}`
   - File Key: `uploads/{timestamp}-{random}-{filename}.ext`
   - Access: Direct S3 URL or pre-signed temporary URL

4. **Benefits**:
   - Scalable storage (unlimited capacity)
   - High availability (99.99% SLA)
   - CDN integration (CloudFront)
   - Global distribution
   - Automatic backups and versioning

#### Configuration

```env
# In .env or docker-compose.yml
APP_MODE=release

# AWS S3 Configuration
AWS_REGION=us-east-1
AWS_S3_BUCKET=das-blog-uploads
AWS_ACCESS_KEY_ID=AKIAIOSFODNN7EXAMPLE
AWS_SECRET_ACCESS_KEY=wJalrXUtnFEMI/K7MDENG/bPxRfiCYEXAMPLEKEY
```

#### AWS Setup

1. **Create S3 Bucket**:
```bash
aws s3 mb s3://das-blog-uploads --region us-east-1
```

2. **Configure CORS** (if needed for browser uploads):
```json
[
  {
    "AllowedHeaders": ["*"],
    "AllowedMethods": ["GET", "PUT", "POST", "DELETE"],
    "AllowedOrigins": ["https://yourdomain.com"],
    "ExposeHeaders": ["ETag"]
  }
]
```

Save as `cors.json` and apply:
```bash
aws s3api put-bucket-cors --bucket das-blog-uploads --cors-configuration file://cors.json
```

3. **Create IAM User** with S3 permissions:
```json
{
  "Version": "2012-10-17",
  "Statement": [
    {
      "Effect": "Allow",
      "Action": [
        "s3:PutObject",
        "s3:GetObject",
        "s3:DeleteObject",
        "s3:ListBucket"
      ],
      "Resource": [
        "arn:aws:s3:::das-blog-uploads",
        "arn:aws:s3:::das-blog-uploads/*"
      ]
    }
  ]
}
```

4. **Configure Bucket Policy** (optional for public read):
```json
{
  "Version": "2012-10-17",
  "Statement": [
    {
      "Sid": "PublicReadGetObject",
      "Effect": "Allow",
      "Principal": "*",
      "Action": "s3:GetObject",
      "Resource": "arn:aws:s3:::das-blog-uploads/*"
    }
  ]
}
```

### Running in Release Mode

```bash
# Configure AWS credentials in .env
cat > .env << EOF
APP_MODE=release
AWS_REGION=us-east-1
AWS_S3_BUCKET=das-blog-uploads
AWS_ACCESS_KEY_ID=your-access-key
AWS_SECRET_ACCESS_KEY=your-secret-key
EOF

# Start services
docker-compose up --build

# Auth service will now use S3 for file storage
```

### Database Options in Release Mode

| Service | Option 1 (AWS) | Option 2 (Self-hosted) |
|---------|---------------|------------------------|
| Auth Service | AWS RDS PostgreSQL | EC2 PostgreSQL |
| Blog Service | MongoDB Atlas | EC2 MongoDB |
| Portfolio Service | AWS RDS MySQL | EC2 MySQL |

---

## Storage Service Architecture

### Class Diagram

```
┌──────────────────────┐
│   StorageService     │  (Abstract Base Class)
│  ────────────────    │
│  + uploadFile()      │
│  + downloadFile()    │
│  + deleteFile()      │
│  + getFileUrl()      │
└──────────┬───────────┘
           │
      ┌────┴────┐
      │         │
┌─────▼─────┐ ┌▼──────────┐
│  GridFS   │ │ S3Storage │
│  Storage  │ │           │
└───────────┘ └───────────┘

┌────────────────────┐
│  StorageFactory    │
│  ────────────────  │
│  + createStorage() │ ──→ Returns appropriate storage
└────────────────────┘     based on APP_MODE
```

### Implementation Files

```
backend/auth-service/src/services/storage/
├── StorageService.js      # Abstract base class
├── GridFSStorage.js       # MongoDB GridFS implementation
├── S3Storage.js           # AWS S3 implementation
└── StorageFactory.js      # Factory to create appropriate storage
```

### Code Example

```javascript
// StorageFactory automatically selects the right storage
const StorageFactory = require('./services/storage/StorageFactory')
const storage = StorageFactory.createStorage()

// Upload file (works with both GridFS and S3)
const result = await storage.uploadFile(buffer, filename, mimetype)
console.log(result.url) // GridFS: /api/auth/files/123 or S3: https://...

// Download file
const file = await storage.downloadFile(fileId)

// Delete file
await storage.deleteFile(fileId)
```

---

## Migration Between Modes

### From Dev to Release

When moving from development to production:

1. **Configure AWS credentials** in `.env`
2. **Create S3 bucket** and configure IAM permissions
3. **Change APP_MODE** to `release`
4. **Redeploy** the application
5. **Migrate existing files** (if needed):

```javascript
// Example migration script (not included in codebase)
const gridfs = new GridFSStorage()
const s3 = new S3Storage()

const files = await gridfs.getAllFiles()
for (const file of files) {
  const data = await gridfs.downloadFile(file.id)
  await s3.uploadFile(data.buffer, file.filename, file.mimetype)
}
```

### From Release to Dev

1. Change `APP_MODE` to `dev`
2. Restart services
3. Files in S3 remain untouched but won't be accessed

---

## Environment Variables Reference

### Required for Both Modes

```env
PORT=3001
NODE_ENV=development
DB_HOST=postgres
DB_PORT=5432
DB_NAME=auth_db
DB_USER=postgres
DB_PASSWORD=postgres
JWT_SECRET=your-jwt-secret
JWT_EXPIRES_IN=7d
MAX_FILE_SIZE=5242880
```

### Dev Mode Specific

```env
APP_MODE=dev
MONGODB_HOST=mongodb
MONGODB_PORT=27017
MONGODB_DATABASE=auth_storage_db
```

### Release Mode Specific

```env
APP_MODE=release
AWS_REGION=us-east-1
AWS_S3_BUCKET=your-bucket-name
AWS_ACCESS_KEY_ID=your-access-key-id
AWS_SECRET_ACCESS_KEY=your-secret-access-key
```

---

## Troubleshooting

### Dev Mode Issues

**Problem**: Files not uploading
- Check MongoDB container is running: `docker ps | grep mongodb`
- Verify MongoDB connection: `docker logs auth-service`
- Check GridFS collections: `docker exec -it mongodb mongo auth_storage_db`

**Problem**: Files not downloading
- Verify fileId is a valid MongoDB ObjectId
- Check GridFS files collection: `db.uploads.files.find()`

### Release Mode Issues

**Problem**: S3 upload fails with 403
- Verify AWS credentials are correct
- Check IAM permissions for PutObject
- Ensure bucket exists and is in the correct region

**Problem**: S3 upload fails with network error
- Check AWS_REGION is correct
- Verify internet connectivity
- Check bucket CORS configuration

**Problem**: Files uploaded but can't be accessed
- Check S3 bucket policy for public read
- Verify file ACL settings
- Use pre-signed URLs for private files

---

## Testing

### Test Dev Mode

```bash
# Start services
docker-compose up --build

# Upload a file
curl -X POST http://localhost:3001/api/auth/upload \
  -H "Authorization: Bearer YOUR_JWT_TOKEN" \
  -F "file=@test.jpg"

# Response should contain GridFS URL:
# {
#   "url": "/api/auth/files/507f1f77bcf86cd799439011",
#   "fileId": "507f1f77bcf86cd799439011"
# }

# Download the file
curl http://localhost:3001/api/auth/files/507f1f77bcf86cd799439011 \
  --output downloaded.jpg
```

### Test Release Mode

```bash
# Set APP_MODE to release
export APP_MODE=release

# Start services
docker-compose up --build

# Upload a file
curl -X POST http://localhost:3001/api/auth/upload \
  -H "Authorization: Bearer YOUR_JWT_TOKEN" \
  -F "file=@test.jpg"

# Response should contain S3 URL:
# {
#   "url": "https://das-blog-uploads.s3.us-east-1.amazonaws.com/uploads/1234567890-abc.jpg",
#   "fileId": "uploads/1234567890-abc.jpg"
# }
```

---

## Security Considerations

### Dev Mode
- Files stored in MongoDB are not publicly accessible
- Requires authentication for uploads
- Downloads are served by the auth service
- Files removed when containers are destroyed

### Release Mode
- Configure bucket policies carefully
- Use IAM roles instead of access keys when possible
- Enable S3 versioning for file history
- Consider using pre-signed URLs for sensitive content
- Enable S3 server-side encryption
- Set up S3 access logging

---

## Performance Considerations

### Dev Mode
- GridFS is suitable for small to medium files
- Performance depends on MongoDB container resources
- File chunks default to 255KB
- Good for development, not recommended for high-traffic production

### Release Mode
- S3 provides virtually unlimited scalability
- Low latency with CloudFront CDN
- Automatic replication across availability zones
- Recommended for production deployments

---

## Cost Analysis

### Dev Mode
- **Cost**: $0 (local Docker containers)
- **Storage**: Limited by local disk space
- **Bandwidth**: Free (localhost)

### Release Mode (AWS Free Tier)
- **S3 Storage**: First 5 GB free (first 12 months)
- **S3 Requests**: 20,000 GET + 2,000 PUT requests/month free
- **Data Transfer**: 15 GB/month free
- **CloudFront** (optional): 50 GB free (first 12 months)

**After Free Tier**:
- S3 Storage: ~$0.023/GB/month
- S3 PUT requests: $0.005 per 1,000 requests
- S3 GET requests: $0.0004 per 1,000 requests
- Data transfer: $0.09/GB

---

## Summary

| Feature | Dev Mode | Release Mode |
|---------|----------|--------------|
| Storage Backend | MongoDB GridFS | AWS S3 |
| Setup Complexity | Simple | Moderate |
| AWS Required | No | Yes |
| Cost | Free | AWS charges apply |
| Scalability | Limited | Unlimited |
| Performance | Good | Excellent |
| Use Case | Development | Production |
| File Access | `/api/auth/files/:id` | Direct S3 URLs |
| Cleanup | Automatic (containers) | Manual or lifecycle rules |

---

## Next Steps

1. **For Local Development**:
   - Set `APP_MODE=dev`
   - Run `docker-compose up --build`
   - Start developing!

2. **For Production Deployment**:
   - Create AWS account
   - Set up S3 bucket and IAM user
   - Configure `.env` with AWS credentials
   - Set `APP_MODE=release`
   - Deploy to AWS infrastructure

3. **Testing Both Modes**:
   - Test locally with dev mode first
   - Verify all features work correctly
   - Switch to release mode for production testing
   - Ensure seamless transition between modes
