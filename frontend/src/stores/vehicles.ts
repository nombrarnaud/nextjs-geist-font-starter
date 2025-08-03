import { defineStore } from 'pinia'
import { ref, computed } from 'vue'
import { apiService } from '@/services/api'

export interface Vehicle {
  id: number
  name: string
  latitude: number
  longitude: number
  speed: number
  altitude: number
  weight: number
  isOnline: boolean
  lastUpdate: Date
  totalDistance?: number
  userId: number
}

export interface VehicleHistory {
  id: number
  vehicleId: number
  timestamp: Date
  latitude: number
  longitude: number
  speed: number
  altitude: number
  weight: number
}

export interface PaginatedVehicles {
  vehicles: Vehicle[]
  totalPages: number
  currentPage: number
  totalElements: number
}

export const useVehicleStore = defineStore('vehicles', () => {
  const vehicles = ref<Vehicle[]>([])
  const vehicleHistory = ref<VehicleHistory[]>([])
  const isLoading = ref(false)
  const error = ref<string | null>(null)
  const currentPage = ref(0)
  const totalPages = ref(0)
  const totalElements = ref(0)

  const onlineVehicles = computed(() => vehicles.value.filter(v => v.isOnline))
  const offlineVehicles = computed(() => vehicles.value.filter(v => !v.isOnline))

  const fetchVehicles = async (page: number = 0, size: number = 10) => {
    isLoading.value = true
    error.value = null
    
    try {
      const response = await apiService.get(`/api/vehicles?page=${page}&size=${size}`)
      const data: PaginatedVehicles = response.data
      
      vehicles.value = data.vehicles.map(v => ({
        ...v,
        lastUpdate: new Date(v.lastUpdate)
      }))
      
      currentPage.value = data.currentPage
      totalPages.value = data.totalPages
      totalElements.value = data.totalElements
      
      return { success: true }
    } catch (err: any) {
      error.value = err.response?.data?.message || 'Erreur lors du chargement des véhicules'
      return { success: false, error: error.value }
    } finally {
      isLoading.value = false
    }
  }

  const fetchVehicleHistory = async (vehicleId: number, months: number = 5) => {
    isLoading.value = true
    error.value = null
    
    try {
      const response = await apiService.get(`/api/vehicles/${vehicleId}/history?months=${months}`)
      vehicleHistory.value = response.data.map((h: any) => ({
        ...h,
        timestamp: new Date(h.timestamp)
      }))
      
      return { success: true }
    } catch (err: any) {
      error.value = err.response?.data?.message || 'Erreur lors du chargement de l\'historique'
      return { success: false, error: error.value }
    } finally {
      isLoading.value = false
    }
  }

  const addVehicle = async (vehicleData: Omit<Vehicle, 'id' | 'lastUpdate' | 'userId'>) => {
    isLoading.value = true
    error.value = null
    
    try {
      const response = await apiService.post('/api/vehicles', vehicleData)
      const newVehicle: Vehicle = {
        ...response.data,
        lastUpdate: new Date(response.data.lastUpdate)
      }
      
      vehicles.value.push(newVehicle)
      return { success: true, vehicle: newVehicle }
    } catch (err: any) {
      error.value = err.response?.data?.message || 'Erreur lors de l\'ajout du véhicule'
      return { success: false, error: error.value }
    } finally {
      isLoading.value = false
    }
  }

  const updateVehicle = async (vehicleId: number, vehicleData: Partial<Vehicle>) => {
    isLoading.value = true
    error.value = null
    
    try {
      const response = await apiService.put(`/api/vehicles/${vehicleId}`, vehicleData)
      const updatedVehicle: Vehicle = {
        ...response.data,
        lastUpdate: new Date(response.data.lastUpdate)
      }
      
      const index = vehicles.value.findIndex(v => v.id === vehicleId)
      if (index !== -1) {
        vehicles.value[index] = updatedVehicle
      }
      
      return { success: true, vehicle: updatedVehicle }
    } catch (err: any) {
      error.value = err.response?.data?.message || 'Erreur lors de la mise à jour du véhicule'
      return { success: false, error: error.value }
    } finally {
      isLoading.value = false
    }
  }

  const deleteVehicle = async (vehicleId: number) => {
    isLoading.value = true
    error.value = null
    
    try {
      await apiService.delete(`/api/vehicles/${vehicleId}`)
      vehicles.value = vehicles.value.filter(v => v.id !== vehicleId)
      
      return { success: true }
    } catch (err: any) {
      error.value = err.response?.data?.message || 'Erreur lors de la suppression du véhicule'
      return { success: false, error: error.value }
    } finally {
      isLoading.value = false
    }
  }

  const getVehicleById = (id: number) => {
    return vehicles.value.find(v => v.id === id)
  }

  // Simulate real-time updates (in a real app, this would be WebSocket or polling)
  const startRealTimeUpdates = () => {
    setInterval(async () => {
      if (vehicles.value.length > 0) {
        // Simulate position updates for online vehicles
        vehicles.value.forEach(vehicle => {
          if (vehicle.isOnline) {
            // Small random movement
            vehicle.latitude += (Math.random() - 0.5) * 0.001
            vehicle.longitude += (Math.random() - 0.5) * 0.001
            vehicle.speed = Math.max(0, vehicle.speed + (Math.random() - 0.5) * 10)
            vehicle.lastUpdate = new Date()
          }
        })
      }
    }, 30000) // Update every 30 seconds
  }

  // Initialize with mock data for development
  const initializeMockData = () => {
    vehicles.value = [
      {
        id: 1,
        name: 'Véhicule V001',
        latitude: 48.8566,
        longitude: 2.3522,
        speed: 45,
        altitude: 35,
        weight: 1200,
        isOnline: true,
        lastUpdate: new Date(),
        totalDistance: 1250,
        userId: 1
      },
      {
        id: 2,
        name: 'Véhicule V002',
        latitude: 48.8606,
        longitude: 2.3376,
        speed: 0,
        altitude: 42,
        weight: 1500,
        isOnline: true,
        lastUpdate: new Date(Date.now() - 5 * 60 * 1000),
        totalDistance: 890,
        userId: 1
      },
      {
        id: 3,
        name: 'Véhicule V003',
        latitude: 48.8738,
        longitude: 2.2950,
        speed: 60,
        altitude: 38,
        weight: 1100,
        isOnline: false,
        lastUpdate: new Date(Date.now() - 30 * 60 * 1000),
        totalDistance: 2100,
        userId: 1
      }
    ]
    
    totalElements.value = vehicles.value.length
    totalPages.value = 1
    currentPage.value = 0
  }

  return {
    vehicles,
    vehicleHistory,
    isLoading,
    error,
    currentPage,
    totalPages,
    totalElements,
    onlineVehicles,
    offlineVehicles,
    fetchVehicles,
    fetchVehicleHistory,
    addVehicle,
    updateVehicle,
    deleteVehicle,
    getVehicleById,
    startRealTimeUpdates,
    initializeMockData
  }
})
