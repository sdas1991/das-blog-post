<template>
  <div class="blog-editor">
    <h1>{{ isEditMode ? 'Edit Post' : 'Create New Post' }}</h1>

    <form @submit.prevent="handleSubmit" class="editor-form">
      <div class="form-group">
        <label>Title *</label>
        <input v-model="post.title" type="text" required placeholder="Enter post title" />
      </div>

      <div class="form-group">
        <label>Slug</label>
        <input v-model="post.slug" type="text" placeholder="auto-generated-from-title" />
        <small>URL-friendly version of the title. Leave blank to auto-generate.</small>
      </div>

      <div class="form-row">
        <div class="form-group">
          <label>Category</label>
          <input v-model="post.category" type="text" placeholder="e.g., Technology, Tutorial" />
        </div>

        <div class="form-group">
          <label>Tags</label>
          <input v-model="tagsInput" type="text" placeholder="javascript, vue, web" />
          <small>Comma-separated tags</small>
        </div>
      </div>

      <div class="form-group">
        <label>Excerpt</label>
        <textarea v-model="post.excerpt" rows="3" placeholder="Brief summary of the post"></textarea>
        <small>Short description shown in post listings</small>
      </div>

      <div class="form-group">
        <label>Content *</label>
        <RichTextEditor v-model="post.content" />
      </div>

      <div class="form-group checkbox-group">
        <label>
          <input v-model="post.published" type="checkbox" />
          Publish immediately
        </label>
      </div>

      <div class="form-actions">
        <button type="submit" class="btn btn-primary" :disabled="loading">
          {{ loading ? 'Saving...' : (isEditMode ? 'Update Post' : 'Create Post') }}
        </button>
        <button type="button" @click="handleCancel" class="btn btn-secondary">
          Cancel
        </button>
      </div>

      <p v-if="error" class="error-message">{{ error }}</p>
      <p v-if="success" class="success-message">{{ success }}</p>
    </form>
  </div>
</template>

<script setup>
import { ref, computed, onMounted } from 'vue'
import { useRoute, useRouter } from 'vue-router'
import { useBlogStore } from '../../stores/blog'
import RichTextEditor from '../../components/RichTextEditor.vue'

const route = useRoute()
const router = useRouter()
const blogStore = useBlogStore()

const isEditMode = computed(() => !!route.params.id)

const post = ref({
  title: '',
  slug: '',
  excerpt: '',
  content: '',
  category: '',
  tags: [],
  published: false
})

const tagsInput = ref('')
const loading = ref(false)
const error = ref('')
const success = ref('')

onMounted(async () => {
  if (isEditMode.value) {
    loading.value = true
    const existingPost = await blogStore.fetchPost(route.params.id)

    if (existingPost) {
      post.value = {
        title: existingPost.title,
        slug: existingPost.slug,
        excerpt: existingPost.excerpt || '',
        content: existingPost.content,
        category: existingPost.category || '',
        tags: existingPost.tags || [],
        published: existingPost.published
      }
      tagsInput.value = existingPost.tags ? existingPost.tags.join(', ') : ''
    } else {
      error.value = 'Post not found'
    }

    loading.value = false
  }
})

const handleSubmit = async () => {
  loading.value = true
  error.value = ''
  success.value = ''

  // Parse tags
  post.value.tags = tagsInput.value
    .split(',')
    .map(tag => tag.trim())
    .filter(tag => tag.length > 0)

  // Auto-generate slug if not provided
  if (!post.value.slug && post.value.title) {
    post.value.slug = post.value.title
      .toLowerCase()
      .replace(/[^a-z0-9]+/g, '-')
      .replace(/(^-|-$)/g, '')
  }

  let result
  if (isEditMode.value) {
    result = await blogStore.updatePost(route.params.id, post.value)
  } else {
    result = await blogStore.createPost(post.value)
  }

  if (result) {
    success.value = isEditMode.value ? 'Post updated successfully!' : 'Post created successfully!'
    setTimeout(() => {
      router.push('/admin')
    }, 1500)
  } else {
    error.value = blogStore.error || 'Failed to save post'
  }

  loading.value = false
}

const handleCancel = () => {
  router.push('/admin')
}
</script>

<style scoped>
.blog-editor {
  max-width: 1000px;
  margin: 0 auto;
}

.blog-editor h1 {
  font-size: 2.5rem;
  margin-bottom: 2rem;
}

.editor-form {
  background: white;
  padding: 2rem;
  border-radius: 8px;
  box-shadow: 0 2px 8px rgba(0,0,0,0.1);
}

.form-row {
  display: grid;
  grid-template-columns: 1fr 1fr;
  gap: 1.5rem;
}

.form-group small {
  display: block;
  margin-top: 0.25rem;
  color: #666;
  font-size: 0.85rem;
}

.checkbox-group label {
  display: flex;
  align-items: center;
  gap: 0.5rem;
  cursor: pointer;
}

.checkbox-group input[type="checkbox"] {
  width: auto;
  cursor: pointer;
}

.form-actions {
  display: flex;
  gap: 1rem;
  margin-top: 2rem;
}

@media (max-width: 768px) {
  .form-row {
    grid-template-columns: 1fr;
  }
}
</style>
