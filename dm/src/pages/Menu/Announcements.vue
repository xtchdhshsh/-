<script setup>
import { ref, onMounted } from 'vue'

const dataList = ref([])

const fetchData = async () => {
  try {
    dataList.value = []
  } catch (err) {
    console.error('加载公告数据失败', err)
  }
}

onMounted(() => {
  fetchData()
})
</script>

<template>
  <div class="page">
    <h2>课程公告</h2>
    <div v-if="dataList.length === 0">暂无数据</div>
    <ul v-else>
      <li v-for="(item, index) in dataList" :key="index">{{ item }}</li>
    </ul>
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
