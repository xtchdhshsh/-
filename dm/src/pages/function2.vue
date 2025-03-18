<template>

  <div class="banner">
    <div class="leftbanner">
      <h1 class="title">SAT求解器</h1>
      <div class="decoration">
        <div class="tip">请输入析取范式:</div>
        <input type="button" value="∨" class="tipbut" data-id="2">
        <input type="button" value="∧" class="tipbut" data-id="3">
        <input type="button" value="→" class="tipbut" data-id="4">
        <input type="button" value="﹁" class="tipbut" data-id="5">
        <input type="button" value="↔" class="tipbut" data-id="6">
        <input type="button" value="()" class="tipbut" data-id="7">
        <input type="text" placeholder='例如：(p∧r)∨r' class="enter">
        <input type="submit" class="submit">
      </div>
      
    </div>

    <div class="rightbanner">
      <div class="toppart">
        <h1>结果</h1>
          <div class="answer"> </div>
      </div>

      <div class="bottompart">
        <h1>成真赋值</h1>
        <div class="showlist" id="showlist1">
        </div>
        
      </div>
    </div>
  </div>

</template>

<script>
import { onMounted } from 'vue';
import axios from 'axios';
export default {
  setup() {
  onMounted(() => {
    const enter = document.querySelector('.enter')
    const submit = document.querySelector('.submit')
    const answer = document.querySelector('.answer')

     //制作符号按钮功能
     for(let i=2;i<=7;i++){
      const tipbutton = document.querySelector(`.tipbut:nth-child(${i})`)
        tipbutton.addEventListener('click',() =>{
           // 获取当前光标位置
          const cursorPosition = enter.selectionStart;
          // 获取光标前后的文本
          const startText = enter.value.substring(0, cursorPosition);
          const endText = enter.value.substring(cursorPosition);
          const textToInsert=tipbutton.value
          enter.value = startText + textToInsert + endText;
          //设置光标位置
          enter.setSelectionRange(cursorPosition + textToInsert.length, cursorPosition + textToInsert.length);
          // enter.value = enter.value + `${tipbutton.value}`
        })
    }
    


    submit.addEventListener('click',()=>{
      console.log(enter.value)
      axios({
        url:`http://localhost:8080/sat/${enter.value}`,
        method:'GET',
        params:{
          dataStr:enter.value
        }
      }).then(result=>{
      
        if(result.data[0]=='false'){
          answer.innerHTML='不可满足'
          //清除之前表格
          const showlist = document.querySelector('.showlist')
          document.getElementById("showlist1").innerHTML = ""
          const title = document.createElement('ul')
          title.innerHTML=`
            <li>p</li>
            <li>q</li>
            <li>r</li>
          `
          showlist.appendChild(title)
        }else{


          answer.innerHTML='可满足'
          let temp = result.data[1]
          let cz = temp.split(',')

          //清除之前表格
          const showlist = document.querySelector('.showlist')
          document.getElementById("showlist1").innerHTML = ""
          const title = document.createElement('ul')
          title.innerHTML=`
            <li>p</li>
            <li>q</li>
            <li>r</li>
          `
          showlist.appendChild(title)
          //渲染数据
          for(let i=0;i<cz.length;i++){

          const ul = document.createElement('ul')
          ul.innerHTML =`
            <li>${cz[i][0]}</li>
            <li>${cz[i][2]}</li>
            <li>${cz[i][4]}</li>
         `
          
        showlist.appendChild(ul)
          }
        }
      })
    })
  });
}

}
</script>

<style >
.clearfix{zoom:1;}
.clearfix::after {
content: ".";
display: block;
height: 0;
clear: both;
visibility: hidden;
}
.banner{
  height: 550px;
  min-width: 1240px;
  display: flex;
  margin: 0 auto;
}
.banner .leftbanner{
  height: 500px;
  width: 500px;
  border: 1px solid 2px;
  margin-left: 50px;
  margin-top: 20px;
}
.leftbanner h1{
  width: 260px;
  border-bottom: 4px solid black;
}
.leftbanner .title{

  font-size: 50px;
}
.leftbanner .tip{
  font-size: 20px;
  padding-top: 10px;
  margin-left: 10px;
  margin-top: 10px;
}
.leftbanner .enter{
  margin-top: 10px;
  margin-left: 80px;
  height: 30px;
}
.leftbanner .decoration{
  width :400px;
  height:200px;
  margin-left: 50px;
  margin-top: 100px;
  border: 2px solid black;
}
.leftbanner .submit{
  height:32px;
  width: 60px;
  margin-left: 10px;
  
}
.leftbanner .tipbut{
  height:30px;
  width: 40px;
  margin-top: 5px;
  margin-left: 20px;
  float: left;

}
.rightbanner{
  height: 500px;
  width: 600px;
  margin-top: 20px;
  right: 200px;
  border: 4px solid black;
}
.rightbanner .toppart{
  height: 130px;
  border-bottom:4px solid black;
}
.rightbanner .toppart h1{
  font-size: 45px;
  margin-left: 20px;
  margin-top: 10px;
}
.rightbanner .answer{
  font-size: 30px;
  margin-top: 20px;
  margin-left: 70px;
}

.rightbanner .bottompart{
  height: 200px;
  width: 600px;

  margin-left: 20px;
  margin-top:10px;
}
.rightbanner .bottompart h1{
  font-size: 45px;
}
.rightbanner .bottompart ul{
  list-style: none;
  margin-top: 30px;
  margin-left: 50px;

}
.rightbanner .bottompart li{
  float: left;
  height: 25px;
  width: 150px;
  border: 3px solid black;
  margin-left: -1px;
  margin-top: -1px;
  line-height: 25px;
  text-align: center;
}
body{
  background-color: #ececec42;
}
</style>