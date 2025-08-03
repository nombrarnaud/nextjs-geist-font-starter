<template>
  <div id="app" class="min-h-screen bg-gray-50">
    <router-view />
  </div>
</template>

<script setup lang="ts">
import { onMounted } from 'vue'
import { useAuthStore } from './stores/auth'

const authStore = useAuthStore()

onMounted(() => {
  // Check for remembered session on app load
  authStore.checkRememberedSession()
})
</script>

<style scoped>
#app {
  font-family: 'Inter', system-ui, sans-serif;
}
</style>
