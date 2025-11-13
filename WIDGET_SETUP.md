# Blog Widget System Setup Guide

This guide explains how to set up and use the blog widget system with S3 image storage.

## Overview

The widget system allows you to create visually appealing cards on the home page, each featuring:
- A custom image (stored in S3)
- A title
- A short description
- Optional link to a blog post or custom URL
- Configurable appearance (layout, colors)

## Prerequisites

1. **AWS Account** with S3 access
2. **S3 Bucket** created for storing widget images
3. **IAM User** with appropriate S3 permissions

## AWS S3 Setup

### 1. Create an S3 Bucket

```bash
# Using AWS CLI
aws s3 mb s3://das-blog-widgets --region us-east-1
```

Or create via AWS Console:
- Go to S3 service
- Click "Create bucket"
- Name: `das-blog-widgets` (or your preferred name)
- Region: `us-east-1` (or your preferred region)
- **Important**: Configure public access settings based on your needs
  - For public widgets: Allow public read access
  - For private widgets with signed URLs: Keep private

### 2. Configure CORS (Optional)

If accessing images from a web browser, configure CORS:

```json
[
    {
        "AllowedHeaders": ["*"],
        "AllowedMethods": ["GET", "PUT", "POST", "DELETE"],
        "AllowedOrigins": ["*"],
        "ExposeHeaders": []
    }
]
```

### 3. Create IAM User and Policy

Create an IAM user with programmatic access and attach this policy:

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
                "arn:aws:s3:::das-blog-widgets",
                "arn:aws:s3:::das-blog-widgets/*"
            ]
        }
    ]
}
```

### 4. Get Access Keys

After creating the IAM user:
1. Go to IAM > Users > [Your User]
2. Security credentials tab
3. Create access key
4. Save the Access Key ID and Secret Access Key

## Environment Configuration

### Backend Configuration

1. Copy the example environment file:
```bash
cd backend/blog-service
cp .env.example .env
```

2. Update the `.env` file with your AWS credentials:
```env
AWS_S3_BUCKET_NAME=das-blog-widgets
AWS_S3_REGION=us-east-1
AWS_ACCESS_KEY_ID=your_access_key_here
AWS_SECRET_ACCESS_KEY=your_secret_key_here
```

### Docker Configuration

For Docker deployments, update the root `.env` file:
```bash
cp .env.example .env
```

Add your AWS credentials:
```env
AWS_S3_BUCKET_NAME=das-blog-widgets
AWS_S3_REGION=us-east-1
AWS_ACCESS_KEY_ID=your_access_key_here
AWS_SECRET_ACCESS_KEY=your_secret_key_here
```

## Running the Application

### Local Development

1. **Start the blog service:**
```bash
cd backend/blog-service
./mvnw spring-boot:run
```

2. **Start the frontend:**
```bash
cd frontend
npm run dev
```

### Docker Deployment

```bash
docker-compose up -d
```

## Using the Widget Manager

### Accessing the Admin Panel

1. Log in to your admin account at `/login`
2. Navigate to Admin Dashboard at `/admin`
3. Click "Manage Widgets"

### Creating a Widget

1. Click "Create Widget" button
2. Fill in the required fields:
   - **Title**: Widget heading (e.g., "Android Development")
   - **Short Description**: Brief description (max 200 characters)
   - **Widget Image**: Upload an image (max 5MB, JPG/PNG/GIF)
   - **Link to Blog Post**: Optional - select a blog post to link to
   - **Display Order**: Lower numbers appear first
   - **Active**: Check to display on home page

3. **Advanced Options** (optional):
   - **Layout Type**: card, banner, or featured
   - **Background Color**: Custom background color
   - **Text Color**: Custom text color
   - **Custom Link URL**: Override blog post link
   - **API Endpoint**: For dynamic content loading

4. Click "Create Widget"

### Editing a Widget

1. Find the widget in the list
2. Click "Edit" button
3. Make your changes
4. Click "Update Widget"

Note: If you change the image, the old image will be automatically deleted from S3.

### Deleting a Widget

1. Click "Delete" button on the widget
2. Confirm deletion
3. Both the widget and its S3 image will be deleted

## Widget Plugin System

Each widget supports a `WidgetConfig` object that allows customization:

```json
{
  "layout": "card",
  "backgroundColor": "#ffffff",
  "textColor": "#333333",
  "apiEndpoint": "/api/custom-data",
  "linkUrl": "https://example.com"
}
```

### Layout Types

- **card** (default): Standard card layout
- **banner**: Wide banner style
- **featured**: Highlighted/featured style

## API Endpoints

### REST Endpoints

#### Upload Widget Image
```
POST /api/blog/upload/widget-image
Content-Type: multipart/form-data

