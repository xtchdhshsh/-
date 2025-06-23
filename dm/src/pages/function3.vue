<script setup>
import axios from '@/utlis/axios'
import { onMounted,ref } from 'vue';
import { ElMessage } from 'element-plus';
import QrcodeVue from 'qrcode.vue';

const previewDialogVisible = ref(false)
const qrDialogVisible = ref(false);

const previewData = ref({ sets: { a: [], b: [], c: [] }, formula: '', answer: '' })
const qrUrl = ref('')

const studentAnswer = ref('')
// 加载 d3
onMounted(() => {
    const scriptD3 = document.createElement('script');
    scriptD3.src = 'https://d3js.org/d3.v4.min.js';
    scriptD3.onload = () => {
        const scriptVenn = document.createElement('script');
        scriptVenn.src = '/venn.js-master/venn.js';
        document.body.appendChild(scriptVenn);
    }
    document.body.appendChild(scriptD3);
});

const tipbtn = value => {
    const formula = document.querySelector('.formula');
    // 获取当前光标位置
    const cursorPosition = formula.selectionStart;
    // 获取光标前后的文本
    const startText = formula.value.substring(0, cursorPosition);
    const endText = formula.value.substring(cursorPosition);
    const textToInsert=value;
    formula.value = startText + textToInsert + endText;
    // formula.addEventListener('blur',()=>{
    //   formula.focus();
    // })
    //设置光标位置
    formula.setSelectionRange(cursorPosition + textToInsert.length, cursorPosition + textToInsert.length);
}

// 读取集合输入的工具
function readSet(selector) {
  const v = document.querySelector(selector).value.trim()
  return v === '' ? [] : v.split(',').map(Number)
}

