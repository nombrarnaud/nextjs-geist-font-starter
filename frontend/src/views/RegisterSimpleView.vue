<template>
  <div class="min-h-screen flex items-center justify-center bg-gray-50 py-12 px-4 sm:px-6 lg:px-8">
    <div class="max-w-md w-full space-y-8">
      <div>
        <img 
          src="https://placehold.co/120x120?text=GPS+Tracker+Logo" 
          alt="GPS Tracker Logo" 
          class="mx-auto h-12 w-auto"
          onerror="this.onerror=null;this.style.display='none';"
        />
        <h2 class="mt-6 text-center text-3xl font-bold text-gray-900">
          Inscription Personnel
        </h2>
        <p class="mt-2 text-center text-sm text-gray-600">
          Ou
          <router-link to="/login" class="font-medium text-primary-600 hover:text-primary-500">
            se connecter
          </router-link>
          /
          <router-link to="/register/business" class="font-medium text-primary-600 hover:text-primary-500">
            compte entreprise
          </router-link>
        </p>
      </div>
      
      <form class="mt-8 space-y-6" @submit.prevent="handleRegister">
        <div v-if="authStore.error" class="bg-red-50 border border-red-200 text-red-700 px-4 py-3 rounded-lg">
          {{ authStore.error }}
        </div>
        
        <div v-if="successMessage" class="bg-green-50 border border-green-200 text-green-700 px-4 py-3 rounded-lg">
          {{ successMessage }}
        </div>
        
        <div class="space-y-4">
          <div>
            <label for="fullName" class="form-label">Nom complet</label>
            <input
              id="fullName"
              v-model="form.fullName"
              type="text"
              required
              class="form-input"
              placeholder="Prénom et nom"
            />
          </div>
          
          <div>
            <label for="phone" class="form-label">Numéro de téléphone</label>
            <input
              id="phone"
              v-model="form.phone"
              type="tel"
              required
              class="form-input"
              placeholder="+33 1 23 45 67 89"
            />
          </div>
          
          <div>
            <label for="identityCardNumber" class="form-label">Numéro de carte d'identité</label>
            <input
              id="identityCardNumber"
              v-model="form.identityCardNumber"
              type="text"
              required
              class="form-input"
              placeholder="Numéro de votre carte d'identité"
            />
          </div>
          
          <div>
            <label for="email" class="form-label">Adresse email</label>
            <input
              id="email"
              v-model="form.email"
              type="email"
              required
              class="form-input"
              placeholder="votre@email.com"
            />
          </div>
          
          <div>
            <label for="password" class="form-label">Mot de passe</label>
            <input
              id="password"
              v-model="form.password"
              type="password"
              required
              class="form-input"
              placeholder="••••••••"
            />
          </div>
          
          <div>
            <label for="confirmPassword" class="form-label">Confirmer le mot de passe</label>
            <input
              id="confirmPassword"
              v-model="form.confirmPassword"
              type="password"
              required
              class="form-input"
              placeholder="••••••••"
            />
          </div>
        </div>

        <div>
          <button
            type="submit"
            :disabled="authStore.isLoading"
            class="group relative w-full flex justify-center py-2 px-4 border border-transparent text-sm font-medium rounded-lg text-white bg-primary-600 hover:bg-primary-700 focus:outline-none focus:ring-2 focus:ring-offset-2 focus:ring-primary-500 disabled:opacity-50 disabled:cursor-not-allowed"
          >
            <span v-if="authStore.isLoading">Inscription...</span>
            <span v-else>Créer le compte personnel</span>
          </button>
        </div>
      </form>
    </div>
  </div>
</template>

<script setup lang="ts">
import { reactive, ref } from 'vue'
import { useRouter } from 'vue-router'
import { useAuthStore } from '@/stores/auth'

const router = useRouter()
const authStore = useAuthStore()
const successMessage = ref('')

const form = reactive({
  fullName: '',
  phone: '',
  identityCardNumber: '',
  email: '',
  password: '',
  confirmPassword: ''
})

const handleRegister = async () => {
  if (form.password !== form.confirmPassword) {
    authStore.error = 'Les mots de passe ne correspondent pas'
    return
  }
  
  const { confirmPassword, ...registrationData } = form
  const result = await authStore.registerSimple(registrationData)
  
  if (result.success) {
    successMessage.value = 'Compte créé avec succès! Vous pouvez maintenant vous connecter.'
    setTimeout(() => {
      router.push('/login')
    }, 2000)
  }
}
</script>
