<template>
  <div id="app">
    <header class="header">
      <nav class="nav">
        <div class="nav-brand">
          <router-link to="/">DAS Blog & Portfolio</router-link>
        </div>
        <div class="nav-links">
          <router-link to="/">Home</router-link>
          <router-link to="/blog">Blog</router-link>
          <router-link to="/portfolio">Portfolio</router-link>
          <router-link v-if="!isAuthenticated" to="/login">Login</router-link>
          <template v-else>
            <router-link to="/admin">Admin</router-link>
            <button @click="handleLogout" class="btn-link">Logout</button>
          </template>
        </div>
      </nav>
    </header>

    <main class="main-content">
      <router-view />
    </main>

    <footer class="footer">
      <p>&copy; 2024 DAS Blog. All rights reserved.</p>
    </footer>
  </div>
</template>

<script setup>
import { computed } from 'vue'
import { useRouter } from 'vue-router'
import { useAuthStore } from './stores/auth'

const authStore = useAuthStore()
const router = useRouter()

const isAuthenticated = computed(() => authStore.isAuthenticated)

const handleLogout = () => {
  authStore.logout()
  router.push('/')
}
</script>

<style scoped>
#app {
  min-height: 100vh;
  display: flex;
  flex-direction: column;
}

.header {
  background: #2c3e50;
  color: white;
  padding: 1rem 0;
  box-shadow: 0 2px 4px rgba(0,0,0,0.1);
}

.nav {
  max-width: 1200px;
  margin: 0 auto;
  padding: 0 2rem;
  display: flex;
  justify-content: space-between;
  align-items: center;
}

.nav-brand a {
  color: white;
  text-decoration: none;
  font-size: 1.5rem;
  font-weight: bold;
}

.nav-links {
  display: flex;
  gap: 2rem;
  align-items: center;
}

.nav-links a {
  color: white;
  text-decoration: none;
  transition: opacity 0.3s;
}

.nav-links a:hover,
.nav-links a.router-link-active {
  opacity: 0.8;
  text-decoration: underline;
}

.btn-link {
  background: none;
  border: none;
  color: white;
  cursor: pointer;
  font-size: 1rem;
  padding: 0;
  transition: opacity 0.3s;
}

.btn-link:hover {
  opacity: 0.8;
}

.main-content {
  flex: 1;
  max-width: 1200px;
  width: 100%;
  margin: 2rem auto;
  padding: 0 2rem;
}

.footer {
  background: #2c3e50;
  color: white;
  padding: 2rem 0;
  text-align: center;
  margin-top: auto;
}
</style>
