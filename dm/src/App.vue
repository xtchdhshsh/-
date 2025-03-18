<script setup>
import { RouterView, RouterLink } from 'vue-router';
import { ref } from 'vue';
const showIframe = ref(false);

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
    </ul>
    <div class="user">
      <img src="/image/user.png" alt="">
      <a href="">登录</a>
    </div>
  </div>
  <!-- 展示区 -->
  <div class="main">
    <RouterView />
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
</style>
