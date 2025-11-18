# Admin Guide - Blog & Portfolio Platform

## Admin Login Credentials

**Default Admin Account:**
- **URL**: http://localhost:3000/login
- **Email**: `admin@das-blog.com`
- **Password**: `Admin@123456`

> **Important**: Change these credentials immediately after your first login in a production environment!

## Admin Dashboard Overview

Access the admin dashboard at: **http://localhost:3000/admin**

The dashboard provides centralized management for:
1. **Blog Posts** - Create, edit, publish/unpublish, and delete blog posts
2. **Portfolio Projects** - Manage your portfolio with GitHub integration
3. **Profile** - Update your professional profile and resume

---

## 1. Blog Post Management

### Creating a New Blog Post

1. Navigate to **Admin Dashboard** → Click **"Create New Post"**
2. Fill in the required fields:
   - **Title**: Post title
   - **Slug**: URL-friendly identifier (auto-generated from title)
   - **Excerpt**: Brief summary for preview cards
   - **Content**: Full blog post content (rich text editor with formatting)
   - **Category**: Select or enter category
   - **Tags**: Comma-separated tags
   - **Theme**: Choose visual theme (Default, Dark, Ocean, Sunset, Forest, Purple, Minimal, Warm)
   - **Published**: Check to publish immediately, uncheck to save as draft

3. Click **"Save Post"**

### Draft vs Published Posts

**Filtering Posts:**
- Use the filter tabs in the dashboard:
  - **All**: Shows all posts
  - **Published**: Shows only live posts visible to visitors
  - **Drafts**: Shows unpublished drafts

**Toggle Publish Status:**
- Click the **"Publish"** or **"Unpublish"** button next to any post in the table
- Drafts won't appear on the public blog until published

### Editing a Blog Post

1. From the Admin Dashboard, click **"Edit"** on any post
2. Make your changes
3. Click **"Save Post"**

### Deleting a Blog Post

1. Click **"Delete"** next to the post you want to remove
2. Confirm the deletion

---

## 2. Portfolio Management

Access Portfolio Manager: **http://localhost:3000/admin/portfolio**

### Adding a New Project

1. Click **"Add New Project"**
2. Fill in project details:

   **Required Fields:**
   - **Title**: Project name
   - **Summary**: One-line description (shown in cards)
   - **Description**: Detailed project description

   **Optional Fields:**
   - **Featured Project**: Check to highlight this project at the top
   - **Main Image URL**: Primary thumbnail image
   - **Live URL**: Link to deployed project
   - **GitHub Repository URL**: Link to GitHub repo
   - **Project Gallery Images**: Add multiple screenshots/images
   - **Technologies**: Comma-separated list (e.g., "React, Node.js, MongoDB, Docker")

3. Click **"Save Project"**

### GitHub Integration

1. Paste your GitHub repository URL in the **"GitHub Repository URL"** field
2. The project card will display a **GitHub** badge that links directly to the repository
3. Visitors can click the badge to view your source code

### Project Gallery Images

1. Click **"+ Add Image"** to add a new image URL field
2. Paste the URL of your project screenshot
3. Add as many images as needed
4. Click the **✕** button to remove an image
5. Gallery image count is displayed on the project card

### Featured Projects

- Check the **"Featured Project"** checkbox when creating/editing
- Featured projects display a ⭐ badge
- Use the sort dropdown to view **"Featured First"**

### Sorting Projects

Use the dropdown in Portfolio Manager to sort by:
- **Newest First**
- **Oldest First**
- **Title A-Z**
- **Featured First**

---

## 3. Profile Management

Access Profile Manager: **http://localhost:3000/admin/profile**

### Basic Information Tab

Update your professional details:
- **Full Name**: Your name
- **Title**: Professional title (e.g., "Senior Software Engineer")
- **Email**: Contact email
- **LinkedIn URL**: Your LinkedIn profile
- **GitHub URL**: Your GitHub profile
- **Bio/Summary**: Professional summary
- **Profile Image URL**: Your profile photo
- **Years of Experience**: Number of years

### Resume Upload Tab

Upload your resume for visitors to download:

1. Click **"Click to select file or drag & drop"**
2. Select your resume file (`.doc`, `.docx`, or `.pdf`)
3. Click **"Upload Resume"**
4. The uploaded resume becomes available for download on your public profile page

**Current Resume:**
- View details of the currently uploaded resume
- Download to verify
- Replace by uploading a new file

### Experience Tab

Manage your work history:

1. Click **"Add Experience"** to add a new position
2. Fill in:
   - **Company**: Company name
   - **Title**: Your job title
   - **Location**: Office location
   - **Start Date**: Format: `MM/YYYY` (e.g., "05/2019")
   - **End Date**: Format: `MM/YYYY` or "Present"
   - **Achievements**: One achievement per line (each becomes a bullet point)

3. Click **"Remove Experience"** to delete an entry
4. Click **"Save Experience"** when done

### Skills Tab

Organize your technical skills by category:

