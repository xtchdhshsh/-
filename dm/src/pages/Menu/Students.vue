<script setup>
import { ref, inject, onMounted } from 'vue'
import { useRoute } from 'vue-router'
import axios from '@/utlis/axios'


onMounted(() => {
})
</script>

<template>
  <div class="page">
    <h2>学生管理</h2>
  </div>
</template>

<style scoped>
.page {
  padding: 0 20px;
}
h2 {
  padding-bottom: 16px;
  font-size: 32px;
}
</style>