//提交按钮 事件监听
const submitBtn = () => {
    const submit = document.querySelector('.submit');
    //获取集合表单信息 并转换成数字型数组
    const Aenter = document.querySelector('.Aenter');
    const Aarr = Aenter.value.trim() === '' ? [] : Aenter.value.split(',').map(Number);

    const Benter = document.querySelector('.Benter');
    const Barr = Benter.value.trim() === '' ? [] : Benter.value.split(',').map(Number);

    const Center = document.querySelector('.Center');
    const Carr = Center.value.trim() === '' ? [] : Center.value.split(',').map(Number);

    const formula = document.querySelector('.formula');
    let F = formula.value;

    let Darr = [];
    if (F.includes('a')) {
        Darr.push('A');
    }
    if (F.includes('b')) {
        Darr.push('B');
    }
    if (F.includes('c')) {
        Darr.push('C');
    }


    // 数据交互
    axios({
        url:'http://localhost:8080/jihe',
        method:'POST',
        data:{
        a:Aarr,
        b:Barr,
        c:Carr,
        shizi:F
        }})
        .then(result => {
        console.log(result)  
        let answer = document.querySelector('.answer')
        // 判断结果是否为空，若为空则显示Ø
        if (!result.data || result.data.length === 0) {
            answer.innerHTML = 'Ø';
        } else {
            answer.innerHTML = result.data;
        }
        
        // define sets and set set intersections
        let temp = result.data.length
        console.log(temp)
        for(let i=0;i<result.data.length;i++){
            result.data[i]=result.data[i]-0
        }

    // 生成set、power排列
    const createPermutation = (numArr) => {
    let count = 0;
    const origin = new Array();
    const permutation = new Array();
    function helper(path, index) {
            if (path.length > 0) {
            origin.push(path);
            }
            for (let i = index; i < numArr.length; i++) { helper([...path, numArr[i]], i + 1); }
        }
        helper([], 0);
        for (let i = 0; i < numArr.length; i++) {
            origin.forEach(str => {
                if(str.length === i + 1){
                    permutation[count] = str;
                    count += 1;
                }
            });
        }
        return permutation;
    };

    // 组合数
    const combination = (n, m) =>{
    const factorial = n => {
        if(n == 0 || n ==1){ return 1; }
        return n * factorial(n - 1);
    }
    return factorial(n) / (factorial(m) * factorial(n - m))
    }

    // 设定
    const A = Aarr;
    const B = Barr;
    const C = Carr;
    const setsSelected =Darr;
    const table = [A, B, C];
    const re = result.data; // 运算结果

    const sets = new Array();
    const setsPermutation = createPermutation(setsSelected);
    let num = setsSelected.length;
    const number = [...Array(num).keys()].map(i => i + 1);
    let power = createPermutation(number); // 权重
    let indexs = new Array();
    const noIntersection = new Array();



//寻找交集
let AB = []
    for(let i = 0;i<A.length;i++){
    for(let j = 0;j<B.length;j++){
        if(A[i] == B[j]){
        AB.push(A[i])
        }
    }
    }

    let AC = []
    for(let i = 0;i<A.length;i++){
    for(let j = 0;j<C.length;j++){
        if(A[i] == C[j]){
        AC.push(A[i])
        }
    }
    }

    let BC = []
    for(let i = 0;i<B.length;i++){
    for(let j = 0;j<C.length;j++){
        if(B[i] == C[j]){
        BC.push(B[i])
        }
    }
    }
    if(BC == null){
    BC.push(0)
    }

    let ABC = []
    for(let i = 0;i<AB.length;i++){
    for(let j = 0;j<BC.length;j++){
        if(AB[i] == BC[j]){
        ABC.push(AB[i])
        }
    }
    }

    let arrLen = [4*A.length,4*B.length,4*C.length,2*AB.length,2*AC.length,2*BC.length,ABC.length]
    console.log(arrLen)

    // 判断包含和互斥
    function judgeSets(arrA,arrB,type){
    const setA = new Set(arrA);
    const setB = new Set(arrB);
    switch(type){
        case "inclusion" : return [...setA].every(value => setB.has(value)) ? true : false;
        case "exclusivity" : return [...setA].every(value => !setB.has(value)) ? true : false;
    }
    }
    
    // 查找没有交集的两个集合
    for(let i = 0; i < num - 1; i++){
    for(let j = i + 1; j < num; j++){
        if(judgeSets(table[i],table[j],"exclusivity")){
        noIntersection.push([setsSelected[i], setsSelected[j]]);
        }
    }
    }

    // 配置sets
    !function(){
    let count = 0;
    for(let i = 0; i < num; i++){
    const jNum = combination(num, i + 1);
    let circleSize = 4 ** (num - i - 1);
    const delPower = new Array();
    for(let j = 0; j < jNum; j++){
        let flagPush = 1;
        for(let k = 0; k < noIntersection.length; k++){
            if(judgeSets(noIntersection[k],setsPermutation[count],"inclusion")){
                flagPush = 0;
                delPower.push(count);
                break;
            }
        }
        if(flagPush){
            const setOne = {sets: setsPermutation[count],size: circleSize};
            sets.push(setOne);
        }
        count += 1;
    }
    power = power.filter((_, i) => ! delPower.includes(i));
    }}();

    const chart = venn.VennDiagram();

    d3.select("#venn").datum(sets).call(chart);
    // 默认配置
    d3.selectAll("#venn .venn-area path")
        .style("fill-opacity", 1)  // 设置透明度
        .style("stroke", "black");

    // 查找运算结果包含的集合
    re.forEach(data => {
        const setsInvolve = new Array();
        for(let i = 0; i < num; i++){
            table[i].forEach(d => {
                if(d === data) { setsInvolve.push(i + 1);}
            })
        }
        indexs.push(setsInvolve);
    });

    // 获取power下标
    const powerIndex = str =>{
    for(let i = 0; i < power.length; i++){
        if(str.join("") === power[i].join("")) { return i; }
    }
    }

    // 转换为index
    indexs.forEach((str,i) => indexs[i] = +powerIndex(str));

    // 去重
    indexs = [...new Set(indexs)];

    // 填色
    d3.selectAll(".venn-area path")
        .style("fill", (d, i) => {
            let color = "white";
            indexs.sort();
            for(const data of indexs){
                if(i === data){
                    indexs.splice(0,1);
                    color = "LightSkyBlue"
                }
            }
            return color;
        });
    
    // 处理空区域
    const dealWithNull = n => {
    let message = "";
    d3.selectAll(".venn-area path")
        .filter((d, i) => i == n)
        .style("fill", "LightGrey");
    d3.selectAll(".venn-area .label")
        .filter((d, i) => i == n)
        .text(message = n < num ? `${String.fromCharCode('A'.charCodeAt(0) + n)}` + ' ' + '0' : '0');
    }

    // 单个集合没有非交集元素
    !function(){
    for(let i = 0; i < num; i++){
        const setI = new Set(table[i]);
        let tableMix = new Array();
        for(let j = 0;j < num; j++){
        if(j != i){ tableMix =tableMix.concat(table[j]); }
        }
        const setMix = new Set(tableMix);
        if([...setI].every(value => setMix.has(value))){
        dealWithNull(i);
    }
    }
    }();

    // 处理三个集合的交集情况
    !function(){
    const setArr = table.map((row,i) => new Set(table[i]));
    if(num == 3){
        const commonAll = [...setArr[0]].filter(value => setArr[1].has(value) && setArr[2].has(value));
        for(let i = 0; i < num; i++){
        for(let j = i + 1;j < num; j++){
            const commonTwo = [...setArr[i]].filter(value => setArr[j].has(value));
            if(commonAll.join("") == commonTwo.join("")) { dealWithNull(powerIndex([i + 1,j + 1])); }
        }
        }
        // 只是三个集合的交集没有的情况
        if(!commonAll.length){
        dealWithNull(2 ** num - 2);
        }
    }
    }();
    })
}

function openPreview() {
  if (!previewData.value.formula) {
    ElMessage.info('请先提交运算以生成预览')
    return
  }
  previewDialogVisible.value = true
}

