<script>
import { onMounted ,ref} from 'vue';
import axios from '@/utlis/axios';
export default {
  setup() {
    const vr = ref(100)

    onMounted(() => {
      const enter = document.querySelector(".enter")

    const submit = document.querySelector('.submit')

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
          enter.addEventListener('blur',()=>{
            enter.focus();
          })
          //设置光标位置
          enter.setSelectionRange(cursorPosition + textToInsert.length, cursorPosition + textToInsert.length);
          // enter.value = enter.value + `${tipbutton.value}`
        })
    }

    const TwoBT = document.querySelector('.two')
    const ThreeBT = document.querySelector('.three')
    const FourBT = document.querySelector('.four')
    const TwoForm = document.querySelector('.Twobianyuan')
    const ThreeForm = document.querySelector('.Tbianyuan')
    const FourForm = document.querySelector('.Fbianyuan')
    TwoBT.addEventListener('click',()=>{
      // TwoForm.style.display = 'block'
      // ThreeForm.style.display = 'none'
      // FourForm.style.display = 'none'
      TwoForm.classList.remove('hide','show')
      ThreeForm.classList.remove('hide','show')
      FourForm.classList.remove('hide','show')

      TwoForm.classList.add('show')
      ThreeForm.classList.add('hide')
      FourForm.classList.add('hide')
    })
    ThreeBT.addEventListener('click',()=>{
      TwoForm.classList.remove('hide','show')
      ThreeForm.classList.remove('hide','show')
      FourForm.classList.remove('hide','show')

      TwoForm.classList.add('hide')
      ThreeForm.classList.add('show')
      FourForm.classList.add('hide')
    })
    FourBT.addEventListener('click',()=>{
      TwoForm.classList.remove('hide','show')
      ThreeForm.classList.remove('hide','show')
      FourForm.classList.remove('hide','show')

      TwoForm.classList.add('hide')
      ThreeForm.classList.add('hide')
      FourForm.classList.add('show')
    })

    

    //点击提交数据传送至后端并返回结果
    submit.addEventListener('click',function(){
      // const character = document.querySelector('.Tbianyuan .answer .answer_char')
      // console.log(enter.value)
      // character.innerHTML = enter.value

      axios({
        url:`/zhenzhi/${enter.value}`,
        method:'GET',

      }).then(result => {

        console.log(result)

        const arr_answer = result.data.data.split(',')
        
    for(let i=2;i<=arr_answer.length+1;i++){
      if(arr_answer.length<=4){
        const answer_ul = document.querySelector(`.Twobianyuan .answer .answer_ul li:nth-child(${i})`)
        answer_ul.innerHTML = arr_answer[i-2]
        const character = document.querySelector('.Twobianyuan .answer .answer_char')
        character.innerHTML = enter.value

      }else if(arr_answer.length<=8){
        const answer_ul = document.querySelector(`.Tbianyuan .answer .answer_ul li:nth-child(${i})`)
        answer_ul.innerHTML = arr_answer[i-2]
        const character = document.querySelector('.Tbianyuan .answer .answer_char')
        character.innerHTML = enter.value
      }else{
        const answer_ul = document.querySelector(`.Fbianyuan .answer .answer_ul li:nth-child(${i})`)
        answer_ul.innerHTML = arr_answer[i-2]
        const character = document.querySelector('.Fbianyuan .answer .answer_char')
        character.innerHTML = enter.value
      }
    }


      })
    })
    //测试渲染
    
    

    // const temp ='1,1,1,0,1,0,1,0'
    // console.log(temp)
    // arr_answer = temp.split(',')
    // console.log(arr_answer.length)
    // for(let i=2;i<=arr_answer.length+1;i++){
    //   if(arr_answer.length<=4){
    //     const answer_ul = document.querySelector(`.Twobianyuan .answer .answer_ul li:nth-child(${i})`)
    //     answer_ul.innerHTML = arr_answer[i-2]
    //   }else if(arr_answer.length<=8){
    //     const answer_ul = document.querySelector(`.Tbianyuan .answer .answer_ul li:nth-child(${i})`)
    //     answer_ul.innerHTML = arr_answer[i-2]
    //   }else{
    //     const answer_ul = document.querySelector(`.Fbianyuan .answer .answer_ul li:nth-child(${i})`)
    //     answer_ul.innerHTML = arr_answer[i-2]
    //   }
    // }


    });
    return { 
        vr
    }
  }
}

</script>

 

