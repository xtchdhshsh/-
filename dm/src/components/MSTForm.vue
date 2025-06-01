<script setup>
import { reactive, ref, onMounted } from "vue";
import axios from "@/utlis/axios";

const emit = defineEmits(["getInputForm", "getResponse", "lujingUpdated", "startAutoPlay"]); 
// 新增 "startAutoPlay" 事件

const inputForm = reactive({ count: 0, node: [], edges: [] }); // 输入表单
const transferForm = reactive({ count: 0, node: [], link: [] }); // 组件传输表单
let nodeSelected = ref();
let LJ; // 用来存储后端返回的路径
const minCount = 3;
const ruleForm = ref();
const rules = reactive({
  count: [{ required: true }],
  node: [{ required: true, message: "请输入节点", trigger: "change" }],
});

// 根据节点个数显示节点信息
const showNode = (n) => {
  inputForm.node = [];
  for (let i = 0; i < n; i++) {
    inputForm.node.push(String.fromCharCode(65 + i));
  }
};

// 当节点信息手动改动时
function changeNode(value) {
  inputForm.node = value.split(",").map((v) => v.trim());
}

// 删除一条边
function removeEdge(index) {
  inputForm.edges.splice(index, 1);
}

// 用来暂存用户想添加的一条边
const newEdgeForm = reactive({
  node1: "",
  node2: "",
  weight: 1,
});

// （1）点击“开始”按钮调用此函数
function startAlgorithm() {
  // 如果还没有生成路径，就给个提示
  if (!LJ) {
    alert("请先生成路径");
    return;
  }
  // 让父组件去执行自动轮播
  emit("startAutoPlay");
}

// （2）点击“生成”按钮后调用此函数——提交表单并获取路径
const submitForm = async (ruleForm) => {
  if (!ruleForm) return;
  await ruleForm.validate((valid) => {
    if (valid) {
      // 1) 组装需要传给父组件的表单信息
      transferForm.count = inputForm.count;
      transferForm.node = inputForm.node;
      transferForm.link = inputForm.edges.map((e) => [e.node1, e.node2, e.weight]);

      // 2) 向后端发送请求，拿到路径
      axios
        .post("/scs", {
          num: inputForm.count,
          powerTable: createPowerTable(),
        })
        .then((response) => {
          console.log(response.data);
          LJ = response.data; // 假设后端直接返回路径数组
        });

      // 3) 将表单和路径都 emit 给父组件
      emit("getInputForm", transferForm);
      emit("lujingUpdated", LJ);
    }
  });
};

// 重置输入框
function resetForm(ruleForm) {
  if (!ruleForm) return;
  ruleForm.resetFields();

  showNode(minCount);
  inputForm.edges = [];
  newEdgeForm.node1 = "";
  newEdgeForm.node2 = "";
  newEdgeForm.weight = 1;
}

// 生成权重表格
const createPowerTable = () => {
  let powerTable = [];
  inputForm.node.forEach((col) => {
    let rowArr = [];
    inputForm.node.forEach((row) => {
      if (col == row) {
        rowArr.push(0);
      } else {
        let power = -1;
        const len = transferForm.link.length;
        for (let i = 0; i < len; i++) {
          const link = transferForm.link[i];
          if (
            (col == link[0] && row == link[1]) ||
            (col == link[1] && row == link[0])
          ) {
            power = +link[2];
            break;
          }
        }
        rowArr.push(power);
      }
    });
    powerTable.push(rowArr);
  });
  return powerTable;
};

// 添加一条边
function addEdge() {
  if (!newEdgeForm.node1 || !newEdgeForm.node2) {
    alert("请选择起点和终点");
    return;
  }
  if (newEdgeForm.node1 === newEdgeForm.node2) {
    alert("起点和终点不能相同");
    return;
  }
  if (newEdgeForm.weight <= 0) {
    alert("权值必须大于0");
    return;
  }

  inputForm.edges.push({
    node1: newEdgeForm.node1,
    node2: newEdgeForm.node2,
    weight: newEdgeForm.weight,
  });

  // 重置
  newEdgeForm.node1 = "";
  newEdgeForm.node2 = "";
  newEdgeForm.weight = 1;
}

onMounted(() => {
  // 节点信息初始化
  inputForm.count = minCount;
  showNode(minCount);
});
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

    <!-- 添加边 -->
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

    <!-- 已添加的边 -->
    <div class="edges-list">
      <div
        v-for="(edge, index) in inputForm.edges"
        :key="index"
        class="edge-item"
      >
        <span class="edge-text">
          {{ edge.node1 }} - {{ edge.node2 }} : {{ edge.weight }}
        </span>
        <el-button
          type="danger"
          size="small"
          @click="removeEdge(index)"
        >
          删除
        </el-button>
      </div>
    </div>

    <!-- 操作按钮 -->
    <div class="buttons">
      <el-form-item>
        <el-button type="primary" @click="submitForm(ruleForm)">生成</el-button>
        <el-button @click="resetForm(ruleForm)">重置</el-button>

        <!-- 点击开始，触发 startAlgorithm()，先判断是否有路径 LJ，再 emit 给父组件 -->
        <el-button type="primary" @click="startAlgorithm">
          开始
        </el-button>
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

.edges-list {
  max-height: 70px;
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
</style>
