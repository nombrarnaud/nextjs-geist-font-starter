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
              to="/profile" 
              class="text-gray-600 hover:text-gray-900 px-3 py-2 rounded-md text-sm font-medium"
            >
              Profil
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
    <div class="max-w-7xl mx-auto py-6 sm:px-6 lg:px-8">
      <div class="flex justify-between items-center mb-6">
        <h2 class="text-2xl font-bold text-gray-900">Gestion des véhicules</h2>
        <button 
          @click="showAddModal = true"
          class="btn-primary"
        >
          Ajouter un véhicule
        </button>
      </div>

      <!-- Error Message -->
      <div v-if="vehicleStore.error" class="mb-4 bg-red-50 border border-red-200 text-red-700 px-4 py-3 rounded-lg">
        {{ vehicleStore.error }}
      </div>

      <!-- Vehicles Grid -->
      <div v-if="vehicleStore.isLoading" class="text-center py-8">
        <p class="text-gray-500">Chargement des véhicules...</p>
      </div>

      <div v-else-if="vehicleStore.vehicles.length === 0" class="text-center py-8">
        <img 
          src="https://placehold.co/200x200?text=Aucun+véhicule+trouvé" 
          alt="Aucun véhicule" 
          class="mx-auto h-32 w-32 opacity-50"
          onerror="this.onerror=null;this.style.display='none';"
        />
        <p class="mt-4 text-gray-500">Aucun véhicule trouvé. Ajoutez votre premier véhicule.</p>
      </div>

      <div v-else class="grid grid-cols-1 md:grid-cols-2 lg:grid-cols-3 gap-6">
        <div 
          v-for="vehicle in vehicleStore.vehicles" 
          :key="vehicle.id"
          class="card hover:shadow-md transition-shadow cursor-pointer"
          @click="goToVehicleDetails(vehicle.id)"
        >
          <div class="flex items-start justify-between">
            <div class="flex-1">
              <h3 class="text-lg font-semibold text-gray-900">{{ vehicle.name }}</h3>
              <div class="mt-2 space-y-1">
                <div class="flex items-center">
                  <span class="text-sm text-gray-500">Statut:</span>
                  <span 
                    :class="vehicle.isOnline ? 'text-green-600' : 'text-red-600'"
                    class="ml-2 text-sm font-medium"
                  >
                    {{ vehicle.isOnline ? 'En ligne' : 'Hors ligne' }}
                  </span>
                </div>
                <div class="flex items-center">
                  <span class="text-sm text-gray-500">Vitesse:</span>
                  <span class="ml-2 text-sm">{{ vehicle.speed }} km/h</span>
                </div>
                <div class="flex items-center">
                  <span class="text-sm text-gray-500">Altitude:</span>
                  <span class="ml-2 text-sm">{{ vehicle.altitude }} m</span>
                </div>
                <div class="flex items-center">
                  <span class="text-sm text-gray-500">Poids:</span>
                  <span class="ml-2 text-sm">{{ vehicle.weight }} kg</span>
                </div>
              </div>
            </div>
            
            <div class="flex flex-col space-y-2">
              <button 
                @click.stop="editVehicle(vehicle)"
                class="text-primary-600 hover:text-primary-700 text-sm"
              >
                Modifier
              </button>
              <button 
                @click.stop="deleteVehicleConfirm(vehicle)"
                class="text-red-600 hover:text-red-700 text-sm"
              >
                Supprimer
              </button>
            </div>
          </div>
          
          <div class="mt-4 pt-4 border-t border-gray-200">
            <p class="text-xs text-gray-500">
              Dernière mise à jour: {{ formatTime(vehicle.lastUpdate) }}
            </p>
          </div>
        </div>
      </div>

      <!-- Pagination -->
      <div v-if="vehicleStore.totalPages > 1" class="mt-8 flex justify-center">
        <nav class="flex space-x-2">
          <button 
            @click="changePage(vehicleStore.currentPage - 1)"
            :disabled="vehicleStore.currentPage === 0"
            class="px-3 py-2 text-sm font-medium text-gray-500 bg-white border border-gray-300 rounded-md hover:bg-gray-50 disabled:opacity-50 disabled:cursor-not-allowed"
          >
            Précédent
          </button>
          
          <button 
            v-for="page in visiblePages" 
            :key="page"
            @click="changePage(page)"
            :class="page === vehicleStore.currentPage ? 'bg-primary-600 text-white' : 'bg-white text-gray-500 hover:bg-gray-50'"
            class="px-3 py-2 text-sm font-medium border border-gray-300 rounded-md"
          >
            {{ page + 1 }}
          </button>
          
          <button 
            @click="changePage(vehicleStore.currentPage + 1)"
            :disabled="vehicleStore.currentPage >= vehicleStore.totalPages - 1"
            class="px-3 py-2 text-sm font-medium text-gray-500 bg-white border border-gray-300 rounded-md hover:bg-gray-50 disabled:opacity-50 disabled:cursor-not-allowed"
          >
            Suivant
          </button>
        </nav>
      </div>
    </div>

    <!-- Add Vehicle Modal -->
    <div v-if="showAddModal" class="fixed inset-0 bg-gray-600 bg-opacity-50 overflow-y-auto h-full w-full z-50">
      <div class="relative top-20 mx-auto p-5 border w-96 shadow-lg rounded-md bg-white">
        <div class="mt-3">
          <h3 class="text-lg font-medium text-gray-900 mb-4">Ajouter un véhicule</h3>
          
          <form @submit.prevent="handleAddVehicle" class="space-y-4">
            <div>
              <label class="form-label">Nom du véhicule</label>
              <input
                v-model="newVehicle.name"
                type="text"
                required
                class="form-input"
                placeholder="Ex: Véhicule V001"
              />
            </div>
            
            <div class="grid grid-cols-2 gap-4">
              <div>
                <label class="form-label">Latitude</label>
                <input
                  v-model.number="newVehicle.latitude"
                  type="number"
                  step="any"
                  required
                  class="form-input"
                  placeholder="48.8566"
                />
              </div>
              <div>
                <label class="form-label">Longitude</label>
                <input
                  v-model.number="newVehicle.longitude"
                  type="number"
                  step="any"
                  required
                  class="form-input"
                  placeholder="2.3522"
                />
              </div>
            </div>
            
            <div class="grid grid-cols-3 gap-4">
              <div>
                <label class="form-label">Vitesse (km/h)</label>
                <input
                  v-model.number="newVehicle.speed"
                  type="number"
                  min="0"
                  required
                  class="form-input"
                  placeholder="0"
                />
              </div>
              <div>
                <label class="form-label">Altitude (m)</label>
                <input
                  v-model.number="newVehicle.altitude"
                  type="number"
                  required
                  class="form-input"
                  placeholder="0"
                />
              </div>
              <div>
                <label class="form-label">Poids (kg)</label>
                <input
                  v-model.number="newVehicle.weight"
                  type="number"
                  min="0"
                  required
                  class="form-input"
                  placeholder="1000"
                />
              </div>
            </div>
            
            <div class="flex items-center">
              <input
                v-model="newVehicle.isOnline"
                type="checkbox"
                class="h-4 w-4 text-primary-600 focus:ring-primary-500 border-gray-300 rounded"
              />
              <label class="ml-2 block text-sm text-gray-900">
                Véhicule en ligne
              </label>
            </div>
            
            <div class="flex justify-end space-x-3 pt-4">
              <button
                type="button"
                @click="showAddModal = false"
                class="btn-secondary"
              >
                Annuler
              </button>
              <button
                type="submit"
                :disabled="vehicleStore.isLoading"
                class="btn-primary"
              >
                {{ vehicleStore.isLoading ? 'Ajout...' : 'Ajouter' }}
              </button>
            </div>
          </form>
        </div>
      </div>
    </div>
  </div>
