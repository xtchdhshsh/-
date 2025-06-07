<script setup>
import { ref, inject, onMounted } from 'vue'
import axios from '@/utlis/axios'
import { useRoute } from 'vue-router'

const route = useRoute()

const courses = inject('courses')
// const teacherName = inject('teacherName') // 暂时不用
const fetchCourses = inject('fetchCourses') // from Profile.vue
const selectedClasses = ref([]) // 多选框绑定的数据
const loadingMap = ref({})

// 删除班级
const showCaptchaDialog = ref(false)
const captchaImageUrl = ref('')
const captchaVerifyCode = ref('')
const currentClassToDelete = ref('')
const captchaInput = ref('')

// 验证码弹窗
const requestCaptcha = async () => {
  captchaInput.value = ''

  try {
    const res = await axios.get('/captcha')

    // console.log(res.data)

    const { verifyCode, image, message } = res.data

    if (message === 'success' && image && verifyCode) {
      captchaImageUrl.value = image
      captchaVerifyCode.value = verifyCode
      showCaptchaDialog.value = true
    } else {
      ElMessage.error('获取验证码失败')
    }
  } catch (err) {
    ElMessage.error('请求验证码失败')
  }
}
// 确认删除
const confirmDelete = async () => {
  const fullClassName = currentClassToDelete.value
  const [cl, classname] = fullClassName.split('-')

  if (captchaInput.value === captchaVerifyCode.value) {
    try {
      loadingMap.value[fullClassName] = true
      const res = await axios.post('/delclass', null, {
        headers: { token: localStorage.getItem('token') },
        params: { classname, cl }
      })

      if (res.data === 'success') {
        ElMessage.success('删除成功')
        await fetchCourses()
        showCaptchaDialog.value = false
      } else {
        ElMessage.error(res.data || '删除失败')
      }
    } catch (err) {
      ElMessage.error('删除请求出错')
    } finally {
      loadingMap.value[fullClassName] = false
    }
  } else {
    ElMessage.error('验证码错误')
  }
}


// 修改班级名称
const editClassName = async (clname, classname) => {
  const newclassname = window.prompt('请输入新的课程名称：', classname)
  if (!newclassname || !newclassname.trim()) {
    return
  }

  try {
    const key = `${clname}-${classname}`
    loadingMap.value[key] = true
    const res = await axios.post('/updateclassname', null, {
      headers: { token: localStorage.getItem('token') },
      params: {
        oldclassname: classname,
        newclassname: newclassname.trim(),
        cl: clname
      }
    })

    if (res.data === 'success') {
      ElMessage.success('修改成功')
      await fetchCourses()
    } else {
      ElMessage.error('修改失败')
    }
  } catch (err) {
    ElMessage.error('修改请求出错')
  } finally {
    loadingMap.value[`${clname}-${classname}`] = false
  }
}

onMounted(() => {
  fetchCourses()
})
</script>

<template>
  <div class="page">
    <h2>查看班级</h2>

    <el-button type="primary" @click="$router.push('/Menu/InnerViews/CreateCourse')">新建分班</el-button>

    <el-table 
      :data="courses"
      row-key="id"
      border 
      style="margin-top: 20px"
      @selection-change="val => (selectedClasses.value = val)"
    >
      <el-table-column type="selection" width="45" />

      <el-table-column type="index" label="序号" width="55" />

      <el-table-column label="班级名称">
        <template #default="{ row }">
          {{ row.clname }}
        </template>
      </el-table-column>

      <el-table-column label="课程名称">
        <template #default="{ row }">
          {{ row.classname }}
        </template>
      </el-table-column>

      <el-table-column label="管理教师">
        <template #default="{ row }">
          {{ row.teachername }}
        </template>
      </el-table-column>

      <el-table-column label="操作" width="240px">
        <template #default="{ row }">
          <div style="display: flex; align-items: center; justify-content: space-around;">
            <el-button
              type="warning"
              size="small"
              :loading="loadingMap[row.classname]"
              @click="() => { 
                currentClassToDelete = row.clname + '-' + row.classname; 
                requestCaptcha(); 
              }"
            >
              删除
            </el-button>

            <el-button 
              type="success" 
              size="small" 
              :loading="loadingMap[row.classname]"
              @click="$router.push({ path: '/Menu/InnerViews/CourseInfo', query: { classname: row.classname, cl: row.clname } })"
            >
              查看
            </el-button>
            
            <el-button
              type="primary"
              size="small"
              :loading="loadingMap[row.classname]"
              @click="editClassName(row.clname, row.classname)"
            >
              修改课程名称
            </el-button>

          </div>
        </template>
      </el-table-column>
    </el-table>
  </div>

  <el-dialog
    v-model="showCaptchaDialog"
    title="如确认删除，请输入验证码"
    width="500px"
    style="padding: 22px;"
    :before-close="() => (showCaptchaDialog = false)"
  >
    <div style="margin-bottom: 16px;">
      正在删除班级：<strong style="color: red; font-size: 18px;">{{ currentClassToDelete }}</strong>
    </div>
    <div style="display: flex; align-items: center; gap: 10px; margin-bottom: 16px;">
      <el-input v-model="captchaInput" placeholder="输入验证码" maxlength="10" />
      <img
        :src="captchaImageUrl"
        @click="requestCaptcha"
        style="cursor: pointer; height: 36px; border: 1px solid #ccc"
        title="点击刷新验证码"
      />
    </div>
    <template #footer>
      <el-button @click="showCaptchaDialog = false">取消</el-button>
      <el-button type="primary" @click="confirmDelete">确认删除</el-button>
    </template>
  </el-dialog>

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
