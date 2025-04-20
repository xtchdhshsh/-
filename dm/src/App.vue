<script setup>
import { RouterView, RouterLink } from 'vue-router';
import { ref, onMounted } from 'vue';
import { useRouter } from 'vue-router'
const showIframe = ref(false);

const username = ref('')
const isLoggedIn = ref(false)
const handleLoginSuccess = (user) => {
  username.value = user
  isLoggedIn.value = true
}

// 跳转个人中心页面
const router = useRouter()
const goToProfile = () => {
  router.push('/profile')
}

const isHovering = ref(false)
// 退出登录
const logout = () => {
  localStorage.removeItem('username');
  localStorage.removeItem('token');
  isLoggedIn.value = false;
  username.value = '';
};

onMounted(() => {
  username.value = localStorage.getItem('savedUsername')
  isLoggedIn.value = !!username.value
})

</script>

<template>
  <!-- 导航栏 -->
  <div class="header clearfix">
    <h1><a href="homepage.html">离散数学智能分析</a></h1>
    <ul class="nav">
      <li><RouterLink to = '/function1'>真值表</RouterLink></li>
      <li><RouterLink to = '/function2'>SAT求解器</RouterLink></li>
      <li><RouterLink to = '/function3'>集合运算</RouterLink></li>
      <li><RouterLink to = '/warshall'>Warshall算法</RouterLink></li>
      <li><RouterLink to = '/dijkstra'>Dijkstra算法</RouterLink></li>
      <li><RouterLink to = '/MST'>最小生成树</RouterLink></li>
      <li><RouterLink to = '/huffman'>Huffman算法</RouterLink></li>
      <li><RouterLink to = '/BBG'>二部图</RouterLink></li>
    </ul>

    <div class="user" @mouseover="isHovering = true" @mouseleave="isHovering = false">
      <img src="/image/user.png" alt="" class="user-icon">
      
      <!-- 用户名 -->
      <template v-if="isLoggedIn">
        <span class="username">{{ username }}</span>
        <!-- 下拉框 -->
        <div v-if="isHovering" class="dropdown">
          <span @click="goToProfile">个人中心</span>
          <span @click="logout">退出登录</span>
        </div>
      </template>
      <!-- 登录按钮 -->
      <template v-else>
        <RouterLink to="/Login">登录</RouterLink>
      </template>
    </div>
  </div>
  
  <!-- 展示区 -->
  <div class="main">
    <RouterView @login-success="handleLoginSuccess" />
  </div>

</template>

<style scoped>
.clearfix{zoom:1;}
.clearfix::after {
  content: ".";
  display: block;
  height: 0;
  clear: both;
  visibility: hidden;
}
*{
  margin: 0;
  padding: 0;
}

.header{
  height: 65px;
  min-width: 1000px;
  background: linear-gradient(to left,#2c5364,#203A43,#0F2027);
  border-bottom: 2px solid black;
  overflow: hidden;
  margin-bottom: 10px;
}
.header h1{
  line-height: 65px;
  margin-left: 20px;
  color: #bdc3c7;
  float: left;
}
.header h1 a{
  text-decoration: none;
  color: #bdc3c7;
}
.header h1 a:hover{
  color: #bdc3c7;
}
.header ul{
  list-style: none;
  line-height: 48px;
}
.header ul li{
  float: left;
  width: 1000x;
  margin-top: 12px;
  margin-left: 18px;
}
.header ul li a {
  text-decoration: none;
  color: #bdc3c7;
}
.header ul li a:hover{
  color:#2f72dc;
}
.header .user{
  float: right;
  height: 67px;
  line-height: 67px;
  margin-right: 20px;

} 
.header .user img{
  height: 30px;
  padding-top: 18px;
  padding-right: 10px;
}
.header .user a{
  text-decoration: none;
  color: white;
  float: right;
}
.nav .router-link-active{
  color: #19CAAD;
}
body{
  background-color: #ececec42;
}

.user {
  display: flex;
  align-items: top;
  font-weight: 700;
  font-size: 18px;
  color: white;
}
.user .user-icon {
  filter: brightness(0) invert(1);
  scale: 2;
}
.user .username {
  cursor: pointer;
}
.user .username:hover {
  color: #2f72dc;
}

/* 下拉框样式 */
.dropdown {
  position: absolute;
  top: 62px;
  right: 2px;
  color: rgb(32, 28, 28);
  width: 100px;
  display: flex;
  flex-direction: column;
  z-index: 999;
  box-shadow: 0 4px 12px rgba(0, 0, 0, 0.50);
  background-color: white;
  font-size: 16px;
  cursor: pointer;
  text-align: center;
}
.dropdown span:hover {
  color: #2f72dc;
}

</style>
