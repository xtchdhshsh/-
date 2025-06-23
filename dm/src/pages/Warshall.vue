<script setup>
import { onMounted, ref } from 'vue';
import axios from '@/utlis/axios';
import { ElMessage } from 'element-plus';
import QrcodeVue from 'qrcode.vue';

const TIME_PERSTEP = 1000; // 每一步的时间

const num = ref(4); // 元素个数
let n = 0; // num.value
let inputTable = new Array();

const previewVisible = ref(false);
const qrVisible = ref(false);
const qrUrl = ref('');
const previewData = ref({n:0,src:[],answer:[]});

async function createQuestion() {
  const matrixStr = getMatrix();
  if(!matrixStr) return ElMessage.warning('请先填写矩阵');

  try{
    const { data: resultArr } = await axios.post('/warshall', {
      num: parseInt(n),
      stringMatrix: matrixStr
    });
    
    const closure = resultArr[n-1];

    const {data: id} = await axios.post('/api/share', {
         type: 'warshall',
         content: JSON.stringify({
           n:n,
           matrix: inputTable,
            pairs: closure,
         })
    })
    if(!id) {
      ElMessage.error('创建题目失败，请稍后再试');
      return;
    }
    qrUrl.value = `${window.location.origin}/answerWarshall/${id}`;
    qrVisible.value = true;
    ElMessage.success('题目创建成功！');
  }catch(err){
    console.error(err);
    ElMessage.error('创建题目失败，请稍后再试');
    return;
  }
}

const createInputMatrix = () => {
    n = num.value;
    const cellLength = 40;
    const table = document.querySelector("#inputTable");
    const matrix = document.querySelector("#inputMatrix");
    let paddingMatrix = 0;
    const marginTable = parseInt((window.getComputedStyle(table)).marginTop);
    const spaceTable = parseInt((window.getComputedStyle(table)).borderSpacing);
    const mo1 = document.querySelector("#mo1");
    const mo2 = document.querySelector("#mo2");
    let heightMo = 0;
    let mul = 0;
    let diffY = 0;
    //检验是否为大于一的正整数
    const reg = /^[0-9]*[1-9][0-9]*$/;
    if (!(reg.test(n) && n >= 2 && n < 13 || n === "")) { return -1; }
    //清空表格
    table.innerHTML = "";
    // 创建新的表格
    for (let i = 0; i < n; i++) {
        const row = table.insertRow();
        for (let j = 0; j < n; j++) {
            const cell = row.insertCell();
            const input = document.createElement("input");
            input.style.width = `${cellLength}px`;
            input.style.height = `${cellLength}px`;
            cell.appendChild(input);
        }
    }
    // 调整表格位置
    matrix.style.paddingTop = `${n*spaceTable}px`
    paddingMatrix = parseInt((window.getComputedStyle(matrix)).paddingTop);
    heightMo = matrix.scrollHeight;
    if(heightMo === matrix.offsetHeight) {
        switch(+n){
            case 2: heightMo = 143; break;
            case 3: heightMo = 243; break;
            case 4: heightMo = 340; break;
            case 5: heightMo = 430; break;
        }
        heightMo -=  paddingMatrix;
    }
    mul = (n * cellLength + spaceTable * n / 2) / heightMo;
    diffY = heightMo - n * cellLength - n * spaceTable;
    table.style.transform = `translateY(-${diffY - paddingMatrix - marginTable}px)`;
    // 调整括号位置与大小
    mo1.style.transform = `translateY(-${diffY / 2}px) scale(${1},${mul})`;
    mo2.style.transform = `translateY(-${diffY / 2}px) scale(${1},${mul})`;
}

// 生成默认矩阵
onMounted(() => {
    createInputMatrix();
})

// 将输入矩阵的数据转成字符串
function getMatrix() {
    const table = document.querySelector("#inputTable");
    let result = "";
    // 获取数据将下标组成字符串
    for (let i = 0; i < n; i++) {
        inputTable[i] = new Array();
        for (let j = 0; j < n; j++) {
            const inputValue = table.rows[i].cells[j].querySelector("input").value;
            if(inputValue == 1) {
                inputTable[i][j] = inputValue;
                result += `${i} ${j}`;
                if(!(i == n - 1 && j == n - 1)) {
                    result += `,`;
                };
            } else {
                inputTable[i][j] = 0;
            }
        }
    }
    return result;
};

// 将矩阵显示出来
function transformMatrix(data,divName = "") {
    const matrix = document.querySelector(`#${divName}`);
    const span = document.createElement("span");
    span.innerHTML = `
        <math class="mathOutput">
            <mrow>
                <mo>(</mo>
                <mtable>
                    ${data.map(row => `
                        <mtr>
                            ${row.map(cell => `<mtd>${cell}</mtd>`).join("")}
                        </mtr>
                    `).join("")}
                </mtable>
                <mo>)</mo>
            </mrow>
        </math>
        `;
    matrix.appendChild(span);
    return span.offsetWidth;
}