</template>

<script setup lang="ts">
import { onMounted, computed, ref, reactive } from 'vue'
import { useRouter } from 'vue-router'
import { useAuthStore } from '@/stores/auth'
import { useVehicleStore, type Vehicle } from '@/stores/vehicles'

const router = useRouter()
const authStore = useAuthStore()
const vehicleStore = useVehicleStore()

const showAddModal = ref(false)
const newVehicle = reactive({
  name: '',
  latitude: 48.8566,
  longitude: 2.3522,
  speed: 0,
  altitude: 0,
  weight: 1000,
  isOnline: true
})

const visiblePages = computed(() => {
  const current = vehicleStore.currentPage
  const total = vehicleStore.totalPages
  const pages = []
  
  const start = Math.max(0, current - 2)
  const end = Math.min(total - 1, current + 2)
  
  for (let i = start; i <= end; i++) {
    pages.push(i)
  }
  
  return pages
})

const changePage = async (page: number) => {
  if (page >= 0 && page < vehicleStore.totalPages) {
    await vehicleStore.fetchVehicles(page, 10)
  }
}

const handleAddVehicle = async () => {
  const result = await vehicleStore.addVehicle(newVehicle)
  
  if (result.success) {
    showAddModal.value = false
    // Reset form
    Object.assign(newVehicle, {
      name: '',
      latitude: 48.8566,
      longitude: 2.3522,
      speed: 0,
      altitude: 0,
      weight: 1000,
      isOnline: true
    })
  }
}

const editVehicle = (vehicle: Vehicle) => {
  // TODO: Implement edit modal
  console.log('Edit vehicle:', vehicle)
}

const deleteVehicleConfirm = async (vehicle: Vehicle) => {
  if (confirm(`Êtes-vous sûr de vouloir supprimer le véhicule "${vehicle.name}" ?`)) {
    await vehicleStore.deleteVehicle(vehicle.id)
  }
}

const goToVehicleDetails = (vehicleId: number) => {
  router.push(`/vehicles/${vehicleId}`)
}

const handleLogout = () => {
  authStore.logout()
  router.push('/login')
}

const formatTime = (date: Date) => {
  return new Intl.DateTimeFormat('fr-FR', {
    hour: '2-digit',
    minute: '2-digit',
    day: '2-digit',
    month: '2-digit',
    year: 'numeric'
  }).format(date)
}

onMounted(async () => {
  // Initialize mock data for development
  vehicleStore.initializeMockData()
  await vehicleStore.fetchVehicles(0, 10)
})
</script>