<template>

  <div class="banner">

    <el-card style="min-width: 480px;width:550px" class="leftbanner">

      <template #header>
        <div class="card-header title">
         <span >命题公式真值表</span>
        </div>
      </template>


      <el-button  value="两变原"  class="two" style="width: 80px;border: 2px solid;">两变原</el-button>
      <el-button  value="三变原"  class="three" style="width: 80px;border: 2px solid;">三变原</el-button>
      <el-button  value="四变原"  class="four" style="width: 80px;border: 2px solid;">四变原</el-button>
      <el-card style="min-width: 480px;height: 200px" class="decoration">

        <div class="tip">请输入命题公式:</div>
        <el-button type="button" class="tipbut" value="∨" id="tb2">∨</el-button>
 
        <el-button type="button" value="∧" class="tipbut" id="tb3">∧</el-button>

        <el-button type="button" value="→" class="tipbut" id="tb4">→</el-button>
 
        <el-button type="button" value="﹁" class="tipbut" id="tb5">﹁</el-button>

        <el-button type="button" value="↔" class="tipbut" id="tb6">↔</el-button>

        <el-button type="button" value="()" class="tipbut" id="tb7">()</el-button>

        
        <input type="text" placeholder='例如：(p∨q)->r∨s' class="enter">
        <el-button type="primary submit" class="submit">提交</el-button>

       </el-card>
      
   </el-card>
    

    <div class="rightbanner" style="border: 0px solid;background-color: white;">
      <!-- 四变原 -->
      <div class="Fbianyuan hide">
      <div class="enter">
        <ul>
          <li class="character">p</li>
          <li>0</li>
          <li>0</li>
          <li>0</li>
          <li>0</li>
          <li>0</li>
          <li>0</li>
          <li>0</li>
          <li>0</li>
          <li>1</li>
          <li>1</li>
          <li>1</li>
          <li>1</li>
          <li>1</li>
          <li>1</li>
          <li>1</li>
          <li>1</li>
        </ul>
        <ul>
          <li class="character">q</li>
          <li>0</li>
          <li>0</li>
          <li>0</li>
          <li>0</li>
          <li>1</li>
          <li>1</li>
          <li>1</li>
          <li>1</li>
          <li>0</li>
          <li>0</li>
          <li>0</li>
          <li>0</li>
          <li>1</li>
          <li>1</li>
          <li>1</li>
          <li>1</li>
        </ul>
        <ul>
          <li class="character">r</li>
          <li>0</li>
          <li>0</li>
          <li>1</li>
          <li>1</li>
          <li>0</li>
          <li>0</li>
          <li>1</li>
          <li>1</li>
          <li>0</li>
          <li>0</li>
          <li>1</li>
          <li>1</li>
          <li>0</li>
          <li>0</li>
          <li>1</li>
          <li>1</li>
        </ul>
        <ul>
          <li class="character">s</li>
          <li>0</li>
          <li>1</li>
          <li>0</li>
          <li>1</li>
          <li>0</li>
          <li>1</li>
          <li>0</li>
          <li>1</li>
          <li>0</li>
          <li>1</li>
          <li>0</li>
          <li>1</li>
          <li>0</li>
          <li>1</li>
          <li>0</li>
          <li>1</li>
        </ul>
      </div>
      <div class="answer">
        <ul class="answer_ul">
          <li class="answer_char"></li>
          <li></li>
          <li></li>
          <li></li>
          <li></li>
          <li></li>
          <li></li>
          <li></li>
          <li></li>
          <li></li>
          <li></li>
          <li></li>
          <li></li>
          <li></li>
          <li></li>
          <li></li>
          <li></li>
        </ul>
      </div>
    </div>
    <!-- 三变原 -->
    <div class="Tbianyuan hide">
      <div class="enter">
        <ul>
          <li class="character">p</li>
          <li>0</li>
          <li>0</li>
          <li>0</li>
          <li>0</li>
          <li>1</li>
          <li>1</li>
          <li>1</li>
          <li>1</li>
        </ul>
        <ul>
          <li class="character">q</li>
          <li>0</li>
          <li>0</li>
          <li>1</li>
          <li>1</li>
          <li>0</li>
          <li>0</li>
          <li>1</li>
          <li>1</li>
        </ul>
        <ul>
          <li class="character">r</li>
          <li>0</li>
          <li>1</li>
          <li>0</li>
          <li>1</li>
          <li>0</li>
          <li>1</li>
          <li>0</li>
          <li>1</li>
        </ul>
      </div>
      <div class="answer">
        <ul class="answer_ul">
          <li class="answer_char"></li>
          <li data-="1"></li>
          <li></li>
          <li></li>
          <li></li>
          <li></li>
          <li></li>
          <li></li>
          <li></li>
        </ul>
      </div>
    </div>
    <!-- 双变元 -->
    <div class="Twobianyuan show">
      <div class="enter">
        <ul>
          <li class="character">p</li>
          <li>0</li>
          <li>0</li>
          <li>1</li>
          <li>1</li>  
        </ul>
        <ul>
          <li class="character">q</li>
          <li>0</li>
          <li>1</li>
          <li>0</li>
          <li>1</li>
        </ul>
      </div>
      <div class="answer">
        <ul class="answer_ul">
          <li class="answer_char"></li>
          <li></li>
          <li></li>
          <li></li>
          <li></li>
        </ul>
      </div>
    </div>

    </div>
  </div>

</template>

<!-- <script>
export default {

}
</script> -->


