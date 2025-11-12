<template>
  <div class="admin-dashboard">
    <h1>Admin Dashboard</h1>

    <div class="dashboard-actions">
      <router-link to="/admin/blog/new" class="btn btn-primary">Create New Post</router-link>
      <router-link to="/admin/portfolio" class="btn btn-secondary">Manage Portfolio</router-link>
    </div>

    <section class="posts-section">
      <h2>Your Blog Posts</h2>
      <div v-if="loading" class="loading">Loading...</div>
      <div v-else-if="posts.length" class="posts-table">
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
            <tr v-for="post in posts" :key="post.id">
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
              </td>
            </tr>
          </tbody>
        </table>
      </div>
      <p v-else>No posts yet. Create your first post!</p>
    </section>
  </div>
</template>

<script setup>
import { ref, onMounted } from 'vue'
import { useBlogStore } from '../../stores/blog'

const blogStore = useBlogStore()
const posts = ref([])
const loading = ref(false)

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

.btn-small:hover {
  opacity: 0.9;
}
</style>
