<template>
  <div class="home">
    <section class="hero">
      <h1>Welcome to DAS Blog & Portfolio</h1>
      <p>Explore my thoughts, projects, and professional journey</p>
      <div class="hero-actions">
        <router-link to="/blog" class="btn btn-primary">Read Blog</router-link>
        <router-link to="/portfolio" class="btn btn-secondary">View Portfolio</router-link>
      </div>
    </section>

    <section class="featured-posts">
      <h2>Latest Blog Posts</h2>
      <div v-if="loading" class="loading">Loading...</div>
      <div v-else-if="recentPosts.length" class="grid grid-2">
        <div v-for="post in recentPosts" :key="post.id" class="card post-card">
          <h3>{{ post.title }}</h3>
          <p class="post-excerpt">{{ post.excerpt }}</p>
          <div class="post-meta">
            <span class="post-date">{{ formatDate(post.publishedAt) }}</span>
            <span v-if="post.category" class="post-category">{{ post.category }}</span>
          </div>
          <router-link :to="`/blog/${post.id}`" class="btn btn-primary">Read More</router-link>
        </div>
      </div>
      <p v-else>No posts yet.</p>
    </section>
  </div>
</template>

<script setup>
import { ref, onMounted } from 'vue'
import { useBlogStore } from '../stores/blog'

const blogStore = useBlogStore()
const recentPosts = ref([])
const loading = ref(false)

onMounted(async () => {
  loading.value = true
  await blogStore.fetchPosts({ published: true, limit: 4 })
  recentPosts.value = blogStore.posts.slice(0, 4)
  loading.value = false
})

const formatDate = (date) => {
  return new Date(date).toLocaleDateString('en-US', {
    year: 'numeric',
    month: 'long',
    day: 'numeric'
  })
}
</script>

<style scoped>
.hero {
  text-align: center;
  padding: 4rem 2rem;
  background: linear-gradient(135deg, #667eea 0%, #764ba2 100%);
  color: white;
  border-radius: 8px;
  margin-bottom: 3rem;
}

.hero h1 {
  font-size: 3rem;
  margin-bottom: 1rem;
}

.hero p {
  font-size: 1.25rem;
  margin-bottom: 2rem;
}

.hero-actions {
  display: flex;
  gap: 1rem;
  justify-content: center;
}

.featured-posts {
  margin-bottom: 3rem;
}

.featured-posts h2 {
  font-size: 2rem;
  margin-bottom: 2rem;
}

.post-card {
  display: flex;
  flex-direction: column;
}

.post-card h3 {
  font-size: 1.5rem;
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
</style>
