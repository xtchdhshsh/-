<script setup>
import { ref, reactive, onMounted, nextTick } from 'vue'
import { useRoute, useRouter } from 'vue-router'
import * as echarts  from 'echarts'
import axios         from '@/utlis/axios'
import { ElMessage } from 'element-plus'

/* ----------------- 基础 ----------------- */
const route  = useRoute()
const router = useRouter()
const loaded = ref(false)

const n        = ref(0)
const nodesArr = ref([])
const linksArr = ref([])   // raw edges
const mstEdges = ref([])   // 正确答案（可选）

/* 选中的边（Set）+ 输入框字符串 ---------------- */
const chosen       = reactive(new Set())
const selectedStr  = ref('')     // 显示到输入框

/* ---------------- 画图 ---------------- */
let chart
let links = []                   // 给 ECharts 的 links（需外层可改）

function drawGraph () {
  const dom = document.getElementById('graph')
  chart = echarts.init(dom)

  /* —— 坐标：A 从左侧开始顺时针 —— */
  const R = 180, cx = 240, cy = 220
  const data = nodesArr.value.map((name,i)=>({
    name,
    x: cx + R*Math.cos(Math.PI - 2*Math.PI*i/n.value),
    y: cy + R*Math.sin(Math.PI - 2*Math.PI*i/n.value),
    label:{color:'#fff'}
  }))

  links = linksArr.value.map(([u,v,w])=>({
    source:u, target:v, value:w,
    lineStyle:{ color:'#999', width:4 }
  }))

  chart.setOption({
    tooltip:{
      formatter:p=>p.dataType==='edge'
          ? `${p.data.source}-${p.data.target} : ${p.data.value}`
          : p.data.name
    },
    series:[{
      type:'graph', layout:'none',
      data, links,
      symbolSize:46, roam:true, draggable:true,
      label:{show:true,color:'#fff'},
      edgeLabel:{show:true,formatter:'{c}',fontSize:14},
      lineStyle:{width:4}
    }]
  })

  /* -------- 点击边：高亮 / 取消 -------- */
  chart.on('click', p => {
    if (p.dataType !== 'edge') return

    // 把端点排个序后拼成 key
    const [u, v] = [p.data.source, p.data.target].sort()
    const key    = `${u}-${v}`               // 带连字符更安全

    // 找到 links 数组中对应的两个方向的边
    links.forEach(lk => {
      if ((lk.source === u && lk.target === v) ||
          (lk.source === v && lk.target === u)) {
        if (chosen.has(key)) {
          lk.lineStyle.color = '#999'
        } else {
          lk.lineStyle.color = '#19CAAD'
        }
      }
    })

    // 更新选择集 & 输入框
    if (chosen.has(key)) chosen.delete(key)
    else                 chosen.add(key)
    selectedStr.value = [...chosen].join(',')

    // 通知 ECharts 重新渲染
    chart.setOption({ series: [{ links: [...links] }] }, false, true)
  })

}

/* ---------------- 提交 ---------------- */
async function submit(){
  if(chosen.size!==n.value-1){
    ElMessage.warning(`最小生成树必须正好选 ${n.value-1} 条边`)
    return
  }
  try{
    await axios.post('/api/answer',{
      questionId:route.params.id,
      answer    : [...chosen]      // ["AC","CF",…]
    })
    ElMessage.success('提交成功')
    router.back()
  }catch(e){
    console.error(e)
    ElMessage.error('提交失败')
  }
}

/* ---------------- 加载题目 ---------------- */
onMounted(async()=>{
  try{
    const { data } = await axios.get(`/api/share/${route.params.id}`)
    let p = typeof data.content==='string' ? JSON.parse(data.content) : data.content
    if(data.type!=='mst'&&p.type!=='mst') throw new Error('类型不符')

    n.value=p.count
    nodesArr.value=p.node
    linksArr.value=p.link
    mstEdges.value=p.mst

    loaded.value=true
    await nextTick()
    drawGraph()
  }catch(e){
    console.error('加载题目失败:',e)
    ElMessage.error('题目加载失败')
  }
})
</script>

<template>
  <div v-if="loaded" class="wrapper">
    <h2 class="title">最小生成树&nbsp;</h2>
    <div>求以下带权图的最小生成树及其权的大小</div>
    <!-- 图 -->
    <div id="graph" class="graph"></div>

    <!-- 选中边显示 -->
    <el-input
        v-model="selectedStr"
        placeholder="已选边，如 AC,CF"
        clearable
        class="edge-input"
    />

    <el-button type="primary" class="submit-btn" @click="submit">
      提交答案
    </el-button>
  </div>

  <div v-else class="loading">题目加载中…</div>
</template>

<style scoped>
.wrapper { max-width: 720px; margin: 20px auto; text-align: center; }
.title   { margin-bottom: 14px; }
.graph   { width: 100%; height: 460px; border:1px solid #666; border-radius:6px; }
.edge-input { max-width: 480px; margin: 12px auto; }
.submit-btn { margin-top: 6px; }
.loading { padding: 60px 0; color:#666 }
</style>
