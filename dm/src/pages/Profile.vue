<script setup>
import { RouterView, RouterLink, useRoute } from 'vue-router';
import { ref, provide, onMounted, computed } from 'vue'
import axios from '@/utlis/axios'

const route = useRoute()
const userType = ref('')
const menus = [
    {
      title: '用户管理',
      items: [
        { label: '用户信息', to: '/Menu/Users' },
      ]
    },
    {
      title: '课程管理',
      items: [
        { label: '题库管理', to: '/Menu/QuestionBank'},
        { label: '提交管理', to: '/Menu/Submission'}
      ]
    },
    {
      title: '班级管理',
      items: [
        { label: '查看班级', to: '/Menu/Courses' },
        { label: '学生管理', to: '/Menu/Students' },
      ]
    }
]

// 获取课程
const courses = ref([])
const fetchCourses = async () => {
  try {
    const res = await axios.post('/getclass', {}, {
      headers: { token: localStorage.getItem('token') }
    })

    // console.log(res.data)

    const rawData = res.data || {}
    if (Array.isArray(rawData.classname) && Array.isArray(rawData.clname)) {
      courses.value.length = 0
      const len = Math.min(rawData.classname.length, rawData.clname.length)
      for (let i = 0; i < len; i++) {
        courses.value.push({
          classname: rawData.classname[i],
          clname: rawData.clname[i],
          teachername: rawData.teachername || ''
        })
      }
    }
  } catch (err) {
    // console.error('获取课程失败', err)
    ElMessage.error('获取课程失败')
  }
}

onMounted(() => {
  fetchCourses()
  userType.value = localStorage.getItem('savedUserType')
})

provide('courses', courses) // to Course.vue, Student.vue
// provide('teacherName', teacherName) // 暂时不用
provide('fetchCourses', fetchCourses) // to Course.vue, Student.vue
</script>

<template>
  <div class="profile">
    <!-- 左侧侧边栏 -->
    <div class="sidebar">
      <h3 class="sidebar-title">个人中心</h3>

      <div
        class="menu-section"
        v-for="(section, index) in menus"
        :key="index"
      >
        <h4 class="section-title">{{ section.title }}</h4>
        <RouterLink
          v-for="item in section.items"
          :key="item.to"
          :to="item.to"
          class="menu-item"
          :class="{ active: route.path === item.to }"
        >
          {{ item.label }}
        </RouterLink>
      </div>
    </div>

    <!-- 右侧主内容 -->
    <div class="main-content">
      <RouterView />
    </div>
  </div>

</template>
  
<style scoped>
.profile {
  display: flex;
  background-color: #fafafa;
  min-height: 100vh;
}

/* 左侧 Sidebar 栏 */
.sidebar {
  position: fixed;
  top: 70px;
  left: 0;
  width: 200px;
  height: auto;
  margin: 5px 20px;
  padding: 10px;
  background-color: #f5f5f5;
  box-shadow: 2px 0 5px rgba(0, 0, 0, 0.05);
  overflow-y: auto;
}
.sidebar-title {
  margin-bottom: 22px;
  padding-left: 10px;
  font-size: 24px;
  color: #333;
}
.menu-section {
  margin-bottom: 20px;
}
.section-title {
  margin: 15px 0 10px 5px;
  font-size: 16px;
  color: #666;
}
.menu-item {
  margin: 2px 0;
  padding: 10px 20px;
  border-radius: 6px;
  font-size: 14px;
  color: black;
  text-decoration: none;
  display: block;
}
.menu-item:hover {
  background-color: #f0f0f0;
  color: #007bff;
}
.menu-item.active {
  background-color: #e1f5fe;
  font-weight: 700;
  color: #2196f3;
}

/* 右侧显示内容 */
.main-content {
  margin-left: 220px;
  flex: 1;
  padding: 30px 40px;
  overflow: auto;
}
</style>