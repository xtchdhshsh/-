<script setup>
import { ref, inject, watch } from 'vue'
import axios from '@/utlis/axios'

const courses = inject('courses')
const fetchCourses = inject('fetchCourses')

// 添加课程
const newClassName = ref('')
const addCourse = async () => {
  if (!newClassName.value.trim()) {
    ElMessage.warning('请输入课程名')
    return
  }
  try {
    const res = await axios.post('/addclass', null, {
      headers: { token: localStorage.getItem('token') },
      params: { classname: newClassName.value }
    })
    if (res.data === 'success') {
      ElMessage.success('添加成功')
      newClassName.value = ''
      await fetchCourses()
    } else {
      ElMessage.error('添加失败')
    }
  } catch (err) {
    ElMessage.error('添加课程失败')
  }
}

// 加入课程
const studentName = ref('') // 学号
const loadingMap = ref({}) // 防止按钮重复点击
const joinCourse = async (classname) => {
  if (!studentName.value.trim()) {
    ElMessage.warning('请先填写学生学号')
    return
  }

  loadingMap.value[classname] = true // 按钮 loading

  try {
    const res = await axios.post('/addstudent', null, {
      headers: { token: localStorage.getItem('token') },
      params: {
        classname,
        studentname: studentName.value
      }
    })
    if (res.data === 'success') {
      ElMessage.success(`学生 ${studentName.value} 加入 ${classname} 成功！`)
      studentName.value = ''
    } else {
      ElMessage.error('加入失败，请检查输入')
    }
  } catch (err) {
    console.error('加入课程失败', err)
    ElMessage.error('加入课程请求出错')
  } finally {
    loadingMap.value[classname] = false // 结束 loading
  }
}


</script>

<template>
  <div class="page">
    <h2>加入课程</h2>
    <div class="toolbar">
      <el-input v-model="newClassName" placeholder="请输入课程名称" style="width: 200px; margin-right: 10px" />
      <el-button type="primary" @click="addCourse">添加课程</el-button>
    </div>

    <div class="toolbar" style="margin-top: 20px;">
      <el-input v-model="studentName" placeholder="请输入学生学号" style="width: 200px; margin-right: 10px" />
      <span>输入学号后，点对应课程右边按钮加入</span>
    </div>

    <el-table :data="courses" border style="margin-top: 20px">
      <el-table-column prop="" label="课程名称">
        <template #default="{ row }">
          {{ row }}
        </template>
      </el-table-column>

      <el-table-column prop="" label="教师" />

      <el-table-column label="操作" width="150">
        <template #default="{ row }">
          <el-button type="success" size="small" @click="joinCourse(row)">加入课程</el-button>
        </template>
      </el-table-column>
    </el-table>
  </div>
</template>

<style scoped>
.page {
  padding: 0 20px;
}
.toolbar {
  margin-top: 20px;
  display: flex;
  align-items: center;
}
h2 {
  font-size: 28px;
}
</style>
