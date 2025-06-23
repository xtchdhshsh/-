<template>
  <div class="banner">
    <!-- 输入区 -->
    <el-card class="leftbanner" style="width: 550px; min-width: 580px;">
      <template #header>
        <div class="title">命题公式真值表</div>
      </template>

      <el-card class="decoration" style="width: 450px; height: 200px;">
        <div class="tip">请输入命题公式:</div>
        <el-button class="tipbut" value="∨">∨</el-button>
        <el-button class="tipbut" value="∧">∧</el-button>
        <el-button class="tipbut" value="→">→</el-button>
        <el-button class="tipbut" value="﹁">﹁</el-button>
        <el-button class="tipbut" value="↔">↔</el-button>
        <el-button class="tipbut" value="()">()</el-button>
        <input type="text" class="enter" placeholder="例如：(p∨q)->r∨s" />
        <el-button type="primary" class="submit">提交</el-button>
        <el-button type="success" class="new-question" @click="openDialog">新建题目</el-button>
      </el-card>
    </el-card>

    <!-- 真值表显示 -->
    <div class="table-container">
      <el-table :data="tableData" border height="500" style="width:auto;" :header-cell-style="{ textAlign:'center', backgroundColor:'#000', color:'#fff' }">
        <el-table-column v-for="(h,i) in tableHeaders" :key="i" :label="h" align="center">
          <template #default="{ row }">{{ row[i] }}</template>
        </el-table-column>
      </el-table>
    </div>

    <!-- ① 题型选择弹窗 -->
    <el-dialog v-model="typeDialogVisible" title="选择题型" width="320px" :close-on-click-modal="false">
      <el-radio-group v-model="questionType" style="display:flex;flex-direction:row;">
        <el-radio label="fill">填空题</el-radio>
        <el-radio label="choice">选择题</el-radio>
      </el-radio-group>
      <template #footer>
        <el-button @click="typeDialogVisible=false">取消</el-button>
        <el-button type="primary" @click="confirmType">下一步</el-button>
      </template>
    </el-dialog>

    <!-- ② 填空 / 选位弹窗 -->
    <el-dialog v-model="fillDialogVisible" :title="questionType==='fill'?'创建填空题':'选择题：点格子编号'" width="80vw" :close-on-click-modal="false">
      <div style="max-height:60vh;overflow:auto;">
        <el-table :data="editableData" border style="width:auto;" :header-cell-style="{ textAlign:'center',backgroundColor:'#000',color:'#fff'}">
          <el-table-column v-for="(h,i) in tableHeaders" :key="i" :label="h" align="center">
            <template #default="{ $index }">
              <span style="cursor:pointer;user-select:none;" @click="toggleBlank($index,i)">{{ displayCell($index,i) }}</span>
            </template>
          </el-table-column>
        </el-table>
      </div>
      <template #footer>
        <el-button v-if="questionType==='fill'" @click="resetBlanks">重置</el-button>
        <el-button v-if="questionType==='choice'" @click="undoLast">撤销</el-button>
        <el-button type="primary" @click="questionType==='fill'?saveFill():saveChoice()">保存题目</el-button>
      </template>
    </el-dialog>

    <!-- 二维码弹窗 -->
    <el-dialog v-model="qrDialogVisible" title="题目二维码" width="320px" :close-on-click-modal="false">
      <div style="display:flex;flex-direction:column;align-items:center;gap:16px;">
        <qrcode-vue :value="qrUrl" :size="256" />
        <span style="word-break:break-all;text-align:center;">{{ qrUrl }}</span>
      </div>
    </el-dialog>
  </div>
</template>

<script setup>
import { ref, onMounted } from 'vue'
import axios from '@/utlis/axios'
import { ElMessage } from 'element-plus'
import QrcodeVue from 'qrcode.vue'

/**************** 真值表原始数据 ****************/
const tableHeaders = ref([])
const tableData    = ref([])

onMounted(()=>{
  const enter=document.querySelector('.enter');
  const submit=document.querySelector('.submit');
  // 逻辑符号按钮插入
  for(let i=2;i<=7;i++){
    const btn=document.querySelector(`.tipbut:nth-child(${i})`)
    if(!btn)continue
    btn.addEventListener('click',()=>{
      const pos=enter.selectionStart
      enter.value=`${enter.value.slice(0,pos)}${btn.value}${enter.value.slice(pos)}`
      enter.setSelectionRange(pos+btn.value.length,pos+btn.value.length)
      enter.focus()
    })
  }
  // 请求真值表
  submit.addEventListener('click',()=>{
    const formula=enter.value.trim(); if(!formula) return
    axios.get(`/zhenzhi/${formula}`).then(({data})=>{
      tableHeaders.value=data.va||[]
      tableData.value   =data.data||[]
      ElMessage.success('获取真值表成功')
    }).catch(()=>ElMessage.error('获取真值表失败'))
  })
})

