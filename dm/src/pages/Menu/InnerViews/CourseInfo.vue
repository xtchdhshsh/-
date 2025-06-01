<script setup>
import { ref, inject, watch, onMounted } from 'vue'
import { useRoute, useRouter } from 'vue-router'
import axios from '@/utlis/axios'
import { ArrowLeft } from '@element-plus/icons-vue'

const route = useRoute() // 路由信息
const router = useRouter() // 路由控制
const goBack = () => {
  router.back()  // 前往 /Menu/Courses
}

const students = ref([])
const selectedClass = ref('')

// 获取学生数据
const fetchStudents = async () => {
  if (!selectedClass.value) {
        students.value = []
        return
  }

  try {
    const res = await axios.post(`/getstudent?classname=${encodeURIComponent(selectedClass.value)}`, {}, {
        headers: { token: localStorage.getItem('token') }
    })
    const rawData = res.data || []

    students.value = rawData.map((stu) => ({
        id: stu,
        name: stu,
        class: selectedClass.value,
        course: selectedClass.value
    }))
  } catch (err) {
        ElMessage.error('获取学生列表失败')
  }
}

// 删除学生
const deleteStudent = async (student) => {
  try {
    const confirmed = window.confirm(`确定要删除学生 ${student.name} 吗？`)
    if (!confirmed) {
      return
    }

    const res = await axios.post('/deletestudent', null, {
      headers: { token: localStorage.getItem('token') },
      params: {
        classname: selectedClass.value,
        studentname: student.name,
      },
    })

    if (res.data === 'success') {
      ElMessage.success('删除成功')
      fetchStudents()
    } else {
      ElMessage.error(res.data || '删除失败')
    }
  } catch (err) {
    ElMessage.error('删除请求失败')
  }
}

watch(selectedClass, () => {
    fetchStudents()
})

// 组件加载时，从路由 query 读取参数
onMounted(() => {
  const queryClass = route.query.classname
  if (queryClass) {
      selectedClass.value = queryClass
  }
})
</script>

<template>
  <div class="page">
    <div 
      style="display: flex; align-items: center; margin-bottom: 16px; cursor: pointer; color: #409EFF;" 
      @click="goBack">
      <el-icon :size="16"><ArrowLeft /></el-icon>
      <span style="margin-left: 2px; font-size: 16px;">返回</span>
    </div>

    <p style="margin-bottom: 20px; font-size: 18px;">
        当前班级：<strong style="color: #409EFF; font-size: 24px;">{{ selectedClass }}</strong>
    </p>

    <el-table :data="students" border stripe style="width: 100%; max-width: 1200px;">
        <el-table-column prop="name" label="姓名" />
        <el-table-column prop="id" label="学号" sortable />
        <el-table-column label="操作" width="80px">
          <template #default="{ row }">
            <el-button 
              type="danger" 
              size="small" 
              @click="deleteStudent(row)"
            >
              删除
            </el-button>
          </template>
        </el-table-column>
    </el-table>
  </div>
</template>

<style scoped>
.page {
    padding: 0 20px;
}
</style>
