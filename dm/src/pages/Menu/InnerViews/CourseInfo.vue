<script setup>
import { ref, onMounted } from 'vue'
import { useRoute, useRouter } from 'vue-router'
import axios from '@/utlis/axios'
import { ArrowLeft } from '@element-plus/icons-vue'

const route = useRoute() // 路由信息
const router = useRouter() // 路由控制
const goBack = () => {
  router.back()  // 前往 /Menu/Courses
}

const students = ref([])
const studentName = ref('') // 添加学生学号
const selectedClass = ref('')
const selectedCourse = ref('')

// 获取学生数据
const fetchStudents = async () => {
  if (!selectedClass.value || !selectedCourse.value) {
    students.value = []
    return
  }

  try {
    const params = new URLSearchParams()
    params.append('classname', selectedCourse.value)
    params.append('cl', selectedClass.value)

    const res = await axios.post('/getstudent', params, {
      headers: { token: localStorage.getItem('token') }
    })

    // console.log(res.data)
    // console.log(typeof res.data)

    if (typeof res.data === 'object') {
      students.value = []
      return
    } else if (typeof res.data === 'string') {
      const rawData = res.data
        .replace(/[\[\]]/g, '') // 去掉所有中括号
        .split(',')             // 拆成 ["2024", "黄", "2022", "陈"]
        .reduce((acc, val, i, arr) => {
          if (i % 2 === 0) {
            acc.push([val.trim(), arr[i + 1].trim()])
          }
          return acc
        }, [])

      students.value = rawData.map(([id, name]) => ({
        id,
        name,
        class: selectedClass.value,
        course: selectedClass.value
      }))
    }
  } catch (err) {
    // console.log(err)
    ElMessage.error('获取学生列表失败')
  }
}

// 添加学生
// const joinCourse = async () => {
//   if (!studentName.value.trim()) {
//     ElMessage.warning('请填写学生学号')
//     return
//   }

//   try {
//     const res = await axios.post('/addstudent', null, {
//       headers: { token: localStorage.getItem('token') },
//       params: {
//         classname: selectedCourse.value,
//         studentname: studentName.value,
//         cl: selectedClass.value
//       }
//     })
//     if (res.data === 'success') {
//       ElMessage.success(`学生 ${studentName.value} 加入 ${selectedClass.value + '-' + selectedCourse.value} 成功`)
//       studentName.value = ''
//       await fetchStudents()
//     } else {
//       ElMessage.error(res.data || '添加学生失败')
//     }
//   } catch (err) {
//     ElMessage.error('添加学生请求出错')
//   }
// }


const deleteStudent = async (student) => {
  // console.log(student)
  try {
    const confirmed = window.confirm(`确定要删除学生 ${student.name} 吗？`)
    if (!confirmed) {
      return
    }

    const params = new URLSearchParams()
    params.append('classname', selectedCourse.value)
    params.append('cl', selectedClass.value)
    params.append('studentname', student.id)

    const res = await axios.post('/delstudent', params, {
      headers: { token: localStorage.getItem('token') }
    })
    
    // console.log(res.data)

    if (res.data === 'success') {
      ElMessage.success('删除成功')
      await fetchStudents()
    } else {
      ElMessage.error(res.data || '删除失败')
    }
  } catch (err) {
    ElMessage.error('删除请求失败')
  }
}

// 组件加载时，从路由 query 读取参数
onMounted(() => {
  const queryCourse = route.query.classname
  const queryClass = route.query.cl
  if (queryClass) {
    selectedClass.value = queryClass
    selectedCourse.value = queryCourse

    fetchStudents()
  }
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

    <p style="margin-bottom: 20px; font-size: 18px;">
      当前班级：<strong style="color: #409EFF; font-size: 24px;">{{ selectedClass + '-' + selectedCourse }}</strong>
    </p>

    <!-- <div style="display: flex; gap: 12px;">
      <el-form-item label="学生学号">
        <el-input v-model="studentName" placeholder="请输入学生学号" />
      </el-form-item>
      <el-form-item>
        <el-button type="success" @click="joinCourse">添加学生</el-button>
      </el-form-item>
    </div> -->

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
