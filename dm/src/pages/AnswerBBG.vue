<!-- src/pages/AnswerBBG.vue -->
<script setup>
import { ref, reactive, nextTick, onMounted } from 'vue'
import { useRoute, useRouter } from 'vue-router'
import * as echarts   from 'echarts'
import axios          from '@/utlis/axios'
import { ElMessage }  from 'element-plus'

/* ---------------- 基础状态 ---------------- */
const route  = useRoute()
const router = useRouter()
const loaded = ref(false)

const nL   = ref(0)              // 左侧点数
const nR   = ref(0)              // 右侧点数
const LArr = ref([])             // ["L_A", …]
const RArr = ref([])             // ["R_C", …]
const edges = ref([])            // [["L_A","R_C"], …]

const matchMode = ref('maximum') // 'maximum' | 'perfect'

/* ---------------- 用户选择 ---------------- */
const chosen    = reactive(new Set())   // "L_A-R_C"
const chosenStr = ref('')

/* ---------------- ECharts ---------------- */
let chart        = null
let echartsLinks = []          // 保存 link 样式，便于修改后刷新

/* ---------------- 加载题目 ---------------- */
onMounted(async () => {
  try {
    const { data } = await axios.get(`/api/share/${route.params.id}`)
    const p = typeof data.content === 'string' ? JSON.parse(data.content) : data.content
    if ((data.type ?? 'bipartite') !== 'bipartite') throw new Error('题目类型不匹配')

    nL.value  = p.count1
    nR.value  = p.count2
    LArr.value = p.node1
    RArr.value = p.node2
    edges.value= p.edges
    matchMode.value = p.matchType || 'maximum'

    loaded.value = true
    await nextTick()
    drawGraph()
  } catch (e) {
    console.error(e)
    ElMessage.error('题目加载失败')
    router.back()
  }
})

/* ---------------- 绘制图形 ---------------- */
function drawGraph () {
  if (!chart) chart = echarts.init(document.getElementById('graph'))

  // 左 / 右列纵向等距
  const stepL = 400 / (nL.value + 1)
  const stepR = 400 / (nR.value + 1)

  const data = [
    ...LArr.value.map((name, i) => ({
      name, x:100, y:50 + stepL * (i + 1),
      itemStyle:{ color:'#6CA6CD' }
    })),
    ...RArr.value.map((name, i) => ({
      name, x:400, y:50 + stepR * (i + 1),
      itemStyle:{ color:'#6CA6CD' }
    }))
  ]

  echartsLinks = edges.value.map(([u, v]) => ({
    source:u, target:v,
    lineStyle:{ color:'#999', width:4 }
  }))

  chart.setOption({
    tooltip:{
      formatter:p=>p.dataType==='edge'
        ? `${p.data.source} – ${p.data.target}`
        : p.data.name
    },
    series:[{
      type:'graph',
      layout:'none',
      data,
      links:echartsLinks,
      symbolSize:40,
      label:{ show:true, color:'#fff' },
      lineStyle:{ width:4 },
      roam:true, draggable:false
    }]
  })

  chart.off('click')           // 防止重复绑定
  chart.on('click', handleEdgeClick)
}

/* ---------------- 点击边 ---------------- */
// 点击连线后的处理
function handleEdgeClick (p) {
  // 只处理边
  if (p.dataType !== 'edge') return;

  const key = `${p.data.source}-${p.data.target}`;
  const u   = p.data.source;
  const v   = p.data.target;

  /* ---------- 顶点冲突检查 ---------- */
  // 已经选中的边里，若有一条跟当前边共享端点，则冲突
  const conflict = [...chosen].some(str => {
    const [x, y] = str.split('-');
    return x === u || x === v || y === u || y === v;
  });
  // 若本来没选它且有冲突，直接返回
  if (!chosen.has(key) && conflict) {
    ElMessage.warning('匹配边不能共用同一节点');
    return;
  }

  /* ---------- 选/取消 ---------- */
  if (chosen.has(key)) chosen.delete(key);
  else                 chosen.add(key);

  // 回显到输入框
  chosenStr.value = [...chosen].join(', ');

  /* ---------- 重新拼 links 数组并刷新 ---------- */
  echartsLinks = edges.value.map(([s, t]) => {
    const k        = `${s}-${t}`;
    const selected = chosen.has(k);
    return {
      source    : s,
      target    : t,
      lineStyle : {
        color : selected ? '#19CAAD' : '#999',
        width : selected ? 6         : 4
      }
    };
  });

  chart.setOption({ series: [ { links: echartsLinks } ] }, false, true);
}


/* ---------------- 提交答案 ---------------- */
async function submit () {
  if (chosen.size === 0) {
    ElMessage.warning('请至少选择一条匹配边')
    return
  }
  try {
    await axios.post('/api/answer', {
      questionId : route.params.id,
      answer     : [...chosen]        // ["L_A-R_C", …]
    })
    ElMessage.success('提交成功')
    router.back()
  } catch (e) {
    console.error(e)
    ElMessage.error('提交失败')
  }
}
</script>

<template>
  <div v-if="loaded" class="wrapper">
    <h2 class="title">二部图匹配</h2>
    <div v-if="matchMode==='maximum'">
      求以下二部图的 <strong>最大匹配</strong>
    </div>
    <div v-else>
      求以下二部图的 <strong>完备匹配</strong>（若存在）
    </div>

    <div id="graph" class="graph"></div>

    <el-input v-model="chosenStr" placeholder="已选匹配边…" readonly class="edge-input"/>

    <el-button type="primary" @click="submit">提交答案</el-button>
  </div>

  <div v-else class="loading">题目加载中…</div>
</template>

<style scoped>
.wrapper    { max-width: 640px; margin:20px auto; text-align:center }
.title      { margin-bottom:14px }
.graph      { width:100%; height:460px; border:1px solid #666; border-radius:6px }
.edge-input { max-width:520px; margin:12px auto }
.loading    { padding:60px 0; color:#666 }
</style>
