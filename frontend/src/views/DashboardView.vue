<template>
  <div class="min-h-screen bg-gray-50">
    <!-- Navigation Header -->
    <nav class="bg-white shadow-sm border-b border-gray-200">
      <div class="max-w-7xl mx-auto px-4 sm:px-6 lg:px-8">
        <div class="flex justify-between h-16">
          <div class="flex items-center">
            <img 
              src="https://placehold.co/40x40?text=GPS" 
              alt="GPS Tracker" 
              class="h-8 w-8"
              onerror="this.onerror=null;this.style.display='none';"
            />
            <h1 class="ml-3 text-xl font-semibold text-gray-900">GPS Tracker</h1>
          </div>
          
          <div class="flex items-center space-x-4">
            <span class="text-sm text-gray-700">
              Bonjour, {{ authStore.user?.fullName }}
            </span>
            <router-link 
              to="/vehicles" 
              class="text-gray-600 hover:text-gray-900 px-3 py-2 rounded-md text-sm font-medium"
            >
              Véhicules
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
      <!-- Stats Cards -->
      <div class="grid grid-cols-1 md:grid-cols-4 gap-6 mb-6">
        <div class="card">
          <div class="flex items-center">
            <div class="flex-shrink-0">
              <div class="w-8 h-8 bg-primary-100 rounded-full flex items-center justify-center">
                <span class="text-primary-600 font-semibold">V</span>
              </div>
            </div>
            <div class="ml-4">
              <p class="text-sm font-medium text-gray-500">Véhicules actifs</p>
              <p class="text-2xl font-semibold text-gray-900">{{ vehicleStore.vehicles.length }}</p>
            </div>
          </div>
        </div>
        
        <div class="card">
          <div class="flex items-center">
            <div class="flex-shrink-0">
              <div class="w-8 h-8 bg-green-100 rounded-full flex items-center justify-center">
                <span class="text-green-600 font-semibold">✓</span>
              </div>
            </div>
            <div class="ml-4">
              <p class="text-sm font-medium text-gray-500">En ligne</p>
              <p class="text-2xl font-semibold text-gray-900">{{ onlineVehicles }}</p>
            </div>
          </div>
        </div>
        
        <div class="card">
          <div class="flex items-center">
            <div class="flex-shrink-0">
              <div class="w-8 h-8 bg-yellow-100 rounded-full flex items-center justify-center">
                <span class="text-yellow-600 font-semibold">⚡</span>
              </div>
            </div>
            <div class="ml-4">
              <p class="text-sm font-medium text-gray-500">Vitesse moy.</p>
              <p class="text-2xl font-semibold text-gray-900">{{ averageSpeed }} km/h</p>
            </div>
          </div>
        </div>
        
        <div class="card">
          <div class="flex items-center">
            <div class="flex-shrink-0">
              <div class="w-8 h-8 bg-blue-100 rounded-full flex items-center justify-center">
                <span class="text-blue-600 font-semibold">📍</span>
              </div>
            </div>
            <div class="ml-4">
              <p class="text-sm font-medium text-gray-500">Distance totale</p>
              <p class="text-2xl font-semibold text-gray-900">{{ totalDistance }} km</p>
            </div>
          </div>
        </div>
      </div>

      <!-- Map Container -->
      <div class="card">
        <div class="flex justify-between items-center mb-4">
          <h2 class="text-lg font-semibold text-gray-900">Localisation des véhicules</h2>
          <button 
            @click="refreshVehicles"
            :disabled="vehicleStore.isLoading"
            class="btn-primary text-sm"
          >
            {{ vehicleStore.isLoading ? 'Actualisation...' : 'Actualiser' }}
          </button>
        </div>
        
        <div class="h-96 bg-gray-100 rounded-lg overflow-hidden">
          <div id="map" class="w-full h-full"></div>
        </div>
      </div>

      <!-- Recent Activity -->
      <div class="mt-6 card">
        <h3 class="text-lg font-semibold text-gray-900 mb-4">Activité récente</h3>
        <div class="space-y-3">
          <div v-for="activity in recentActivity" :key="activity.id" class="flex items-center space-x-3">
            <div class="flex-shrink-0">
              <div class="w-2 h-2 bg-primary-500 rounded-full"></div>
            </div>
            <div class="flex-1">
              <p class="text-sm text-gray-900">{{ activity.message }}</p>
              <p class="text-xs text-gray-500">{{ formatTime(activity.timestamp) }}</p>
            </div>
          </div>
        </div>
      </div>
    </div>
  </div>
