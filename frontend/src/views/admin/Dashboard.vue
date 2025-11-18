<template>
  <div class="admin-dashboard">
    <h1>Admin Dashboard</h1>

    <div class="dashboard-actions">
      <router-link to="/admin/blog/new" class="btn btn-primary">Create New Post</router-link>
      <router-link to="/admin/portfolio" class="btn btn-secondary">Manage Portfolio</router-link>
      <router-link to="/admin/profile" class="btn btn-info">Manage Profile</router-link>
    </div>

    <section class="posts-section">
      <div class="section-header">
        <h2>Your Blog Posts</h2>
        <div class="filter-tabs">
          <button
            :class="['filter-btn', { active: filter === 'all' }]"
            @click="filter = 'all'"
          >
            All ({{ posts.length }})
          </button>
          <button
            :class="['filter-btn', { active: filter === 'published' }]"
            @click="filter = 'published'"
          >
            Published ({{ publishedCount }})
          </button>
          <button
            :class="['filter-btn', { active: filter === 'draft' }]"
            @click="filter = 'draft'"
          >
            Drafts ({{ draftCount }})
          </button>
        </div>
      </div>

      <div v-if="loading" class="loading">Loading...</div>
      <div v-else-if="filteredPosts.length" class="posts-table">
        <table>
          <thead>
            <tr>
              <th>Title</th>
              <th>Status</th>
              <th>Category</th>
              <th>Published</th>
              <th>Actions</th>
            </tr>
          </thead>
          <tbody>
            <tr v-for="post in filteredPosts" :key="post.id">
              <td>{{ post.title }}</td>
              <td>
                <span :class="['status-badge', post.published ? 'status-published' : 'status-draft']">
                  {{ post.published ? 'Published' : 'Draft' }}
                </span>
              </td>
              <td>{{ post.category || '-' }}</td>
              <td>{{ post.publishedAt ? formatDate(post.publishedAt) : '-' }}</td>
              <td class="actions">
                <router-link :to="`/blog/${post.id}`" class="btn-small btn-secondary">View</router-link>
                <router-link :to="`/admin/blog/edit/${post.id}`" class="btn-small btn-primary">Edit</router-link>
                <button @click="handleDelete(post.id)" class="btn-small btn-danger">Delete</button>
                <button
                  @click="togglePublish(post)"
                  class="btn-small"
                  :class="post.published ? 'btn-warning' : 'btn-success'"
                >
                  {{ post.published ? 'Unpublish' : 'Publish' }}
                </button>
              </td>
            </tr>
          </tbody>
        </table>
      </div>
      <p v-else>No {{ filter === 'all' ? '' : filter }} posts yet.</p>
    </section>
  </div>
</template>

<script setup>
import { ref, computed, onMounted } from 'vue'
import { useBlogStore } from '../../stores/blog'

const blogStore = useBlogStore()
const posts = ref([])
const loading = ref(false)
const filter = ref('all')

const filteredPosts = computed(() => {
  if (filter.value === 'published') {
    return posts.value.filter(p => p.published)
  } else if (filter.value === 'draft') {
    return posts.value.filter(p => !p.published)
  }
  return posts.value
})

const publishedCount = computed(() => posts.value.filter(p => p.published).length)
const draftCount = computed(() => posts.value.filter(p => !p.published).length)

onMounted(async () => {
  loading.value = true
  await blogStore.fetchPosts({})
  posts.value = blogStore.posts
  loading.value = false
})

const handleDelete = async (id) => {
  if (!confirm('Are you sure you want to delete this post?')) {
    return
  }

  const success = await blogStore.deletePost(id)
  if (success) {
    posts.value = posts.value.filter(p => p.id !== id)
  } else {
    alert('Failed to delete post')
  }
}

const togglePublish = async (post) => {
  const newStatus = !post.published
  const action = newStatus ? 'publish' : 'unpublish'

  if (!confirm(`Are you sure you want to ${action} this post?`)) {
    return
  }

  try {
    // Update the post with new published status
    const updatedPost = { ...post, published: newStatus }
    if (newStatus && !post.publishedAt) {
      updatedPost.publishedAt = new Date().toISOString()
    }

    const success = await blogStore.updatePost(post.id, updatedPost)
    if (success) {
      post.published = newStatus
      if (newStatus && !post.publishedAt) {
        post.publishedAt = new Date().toISOString()
      }
    } else {
      alert(`Failed to ${action} post`)
    }
  } catch (error) {
    console.error(`Error ${action}ing post:`, error)
    alert(`Failed to ${action} post`)
  }
}

const formatDate = (date) => {
  return new Date(date).toLocaleDateString('en-US', {
    year: 'numeric',
    month: 'short',
    day: 'numeric'
  })
}
</script>

<style scoped>
.admin-dashboard h1 {
  font-size: 2.5rem;
  margin-bottom: 2rem;
}

.dashboard-actions {
  display: flex;
  gap: 1rem;
  margin-bottom: 3rem;
}

.section-header {
  display: flex;
  justify-content: space-between;
  align-items: center;
  margin-bottom: 1.5rem;
  flex-wrap: wrap;
  gap: 1rem;
}

.section-header h2 {
  font-size: 1.75rem;
  margin: 0;
}

.filter-tabs {
  display: flex;
  gap: 0.5rem;
}

.filter-btn {
  padding: 0.5rem 1rem;
  border: 1px solid #ddd;
  background: white;
  border-radius: 6px;
  cursor: pointer;
  font-size: 0.9rem;
  transition: all 0.3s ease;
}

.filter-btn:hover {
  background: #f0f0f0;
}

.filter-btn.active {
  background: #2c5282;
  color: white;
  border-color: #2c5282;
}

.posts-section h2 {
  font-size: 1.75rem;
  margin-bottom: 1.5rem;
}

.posts-table {
  background: white;
  border-radius: 8px;
  padding: 1.5rem;
  box-shadow: 0 2px 8px rgba(0,0,0,0.1);
  overflow-x: auto;
}

table {
  width: 100%;
  border-collapse: collapse;
}

th {
  text-align: left;
  padding: 1rem;
  border-bottom: 2px solid #ddd;
  font-weight: 600;
}

td {
  padding: 1rem;
  border-bottom: 1px solid #eee;
}

.status-badge {
  padding: 0.25rem 0.75rem;
  border-radius: 12px;
  font-size: 0.85rem;
  font-weight: 500;
}

.status-published {
  background: #d4edda;
  color: #155724;
}

.status-draft {
  background: #fff3cd;
  color: #856404;
}

.actions {
  display: flex;
  gap: 0.5rem;
}

.btn-small {
  padding: 0.5rem 0.75rem;
  font-size: 0.85rem;
  border: none;
  border-radius: 4px;
  cursor: pointer;
  text-decoration: none;
  display: inline-block;
  transition: all 0.3s;
}

.btn-small.btn-primary {
  background: #42b983;
  color: white;
}

.btn-small.btn-secondary {
  background: #6c757d;
  color: white;
}

.btn-small.btn-danger {
  background: #dc3545;
  color: white;
}

.btn-small.btn-success {
  background: #28a745;
  color: white;
}

.btn-small.btn-warning {
  background: #ffc107;
  color: #333;
}

.btn-small:hover {
  opacity: 0.9;
}

.btn.btn-info {
  background: #17a2b8;
  color: white;
}
</style>
