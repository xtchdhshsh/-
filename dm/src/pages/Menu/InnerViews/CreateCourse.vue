<script setup>
import { ref } from 'vue'
import axios from '@/utlis/axios'
import { useRouter } from 'vue-router'
import { ArrowLeft } from '@element-plus/icons-vue'

const router = useRouter()
const goBack = () => {
  router.back()  // 前往 /Menu/Courses
}

const mode = ref('manual')

const className = ref('')
const courseName = ref('')

// 添加班级
const addCourse = async () => {
  if (!className.value.trim() || !courseName.value.trim()) {
    ElMessage.warning('请填写完整的班级信息')
    return
  }

  const params = new URLSearchParams()
  params.append('cl', className.value)
  params.append('classname', courseName.value)

  try {
    const res = await axios.post('/addclass', params, {
      headers: { token: localStorage.getItem('token') }
    })

    if (res.data === 'success') {
      ElMessage.success('添加班级成功')
      router.back() // 返回 Course.vue
    } else {
      ElMessage.error(res.data || '添加失败')
    }
  } catch (err) {
    ElMessage.error('添加班级请求出错')
  }
}

// 批量导入学生，并自动创建班级
const uploadExcel = async (options) => {
  const { file } = options

  const formData = new FormData()
  formData.append('file', file)

  try {
    const res = await axios.post(`/addstudentbyexcel`, formData, {
      headers: {
        'Content-Type': 'multipart/form-data',
        token: localStorage.getItem('token')
      },
      params: { classname: className.value }
    })

    if (res.data === 'success') {
      ElMessage.success('学生批量导入成功')
    } else {
      ElMessage.error(res.data || '导入失败')
    }
  } catch (err) {
    ElMessage.error('上传失败')
  }
}
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

    <h2>新建班级</h2>

    <el-radio-group v-model="mode" style="margin-bottom: 30px;">
      <el-radio-button :value="'manual'">手工录入</el-radio-button>
      <el-radio-button :value="'excel'">批量导入</el-radio-button>
    </el-radio-group>

    <el-form label-width="100px" style="max-width: 600px;">
      <template v-if="mode === 'manual'">
        <el-form-item label="班级名称">
          <el-input v-model="className" placeholder="请输入班级名称" />
        </el-form-item>
        <el-form-item label="课程名称">
          <el-input v-model="courseName" placeholder="请输入课程名称" />
        </el-form-item>

        <el-form-item>
          <el-button type="primary" @click="addCourse">创建班级</el-button>
        </el-form-item>
      </template>

      <template v-else>
        <el-form-item label="导入学生">
          <el-upload
            :show-file-list="false"
            :http-request="uploadExcel"
            accept=".xlsx, .xls"
          >
            <el-button type="primary">上传 Excel 文件</el-button>
          </el-upload>
        </el-form-item>
      </template>
    </el-form>
  </div>
</template>

<style scoped>
.page {
  padding: 20px;
  max-width: 800px;
}
h2 {
  font-size: 32px;
  margin-bottom: 22px;
}
</style>