Body: file=<image file>

Response:
{
  "url": "https://bucket.s3.region.amazonaws.com/widgets/uuid-filename.jpg",
  "message": "Image uploaded successfully"
}
```

#### Delete Widget Image
```
DELETE /api/blog/upload/widget-image?url=<image-url>

Response:
{
  "message": "Image deleted successfully"
}
```

### GraphQL Endpoints

All widget operations are available through GraphQL at `/api/blog/graphql`.

#### Queries

```graphql
# Get all widgets
query {
  widgets {
    id
    title
    shortDescription
    imageUrl
    active
    displayOrder
  }
}

# Get active widgets (for home page)
query {
  activeWidgets {
    id
    title
    shortDescription
    imageUrl
    displayOrder
    config {
      layout
      backgroundColor
      textColor
      linkUrl
    }
  }
}

# Get single widget
query {
  widget(id: "widget-id") {
    id
    title
    shortDescription
    imageUrl
  }
}
```

#### Mutations

```graphql
# Create widget
mutation {
  createWidget(input: {
    title: "Android Development"
    shortDescription: "Learn Android app development with Kotlin"
    imageUrl: "https://bucket.s3.region.amazonaws.com/widgets/android.jpg"
    displayOrder: 0
    active: true
    config: {
      layout: "card"
      backgroundColor: "#ffffff"
      textColor: "#333333"
    }
  }) {
    id
    title
  }
}

# Update widget
mutation {
  updateWidget(id: "widget-id", input: {
    title: "Updated Title"
    active: false
  }) {
    id
    title
    active
  }
}

# Delete widget
mutation {
  deleteWidget(id: "widget-id")
}

# Reorder widgets
mutation {
  reorderWidgets(widgetIds: ["id1", "id2", "id3"]) {
    id
    displayOrder
  }
}
```

## Troubleshooting

### Images Not Uploading

1. **Check AWS credentials**: Ensure `AWS_ACCESS_KEY_ID` and `AWS_SECRET_ACCESS_KEY` are correct
2. **Check bucket permissions**: Verify IAM user has `s3:PutObject` permission
3. **Check bucket name**: Ensure `AWS_S3_BUCKET_NAME` matches your actual bucket name
4. **Check region**: Verify `AWS_S3_REGION` is correct

### Images Not Displaying

1. **Check S3 bucket policy**: Ensure bucket allows public read access (if needed)
2. **Check CORS configuration**: Verify CORS is configured for web access
3. **Check image URLs**: Ensure URLs are formatted correctly

### Connection Errors

1. **Check internet connectivity**: S3 requires internet access
2. **Check firewall rules**: Ensure outbound HTTPS traffic is allowed
3. **Check AWS service status**: Verify S3 service is operational in your region

## Security Best Practices

1. **Never commit AWS credentials** to version control
2. **Use IAM roles** for EC2/ECS deployments instead of access keys
3. **Rotate access keys** regularly
4. **Use least-privilege permissions** - only grant necessary S3 permissions
5. **Enable S3 bucket versioning** for backup/recovery
6. **Enable S3 server-side encryption** for data at rest
7. **Use CloudFront** for serving images with better performance and security

## Cost Optimization

1. **Use appropriate storage class**: Standard for frequently accessed images
2. **Enable lifecycle policies**: Archive or delete old unused images
3. **Use CloudFront CDN**: Reduce S3 data transfer costs
4. **Compress images**: Optimize images before upload to reduce storage costs
5. **Monitor usage**: Set up AWS Budgets alerts

## Next Steps

- Configure CloudFront for faster image delivery
- Implement image optimization/resizing on upload
- Add analytics to track widget performance
- Create widget templates for common use cases
- Implement A/B testing for widgets
