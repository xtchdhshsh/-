<script setup>
import { ref, inject, watch } from 'vue'
import axios from '@/utlis/axios'

const classOptions = inject('courses')

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
    // console.error('拉取学生失败', err)
    ElMessage.error('获取学生列表失败')
  }
}

watch(selectedClass, () => {
  fetchStudents()
})
</script>

<template>
  <div class="page">
    <h2>学生信息管理</h2>

    <el-form style="margin-bottom: 20px;">
      <el-form-item label="班级">
        <el-select v-model="selectedClass" placeholder="请选择班级" style="width: 240px;">
          <el-option
            v-for="cls in classOptions"
            :key="cls.name"
            :label="cls.name"
            :value="cls.name"
          />
        </el-select>
      </el-form-item>
    </el-form>

    <el-table :data="students" border stripe>
      <el-table-column prop="id" label="学号" />
      <el-table-column prop="name" label="姓名" />
      <el-table-column prop="class" label="班级" />
      <el-table-column prop="course" label="课程" />
    </el-table>
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