// 点击按钮开始运算
const compute = (() => {
    axios.post(
        "/warshall",
        {
            num: parseInt(n),
            stringMatrix: getMatrix()
        }
    ).then( response => {
        let dataGet = response.data;
        let widthExpression = 0;
        let nLine = 0;
        // 清空
        document.querySelector("#outputMatrix").innerHTML = "";
        document.querySelector("#stepsArea").innerHTML = "";
        // 传递闭包
        widthExpression += transformMatrix(dataGet[n - 1],"outputMatrix");
        // 计算过程
        for (let i = 0; i <= n; i++) {
            setTimeout(() => {
                const matrix = document.querySelector("#stepsArea");
                const span = document.createElement("span");
                const comma = document.createElement("span");
                // A_i =
                span.innerHTML = `<math class = "mathOutput" :style:"fontSize: {{textSize}} + 'px'><msub><mi>A</mi><mn>${i}</mn></msub><mo>=</mo></math>`;
                matrix.appendChild(span);

                if(i === 0) {
                    const initialMatrix = inputTable.slice(0, n).map(row => row.slice(0, n));
                    transformMatrix(initialMatrix, "stepsArea");
                    widthExpression += span.offsetWidth + comma.offsetWidth;
                    nLine = parseInt(matrix.offsetWidth / widthExpression);
                } else {
                    transformMatrix(dataGet[i - 1], "stepsArea");
                }
                // 逗号
                if (i != n) {
                    comma.innerHTML = `<math class = "mathOutput"><mo>,</mo></math>`;
                    matrix.appendChild(comma);
                }
                // 换行
                if((i + 1) % (nLine - 1) == 0){
                    comma.innerHTML += `<br>`;
                }
            }, i * TIME_PERSTEP);
        }
    })
})
</script>

<template>
    <main>
        <div class="inputAndOutput">
            <!-- 输入 -->
            <div class="inputArea">
                <h2>关系矩阵</h2>
                <div id="inputFunction">
                    <label>
                        元素个数：<input id="inputN" v-model="num" @input="createInputMatrix" placeholder="请输入2到12之间的正整数">
                    </label>
                    <button class="new_question" @click="createQuestion">新建题目</button>
                    <button id="submitBtn" @click="compute">开始运算</button>
                </div>
                <div id="inputMatrix">
                    <math>
                        <mo id="mo1">(</mo>
                        <mrow id="mrow">
                            <mi id="mi">
                                <table id="inputTable"></table>
                            </mi>
                        </mrow>
                        <mo id="mo2">)</mo>
                    </math>
                </div>
            </div>
            <!-- 最终结果 -->
            <div class="outputArea">
                <h2>最终结果</h2>
                <div id="outputMatrix"></div>
            </div>
        </div>
        <!-- 中间步骤 -->
        <div class="steps">
            <h2>计算过程</h2>
            <div id="stepsArea"></div>
        </div>
      <!-- 题目预览弹窗：只展示关系矩阵，学生看不到答案 -->
      <el-dialog v-model="previewVisible"
                 title="题目预览"
                 width="430px"
                 :close-on-click-modal="false">
        <template #default>
          <p><strong>元素个数：</strong>{{ previewData.n }}</p>
          <table class="preview-matrix">
            <tr v-for="(row,r) in previewData.src" :key="r">
              <td v-for="(c,idx) in row" :key="idx">{{ c }}</td>
            </tr>
          </table>
        </template>
        <template #footer>
          <el-button type="primary" @click="previewVisible=false">确定</el-button>
        </template>
      </el-dialog>

      <!-- 二维码弹窗 -->
      <el-dialog v-model="qrVisible"
                 title="题目二维码"
                 width="320px"
                 :close-on-click-modal="false">
        <div class="qr-box">
          <qrcode-vue :value="qrUrl" :size="256" />
          <p class="qr-link">{{ qrUrl }}</p>
        </div>
      </el-dialog>
    </main>
</template>

<style scoped>
main{
    width: 1240px;
    margin: 0 auto;
}
h2 {
    background-color: #e3e2e2;
    color: #333;
    border-bottom: 1px solid #333;
}
.inputAndOutput {
    display: flex;
    justify-content: space-between;
}
.steps {
    margin-top: 10px;
}
.steps h2{
    border: 1px solid #333;
    border-bottom: 0px;
}
.inputArea {
    width: 50%;
    height: 500px;
    margin-right: 25px;
    border: 1px solid #333;
}
.outputArea {
    width: 50%;
    margin-left: 25px;
    border: 1px solid #333;
}
#stepsArea {
    width: 100%;
    height: 800px;
    border: 1px solid #333;
    overflow: auto;
}
#inputFunction{
    display: flex;
    justify-content: space-between;
    padding: 5px 5px;
    border-bottom: 1px solid #333;
}
#inputN{
    min-width: 180px;
    height: 30px;
    font-size: 16px;
}
#submitBtn{
    min-width: 120px;
    height: 30px;
    font-size: 16px;
    background-color: #d1c8c8;
    cursor: pointer;
}
#submitBtn:hover{
    background-color: #c5f0c5;
}
#inputMatrix{
    display: flex;
    justify-content: center;
    padding-top: 20px;
    height: 420px;
    overflow: auto;
}
#inputTable{
    margin: 10px;
    border-spacing: 5px;
}
#outputMatrix{
    text-align: center;
    overflow: auto;
}
.preview-matrix{
  border-collapse:collapse;
  margin:8px 0;
}
.preview-matrix td{
  border:1px solid #666;
  width:26px;
  height:26px;
  text-align:center;
}
.qr-box{
  display:flex;
  flex-direction:column;
  align-items:center;
  gap:12px;
}
.qr-link{
  word-break:break-all;
  font-size:12px;
  color:#666;
  text-align:center;
}
</style>

<style>
.mathOutput{
    font-size: 28px;
    padding: 10px;
    border-spacing: 10px;
}
</style>