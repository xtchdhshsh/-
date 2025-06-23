<script setup>
import { reactive, ref, onMounted } from 'vue'
import axios from '@/utlis/axios'
import { ElMessage } from 'element-plus'
import QrcodeVue from 'qrcode.vue'

const qrVisible = ref(false)
const qrUrl     = ref('')
const emit = defineEmits(["getInputForm", "getResponse"])
const minCount = 3


const rule = reactive({
  count: [{ required: true }],
  node: [{ required: true, message: '请输入节点', trigger: 'change' }],
})

async function createQuestion () {
  if (!inputForm.count || inputForm.node.length < minCount) {
    ElMessage.warning('请先完成节点设置')
    return
  }
  if (!inputForm.edges.length) {
    ElMessage.warning('请至少添加一条边')
    return
  }
  if (!nodeSelected.value) {
    ElMessage.warning('请选择起点')
    return
  }
  // 题目内容（答案暂无，用 AnswerDijkstra 页面去判）
  const payload = {
        count : inputForm.count,
        node  : inputForm.node,
        link  : inputForm.edges.map(e => [e.node1, e.node2, e.weight]),
        start : nodeSelected.value
  }
  try {
    const { data: id } = await axios.post('/api/share', {
          type   : 'dijkstra',            // ← 分类
          content: JSON.stringify(payload)
    })
    ElMessage.success(`题目已创建`)
    qrUrl.value  = `${window.location.origin}/answerDijkstra/${id}`
    qrVisible.value = true
  } catch (e) {
    console.error(e)
    ElMessage.error('创建失败')
  }
}

// ======== 表单数据 ========
const inputForm = reactive({
  count: 0,   // 节点个数
  node: [],   // 节点列表
  edges: []   // 已添加的边
})

// 用于传给父组件/后端的
const transferForm = reactive({
  count: 0,
  node: [],
  link: []
})

// 选定起点
const nodeSelected = ref()

// 验证
const ruleForm = ref()
const rules = reactive({
  count: [{ required: true }],
  node: [{ required: true, message: '请输入节点', trigger: 'change' }],
})

// ========== 1) 初始化：根据节点数生成默认节点 ==========
function showNode(n) {
  inputForm.node = []
  for (let i = 0; i < n; i++) {
    inputForm.node.push(String.fromCharCode(65 + i))
  }
}

// 若还支持自定义输入（逗号分隔）
function changeNode(value) {
  inputForm.node = value.split(',').map(v => v.trim())
}

// ========== 2) “添加边”表单（小区域） ==========
const newEdgeForm = reactive({
  node1: '',
  node2: '',
  weight: 1
})

function addEdge() {
  if (!newEdgeForm.node1 || !newEdgeForm.node2) {
    alert('请选择起点和终点')
    return
  }
  if (newEdgeForm.node1 === newEdgeForm.node2) {
    alert('起点和终点不能相同')
    return
  }
  if (newEdgeForm.weight <= 0) {
    alert('权值必须大于0')
    return
  }

  // 插入 edges
  inputForm.edges.push({
    node1: newEdgeForm.node1,
    node2: newEdgeForm.node2,
    weight: newEdgeForm.weight
  })

  // 重置
  newEdgeForm.node1 = ''
  newEdgeForm.node2 = ''
  newEdgeForm.weight = 1
}

// ========== 3) 删除边 ==========
function removeEdge(index) {
  inputForm.edges.splice(index, 1)
}

// ========== 4) 提交生成 ==========
async function submitForm(ruleForm) {
  if (!ruleForm) return
  await ruleForm.validate(valid => {
    if (valid) {
      transferForm.count = inputForm.count
      transferForm.node = inputForm.node
      transferForm.link = inputForm.edges.map(e => [ e.node1, e.node2, e.weight ])
      emit("getInputForm", transferForm)
    }
  })
}

// ========== 5) 重置表单 ==========
function resetForm(ruleForm) {
  if (!ruleForm) return
  ruleForm.resetFields()

  showNode(minCount)
  inputForm.edges = []
  newEdgeForm.node1 = ''
  newEdgeForm.node2 = ''
  newEdgeForm.weight = 1
}

// ========== 6) 查找路径 ==========
function createPowerTable() {
  let powerTable = []
  inputForm.node.forEach(col => {
    let rowArr = []
    inputForm.node.forEach(row => {
      if (col === row) {
        rowArr.push(0)
      } else {
        let power = -1
        for (let link of transferForm.link) {
          if (
            (col === link[0] && row === link[1]) ||
            (col === link[1] && row === link[0])
          ) {
            power = +link[2]
            break
          }
        }
        rowArr.push(power)
      }
    })
    powerTable.push(rowArr)
  })
  return powerTable
}

