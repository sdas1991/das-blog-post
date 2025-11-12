<template>
  <div class="blog-list">
    <h1>Blog Posts</h1>

    <div class="filters">
      <input
        v-model="searchQuery"
        type="text"
        placeholder="Search posts..."
        class="search-input"
        @input="handleSearch"
      />
      <select v-model="selectedCategory" @change="handleFilter" class="filter-select">
        <option value="">All Categories</option>
        <option v-for="category in categories" :key="category" :value="category">
          {{ category }}
        </option>
      </select>
    </div>

    <div v-if="loading" class="loading">Loading...</div>
    <div v-else-if="posts.length" class="posts-grid">
      <div v-for="post in posts" :key="post.id" class="card post-card">
        <h2>{{ post.title }}</h2>
        <p class="post-excerpt">{{ post.excerpt }}</p>
        <div class="post-meta">
          <span class="post-date">{{ formatDate(post.publishedAt) }}</span>
          <span v-if="post.category" class="post-category">{{ post.category }}</span>
        </div>
        <div v-if="post.tags && post.tags.length" class="post-tags">
          <span v-for="tag in post.tags" :key="tag" class="tag">{{ tag }}</span>
        </div>
        <router-link :to="`/blog/${post.id}`" class="btn btn-primary">Read More</router-link>
      </div>
    </div>
    <p v-else>No posts found.</p>
  </div>
</template>

<script setup>
import { ref, onMounted } from 'vue'
import { useBlogStore } from '../stores/blog'

const blogStore = useBlogStore()
const posts = ref([])
const categories = ref([])
const loading = ref(false)
const searchQuery = ref('')
const selectedCategory = ref('')

onMounted(async () => {
  loading.value = true
  await Promise.all([
    blogStore.fetchPosts({ published: true }),
    blogStore.fetchCategories()
  ])
  posts.value = blogStore.posts
  categories.value = blogStore.categories
  loading.value = false
})

const handleSearch = () => {
  filterPosts()
}

const handleFilter = () => {
  filterPosts()
}

const filterPosts = () => {
  loading.value = true
  const filters = { published: true }

  if (searchQuery.value) {
    filters.search = searchQuery.value
  }

  if (selectedCategory.value) {
    filters.category = selectedCategory.value
  }

  blogStore.fetchPosts(filters).then(() => {
    posts.value = blogStore.posts
    loading.value = false
  })
}

const formatDate = (date) => {
  return new Date(date).toLocaleDateString('en-US', {
    year: 'numeric',
    month: 'long',
    day: 'numeric'
  })
}
</script>

<style scoped>
.blog-list h1 {
  font-size: 2.5rem;
  margin-bottom: 2rem;
}

.filters {
  display: flex;
  gap: 1rem;
  margin-bottom: 2rem;
}

.search-input,
.filter-select {
  padding: 0.75rem;
  border: 1px solid #ddd;
  border-radius: 4px;
  font-size: 1rem;
}

.search-input {
  flex: 1;
}

.filter-select {
  min-width: 200px;
}

.posts-grid {
  display: grid;
  gap: 2rem;
}

.post-card {
  display: flex;
  flex-direction: column;
}

.post-card h2 {
  font-size: 1.75rem;
  margin-bottom: 1rem;
}

.post-excerpt {
  flex: 1;
  color: #666;
  margin-bottom: 1rem;
}

.post-meta {
  display: flex;
  gap: 1rem;
  margin-bottom: 1rem;
  font-size: 0.9rem;
  color: #888;
}

.post-category {
  background: #42b983;
  color: white;
  padding: 0.25rem 0.5rem;
  border-radius: 4px;
}

.post-tags {
  display: flex;
  flex-wrap: wrap;
  gap: 0.5rem;
  margin-bottom: 1rem;
}

.tag {
  background: #e9ecef;
  padding: 0.25rem 0.5rem;
  border-radius: 4px;
  font-size: 0.85rem;
}
</style>
