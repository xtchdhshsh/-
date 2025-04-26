<script setup>
import { RouterView, RouterLink, useRoute } from 'vue-router';
import { ref, provide, onMounted } from 'vue'
import axios from '@/utlis/axios'

const route = useRoute()
const menus = [
  {
    title: '学生管理',
    items: [
      { label: '用户信息', to: '/Menu/Users' },
      
      { label: '课程作业', to: ''}
    ]
  },
  {
    title: '课程管理',
    items: [
      { label: '加入课程', to: '/Menu/Courses' },
      { label: '课程公告', to: '/Menu/Announcements' }
    ]
  },
  {
    title: '系统管理',
    items: [
      { label: '学生信息', to: '/Menu/Students' },
      { label: '教师管理', to: '/Menu/Teachers' }
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
    const rawData = res.data
    const matches = rawData.match(/\[([^\[\]]+?)\]/g) || []
    courses.value = matches.map(match => match.replace(/[\[\]]/g, ''))
  } catch (err) {
    console.error('获取课程失败', err)
    ElMessage.error('获取课程失败')
  }
}

onMounted(fetchCourses)

provide('courses', courses)
provide('fetchCourses', fetchCourses)
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
}

.sidebar {
  width: 200px;
  height: auto;
  background-color: #f5f5f5;
  padding: 10px;
  box-shadow: 2px 0 5px rgba(0, 0, 0, 0.05);
}

.sidebar-title {
  font-size: 24px;
  margin-bottom: 22px;
  padding-left: 10px;
  color: #333;
}

.menu-section {
  margin-bottom: 20px;
}

.section-title {
  font-size: 16px;
  color: #666;
  margin: 15px 0 10px 5px;
}

.menu-item {
  display: block;
  padding: 10px 20px;
  font-size: 14px;
  color: black;
  text-decoration: none;
  border-radius: 6px;
  margin: 2px 0;
}
.menu-item:hover {
  background-color: #f0f0f0;
  color: #007bff;
}
.menu-item.active {
  background-color: #e1f5fe;
  color: #2196f3;
  font-weight: 700;
}

.main-content {
  flex: 1;
  padding: 20px 30px;
}
</style>