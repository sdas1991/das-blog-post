<template>
  <div class="auth-page">
    <div class="card auth-card">
      <h1>Register</h1>
      <form @submit.prevent="handleRegister">
        <div class="form-group">
          <label>Name</label>
          <input v-model="form.name" type="text" required />
        </div>
        <div class="form-group">
          <label>Email</label>
          <input v-model="form.email" type="email" required />
        </div>
        <div class="form-group">
          <label>Password</label>
          <input v-model="form.password" type="password" required minlength="6" />
        </div>
        <div class="form-group">
          <label>Confirm Password</label>
          <input v-model="form.confirmPassword" type="password" required />
        </div>
        <button type="submit" class="btn btn-primary" :disabled="loading">
          {{ loading ? 'Registering...' : 'Register' }}
        </button>
        <p v-if="error" class="error-message">{{ error }}</p>
      </form>
      <p class="auth-link">
        Already have an account? <router-link to="/login">Login</router-link>
      </p>
    </div>
  </div>
</template>

<script setup>
import { ref } from 'vue'
import { useRouter } from 'vue-router'
import { useAuthStore } from '../stores/auth'

const router = useRouter()
const authStore = useAuthStore()

const form = ref({
  name: '',
  email: '',
  password: '',
  confirmPassword: ''
})

const loading = ref(false)
const error = ref('')

const handleRegister = async () => {
  if (form.value.password !== form.value.confirmPassword) {
    error.value = 'Passwords do not match'
    return
  }

  loading.value = true
  error.value = ''

  const success = await authStore.register({
    name: form.value.name,
    email: form.value.email,
    password: form.value.password
  })

  if (success) {
    router.push('/admin')
  } else {
    error.value = authStore.error || 'Registration failed. Please try again.'
  }

  loading.value = false
}
</script>

<style scoped>
.auth-page {
  display: flex;
  justify-content: center;
  align-items: center;
  min-height: 60vh;
}

.auth-card {
  max-width: 400px;
  width: 100%;
}

.auth-card h1 {
  font-size: 2rem;
  margin-bottom: 2rem;
  text-align: center;
}

.auth-link {
  text-align: center;
  margin-top: 1.5rem;
}

.auth-link a {
  color: #42b983;
  font-weight: 500;
}
</style>