function searchPath() {
  axios.post("/dijkstra", {
    num: inputForm.count,
    powerTable: createPowerTable(),
    nodeSelected: nodeSelected.value.charCodeAt() - 65
  })
  .then(response => {
    let { jieguo: valueTable, lujing: result } = response.data
    let resp = {}
    let path = []
    let nodeStep = []
    let pathSort = []
    let count = 0

    result = result.map(r => r.split(":"))
    result.forEach(r => {
      r[0] = [...r[0]]
      pathSort.push(+r[1])
    })
    pathSort.sort((a, b) => a - b)

    nodeStep.push(nodeSelected.value)
    while(result.length !== 0) {
      const lenR = result.length
      for(let i = 0; i < lenR; i++) {
        if(result[i][1] == pathSort[inputForm.count - 1 - lenR]) {
          const lenRI = result[i][0].length
          path.push(result[i])
          nodeStep.push(result[i][0][lenRI - 1])
          result.splice(i, 1)
          break
        }
      }
      count++
      if(count > 20) break
    }

    emit("getResponse", resp = {
      nodeSelected: nodeSelected,
      path: path,
      valueTable: valueTable,
      nodeStep: nodeStep
    })
  })
}

// ========== 7) onMounted 初始化 ==========
onMounted(() => {
  showNode(minCount)
})
</script>

<template>
  <el-form
    ref="ruleForm"
    :model="inputForm"
    :rules="rules"
    class="inputArea"
  >
    <!-- 基本信息 -->
    <el-form-item label="节点个数" prop="count">
      <el-input-number
        v-model="inputForm.count"
        :min="minCount"
        :max="10"
        @change="showNode"
      />
    </el-form-item>
    <el-form-item label="节点信息" prop="node">
      <el-input
        v-model.trim="inputForm.node"
        @change="changeNode"
      />
    </el-form-item>

    <!-- 添加边：占用区域小一些，做成单行或两行排布 -->
    <div class="add-edge-inline">
      <span>起点</span>
      <el-select v-model="newEdgeForm.node1" placeholder="起点" style="width: 70px;">
        <el-option
          v-for="(n, idx) in inputForm.node"
          :key="idx"
          :label="n"
          :value="n"
        />
      </el-select>

      <span>终点</span>
      <el-select v-model="newEdgeForm.node2" placeholder="终点" style="width: 70px;">
        <el-option
          v-for="(n, idx) in inputForm.node"
          :key="idx"
          :label="n"
          :value="n"
        />
      </el-select>

      <span>权值</span>
      <el-input-number
        v-model="newEdgeForm.weight"
        :min="1"
        style="width: 100px;"
      />

      <el-button type="primary" @click="addEdge">
        添加
      </el-button>
    </div>

    <!-- 已添加的边：容器固定高度并可滚动 -->
    <div class="edges-list">
  <div
    v-for="(edge, index) in inputForm.edges"
    :key="index"
    class="edge-item"
  >
    <!-- 显示边信息 -->
    <span class="edge-text">
      {{ edge.node1 }} - {{ edge.node2 }} : {{ edge.weight }}
    </span>
    <!-- 删除按钮 -->
    <el-button
      type="danger"
      size="small"
      @click="removeEdge(index)"
    >
      删除
    </el-button>
  </div>
</div>
    <!-- 操作按钮区 -->
    <div class="buttons">
      <el-form-item>
        <el-button type="primary" @click="submitForm(ruleForm)">生成</el-button>
        <el-button @click="resetForm(ruleForm)">重置</el-button>
        <el-button type="success" @click="createQuestion">新建题目</el-button>
      </el-form-item>

      <el-form-item>
        <el-select class="selectNode" v-model="nodeSelected" placeholder="选择起点">
          <el-option
            v-for="item in inputForm.node"
            :key="item"
            :label="item"
            :value="item"
          />
        </el-select>
        <el-button @click="searchPath" type="success">查找路径</el-button>
      </el-form-item>

      <el-dialog v-model="qrVisible" title="题目二维码" width="320px" :close-on-click-modal="false">
          <div style="display:flex;flex-direction:column;align-items:center;gap:12px;">
            <qrcode-vue :value="qrUrl" :size="260" />
            <p style="word-break:break-all;text-align:center;">{{ qrUrl }}</p>
          </div>
        </el-dialog>
    </div>
  </el-form>
</template>

<style scoped>
.inputArea {
  width: 500px;
  border: 1px solid #666;
  border-radius: 8px;
  padding: 15px;
}

/* 添加边的区域 - 占用小一些，可用一行或两行 */
.add-edge-inline {
  display: flex;
  align-items: center;
  gap: 6px;
  margin: 10px 0;
}

/* 列表容器 - 固定高度 + overflow auto */
.edges-list {
  /* 固定或最大高度，超出可滚动 */
  max-height: 70px;      /* 可根据需求调整 */
  overflow-y: auto;
  
  /* 让内部网格自动排成2列 */
  display: grid;
  grid-template-columns: 1fr 1fr;  /* 2 列平均分 */
  gap: 8px;                        /* 网格项之间的间隙 */
}

/* 每个边是一格网格项 */
.edge-item {
  display: flex;                /* 一行内排 */
  align-items: center;
  justify-content: space-between;
  padding: 4px;
  border: 1px solid #eee;       /* 简单分隔样式 */
  border-radius: 4px;
}

/* 文本部分，可加一些间距或其他样式 */
.edge-text {
  margin-right: 8px;
}


/* 下方按钮区域 */
.buttons {
  display: flex;
  justify-content: space-between;
  padding: 0 10px;
  margin-top: 10px;
}

.selectNode {
  width: 100px;
  margin: 0 8px;
}
</style>