<style scoped>
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
      height:480px;
      width: 600px;
      border: 1px solid;
      margin-left: 50px;
      margin-top: 30px;
      position: relative;
    }
    .leftbanner h1{
      width: 360px;
      border-bottom: 4px solid black;
    }
    .leftbanner .title{
      font-size: 50px;
    }
    .leftbanner .tip{
      font-size: 20px;
      padding-top: 10px;
      margin-left: 10px;


    }
    .leftbanner .tipbut{
      height:32px;
      width: 40px;
      margin-top: 10px;
      margin-left: 20px;
      float: left;
      display: block;
    }
    .leftbanner .enter{
      margin-top: 30px;
      margin-left: 80px;
      height: 30px;
    }
    .leftbanner .decoration{
      width :450px;
      height:200px;
      margin-left: 100px;
      margin-top: 100px;
      border: 1px solid black;
      position: absolute;
      right: 50%;
      bottom: 10%;
      transform: translateX(50%);
    }
    .leftbanner .submit{
      height:32px;
      width: 60px;
      margin-left: 10px;
      position: relative;

    }

    .rightbanner{
      height: 500px;
      width: 650px;
      margin-top: 10px;
      margin-left: 30px;
      right: 150px;
    }
    .show{
      display: block;
    }
    .hide{
      display: none;
    }
    /* 表格按钮 */
    .banner .leftbanner .two{
      height:32px;
      width: 60px;
      margin-top: 10px;
      margin-left: 40px;
      float: left;
      display: block;
    }
    .banner .leftbanner .three{
      height:32px;
      width: 60px;
      margin-top: 10px;
      margin-left: 40px;
      float: left;
      display: block;
    }
    .banner .leftbanner .four{
      height:32px;
      width: 60px;
      margin-top: 10px;
      margin-left: 40px;
      float: left;
      display: block;
    }
    /* 双变原css */

    .rightbanner .Twobianyuan .enter ul{
      height: 500px;
      width: 150px;
      float: left;
      list-style: none;
      margin-left: -1px;
    }
    .rightbanner .Twobianyuan .enter li{
      height: 100px;
      border: 1px solid black;
      margin-top: -1px;
      text-align: center;
      line-height: 28px;
    }
    .rightbanner .Twobianyuan .enter .character{
      height: 100px;
      border: 3px solid black;
      text-align: center;
      line-height: 40px;
    }

    .rightbanner .Twobianyuan .answer ul{
      height: 500px;
      width: 300px;
      float: left;
      list-style: none;
      margin-left: -1px;
    }

    .rightbanner .Twobianyuan .answer li{
      height: 100px;
      border: 1px solid black;
      margin-top: -1px;
      text-align: center;
      line-height: 28px;
    }

    .rightbanner .Twobianyuan .answer .answer_char{
      height: 100px;
      border: 3px solid black;
      text-align: center;
      line-height: 40px;
    }
    /* 三变原CSS */

    .rightbanner .Tbianyuan .enter ul{
      height: 500px;
      width: 100px;
      float: left;
      list-style: none;
      margin-left: -1px;
    }
    .rightbanner .Tbianyuan .enter li{
      height: 55px;
      border: 1px solid black;
      margin-top: -1px;
      text-align: center;
      line-height: 28px;
    }
    .rightbanner .Tbianyuan .enter .character{
      height: 50px;
      border: 3px solid black;
      text-align: center;
      line-height: 40px;
    }

    .rightbanner .Tbianyuan .answer ul{
      height: 500px;
      width: 300px;
      float: left;
      list-style: none;
      margin-left: -1px;
    }

    .rightbanner .Tbianyuan .answer li{
      height: 55px;
      border: 1px solid black;
      margin-top: -1px;
      text-align: center;
      line-height: 28px;
    }

    .rightbanner .Tbianyuan .answer .answer_char{
      height: 50px;
      border: 3px solid black;
      text-align: center;
      line-height: 40px;
    }
    /* 四变原 */

    .rightbanner .Fbianyuan .enter ul{
      height: 500px;
      width: 74px;

      float: left;
      list-style: none;
      margin-left: -1px;
    }
    .rightbanner .Fbianyuan .enter li{
      height: 28px;
      border: 1px solid black;
      margin-top: -1px;
      text-align: center;
      line-height: 28px;
    }
    .rightbanner .Fbianyuan .enter .character{
      height: 40px;
      border: 3px solid black;
      text-align: center;
      line-height: 40px;
    }
    .rightbanner .Fbianyuan .answer ul{
      height: 500px;
      list-style: none;

    }
    .rightbanner .Fbianyuan .answer li{
      height: 28px;
      border: 1px solid black;
      margin-top: -1px;
      text-align: center;
      line-height: 28px;
      list-style: none;
    }
    .rightbanner .Fbianyuan .answer .answer_char{
      height: 40px;
      border: 3px solid black;
      text-align: center;
      line-height: 40px;
    }
    body{
      background-color: #ececec42;
    }
  </style>

