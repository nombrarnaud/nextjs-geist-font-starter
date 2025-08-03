import { defineStore } from 'pinia'
import { ref, computed } from 'vue'
import { apiService } from '@/services/api'

export interface User {
  id: number
  email: string
  fullName: string
  phone: string
  userType: 'BUSINESS' | 'SIMPLE'
  companyName?: string
  registrationNumber?: string
  managerName?: string
  identityCardNumber?: string
}

export interface LoginCredentials {
  email: string
  password: string
  rememberMe: boolean
}

export interface BusinessRegistration {
  companyName: string
  registrationNumber: string
  phone: string
  managerName: string
  email: string
  password: string
}

export interface SimpleRegistration {
  phone: string
  identityCardNumber: string
  fullName: string
  email: string
  password: string
}

export const useAuthStore = defineStore('auth', () => {
  const user = ref<User | null>(null)
  const token = ref<string | null>(null)
  const isLoading = ref(false)
  const error = ref<string | null>(null)

  const isAuthenticated = computed(() => !!user.value && !!token.value)

  const login = async (credentials: LoginCredentials) => {
    isLoading.value = true
    error.value = null
    
    try {
      const response = await apiService.post('/api/login', credentials)
      
      user.value = response.data.user
      token.value = response.data.token
      
      // Store in localStorage if remember me is checked
      if (credentials.rememberMe) {
        localStorage.setItem('auth_token', token.value)
        localStorage.setItem('user_data', JSON.stringify(user.value))
      } else {
        sessionStorage.setItem('auth_token', token.value)
        sessionStorage.setItem('user_data', JSON.stringify(user.value))
      }
      
      return { success: true }
    } catch (err: any) {
      error.value = err.response?.data?.message || 'Erreur de connexion'
      return { success: false, error: error.value }
    } finally {
      isLoading.value = false
    }
  }

  const registerBusiness = async (data: BusinessRegistration) => {
    isLoading.value = true
    error.value = null
    
    try {
      await apiService.post('/api/register/business', data)
      return { success: true }
    } catch (err: any) {
      error.value = err.response?.data?.message || 'Erreur lors de l\'inscription'
      return { success: false, error: error.value }
    } finally {
      isLoading.value = false
    }
  }

  const registerSimple = async (data: SimpleRegistration) => {
    isLoading.value = true
    error.value = null
    
    try {
      await apiService.post('/api/register/simple', data)
      return { success: true }
    } catch (err: any) {
      error.value = err.response?.data?.message || 'Erreur lors de l\'inscription'
      return { success: false, error: error.value }
    } finally {
      isLoading.value = false
    }
  }

  const logout = () => {
    user.value = null
    token.value = null
    localStorage.removeItem('auth_token')
    localStorage.removeItem('user_data')
    sessionStorage.removeItem('auth_token')
    sessionStorage.removeItem('user_data')
  }

  const checkRememberedSession = () => {
    const storedToken = localStorage.getItem('auth_token') || sessionStorage.getItem('auth_token')
    const storedUser = localStorage.getItem('user_data') || sessionStorage.getItem('user_data')
    
    if (storedToken && storedUser) {
      token.value = storedToken
      user.value = JSON.parse(storedUser)
    }
  }

  const updateProfile = async (userData: Partial<User>) => {
    isLoading.value = true
    error.value = null
    
    try {
      const response = await apiService.put('/api/user/profile', userData)
      user.value = response.data
      
      // Update stored user data
      const storage = localStorage.getItem('auth_token') ? localStorage : sessionStorage
      storage.setItem('user_data', JSON.stringify(user.value))
      
      return { success: true }
    } catch (err: any) {
      error.value = err.response?.data?.message || 'Erreur lors de la mise à jour'
      return { success: false, error: error.value }
    } finally {
      isLoading.value = false
    }
  }

  return {
    user,
    token,
    isLoading,
    error,
    isAuthenticated,
    login,
    registerBusiness,
    registerSimple,
    logout,
    checkRememberedSession,
    updateProfile
  }
})
