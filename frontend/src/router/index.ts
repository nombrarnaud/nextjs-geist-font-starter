import { createRouter, createWebHistory } from 'vue-router'
import { useAuthStore } from '@/stores/auth'
import LoginView from '@/views/LoginView.vue'
import RegisterBusinessView from '@/views/RegisterBusinessView.vue'
import RegisterSimpleView from '@/views/RegisterSimpleView.vue'
import DashboardView from '@/views/DashboardView.vue'
import VehicleListView from '@/views/VehicleListView.vue'
import VehicleDetailsView from '@/views/VehicleDetailsView.vue'
import UserProfileView from '@/views/UserProfileView.vue'

const router = createRouter({
  history: createWebHistory(import.meta.env.BASE_URL),
  routes: [
    {
      path: '/',
      redirect: '/login'
    },
    {
      path: '/login',
      name: 'login',
      component: LoginView,
      meta: { requiresGuest: true }
    },
    {
      path: '/register/business',
      name: 'register-business',
      component: RegisterBusinessView,
      meta: { requiresGuest: true }
    },
    {
      path: '/register/simple',
      name: 'register-simple',
      component: RegisterSimpleView,
      meta: { requiresGuest: true }
    },
    {
      path: '/dashboard',
      name: 'dashboard',
      component: DashboardView,
      meta: { requiresAuth: true }
    },
    {
      path: '/vehicles',
      name: 'vehicles',
      component: VehicleListView,
      meta: { requiresAuth: true }
    },
    {
      path: '/vehicles/:id',
      name: 'vehicle-details',
      component: VehicleDetailsView,
      meta: { requiresAuth: true }
    },
    {
      path: '/profile',
      name: 'profile',
      component: UserProfileView,
      meta: { requiresAuth: true }
    }
  ]
})

// Navigation guards
router.beforeEach((to, from, next) => {
  const authStore = useAuthStore()
  
  if (to.meta.requiresAuth && !authStore.isAuthenticated) {
    next('/login')
  } else if (to.meta.requiresGuest && authStore.isAuthenticated) {
    next('/dashboard')
  } else {
    next()
  }
})

export default router
