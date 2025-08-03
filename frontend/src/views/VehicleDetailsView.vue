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
    <div class="max-w-7xl mx-auto py-6 sm:px-6 lg:px-8">
      <div v-if="vehicleStore.isLoading" class="text-center py-8">
        <p class="text-gray-500">Chargement des détails du véhicule...</p>
      </div>

      <div v-else-if="!vehicle" class="text-center py-8">
        <p class="text-red-500">Véhicule non trouvé</p>
        <router-link to="/vehicles" class="mt-4 btn-primary inline-block">
          Retour à la liste
        </router-link>
      </div>

      <div v-else class="space-y-6">
        <!-- Vehicle Header -->
        <div class="flex justify-between items-start">
          <div>
            <h2 class="text-3xl font-bold text-gray-900">{{ vehicle.name }}</h2>
            <div class="mt-2 flex items-center space-x-4">
              <span 
                :class="vehicle.isOnline ? 'bg-green-100 text-green-800' : 'bg-red-100 text-red-800'"
                class="inline-flex items-center px-2.5 py-0.5 rounded-full text-xs font-medium"
              >
                {{ vehicle.isOnline ? 'En ligne' : 'Hors ligne' }}
              </span>
              <span class="text-sm text-gray-500">
                Dernière mise à jour: {{ formatTime(vehicle.lastUpdate) }}
              </span>
            </div>
          </div>
          
          <router-link to="/vehicles" class="btn-secondary">
            Retour à la liste
          </router-link>
        </div>

        <!-- Current Metrics -->
        <div class="grid grid-cols-1 md:grid-cols-4 gap-6">
          <div class="card">
            <div class="flex items-center">
              <div class="flex-shrink-0">
                <div class="w-8 h-8 bg-blue-100 rounded-full flex items-center justify-center">
                  <span class="text-blue-600 font-semibold">⚡</span>
                </div>
              </div>
              <div class="ml-4">
                <p class="text-sm font-medium text-gray-500">Vitesse</p>
                <p class="text-2xl font-semibold text-gray-900">{{ vehicle.speed }} km/h</p>
              </div>
            </div>
          </div>
          
          <div class="card">
            <div class="flex items-center">
              <div class="flex-shrink-0">
                <div class="w-8 h-8 bg-green-100 rounded-full flex items-center justify-center">
                  <span class="text-green-600 font-semibold">⛰️</span>
                </div>
              </div>
              <div class="ml-4">
                <p class="text-sm font-medium text-gray-500">Altitude</p>
                <p class="text-2xl font-semibold text-gray-900">{{ vehicle.altitude }} m</p>
              </div>
            </div>
          </div>
          
          <div class="card">
            <div class="flex items-center">
              <div class="flex-shrink-0">
                <div class="w-8 h-8 bg-yellow-100 rounded-full flex items-center justify-center">
                  <span class="text-yellow-600 font-semibold">⚖️</span>
                </div>
              </div>
              <div class="ml-4">
                <p class="text-sm font-medium text-gray-500">Poids</p>
                <p class="text-2xl font-semibold text-gray-900">{{ vehicle.weight }} kg</p>
              </div>
            </div>
          </div>
          
          <div class="card">
            <div class="flex items-center">
              <div class="flex-shrink-0">
                <div class="w-8 h-8 bg-purple-100 rounded-full flex items-center justify-center">
                  <span class="text-purple-600 font-semibold">📍</span>
                </div>
              </div>
              <div class="ml-4">
                <p class="text-sm font-medium text-gray-500">Distance totale</p>
                <p class="text-2xl font-semibold text-gray-900">{{ vehicle.totalDistance || 0 }} km</p>
              </div>
            </div>
          </div>
        </div>

        <!-- Current Location Map -->
        <div class="card">
          <div class="flex justify-between items-center mb-4">
            <h3 class="text-lg font-semibold text-gray-900">Position actuelle</h3>
            <div class="text-sm text-gray-500">
              {{ vehicle.latitude.toFixed(6) }}, {{ vehicle.longitude.toFixed(6) }}
            </div>
          </div>
          
          <div class="h-64 bg-gray-100 rounded-lg overflow-hidden">
            <div id="current-map" class="w-full h-full"></div>
          </div>
        </div>

        <!-- Movement History -->
        <div class="card">
          <div class="flex justify-between items-center mb-4">
            <h3 class="text-lg font-semibold text-gray-900">Historique des déplacements (5 derniers mois)</h3>
            <button 
              @click="refreshHistory"
              :disabled="vehicleStore.isLoading"
              class="btn-primary text-sm"
            >
              {{ vehicleStore.isLoading ? 'Actualisation...' : 'Actualiser' }}
            </button>
          </div>
          
          <div class="h-96 bg-gray-100 rounded-lg overflow-hidden mb-6">
            <div id="history-map" class="w-full h-full"></div>
          </div>

          <!-- History Timeline -->
          <div class="space-y-4">
            <h4 class="text-md font-medium text-gray-900">Historique détaillé</h4>
            
            <div v-if="vehicleStore.vehicleHistory.length === 0" class="text-center py-8">
              <p class="text-gray-500">Aucun historique disponible</p>
            </div>
            
            <div v-else class="space-y-3 max-h-64 overflow-y-auto">
              <div 
                v-for="entry in vehicleStore.vehicleHistory.slice(0, 20)" 
                :key="entry.id"
                class="flex items-center space-x-4 p-3 bg-gray-50 rounded-lg"
              >
                <div class="flex-shrink-0">
                  <div class="w-2 h-2 bg-primary-500 rounded-full"></div>
                </div>
                <div class="flex-1 grid grid-cols-2 md:grid-cols-5 gap-4 text-sm">
                  <div>
                    <span class="font-medium">{{ formatTime(entry.timestamp) }}</span>
                  </div>
                  <div>
                    <span class="text-gray-500">Position:</span>
                    <span class="ml-1">{{ entry.latitude.toFixed(4) }}, {{ entry.longitude.toFixed(4) }}</span>
                  </div>
                  <div>
                    <span class="text-gray-500">Vitesse:</span>
                    <span class="ml-1">{{ entry.speed }} km/h</span>
                  </div>
                  <div>
                    <span class="text-gray-500">Altitude:</span>
                    <span class="ml-1">{{ entry.altitude }} m</span>
                  </div>
                  <div>
                    <span class="text-gray-500">Poids:</span>
                    <span class="ml-1">{{ entry.weight }} kg</span>
                  </div>
                </div>
              </div>
            </div>
          </div>
        </div>
      </div>
    </div>
  </div>
