<script setup>
import { ref, inject } from 'vue'
import axios from '@/utlis/axios'

const courses = inject('courses')
// const teacherName = inject('teacherName') // 暂时不用
const fetchCourses = inject('fetchCourses') // from Profile.vue

// 添加班级
const newClassName = ref('')
const addCourse = async () => {
  if (!newClassName.value.trim()) {
    ElMessage.warning('请输入班级名')
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
    console.error('添加班级失败', err)
    ElMessage.error('添加班级请求出错')
  }
}

// 加入班级
const studentName = ref('')
const loadingMap = ref({})
const joinCourse = async (classname) => {
  if (!studentName.value.trim()) {
    ElMessage.warning('请先输入要添加学生的学号')
    return
  }

  loadingMap.value[classname] = true

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
    // console.error('加入课程失败', err)
    ElMessage.error('加入课程请求出错')
  } finally {
    loadingMap.value[classname] = false
  }
}

// 导入学生(execl)
const uploadExcel = async (options) => {
  const { file } = options
  const classname = options.data.classname

  const formData = new FormData()
  formData.append('file', file)

  try {
    const res = await axios.post(`/addstudentbyexcel`, formData, {
      headers: {
        'Content-Type': 'multipart/form-data',
        token: localStorage.getItem('token')
      },
      params: { classname }
    })

    if (res.data === 'success') {
      ElMessage.success('学生批量导入成功')
    } else {
      ElMessage.error(res.data || '导入失败')
    }
  } catch (err) {
    // console.error('上传出错', err)
    ElMessage.error('上传失败')
  }
}
</script>

<template>
  <div class="page">
    <h2>加入班级</h2>

    <div class="toolbar">
      <el-input v-model="newClassName" placeholder="请输入班级名称" style="width: 200px; margin-right: 10px" />
      <el-button type="primary" @click="addCourse">添加班级</el-button>
    </div>

    <div class="toolbar" style="margin-top: 20px;">
      <el-input v-model="studentName" placeholder="请输入学生学号" style="width: 200px; margin-right: 10px" />
      <el-text type="info">输入学号后，点击对应班级右边的加入按钮</el-text>
    </div>

    <el-table :data="courses" border style="margin-top: 20px">
      <el-table-column prop="name" label="班级名称">
        <template #default="{ row }">
          {{ row.name }}
        </template>
      </el-table-column>

      <el-table-column label="操作" width="180px">
        <template #default="{ row }">
          <div style="display: flex; align-items: center; justify-content: space-around;">
            <el-button 
              type="success" 
              size="small" 
              :loading="loadingMap[row.name]" 
              @click="joinCourse(row.name)"
            >
              加入班级
            </el-button>

            <el-upload
              :data="{ classname: row.name }"
              :show-file-list="false"
              :http-request="uploadExcel"
              style="margin-left: 10px;"
            >
              <el-button size="small" type="primary">导入学生</el-button>
            </el-upload>

          </div>
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
.toolbar {
  margin-top: 20px;
  display: flex;
  align-items: center;
}
h2 {
  font-size: 32px;
}
</style>
