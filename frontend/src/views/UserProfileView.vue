<template>
  <div class="min-h-screen bg-gray-50">
    <!-- Navigation Header -->
    <nav class="bg-white shadow-sm border-b border-gray-200">
      <div class="max-w-7xl mx-auto px-4 sm:px-6 lg:px-8">
        <div class="flex justify-between h-16">
          <div class="flex items-center">
            <router-link to="/dashboard" class="flex items-center">
              <img 
                src="https://placehold.co/40x40?text=GPS" 
                alt="GPS Tracker" 
                class="h-8 w-8"
                onerror="this.onerror=null;this.style.display='none';"
              />
              <h1 class="ml-3 text-xl font-semibold text-gray-900">GPS Tracker</h1>
            </router-link>
          </div>
          
          <div class="flex items-center space-x-4">
            <router-link 
              to="/dashboard" 
              class="text-gray-600 hover:text-gray-900 px-3 py-2 rounded-md text-sm font-medium"
            >
              Tableau de bord
            </router-link>
            <router-link 
              to="/vehicles" 
              class="text-gray-600 hover:text-gray-900 px-3 py-2 rounded-md text-sm font-medium"
            >
              Véhicules
            </router-link>
            <button 
              @click="handleLogout"
              class="text-gray-600 hover:text-gray-900 px-3 py-2 rounded-md text-sm font-medium"
            >
              Déconnexion
            </button>
          </div>
        </div>
      </div>
    </nav>

    <!-- Main Content -->
    <div class="max-w-3xl mx-auto py-6 sm:px-6 lg:px-8">
      <div class="space-y-6">
        <!-- Profile Header -->
        <div class="card">
          <div class="flex items-center space-x-4">
            <div class="flex-shrink-0">
              <img 
                src="https://placehold.co/80x80?text=Profil+Utilisateur" 
                alt="Photo de profil" 
                class="h-20 w-20 rounded-full"
                onerror="this.onerror=null;this.style.display='none';"
              />
            </div>
            <div>
              <h2 class="text-2xl font-bold text-gray-900">{{ authStore.user?.fullName }}</h2>
              <p class="text-sm text-gray-500">{{ authStore.user?.email }}</p>
              <span 
                :class="authStore.user?.userType === 'BUSINESS' ? 'bg-blue-100 text-blue-800' : 'bg-green-100 text-green-800'"
                class="inline-flex items-center px-2.5 py-0.5 rounded-full text-xs font-medium mt-2"
              >
                {{ authStore.user?.userType === 'BUSINESS' ? 'Compte Entreprise' : 'Compte Personnel' }}
              </span>
            </div>
          </div>
        </div>

        <!-- Success/Error Messages -->
        <div v-if="successMessage" class="bg-green-50 border border-green-200 text-green-700 px-4 py-3 rounded-lg">
          {{ successMessage }}
        </div>
        
        <div v-if="authStore.error" class="bg-red-50 border border-red-200 text-red-700 px-4 py-3 rounded-lg">
          {{ authStore.error }}
        </div>

        <!-- Profile Form -->
        <div class="card">
          <h3 class="text-lg font-semibold text-gray-900 mb-6">Informations du profil</h3>
          
          <form @submit.prevent="handleUpdateProfile" class="space-y-6">
            <!-- Common Fields -->
            <div class="grid grid-cols-1 md:grid-cols-2 gap-6">
              <div>
                <label for="fullName" class="form-label">
                  {{ authStore.user?.userType === 'BUSINESS' ? 'Nom du gérant' : 'Nom complet' }}
                </label>
                <input
                  id="fullName"
                  v-model="form.fullName"
                  type="text"
                  required
                  class="form-input"
                  :placeholder="authStore.user?.userType === 'BUSINESS' ? 'Nom complet du gérant' : 'Votre nom complet'"
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

            <!-- Business-specific fields -->
            <div v-if="authStore.user?.userType === 'BUSINESS'" class="space-y-6">
              <div class="border-t border-gray-200 pt-6">
                <h4 class="text-md font-medium text-gray-900 mb-4">Informations de l'entreprise</h4>
                
                <div class="grid grid-cols-1 md:grid-cols-2 gap-6">
                  <div>
                    <label for="companyName" class="form-label">Nom de l'entreprise</label>
                    <input
                      id="companyName"
                      v-model="form.companyName"
                      type="text"
                      required
                      class="form-input"
                      placeholder="Nom de votre entreprise"
                    />
                  </div>
                  
                  <div>
                    <label for="registrationNumber" class="form-label">Numéro d'enregistrement</label>
                    <input
                      id="registrationNumber"
                      v-model="form.registrationNumber"
                      type="text"
                      required
                      class="form-input"
                      placeholder="Numéro d'enregistrement"
                    />
                  </div>
                </div>
              </div>
            </div>

            <!-- Simple user-specific fields -->
            <div v-if="authStore.user?.userType === 'SIMPLE'" class="space-y-6">
              <div class="border-t border-gray-200 pt-6">
                <h4 class="text-md font-medium text-gray-900 mb-4">Informations personnelles</h4>
                
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
              </div>
            </div>

            <!-- Submit Button -->
            <div class="flex justify-end pt-6">
              <button
                type="submit"
                :disabled="authStore.isLoading"
                class="btn-primary"
              >
                {{ authStore.isLoading ? 'Mise à jour...' : 'Mettre à jour le profil' }}
              </button>
            </div>
          </form>
        </div>

        <!-- Change Password Section -->
        <div class="card">
          <h3 class="text-lg font-semibold text-gray-900 mb-6">Changer le mot de passe</h3>
          
          <form @submit.prevent="handleChangePassword" class="space-y-6">
            <div>
              <label for="currentPassword" class="form-label">Mot de passe actuel</label>
              <input
                id="currentPassword"
                v-model="passwordForm.currentPassword"
                type="password"
                required
                class="form-input"
                placeholder="••••••••"
              />
            </div>
            
            <div class="grid grid-cols-1 md:grid-cols-2 gap-6">
              <div>
                <label for="newPassword" class="form-label">Nouveau mot de passe</label>
                <input
                  id="newPassword"
                  v-model="passwordForm.newPassword"
                  type="password"
                  required
                  class="form-input"
                  placeholder="••••••••"
                />
              </div>
              
              <div>
                <label for="confirmNewPassword" class="form-label">Confirmer le nouveau mot de passe</label>
                <input
                  id="confirmNewPassword"
                  v-model="passwordForm.confirmNewPassword"
                  type="password"
                  required
                  class="form-input"
                  placeholder="••••••••"
                />
              </div>
            </div>

            <div class="flex justify-end">
              <button
                type="submit"
                :disabled="authStore.isLoading"
                class="btn-primary"
              >
                {{ authStore.isLoading ? 'Changement...' : 'Changer le mot de passe' }}
              </button>
            </div>
          </form>
        </div>

        <!-- Account Statistics -->
        <div class="card">
          <h3 class="text-lg font-semibold text-gray-900 mb-6">Statistiques du compte</h3>
          
          <div class="grid grid-cols-1 md:grid-cols-3 gap-6">
            <div class="text-center">
              <div class="text-2xl font-bold text-primary-600">{{ vehicleStore.vehicles.length }}</div>
              <div class="text-sm text-gray-500">Véhicules suivis</div>
            </div>
            
            <div class="text-center">
              <div class="text-2xl font-bold text-green-600">{{ vehicleStore.onlineVehicles.length }}</div>
              <div class="text-sm text-gray-500">Véhicules en ligne</div>
            </div>
            
            <div class="text-center">
              <div class="text-2xl font-bold text-blue-600">{{ totalDistance }}</div>
              <div class="text-sm text-gray-500">Distance totale (km)</div>
            </div>
          </div>
        </div>

        <!-- Danger Zone -->
        <div class="card border-red-200">
          <h3 class="text-lg font-semibold text-red-900 mb-4">Zone de danger</h3>
          <p class="text-sm text-gray-600 mb-4">
            Une fois que vous supprimez votre compte, il n'y a pas de retour en arrière. Soyez certain.
          </p>
          
          <button
            @click="showDeleteConfirm = true"
            class="bg-red-600 hover:bg-red-700 text-white font-medium py-2 px-4 rounded-lg transition-colors duration-200"
          >
            Supprimer le compte
          </button>
        </div>
      </div>
    </div>

    <!-- Delete Confirmation Modal -->
    <div v-if="showDeleteConfirm" class="fixed inset-0 bg-gray-600 bg-opacity-50 overflow-y-auto h-full w-full z-50">
      <div class="relative top-20 mx-auto p-5 border w-96 shadow-lg rounded-md bg-white">
        <div class="mt-3">
          <h3 class="text-lg font-medium text-gray-900 mb-4">Confirmer la suppression</h3>
          <p class="text-sm text-gray-600 mb-6">
            Êtes-vous sûr de vouloir supprimer votre compte ? Cette action est irréversible et supprimera tous vos véhicules et données.
          </p>
          
          <div class="flex justify-end space-x-3">
            <button
              @click="showDeleteConfirm = false"
              class="btn-secondary"
            >
              Annuler
            </button>
            <button
              @click="handleDeleteAccount"
              class="bg-red-600 hover:bg-red-700 text-white font-medium py-2 px-4 rounded-lg transition-colors duration-200"
            >
              Supprimer définitivement
            </button>
          </div>
        </div>
      </div>
    </div>
  </div>