1. Click **"Add Skill Category"** to create a new category
2. Enter:
   - **Category Name**: e.g., "Frameworks", "Databases", "DevOps"
   - **Skills**: Comma-separated list (e.g., "Spring Boot, Ktor, RxJava")

3. Click **"Remove Category"** to delete
4. Click **"Save Skills"** when done

---

## 4. Public Profile Page

Your public profile is available at: **http://localhost:3000/profile**

### Features:
- Professional hero section with name, title, and bio
- Social links (LinkedIn, GitHub)
- Statistics cards (blog posts, projects, views, years of experience)
- Professional summary
- Technical skills organized by category
- Work experience timeline
- Education details
- Publications
- **Download Resume (PDF)** button - Downloads your uploaded resume

**Note**: Phone number and address are NOT displayed for privacy

---

## 5. Blog Post Themes

Choose from 8 visual themes when creating/editing blog posts:

1. **Default** - Clean white background
2. **Dark** - Black background with light blue accents
3. **Ocean** - Blue gradient theme
4. **Sunset** - Warm orange tones
5. **Forest** - Green nature theme
6. **Purple** - Elegant purple palette
7. **Minimal** - Simple grayscale
8. **Warm** - Cozy brown/orange tones

Themes are applied to the blog post content area, keeping the header separate.

---

## 6. Rich Text Editor Features

The blog post editor supports:

**Formatting:**
- **Bold**, *Italic*, ~~Strikethrough~~
- Inline `code`
- Headings (H1, H2, H3)

**Content Blocks:**
- Bullet lists
- Numbered lists
- Code blocks with syntax highlighting
- Block quotes
- Tables
- Images
- Links

**Actions:**
- Undo / Redo

---

## 7. Best Practices

### Blog Posts:
- ✅ Use clear, descriptive titles
- ✅ Write engaging excerpts (they appear in preview cards)
- ✅ Use tags for better organization
- ✅ Choose themes that match your content tone
- ✅ Save as draft first, publish when ready
- ✅ Use images to make posts more engaging

### Portfolio Projects:
- ✅ Add a concise summary (1 line)
- ✅ Include GitHub links for open-source projects
- ✅ Upload multiple screenshots to showcase features
- ✅ Mark your best work as "Featured"
- ✅ Keep technology lists relevant and up-to-date
- ✅ Add live demo links when available

### Profile:
- ✅ Keep experience achievements specific and quantifiable
- ✅ Organize skills logically by category
- ✅ Update resume regularly
- ✅ Use professional profile image
- ✅ Keep bio concise and impactful

---

## 8. Troubleshooting

### Can't login?
- Verify you're using the correct email and password
- Clear browser cache and cookies
- Check console for errors (F12 → Console)

### Changes not saving?
- Check for validation errors in red
- Ensure all required fields are filled
- Check browser console for errors

### Images not loading?
- Verify image URLs are publicly accessible
- Use HTTPS URLs when possible
- Check image format (JPG, PNG, GIF, WebP)

### Resume upload not working?
- Ensure file is `.doc`, `.docx`, or `.pdf`
- Check file size (should be under 5MB)
- Try a different browser

---

## 9. Database Models

### Blog Post Fields:
- `id`, `title`, `slug`, `excerpt`, `content`
- `author`, `tags`, `category`
- `published` (boolean), `publishedAt`, `createdAt`, `updatedAt`
- `views`, `theme`

### Portfolio Project Fields:
- `id`, `title`, `summary`, `description`
- `imageUrl`, `projectImages` (array)
- `liveUrl`, `githubUrl`
- `featured` (boolean)
- `technologies` (array)
- `createdAt`, `updatedAt`

---

## 10. Support & Documentation

For more information:
- **Frontend Documentation**: See `/frontend/README.md`
- **Backend Documentation**: See `/MODE_CONFIGURATION.md` and `/AWS_DEPLOYMENT.md`
- **UI Overview**: See `/FRONTEND_UI_OVERVIEW.md`

---

## Quick Reference Commands

**Start in Development Mode:**
```bash
docker compose up
```

**Access Points:**
- Frontend: http://localhost:3000
- Admin Login: http://localhost:3000/login
- Admin Dashboard: http://localhost:3000/admin
- Profile Page: http://localhost:3000/profile
- Blog: http://localhost:3000/blog
- Portfolio: http://localhost:3000/portfolio

**Admin Routes:**
- `/admin` - Dashboard
- `/admin/blog/new` - Create new blog post
- `/admin/blog/edit/:id` - Edit blog post
- `/admin/portfolio` - Manage portfolio
- `/admin/profile` - Manage professional profile

---

## Security Notes

⚠️ **Important Security Considerations:**

1. **Change default credentials** immediately in production
2. **Use HTTPS** in production environments
3. **Enable CORS** restrictions for API endpoints
4. **Implement rate limiting** (already configured in API Gateway)
5. **Validate all file uploads** on the server side
6. **Sanitize user inputs** to prevent XSS attacks
7. **Use environment variables** for sensitive configuration

---

**Happy Blogging! 🚀**
