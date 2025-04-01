<script setup>
import { reactive, ref, onMounted } from "vue";
import axios from "@/utlis/axios";

const emit = defineEmits(["getInputForm", "getResponse", "updateMatchingType"]);
const minCount = 2;
const maxCount = 10;

function AMM() {
  console.log("最大匹配");
  emit("updateMatchingType", { isAll: true, isPerfect: false });
}

function PM() {
  console.log("完备匹配");
  emit("updateMatchingType", { isAll: false, isPerfect: true });
}

const inputForm = reactive({
  count1: 2,
  count2: 2,
  node1: [],
  node2: [],
  edges: [],
});

// ========== 用于传给父组件/后端的 ==========
const transferForm = reactive({
  count1: 0,
  count2: 0,
  // 合并后的节点列表
  node1: [],
  node2: [],
  link: [],
});

// “添加边”表单
const newEdgeForm = reactive({
  node1: "",
  node2: "",
});

// 选定的起点（若需要查找路径）
const nodeSelected = ref(null);

// ========== 验证 ==========
const ruleForm = ref();
const rules = reactive({
  count1: [{ required: true, message: "请设置第一个节点集个数" }],
  count2: [{ required: true, message: "请设置第二个节点集个数" }],
});

// ========== 2) 初始化两个节点集 ==========
// 示例：都从 'A' 开始，后续 B、C...
function showNode1(num) {
  inputForm.node1 = [];
  for (let i = 0; i < num; i++) {
    // 这里是 A, B, C, ...
    inputForm.node1.push(String.fromCharCode(65 + i));
  }
}

function showNode2(num) {
  inputForm.node2 = [];
  for (let i = 0; i < num; i++) {
    // 这里也同样用 A, B, C, ...
    // 如果你想完全避免和 node1 重名，可改为  String.fromCharCode(65 + i) + "'" 之类
    inputForm.node2.push(String.fromCharCode(65 + i));
  }
}

// ========== 3) 添加边（只能在 node1 和 node2 间） ==========
function addEdge() {
  if (!newEdgeForm.node1 || !newEdgeForm.node2) {
    alert("请选择起点和终点");
    return;
  }
  // 在 bipartite 里通常是 node1 in Set1, node2 in Set2
  inputForm.edges.push({
    node1: newEdgeForm.node1,
    node2: newEdgeForm.node2,
  });
  // 重置
  newEdgeForm.node1 = "";
  newEdgeForm.node2 = "";
}

// ========== 4) 删除边 ==========
function removeEdge(index) {
  inputForm.edges.splice(index, 1);
}

const charToIndex = (ch) => ch.charCodeAt(0) - "A".charCodeAt(0);
// ========== 5) 提交生成 ==========
async function submitForm(ruleForm) {
  if (!ruleForm) return;
  await ruleForm.validate((valid) => {
    if (valid) {
      // 1) 将 count1, count2 存起来
      transferForm.count1 = inputForm.count1;
      transferForm.count2 = inputForm.count2;
      // 2) 合并两个节点集作为全图节点
      transferForm.node1 = inputForm.node1;
      transferForm.node2 = inputForm.node2;
      // 3) edges => link 形式
      //    [node1, node2, weight]
      transferForm.link = inputForm.edges.map((e) => [e.node1, e.node2]);
      console.log(transferForm);
      emit("getInputForm", transferForm);
    }
  });
  // 这里可以直接调用后端接口，或 emit 给父组件
  axios
    .get("/bipartite/findAllMaxMatching", {
      params: {
        U: inputForm.count1,
        V: inputForm.count2,
        edges: inputForm.edges
          .map((e) => `${charToIndex(e.node1)},${charToIndex(e.node2)}`)
          .join(";"),
      },
    })
    .then((response) => {
      const resp = {
        allMatching: response.data.allMatching.map((item) => {
          return Object.entries(item).map(([key, value]) => {
            return {
              node1: Number(key),
              node2: value,
            };
          });
        }),
        allMatchingCount: response.data.allMatching.length,
        hasPerfect: response.data.hasPerfectMatching,
        perfectMatching: (response.data.perfectMatchings || []).map((item) => {
          return Object.entries(item).map(([key, value]) => {
            return {
              node1: Number(key),
              node2: value,
            };
          });
        }),
        perfectMatchingCount: (response.data.perfectMatchings || []).length,
      };
      console.log("resp", resp);
      emit("getResponse", resp);
    })
    .catch((err) => console.error(err));
}

