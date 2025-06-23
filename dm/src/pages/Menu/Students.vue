<script setup>
import { ref, onMounted } from 'vue'
import axios from '@/utlis/axios'
import { useRoute, useRouter } from 'vue-router'

const route = useRoute()
const router = useRouter()

// 数据
const courses = ref([])
const selectedClasses = ref([])

// 请求实验列表
const getExperiments = async () => {
  try {
    const res = await axios.get('/getexperiment', {
      headers: { token: localStorage.getItem('token') }
    })

    let raw = res.data

    if (typeof raw === 'string' && raw.startsWith('[') && raw.endsWith(']')) {
      courses.value = raw.slice(1, -1).split(',').map((s, i) => ({
        id: i + 1,
        name: s.trim()
      }))
    } else if (Array.isArray(raw)) {
      courses.value = raw
    } else {
      courses.value = []
    }
  } catch (error) {
    console.error('获取实验列表失败:', error)
  }
}

const enterExperiment = (experiment) => {
  router.push({
    path: '/Menu/InnerViews/Experiment',
    query: { experiment }
  })
  console.log(experiment);
}

onMounted(() => {
  getExperiments()
})
</script>

<template>
  <div class="page">
    <h2>学生实验管理</h2>

    <el-table 
      :data="courses"
      row-key="id"
      border 
      style="margin-top: 20px"
      @selection-change="val => (selectedClasses.value = val)"
    >
      <el-table-column type="selection" width="45" />

      <el-table-column type="index" label="序号" width="55" />

      <el-table-column prop="name" label="实验名称" />

      <el-table-column label="操作">
        <template #default="{ row }">
          <el-button type="primary" @click="enterExperiment(row.name)">进入</el-button>
        </template>
      </el-table-column>
    </el-table>
  </div>
</template>

<style scoped>
.page {
  padding: 0 20px;
  max-width: 1200px;
}
h2 {
  font-size: 32px;
  margin-bottom: 22px;
}
</style>