</template>

<script setup lang="ts">
import { onMounted, computed, ref } from 'vue'
import { useRouter } from 'vue-router'
import { useAuthStore } from '@/stores/auth'
import { useVehicleStore } from '@/stores/vehicles'
import L from 'leaflet'

const router = useRouter()
const authStore = useAuthStore()
const vehicleStore = useVehicleStore()

const map = ref<L.Map | null>(null)
const recentActivity = ref([
  {
    id: 1,
    message: 'Véhicule V001 a démarré son trajet',
    timestamp: new Date(Date.now() - 5 * 60 * 1000)
  },
  {
    id: 2,
    message: 'Véhicule V002 a atteint sa destination',
    timestamp: new Date(Date.now() - 15 * 60 * 1000)
  },
  {
    id: 3,
    message: 'Véhicule V003 en stationnement prolongé',
    timestamp: new Date(Date.now() - 30 * 60 * 1000)
  }
])

const onlineVehicles = computed(() => {
  return vehicleStore.vehicles.filter(v => v.isOnline).length
})

const averageSpeed = computed(() => {
  const speeds = vehicleStore.vehicles.map(v => v.speed).filter(s => s > 0)
  return speeds.length > 0 ? Math.round(speeds.reduce((a, b) => a + b, 0) / speeds.length) : 0
})

const totalDistance = computed(() => {
  return vehicleStore.vehicles.reduce((total, v) => total + (v.totalDistance || 0), 0)
})

const initMap = () => {
  // Initialize Leaflet map
  map.value = L.map('map').setView([48.8566, 2.3522], 10) // Paris center

  // Add OpenStreetMap tiles
  L.tileLayer('https://{s}.tile.openstreetmap.org/{z}/{x}/{y}.png', {
    attribution: '© OpenStreetMap contributors'
  }).addTo(map.value)

  // Add vehicle markers
  updateMapMarkers()
}

const updateMapMarkers = () => {
  if (!map.value) return

  vehicleStore.vehicles.forEach(vehicle => {
    const marker = L.marker([vehicle.latitude, vehicle.longitude])
      .addTo(map.value!)
      .bindPopup(`
        <div class="p-2">
          <h4 class="font-semibold">${vehicle.name}</h4>
          <p class="text-sm">Vitesse: ${vehicle.speed} km/h</p>
          <p class="text-sm">Altitude: ${vehicle.altitude} m</p>
          <p class="text-sm">Poids: ${vehicle.weight} kg</p>
          <p class="text-xs text-gray-500">Dernière mise à jour: ${formatTime(vehicle.lastUpdate)}</p>
        </div>
      `)

    // Custom marker color based on vehicle status
    const markerColor = vehicle.isOnline ? '#10b981' : '#ef4444'
    marker.getElement()?.style.setProperty('filter', `hue-rotate(${markerColor})`)
  })
}

const refreshVehicles = async () => {
  await vehicleStore.fetchVehicles()
  updateMapMarkers()
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
    month: '2-digit'
  }).format(date)
}

onMounted(async () => {
  await vehicleStore.fetchVehicles()
  
  // Initialize map after DOM is ready
  setTimeout(() => {
    initMap()
  }, 100)
})
</script>

<style scoped>
#map {
  z-index: 1;
}
</style>
