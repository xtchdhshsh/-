<script setup>
import { ref, onMounted } from 'vue'
import axios from '@/utlis/axios'
import { ArrowLeft } from '@element-plus/icons-vue'
import { useRoute, useRouter } from 'vue-router'

const route = useRoute()
const router = useRouter()
const goBack = () => {
  router.back()  // 前往 /Menu/Student
}

const courses = ref([])
const experimentName = ref('')

const parseRawData = (str) => {
  str = str.trim().slice(2, -2)
  const rows = str.split('], [')

  return rows.map(row => {
    const parts = row.split(',').map(p => p.trim())
    return {
      id: parts[0],
      className: parts[1],
      score: parts[2]
    }
  })
}

const getMarks = async () => {

  try {
    const res = await axios.get('/getmark', {
      params: { experiment: experimentName.value },
      headers: { token: localStorage.getItem('token') }
    })

    let raw = res.data

    // console.log(raw)

    if (typeof raw === 'string') {
      courses.value = parseRawData(raw)
    } else {
    //   console.warn('返回数据不是字符串:', raw)
      courses.value = []
    }

  } catch (err) {
    console.error('获取成绩数据失败:', err)
    courses.value = []
  }
}

onMounted(() => {
  experimentName.value = route.query.experiment
  getMarks()
})
</script>

<template>
  <div class="page">
    <div 
      style="display: flex; align-items: center; margin-bottom: 16px; cursor: pointer; color: #409EFF;" 
      @click="goBack"
    >
      <el-icon :size="16"><ArrowLeft /></el-icon>
      <span style="margin-left: 2px; font-size: 16px;">返回</span>
    </div>

    <h2>{{ experimentName }} —— 学生数据</h2>

    <el-table :data="courses" border style="margin-top: 20px">
      <el-table-column type="index" label="序号" width="55" />
      <el-table-column prop="id" label="学号" />
      <el-table-column prop="className" label="班级" />
      <el-table-column prop="score" label="成绩" />
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