</template>

<script setup lang="ts">
import { onMounted, computed, ref, reactive } from 'vue'
import { useRouter } from 'vue-router'
import { useAuthStore } from '@/stores/auth'
import { useVehicleStore } from '@/stores/vehicles'

const router = useRouter()
const authStore = useAuthStore()
const vehicleStore = useVehicleStore()

const successMessage = ref('')
const showDeleteConfirm = ref(false)

const form = reactive({
  fullName: '',
  email: '',
  phone: '',
  companyName: '',
  registrationNumber: '',
  identityCardNumber: ''
})

const passwordForm = reactive({
  currentPassword: '',
  newPassword: '',
  confirmNewPassword: ''
})

const totalDistance = computed(() => {
  return vehicleStore.vehicles.reduce((total, v) => total + (v.totalDistance || 0), 0)
})

const initializeForm = () => {
  if (authStore.user) {
    form.fullName = authStore.user.fullName
    form.email = authStore.user.email
    form.phone = authStore.user.phone
    
    if (authStore.user.userType === 'BUSINESS') {
      form.companyName = authStore.user.companyName || ''
      form.registrationNumber = authStore.user.registrationNumber || ''
    } else {
      form.identityCardNumber = authStore.user.identityCardNumber || ''
    }
  }
}

const handleUpdateProfile = async () => {
  const result = await authStore.updateProfile(form)
  
  if (result.success) {
    successMessage.value = 'Profil mis à jour avec succès!'
    setTimeout(() => {
      successMessage.value = ''
    }, 3000)
  }
}

const handleChangePassword = async () => {
  if (passwordForm.newPassword !== passwordForm.confirmNewPassword) {
    authStore.error = 'Les nouveaux mots de passe ne correspondent pas'
    return
  }
  
  // TODO: Implement password change API call
  console.log('Change password:', passwordForm)
  
  // Reset form on success
  Object.assign(passwordForm, {
    currentPassword: '',
    newPassword: '',
    confirmNewPassword: ''
  })
  
  successMessage.value = 'Mot de passe changé avec succès!'
  setTimeout(() => {
    successMessage.value = ''
  }, 3000)
}

const handleDeleteAccount = async () => {
  // TODO: Implement account deletion API call
  console.log('Delete account')
  
  // For now, just logout and redirect
  authStore.logout()
  router.push('/login')
}

const handleLogout = () => {
  authStore.logout()
  router.push('/login')
}

onMounted(() => {
  initializeForm()
  
  // Load vehicles for statistics
  if (vehicleStore.vehicles.length === 0) {
    vehicleStore.initializeMockData()
  }
})
</script>
