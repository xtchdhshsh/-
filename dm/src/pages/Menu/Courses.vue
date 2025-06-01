<script setup>
import { ref, inject } from 'vue'
import axios from '@/utlis/axios'

const courses = inject('courses')
// const teacherName = inject('teacherName') // 暂时不用
const fetchCourses = inject('fetchCourses') // from Profile.vue
const selectedClasses = ref([]) // 多选框绑定的数据
const loadingMap = ref({})

// 添加班级
// const newClassName = ref('')
// const addCourse = async () => {
//   if (!newClassName.value.trim()) {
//     ElMessage.warning('请输入班级名')
//     return
//   }
//   try {
//     const res = await axios.post('/addclass', null, {
//       headers: { token: localStorage.getItem('token') },
//       params: { classname: newClassName.value }
//     })
//     if (res.data === 'success') {
//       ElMessage.success('添加成功')
//       newClassName.value = ''
//       await fetchCourses()
//     } else {
//       ElMessage.error('添加失败')
//     }
//   } catch (err) {
//     console.error('添加班级失败', err)
//     ElMessage.error('添加班级请求出错')
//   }
// }

// 删除班级
const showCaptchaDialog = ref(false)
const captchaImageUrl = ref('')
const currentClassToDelete = ref('')
const captchaInput = ref('')
// 验证码弹窗
const requestDeleteWithCaptcha = async (classname) => {
  currentClassToDelete.value = classname
  captchaInput.value = ''

  // 后端返回图形验证码 URL
  captchaImageUrl.value = `/getcaptcha?rand=${Date.now()}`
  showCaptchaDialog.value = true
}
// 确认删除（提交验证码）
const confirmDelete = async () => {
  const classname = currentClassToDelete.value
  try {
    loadingMap.value[classname] = true
    const res = await axios.delete('/deleteclass', {
      headers: { token: localStorage.getItem('token') },
      params: {
        classname,
        captcha: captchaInput.value
      }
    })

    if (res.data === 'success') {
      ElMessage.success('删除成功')
      await fetchCourses()
      showCaptchaDialog.value = false
    } else {
      ElMessage.error(res.data || '验证码错误或删除失败')
    }
  } catch (err) {
    ElMessage.error('删除请求出错')
  } finally {
    loadingMap.value[classname] = false
  }
}

// 修改班级名称
const editClassName = async (oldName) => {
  const newName = window.prompt('请输入新的班级名称：', oldName)
  if (!newName || !newName.trim()) {
    return
  }

  try {
    loadingMap.value[oldName] = true
    const res = await axios.put('/updateclassname', null, {
      headers: { token: localStorage.getItem('token') },
      params: { oldname: oldName, newname: newName.trim() }
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
    loadingMap.value[oldName] = false
  }
}

// 加入班级
// const studentName = ref('')
// const joinCourse = async (classname) => {
//   if (!studentName.value.trim()) {
//     ElMessage.warning('请先输入要添加学生的学号')
//     return
//   }

//   loadingMap.value[classname] = true

//   try {
//     const res = await axios.post('/addstudent', null, {
//       headers: { token: localStorage.getItem('token') },
//       params: {
//         classname,
//         studentname: studentName.value
//       }
//     })
//     if (res.data === 'success') {
//       ElMessage.success(`学生 ${studentName.value} 加入 ${classname} 成功！`)
//       studentName.value = ''
//     } else {
//       ElMessage.error('加入失败，请检查输入')
//     }
//   } catch (err) {
//     // console.error('加入课程失败', err)
//     ElMessage.error('加入课程请求出错')
//   } finally {
//     loadingMap.value[classname] = false
//   }
// }

// 导入学生(execl)
// const uploadExcel = async (options) => {
//   const { file } = options
//   const classname = options.data.classname

//   const formData = new FormData()
//   formData.append('file', file)

//   try {
//     const res = await axios.post(`/addstudentbyexcel`, formData, {
//       headers: {
//         'Content-Type': 'multipart/form-data',
//         token: localStorage.getItem('token')
//       },
//       params: { classname }
//     })

//     if (res.data === 'success') {
//       ElMessage.success('学生批量导入成功')
//     } else {
//       ElMessage.error(res.data || '导入失败')
//     }
//   } catch (err) {
//     // console.error('上传出错', err)
//     ElMessage.error('上传失败')
//   }
// }
</script>

<template>
  <div class="page">
    <h2>查看班级</h2>

    <el-button type="primary" @click="$router.push('/Menu/InnerViews/CreateCourse')">新建分班</el-button>

    <!-- <div class="toolbar" style="margin-top: 20px;">
      <el-input v-model="studentName" placeholder="请输入学生学号" style="width: 200px; margin-right: 10px" />
      <el-text type="info">输入学号后，点击对应班级右边的加入按钮</el-text>
    </div> -->

    <el-table 
      :data="courses" 
      border 
      style="margin-top: 20px"
      @selection-change="val => (selectedClasses.value = val)"
    >
      <el-table-column type="selection" width="45" />

      <el-table-column type="index" label="序号" width="55" />

      <el-table-column prop="name" label="班级名称">
        <template #default="{ row }">
          {{ row.name }}
        </template>
      </el-table-column>

      <el-table-column label="学生数量">
        <template #default="{ row }">
          {{ row.studentCount || '未知' }}
        </template>
      </el-table-column>
      <el-table-column label="管理教师">
        <template #default="{ row }">
          {{ row.teacher || '暂无' }}
        </template>
      </el-table-column>

      <el-table-column label="操作" width="240px">
        <template #default="{ row }">
          <div style="display: flex; align-items: center; justify-content: space-around;">
            <!-- <el-button 
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
            </el-upload> -->

            <el-button
              type="warning"
              size="small"
              :loading="loadingMap[row.name]"
              @click="requestDeleteWithCaptcha(row.name)"
            >
              删除
            </el-button>

            <el-button 
              type="success" 
              size="small" 
              :loading="loadingMap[row.name]"
              @click="$router.push({ path: '/Menu/InnerViews/CourseInfo', query: { classname: row.name } })"
            >
              查看
            </el-button>
            
            <el-button
              type="primary"
              size="small"
              :loading="loadingMap[row.name]"
              @click="editClassName(row.name)"
            >
              修改班级名称
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
      <el-input v-model="captchaInput" placeholder="输入验证码" maxlength="4" />
      <img
        :src="captchaImageUrl"
        @click="captchaImageUrl = `/getcaptcha?rand=${Date.now()}`"
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
.toolbar {
  margin-top: 20px;
  display: flex;
  align-items: center;
}
h2 {
  font-size: 32px;
  margin-bottom: 22px;
}
</style>
