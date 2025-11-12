import { defineStore } from 'pinia'
import { ref, computed } from 'vue'
import { authService } from '../services/api'

export const useAuthStore = defineStore('auth', () => {
  const user = ref(null)
  const token = ref(localStorage.getItem('token'))
  const loading = ref(false)
  const error = ref(null)

  const isAuthenticated = computed(() => !!token.value)

  async function login(email, password) {
    try {
      loading.value = true
      error.value = null
      const data = await authService.login(email, password)

      token.value = data.token
      user.value = data.user
      localStorage.setItem('token', data.token)

      return true
    } catch (err) {
      error.value = err.response?.data?.message || 'Login failed'
      return false
    } finally {
      loading.value = false
    }
  }

  async function register(userData) {
    try {
      loading.value = true
      error.value = null
      const data = await authService.register(userData)

      token.value = data.token
      user.value = data.user
      localStorage.setItem('token', data.token)

      return true
    } catch (err) {
      error.value = err.response?.data?.message || 'Registration failed'
      return false
    } finally {
      loading.value = false
    }
  }

  async function fetchCurrentUser() {
    if (!token.value) return

    try {
      loading.value = true
      const data = await authService.getCurrentUser()
      user.value = data
    } catch (err) {
      // Token might be invalid
      logout()
    } finally {
      loading.value = false
    }
  }

  function logout() {
    token.value = null
    user.value = null
    localStorage.removeItem('token')
  }

  return {
    user,
    token,
    loading,
    error,
    isAuthenticated,
    login,
    register,
    fetchCurrentUser,
    logout
  }
})
