<script setup>
import { ref, reactive, onMounted, computed, nextTick } from 'vue'
import { useRoute, useRouter } from 'vue-router'
import * as echarts from 'echarts'
import axios         from '@/utlis/axios'
import { ElMessage } from 'element-plus'

/* ------------------------------------------------------------------ */
/* 基础状态                                                             */
/* ------------------------------------------------------------------ */
const route   = useRoute()
const router  = useRouter()

const loaded  = ref(false)   // 题目是否加载完
const posting = ref(false)   // 是否正在提交

/* ---------------- 题目信息 ---------------- */
const n        = ref(0)
const nodesArr = ref([])          // ["A","B",...]
const linksArr = ref([])          // [[u,v,w], ...]
const start    = ref('')          // 起点

/* ---------------- 学生答案 ---------------- */
const answer = reactive([])       // {target, path, dist}

/* ------------------------------------------------------------------ */
/* 载入题目                                                             */
/* ------------------------------------------------------------------ */
onMounted(loadQuestion)

async function loadQuestion () {
  try {
    const { data } = await axios.get(`/api/share/${route.params.id}`)
    const payload  = JSON.parse(data.content || '{}')

    if (payload.type !== 'dijkstra' && data.type !== 'dijkstra') {
      throw new Error('类型错误')
    }

    n.value        = payload.count
    nodesArr.value = payload.node
    linksArr.value = payload.link
    start.value    = payload.start

    /* 生成空白答案行 */
    answer.splice(0, answer.length)     // 清空
    nodesArr.value.forEach(t => {
      if (t !== start.value) answer.push({ target:t, path:'', dist:'' })
    })

    loaded.value = true
    await nextTick()
    drawGraph()                         // 画 ECharts
  } catch (e) {
    console.error(e)
    ElMessage.error('加载失败')
    router.replace('/')
  }
}

/* ------------------------------------------------------------------ */
/* 画图                                                                 */
/* ------------------------------------------------------------------ */
let chartInstance
function drawGraph () {
  const dom = document.getElementById('graph')
  if (!dom) return           // 防御
  chartInstance = echarts.init(dom)

  /* === 给节点一个“手动环形”初始坐标，可拖拽 === */
  const R   = 180
  const cx  = 250, cy = 220
  const data = nodesArr.value.map((name, i) => {
    const θ = Math.PI - (2 * Math.PI * i) / nodesArr.value.length
    return {
      name,
      x: cx + R*Math.cos(θ),
      y: cy + R*Math.sin(θ),
      itemStyle:{ color : name===start.value ? '#F4606C':'#6CA6CD' }
    }
  })

  const links = linksArr.value.map(e => ({
    source : e[0],
    target : e[1],
    value  : e[2]
  }))

  chartInstance.setOption({
    tooltip : {
      formatter: p => (p.dataType==='edge')
          ? `${p.data.source} - ${p.data.target} : ${p.data.value}`
          : p.data.name
    },
    series : [{
      type        : 'graph',
      layout      : 'none',
      draggable   : true,
      roam        : true,
      label       : { show:true, fontWeight:600, color:'#fff' },
      edgeLabel   : { show:true, fontSize:14, formatter:'{c}' },
      symbolSize  : 46,
      lineStyle   : { width:4 },
      data, links
    }]
  })
}

/* ------------------------------------------------------------------ */
/* 提交答案                                                             */
/* ------------------------------------------------------------------ */
async function submit () {
  /* 简单校验 */
  for (const row of answer) {
    if (!row.path.trim() || row.dist==='' || isNaN(+row.dist)) {
      ElMessage.warning('所有目标点都必须填写路径和数值')
      return
    }
  }
  posting.value = true
  try {
    await axios.post('/api/answer', {
      questionId : route.params.id,
      answer     : answer.map(r => ({ ...r, dist:+r.dist }))
    })
    ElMessage.success('提交成功')
    router.back()
  } catch (e) {
    console.error(e)
    ElMessage.error('提交失败')
  } finally {
    posting.value = false
  }
}

/* ------------------------------------------------------------------ */
/* 布局判断（≤7: 左右；>7: 上下）                                       */
/* ------------------------------------------------------------------ */
const layoutClass = computed(() => (nodesArr.value.length <= 7 ? 'side' : 'stack'))
</script>

<template>
  <div v-if="loaded" class="wrapper">
    <h2 class="title">
      Dijkstra&nbsp;最短路（起点&nbsp;{{ start }}）
    </h2>
    <div>求以下无向带权图中顶点XX到其他顶点的最短路径及长度</div>
    <!-- 图 + 输入表 -->
    <div :class="zone">
      <!-- 图形 -->
      <div id="graph" class="graph"></div>

      <!-- 表格输入 -->
      <table class="ans-table">
        <thead>
        <tr><th>目标</th><th>最短路径</th><th>距离</th></tr>
        </thead>
        <tbody>
        <tr v-for="row in answer" :key="row.target">
          <td>{{ row.target }}</td>
          <td><input v-model="row.path"  placeholder="A->B->…" /></td>
          <td><input v-model="row.dist"  placeholder="数字"     /></td>
        </tr>
        </tbody>
      </table>
    </div>

    <el-button
        type="primary"
        class="submit-btn"
        :loading="posting"
        @click="submit"
    >提交答案</el-button>
  </div>

  <div v-else class="loading">题目加载中…</div>
</template>

<style scoped>
.wrapper { max-width: 1100px; margin: 20px auto; padding: 0 12px; }
.title   { text-align:center; margin-bottom:16px; }

.zone{
 display: flex;
 flex-direction: column;     /* 纵向 */
  align-items: center;
  gap: 24px;                  /* 图与表的间距 */
}

.graph      { width: 100%; max-width: 700px; height: 440px;
  border:1px solid #aaa; border-radius:6px; margin:0 auto; }

.ans-table  { width: 100%; max-width: 700px; border-collapse:collapse;
  margin: 0 auto; }

.ans-table th,
.ans-table td{ border:1px solid #666; padding:6px 10px; text-align:center; }
.ans-table input{ width:100%; border:none; outline:none; text-align:center; }

.submit-btn { display:block; margin:26px auto 0; }

.loading    { text-align:center; padding:60px 0; color:#666; }
</style>