/**************** 弹窗&状态 ****************/
const typeDialogVisible = ref(false)
const questionType      = ref('fill') 
const fillDialogVisible = ref(false)
const editableData      = ref([])
const blanks            = ref(new Set())       // 用于填空
const blankOrder        = ref([])        // 顺序数组，用于选择题

/**************** 工具函数 ****************/
const keyOf = (r,c)=>`${r}-${c}`

function openDialog(){
  if(!tableData.value.length){ElMessage.info('请先生成真值表');return}
  blanks.value.clear(); blankOrder.value.length=0
  questionType.value='fill'
  typeDialogVisible.value=true
}

function confirmType(){
  typeDialogVisible.value=false
  editableData.value=tableData.value.map(r=>[...r])
  fillDialogVisible.value=true
}

function toggleBlank(r,c){
  const k=keyOf(r,c)
  if(questionType.value==='fill'){
    if(blanks.value.has(k)) blanks.value.delete(k); else blanks.value.add(k)
  }else{ // choice 顺序编号
    if(blankOrder.value.includes(k)){
      // 取消该格并重排序号
      blankOrder.value=blankOrder.value.filter(v=>v!==k)
    }else{
      blankOrder.value.push(k)
    }
  }
}

function displayCell(r,c){
  const k=keyOf(r,c)
  if(questionType.value==='fill') return blanks.value.has(k)?'':editableData.value[r][c]
  const idx=blankOrder.value.indexOf(k)
  return idx>-1?`?${idx+1}`:editableData.value[r][c]
}

function resetBlanks(){ blanks.value.clear() }
function undoLast(){ blankOrder.value.pop() }

/**************** 生成选项工具 ****************/
function buildChoiceOptions(){
  // 把选中空位的值按顺序串联生成正确答案
  const correct=blankOrder.value.map(k=>{
    const [r,c]=k.split('-').map(Number)
    return editableData.value[r][c]
  }).join('')
  // 生成干扰项
  const flipOne=(s)=>{
    const arr=s.split('');
    const i=Math.floor(Math.random()*arr.length)
    arr[i]=arr[i]==='1'?'0':'1';
    return arr.join('')
  }
  const set=new Set([correct])
  let guard=0
  while(set.size<4 && guard<100){
    let d=flipOne(correct)
    // 确保至少1位不同且不重复
    if(d!==correct) set.add(d)
    guard++
  }
  const keys=['A','B','C','D']
  return [...set].sort(()=>Math.random()-0.5).map((v,i)=>({key:keys[i],val:v,right:v===correct}))
}

/**************** 二维码与保存 ****************/
const qrDialogVisible=ref(false)
const qrUrl=ref('')

async function saveFill(){
  if(!blanks.value.size){ElMessage.info('请至少挖一个空');return}
  const type = '真值表'
  const payload={
    type:'fill',
    headers:tableHeaders.value,
    data:editableData.value.map((row,r)=>row.map((cell,c)=>blanks.value.has(keyOf(r,c))?null:cell))
  }
  await send(payload, type)
}

async function saveChoice(){
  if(!blankOrder.value.length){ElMessage.info('请选择要作为答案的格子');return}
  const options=buildChoiceOptions()
  if(options.length<4){ElMessage.error('生成选项失败');return}
  const type = '真值表'
  const payload={
    type:'choice',
    headers:tableHeaders.value,
    fullTable:editableData.value,
    blanks:blankOrder.value,   // 记录顺序格子
    options
  }
  await send(payload, type)
}

async function send(payload, type){
  try{
    const body = {
      type: type,
      content: JSON.stringify(payload)
    }
    const {data:id}=await axios.post('/api/share',body)
    if(!id) throw new Error('无效id')
    qrUrl.value=`${window.location.origin}/answer/${id}`
    fillDialogVisible.value=false
    qrDialogVisible.value=true
    ElMessage.success('题目创建成功！')
  }catch(e){console.error(e);ElMessage.error('题目创建失败')}
}
</script>

<style scoped>
.banner{display:flex;justify-content:center;min-width:1240px;}
.leftbanner{width:600px;margin:auto;}
.decoration{position:relative;width:450px;height:200px;margin-top:100px;}
.tip{font-size:20px;padding-top:10px;margin-left:10px;}
.tipbut{margin-top:10px;margin-left:20px;}
.enter{margin-top:30px;margin-left:80px;height:30px;width:220px;padding:0 6px;}
.submit{height:32px;width:60px;margin-left:10px;}
.new-question{height:32px;margin-left:10px;}
.table-container{margin-left:20px;width:100%;height:500px;margin-top:20px;margin-bottom:20px;overflow-x:auto;}
.el-table{width:auto;}
</style>
