import { createRouter, createWebHistory } from 'vue-router'
import { useAuthStore } from '../stores/auth'

const router = createRouter({
  history: createWebHistory(),
  routes: [
    {
      path: '/',
      name: 'home',
      component: () => import('../views/Home.vue')
    },
    {
      path: '/blog',
      name: 'blog',
      component: () => import('../views/BlogList.vue')
    },
    {
      path: '/blog/:id',
      name: 'blog-post',
      component: () => import('../views/BlogPost.vue')
    },
    {
      path: '/portfolio',
      name: 'portfolio',
      component: () => import('../views/Portfolio.vue')
    },
    {
      path: '/login',
      name: 'login',
      component: () => import('../views/Login.vue')
    },
    {
      path: '/register',
      name: 'register',
      component: () => import('../views/Register.vue')
    },
    {
      path: '/admin',
      name: 'admin',
      component: () => import('../views/admin/Dashboard.vue'),
      meta: { requiresAuth: true }
    },
    {
      path: '/admin/blog/new',
      name: 'admin-blog-new',
      component: () => import('../views/admin/BlogEditor.vue'),
      meta: { requiresAuth: true }
    },
    {
      path: '/admin/blog/edit/:id',
      name: 'admin-blog-edit',
      component: () => import('../views/admin/BlogEditor.vue'),
      meta: { requiresAuth: true }
    },
    {
      path: '/admin/portfolio',
      name: 'admin-portfolio',
      component: () => import('../views/admin/PortfolioManager.vue'),
      meta: { requiresAuth: true }
    },
    {
      path: '/admin/widgets',
      name: 'admin-widgets',
      component: () => import('../views/admin/WidgetManager.vue'),
      meta: { requiresAuth: true }
    }
  ]
})

router.beforeEach((to, from, next) => {
  const authStore = useAuthStore()

  if (to.meta.requiresAuth && !authStore.isAuthenticated) {
    next('/login')
  } else {
    next()
  }
})

export default router
