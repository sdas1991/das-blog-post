<template>
  <div class="blog-post">
    <div v-if="loading" class="loading">Loading...</div>
    <div v-else-if="post" class="post-content">
      <article class="card post-header">
        <h1>{{ post.title }}</h1>
        <div class="post-meta">
          <span class="post-author">By {{ post.author?.name || 'Anonymous' }}</span>
          <span class="post-date">{{ formatDate(post.publishedAt) }}</span>
          <span v-if="post.category" class="post-category">{{ post.category }}</span>
        </div>
        <div v-if="post.tags && post.tags.length" class="post-tags">
          <span v-for="tag in post.tags" :key="tag" class="tag">{{ tag }}</span>
        </div>
      </article>

      <article :class="['post-body-wrapper', getThemeClass(post.theme)]">
        <div class="post-body" v-html="post.content"></div>
      </article>

      <section class="comments-section card">
        <h2>Comments</h2>
        <div v-if="isAuthenticated" class="comment-form">
          <textarea
            v-model="newComment"
            placeholder="Write a comment..."
            rows="4"
          ></textarea>
          <button @click="submitComment" class="btn btn-primary" :disabled="!newComment.trim()">
            Post Comment
          </button>
        </div>
        <p v-else class="login-prompt">
          <router-link to="/login">Login</router-link> to comment
        </p>

        <div v-if="comments.length" class="comments-list">
          <div v-for="comment in comments" :key="comment.id" class="comment">
            <div class="comment-header">
              <strong>{{ comment.author?.name || 'Anonymous' }}</strong>
              <span class="comment-date">{{ formatDate(comment.createdAt) }}</span>
            </div>
            <p class="comment-content">{{ comment.content }}</p>
          </div>
        </div>
        <p v-else class="no-comments">No comments yet. Be the first to comment!</p>
      </section>
    </div>
    <p v-else>Post not found.</p>
  </div>
</template>

<script setup>
import { ref, computed, onMounted } from 'vue'
import { useRoute } from 'vue-router'
import { useBlogStore } from '../stores/blog'
import { useAuthStore } from '../stores/auth'
import { commentService } from '../services/api'

const route = useRoute()
const blogStore = useBlogStore()
const authStore = useAuthStore()

const post = ref(null)
const comments = ref([])
const newComment = ref('')
const loading = ref(false)

const isAuthenticated = computed(() => authStore.isAuthenticated)

onMounted(async () => {
  loading.value = true
  const postId = route.params.id
  post.value = await blogStore.fetchPost(postId)

  if (post.value) {
    try {
      comments.value = await commentService.getComments(postId)
    } catch (error) {
      console.error('Failed to load comments:', error)
    }
  }

  loading.value = false
})

const submitComment = async () => {
  if (!newComment.value.trim()) return

  try {
    const comment = await commentService.createComment(route.params.id, newComment.value)
    comments.value.push(comment)
    newComment.value = ''
  } catch (error) {
    alert('Failed to post comment. Please try again.')
  }
}

const formatDate = (date) => {
  return new Date(date).toLocaleDateString('en-US', {
    year: 'numeric',
    month: 'long',
    day: 'numeric'
  })
}

const getThemeClass = (theme) => {
  if (!theme) return 'blog-theme-default'
  return `blog-theme-${theme}`
}
</script>

<style scoped>
.blog-post {
  max-width: 800px;
  margin: 0 auto;
}

.post-header {
  margin-bottom: 1.5rem;
}

.post-header h1 {
  font-size: 2.5rem;
  margin-bottom: 1rem;
}

.post-body-wrapper {
  margin-bottom: 2rem;
}

.post-meta {
  display: flex;
  gap: 1rem;
  margin-bottom: 1rem;
  font-size: 0.9rem;
  color: #888;
  flex-wrap: wrap;
}

.post-author {
  font-weight: 500;
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
  margin-bottom: 2rem;
}

.tag {
  background: #e9ecef;
  padding: 0.25rem 0.5rem;
  border-radius: 4px;
  font-size: 0.85rem;
}

.post-body {
  line-height: 1.8;
  font-size: 1.1rem;
}

.post-body :deep(h1),
.post-body :deep(h2),
.post-body :deep(h3) {
  margin: 1.5rem 0 1rem;
}

.post-body :deep(p) {
  margin: 1rem 0;
}

.post-body :deep(ul),
.post-body :deep(ol) {
  margin: 1rem 0;
  padding-left: 2rem;
}

.post-body :deep(li) {
  margin: 0.5rem 0;
}

.comments-section {
  margin-top: 3rem;
}

.comments-section h2 {
  font-size: 1.75rem;
  margin-bottom: 1.5rem;
}

.comment-form {
  margin-bottom: 2rem;
}

.comment-form textarea {
  width: 100%;
  padding: 0.75rem;
  border: 1px solid #ddd;
  border-radius: 4px;
  font-size: 1rem;
  margin-bottom: 1rem;
  font-family: inherit;
}

.login-prompt {
  text-align: center;
  padding: 2rem;
  background: #f5f5f5;
  border-radius: 4px;
  margin-bottom: 2rem;
}

.login-prompt a {
  color: #42b983;
  font-weight: 500;
}

.comments-list {
  display: flex;
  flex-direction: column;
  gap: 1.5rem;
}

.comment {
  padding: 1rem;
  background: #f5f5f5;
  border-radius: 4px;
}

.comment-header {
  display: flex;
  justify-content: space-between;
  margin-bottom: 0.5rem;
}

.comment-date {
  font-size: 0.85rem;
  color: #888;
}

.comment-content {
  line-height: 1.6;
}

.no-comments {
  text-align: center;
  color: #888;
  padding: 2rem;
}
</style>