</template>

<script setup lang="ts">
import { onMounted, computed, ref } from 'vue'
import { useRoute, useRouter } from 'vue-router'
import { useAuthStore } from '@/stores/auth'
import { useVehicleStore } from '@/stores/vehicles'
import L from 'leaflet'

const route = useRoute()
const router = useRouter()
const authStore = useAuthStore()
const vehicleStore = useVehicleStore()

const currentMap = ref<L.Map | null>(null)
const historyMap = ref<L.Map | null>(null)

const vehicleId = computed(() => parseInt(route.params.id as string))
const vehicle = computed(() => vehicleStore.getVehicleById(vehicleId.value))

const initCurrentMap = () => {
  if (!vehicle.value) return

  currentMap.value = L.map('current-map').setView([vehicle.value.latitude, vehicle.value.longitude], 15)

  L.tileLayer('https://{s}.tile.openstreetmap.org/{z}/{x}/{y}.png', {
    attribution: '© OpenStreetMap contributors'
  }).addTo(currentMap.value)

  // Add current position marker
  L.marker([vehicle.value.latitude, vehicle.value.longitude])
    .addTo(currentMap.value)
    .bindPopup(`
      <div class="p-2">
        <h4 class="font-semibold">${vehicle.value.name}</h4>
        <p class="text-sm">Position actuelle</p>
        <p class="text-xs text-gray-500">${formatTime(vehicle.value.lastUpdate)}</p>
      </div>
    `)
}

const initHistoryMap = () => {
  if (!vehicle.value) return

  historyMap.value = L.map('history-map').setView([vehicle.value.latitude, vehicle.value.longitude], 12)

  L.tileLayer('https://{s}.tile.openstreetmap.org/{z}/{x}/{y}.png', {
    attribution: '© OpenStreetMap contributors'
  }).addTo(historyMap.value)

  // Generate mock history data for demonstration
  const mockHistory = generateMockHistory()
  
  if (mockHistory.length > 0) {
    // Create polyline for the path
    const coordinates = mockHistory.map(h => [h.latitude, h.longitude] as [number, number])
    L.polyline(coordinates, { color: '#0ea5e9', weight: 3 }).addTo(historyMap.value)

    // Add markers for significant points
    mockHistory.forEach((entry, index) => {
      if (index % 5 === 0) { // Show every 5th point to avoid clutter
        L.marker([entry.latitude, entry.longitude])
          .addTo(historyMap.value!)
          .bindPopup(`
            <div class="p-2">
              <p class="text-sm font-semibold">${formatTime(entry.timestamp)}</p>
              <p class="text-xs">Vitesse: ${entry.speed} km/h</p>
              <p class="text-xs">Altitude: ${entry.altitude} m</p>
            </div>
          `)
      }
    })

    // Fit map to show all points
    const group = new L.FeatureGroup(historyMap.value.getLayers())
    historyMap.value.fitBounds(group.getBounds().pad(0.1))
  }
}

const generateMockHistory = () => {
  if (!vehicle.value) return []

  const history = []
  const now = new Date()
  const baseLatitude = vehicle.value.latitude
  const baseLongitude = vehicle.value.longitude

  // Generate 50 points over the last 5 months
  for (let i = 0; i < 50; i++) {
    const daysAgo = Math.floor(Math.random() * 150) // Random day in last 5 months
    const timestamp = new Date(now.getTime() - daysAgo * 24 * 60 * 60 * 1000)
    
    // Random movement within a reasonable area
    const latOffset = (Math.random() - 0.5) * 0.1
    const lngOffset = (Math.random() - 0.5) * 0.1
    
    history.push({
      id: i + 1,
      vehicleId: vehicle.value.id,
      timestamp,
      latitude: baseLatitude + latOffset,
      longitude: baseLongitude + lngOffset,
      speed: Math.floor(Math.random() * 80),
      altitude: vehicle.value.altitude + Math.floor((Math.random() - 0.5) * 20),
      weight: vehicle.value.weight + Math.floor((Math.random() - 0.5) * 100)
    })
  }

  // Sort by timestamp (oldest first)
  return history.sort((a, b) => a.timestamp.getTime() - b.timestamp.getTime())
}

const refreshHistory = async () => {
  await vehicleStore.fetchVehicleHistory(vehicleId.value, 5)
  
  // Reinitialize history map with new data
  setTimeout(() => {
    if (historyMap.value) {
      historyMap.value.remove()
      initHistoryMap()
    }
  }, 100)
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
  // Initialize mock data if not already loaded
  if (vehicleStore.vehicles.length === 0) {
    vehicleStore.initializeMockData()
  }

  // Load vehicle history
  await vehicleStore.fetchVehicleHistory(vehicleId.value, 5)

  // Initialize maps after DOM is ready
  setTimeout(() => {
    initCurrentMap()
    initHistoryMap()
  }, 100)
})
</script>

<style scoped>
#current-map,
#history-map {
  z-index: 1;
}
</style>
