<script setup>
import { ref, onMounted } from 'vue'

const form = ref({
  username: '',
  studentId: '',
  email: ''
})

const isEditing = ref(false)

// 点击修改
const enableEdit = () => {
  isEditing.value = true
}
// 点击保存
const saveForm = async () => {
  try {
    await ElMessageBox.confirm(
      '确定要保存修改吗？',
      '提示',
      {
        confirmButtonText: '确认',
        cancelButtonText: '取消',
        type: 'warning',
      }
    )
    // await axios.post('', form.value)

    localStorage.setItem('saveedUserInfo', JSON.stringify(form.value))
    ElMessage.success('保存成功！')
    isEditing.value = false
  } catch (err) {
    if (err !== 'cancel') {
      console.error('保存失败', err)
      ElMessage.error('保存失败，请重试')
    }
  }
}

onMounted(() => {
  const savedUsername = localStorage.getItem('savedUsername')
  form.value.username = savedUsername
  const savedInfo = localStorage.getItem('saveedUserInfo')
  if (savedInfo) {
    Object.assign(form.value, JSON.parse(savedInfo))
  }
})
</script>

<template>
  <div class="page">
    <h2>学生信息</h2>

    <el-form :model="form" label-width="100px">
      <el-form-item label="用户名">
        <el-input v-model="form.username" disabled />
      </el-form-item>

      <el-form-item label="学号">
        <el-input v-model="form.studentId" :disabled="!isEditing" placeholder="请输入学号" />
      </el-form-item>

      <el-form-item label="邮箱">
        <el-input v-model="form.email" :disabled="!isEditing" placeholder="请输入邮箱" />
      </el-form-item>

      <el-form-item>
        <el-button v-if="!isEditing" type="primary" @click="enableEdit">修改</el-button>
        <el-button v-else type="success" @click="saveForm">保存</el-button>
      </el-form-item>
    </el-form>
  </div>
</template>

<style scoped>
.page {
  padding: 20px;
  max-width: 1200px;
}
h2 {
  margin-bottom: 20px;
}
</style>
