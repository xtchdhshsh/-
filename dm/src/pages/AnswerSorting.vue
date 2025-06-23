<!-- src/pages/AnswerSorting.vue -->
<script setup>
import { ref, reactive, computed, watch, onMounted, nextTick } from 'vue'
import { useRoute, useRouter } from 'vue-router'
import draggable from 'vuedraggable'
import axios       from '@/utlis/axios'
import { ElMessage } from 'element-plus'

/* ---------------- 基础状态 ---------------- */
const route     = useRoute()
const router    = useRouter()
const loaded    = ref(false)
const submitted = ref(false)

/* ---------------- 题面数据 ---------------- */
const algo   = ref('')
const origin = ref([])          // 原始数组 [{id,val}]
const work   = ref([])          // 当前操作数组（拖拽 v-model 绑定）

const algoName = computed(()=>{
  const m = {bubble:'冒泡排序', selection:'选择排序', insertion:'插入排序',
             quick:'快速排序',  merge:'归并排序'}
  return m[algo.value] ?? ''
})

/* ---------------- 辅助 UI 状态 ---------------- */
const barState = reactive({})   // id -> 'ok' | 'wrong'

/* ---------- 工具 ---------- */
function asc (arr){
  for (let i=1;i<arr.length;i++) if(arr[i-1]>arr[i]) return false
  return true
}
function clearState(){ Object.keys(barState).forEach(k=>delete barState[k]) }

/* ---------- 初始化 ---------- */
onMounted( async () =>{
  try{
    const { data } = await axios.get(`/api/share/${route.params.id}`)
    const p = typeof data.content==='string' ? JSON.parse(data.content):data.content
    if(data.type!=='sorting') throw new Error('题型不符')
    if(!Array.isArray(p.initArray)||p.initArray.length===0){
      throw new Error('题干缺少 initArray')
    }

    algo.value   = p.algo ?? ''
    origin.value = p.initArray.map((v,i)=>({id:i,val:v}))
    work.value   = origin.value.map(o=>({...o}))

    document.title = `排序题 #${route.params.id}`
    loaded.value   = true
    await nextTick()
  }catch(e){
    console.error(e)
    ElMessage.error(e.message || '题目加载失败')
    router.back()
  }
})

/* ---------- 操作 ---------- */
function reset(){
  work.value = origin.value.map(o=>({...o}))
  submitted.value=false
  clearState()
}

function doubleClickReverse(){
  if(submitted.value) return
  work.value.reverse()
}

async function submit(){
  if(submitted.value) return
  const vals = work.value.map(o=>o.val)
  clearState()

  if(asc(vals)){
    // 全对
    work.value.forEach(o=>barState[o.id]='ok')
  }else{
    // 标记错误位置
    for(let i=0;i<vals.length;i++){
      const should = [...vals].sort((a,b)=>a-b)[i]
      const cur    = work.value[i].val
      if(cur===should) barState[work.value[i].id]='ok'
      else             barState[work.value[i].id]='wrong'
    }
    ElMessage.warning('仍有元素位置不正确，请继续调整')
    return
  }

  /* 提交到后端（仅上传最终序列）*/
  try{
    await axios.post('/api/answer',{
      questionId:route.params.id,
      answer:vals
    })
    submitted.value=true
    ElMessage.success('全部正确，已提交！')
  }catch{
    ElMessage.error('提交失败，请稍后重试')
  }
}
</script>

<template>
  <div v-if="loaded" class="wrapper">
    <h2 class="title">排序题</h2>

    <p class="tip">
      请<strong>按升序</strong>排列下列数组后提交
      <span v-if="algoName">（推荐算法：{{ algoName }}）</span>
    </p>

    <!-- 拖拽条形块 -->
    <draggable
      v-model="work"
      item-key="id"
      animation="200"
      class="drag-zone"
      :disabled="submitted"
    >
      <template #item="{element,index}">
        <div
          class="bar"
          :class="barState[element.id]"
          :style="{height: element.val*3 + 'px'}"
          @dblclick.stop="doubleClickReverse"
        >
          {{ element.val }}
        </div>
      </template>
    </draggable>

    <div class="btn-group">
      <el-button type="info"    @click="reset"  :disabled="false">重置</el-button>
      <el-button type="primary" @click="submit" :disabled="false">提交答案</el-button>
    </div>
  </div>

  <el-skeleton v-else animated :rows="4"/>
</template>

<style scoped>
.wrapper{max-width:680px;margin:30px auto;text-align:center}
.title  {margin-bottom:12px}
.tip    {margin:6px 0 18px;font-size:16px}

.drag-zone{
  display:flex;justify-content:center;align-items:flex-end;gap:16px;
  min-height:260px;                /* 高度也稍微再大一点 */
  border:1px dashed #ccc;padding:24px;border-radius:8px;
  user-select:none
}
.bar{
  width:72px;
  background:#409eff;
  color:#000;                     /* 黑色数字 */
  border-radius:6px 6px 0 0;
  font-size:14px;
  padding-bottom:4px;
  display:flex;align-items:flex-end;justify-content:center;
  cursor:grab;
  transition:transform .25s;
}
.bar.ok   {background:#67c23a;color:#000}
.bar.wrong{background:#f56c6c;color:#000;animation:shake .3s}
@keyframes shake{
  0%,100%{transform:translateX(0)}
  25%    {transform:translateX(-3px)}
  75%    {transform:translateX( 3px)}
}
.btn-group{margin-top:24px;display:flex;justify-content:center;gap:32px}
</style>