// “新建题目”按钮：发送 a,b,c 和公式生成题目
async function createQuestion() {
  const A = readSet('.Aenter')
  const B = readSet('.Benter')
  const C = readSet('.Center')
  const formula = document.querySelector('.formula').value.trim()
  const answer  = document.querySelector('.answer').innerText
  previewData.value = {
    sets: { a: A, b: B, c: C },
    formula,
    answer
  }
  try {
    const { data: id } = await axios.post('/api/share', {
      type:    'set',
      content: JSON.stringify({
        sets: { a: A, b: B, c: C },
        formula,
        answer
      })
    })
    qrUrl.value = `${window.location.origin}/answerSat/${id}`
    qrDialogVisible.value = true
    ElMessage.success('题目创建成功！')
    // 创建成功后直接打开预览弹窗
    previewDialogVisible.value = true
  } catch {
    ElMessage.error('新建题目失败')
  }
}


</script>

<template>
    <body>
    <div class="banner">
        <div class="leftbanner">
        <h1 class="title">集合运算</h1>
        <div class="decoration">
            <div class="tip">请输入集合元素：</div>
            <div class="inputDecoration"><p class="A">a :</p><input type="text" placeholder='例如：1,2,3' class="Aenter"></div>
            <div class="inputDecoration"><p class="B">b :</p><input type="text" placeholder='例如：2,3,4' class="Benter"></div>
            <div class="inputDecoration"><p class="C">c :</p><input type="text" placeholder='例如：3,4,5' class="Center"></div>
            <div class="tip">请输入集合运算式子：</div>
            <input type="button" value="∪" class="tipbut" @click="tipbtn('∪')" data-id="2">
            <input type="button" value="∩" class="tipbut" @click="tipbtn('∩')" data-id="3">
            <input type="button" value="△" class="tipbut" @click="tipbtn('△')" data-id="4">
            <input type="button" value="-" class="tipbut" @click="tipbtn('-')" data-id="5">
            <input type="text" placeholder='例如：a-b' class="formula">
            <button class="submit" @click="submitBtn">提交</button>
          <button class="new-question" @click="createQuestion">新建题目</button>
        </div>

        </div>
        <div class="rightbanner">
        <p>文氏图</p>
        <div id="venn" style='height:350px'></div>
        
        <p>运算结果</p>
        <div class="answer">
           
        </div>
        </div>

      <!-- 二维码弹窗 -->
      <el-dialog
          v-model="qrDialogVisible"
          title="题目二维码"
          width="320px"
          :close-on-click-modal="false"
      >
        <div style="display:flex;flex-direction:column;align-items:center;gap:16px;">
          <qrcode-vue :value="qrUrl" :size="256" />
          <span style="word-break:break-all;text-align:center;">{{ qrUrl }}</span>
        </div>
      </el-dialog>
    </div>
    </body>
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
.banner{
    height: 600px;
    min-width: 1240px;
    display: flex;
}
.banner .leftbanner{
    height: 500px;
    width: 500px;
    border: 1px solid 2px;
    margin-left: 50px;
    margin-top: 20px;
}
.leftbanner h1{
    width: 200px;
    border-bottom: 4px solid black;
    font-size: 50px;
}
.decoration{
    width :400px;
    height:400px;
    margin-left: 50px;
    margin-top: 60px;
    border: 2px solid black;
}
.decoration .tip{
    font-size: 20px;
    padding-top: 10px;
    margin-left: 10px;
    margin-top: 10px;
    
}
.decoration input{
    height:32px;
    width: 220px;
    margin-left: 80px;
    margin-top: 10px;
    margin-bottom: 10px;
}
.decoration .formula{
    height:32px;
    width: 220px;
    margin-left: 60px;
    margin-top: 15px;
    margin-bottom: 10px;
}
.leftbanner .tipbut{
    height:32px;
    width: 40px;
    margin-top: 20px;
    margin-left: 50px;
    float: left;
    display: block;
}
.decoration .submit{
    height:40px;
    width: 60px;
    margin-left: 10px;
}
.decoration .inputDecoration{
    display: flex;
    justify-content: center;
    align-items: center;
}
.decoration .inputDecoration input{
    margin-left: 10px;
}
.decoration .A{
    font-size: 20px;
}
.decoration .B{
    font-size: 20px;
}
.decoration .C{
    font-size: 20px;
}
.rightbanner{
    height: 600px;
    width: 600px;
    margin-top: 20px;
    right: 200px;
    border: 4px solid black;
}
.rightbanner p{
    font-size: 30px;
    margin-left: 20px;
    margin-top: 20px;
}
.rightbanner .answer{
    margin-left: 50px;
    font-size: 50px;
}

#venn{
    border-bottom: 4px solid black;
}
body{
    background-color: #ececec42;
}

.new-question { background-color: #67C23A; color: #fff; border: none; display: flex; padding: 6px 12px; border-radius: 4px; cursor: pointer; transition: background-color 0.3s; }
.new-question:hover { background-color: #5DAE2C; }

</style>