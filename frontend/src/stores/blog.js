import { defineStore } from 'pinia'
import { ref } from 'vue'
import { blogService } from '../services/api'

export const useBlogStore = defineStore('blog', () => {
  const posts = ref([])
  const currentPost = ref(null)
  const categories = ref([])
  const tags = ref([])
  const loading = ref(false)
  const error = ref(null)

  async function fetchPosts(filters = {}) {
    try {
      loading.value = true
      error.value = null
      posts.value = await blogService.getPosts(filters)
    } catch (err) {
      error.value = err.response?.data?.message || 'Failed to fetch posts'
    } finally {
      loading.value = false
    }
  }

  async function fetchPost(id) {
    try {
      loading.value = true
      error.value = null
      currentPost.value = await blogService.getPost(id)
      return currentPost.value
    } catch (err) {
      error.value = err.response?.data?.message || 'Failed to fetch post'
      return null
    } finally {
      loading.value = false
    }
  }

  async function createPost(postData) {
    try {
      loading.value = true
      error.value = null
      const newPost = await blogService.createPost(postData)
      posts.value.unshift(newPost)
      return newPost
    } catch (err) {
      error.value = err.response?.data?.message || 'Failed to create post'
      return null
    } finally {
      loading.value = false
    }
  }

  async function updatePost(id, postData) {
    try {
      loading.value = true
      error.value = null
      const updatedPost = await blogService.updatePost(id, postData)
      const index = posts.value.findIndex(p => p.id === id)
      if (index !== -1) {
        posts.value[index] = updatedPost
      }
      return updatedPost
    } catch (err) {
      error.value = err.response?.data?.message || 'Failed to update post'
      return null
    } finally {
      loading.value = false
    }
  }

  async function deletePost(id) {
    try {
      loading.value = true
      error.value = null
      await blogService.deletePost(id)
      posts.value = posts.value.filter(p => p.id !== id)
      return true
    } catch (err) {
      error.value = err.response?.data?.message || 'Failed to delete post'
      return false
    } finally {
      loading.value = false
    }
  }

  async function fetchCategories() {
    try {
      categories.value = await blogService.getCategories()
    } catch (err) {
      console.error('Failed to fetch categories:', err)
    }
  }

  async function fetchTags() {
    try {
      tags.value = await blogService.getTags()
    } catch (err) {
      console.error('Failed to fetch tags:', err)
    }
  }

  return {
    posts,
    currentPost,
    categories,
    tags,
    loading,
    error,
    fetchPosts,
    fetchPost,
    createPost,
    updatePost,
    deletePost,
    fetchCategories,
    fetchTags
  }
})