function resetForm(ruleForm) {
  if (!ruleForm) return;
  ruleForm.resetFields();
  inputForm.count1 = minCount;
  inputForm.count2 = minCount;
  showNode1(minCount);
  showNode2(minCount);
  inputForm.edges = [];
  newEdgeForm.node1 = "";
  newEdgeForm.node2 = "";
  newEdgeForm.weight = 1;
}

onMounted(() => {
  showNode1(inputForm.count1);
  showNode2(inputForm.count2);
});
</script>

<template>
  <el-form ref="ruleForm" :model="inputForm" :rules="rules" class="inputArea">
    <!-- 两个节点集的大小 -->
    <el-row :gutter="20">
      <el-col :span="12">
        <el-form-item label="节点集1个数" prop="count1">
          <el-input-number
            v-model="inputForm.count1"
            :min="minCount"
            :max="10"
            @change="showNode1"
          />
        </el-form-item>
      </el-col>
      <el-col :span="12">
        <el-form-item label="节点集2个数" prop="count2">
          <el-input-number
            v-model="inputForm.count2"
            :min="minCount"
            :max="10"
            @change="showNode2"
          />
        </el-form-item>
      </el-col>
    </el-row>

    <el-form-item label="节点集1" prop="node1">
      <el-input v-model.trim="inputForm.node1" />
    </el-form-item>

    <el-form-item label="节点集2" prop="node2">
      <el-input v-model.trim="inputForm.node2" />
    </el-form-item>

    <div class="add-edge-inline">
      <span>起点(集1)</span>
      <el-select
        v-model="newEdgeForm.node1"
        placeholder="起点"
        style="width: 70px"
      >
        <el-option
          v-for="(n, idx) in inputForm.node1"
          :key="idx"
          :label="n"
          :value="n"
        />
      </el-select>

      <span>终点(集2)</span>
      <el-select
        v-model="newEdgeForm.node2"
        placeholder="终点"
        style="width: 70px"
      >
        <el-option
          v-for="(n, idx) in inputForm.node2"
          :key="idx"
          :label="n"
          :value="n"
        />
      </el-select>

      <el-button type="primary" @click="addEdge"> 添加 </el-button>
    </div>

    <!-- 已添加的边 -->
    <div class="edges-list">
      <div
        v-for="(edge, index) in inputForm.edges"
        :key="index"
        class="edge-item"
      >
        <span class="edge-text"> {{ edge.node1 }} - {{ edge.node2 }} </span>
        <el-button type="danger" size="small" @click="removeEdge(index)">
          删除
        </el-button>
      </div>
    </div>

    <!-- 操作按钮区 -->
    <div class="buttons">
      <el-form-item>
        <el-button type="primary" @click="submitForm(ruleForm)">生成</el-button>
        <el-button @click="resetForm(ruleForm)">重置</el-button>
      </el-form-item>

      <el-form-item>
        <el-button @click="AMM" type="success">最大匹配</el-button>
      </el-form-item>

      <el-form-item>
        <el-button @click="PM" type="success">完备匹配</el-button>
      </el-form-item>
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

.add-edge-inline {
  display: flex;
  align-items: center;
  gap: 6px;
  margin: 10px 0;
}

/* 列表容器 - 固定高度 + overflow auto */
.edges-list {
  max-height: 70px; /* 可自行调整 */
  overflow-y: auto;
  display: grid;
  grid-template-columns: 1fr 1fr;
  gap: 8px;
}

.edge-item {
  display: flex;
  align-items: center;
  justify-content: space-between;
  padding: 4px;
  border: 1px solid #eee;
  border-radius: 4px;
}

.edge-text {
  margin-right: 8px;
}

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
