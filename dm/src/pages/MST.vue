<script setup>
import * as echarts from "echarts";
import axios from "axios";
import { onMounted, reactive, ref } from "vue";
import { ElMessage, ElMessageBox } from "element-plus";
import dijkstraForm from "@/components/MSTForm.vue";
import {
  ArrowLeft,
  ArrowRight,
  Delete,
  Edit,
  Share,
} from "@element-plus/icons-vue";

// 路径数组 lujing，示例中每一项是字符串，如 "AC"、"CF" 等
let lujing;
let num = ref();
let nodes = ref([]);
let links = ref([]);
let response = {};
let LJ = ref([]);  // 存储从子组件传来的路径
let outputTable = reactive([]);
let chart;
let chart1;
let option = {
  // 悬停详情
  tooltip: {
    formatter: function (ele) {
      const data = ele.data;
      switch (ele.dataType) {
        case "node":
          return `节点：${data.name}`;
        case "edge":
          return `边：${data.source} <-> ${data.target}，权重：${data.value}`;
      }
    },
  },
  series: {
    type: "graph",
    layout: "none",
    draggable: true,
    roam: true,
    // 节点label
    label: {
      show: true,
      fontSize: 24,
      fontWeight: 700,
      color: "#666",
    },
    // 连线label
    edgeLabel: {
      show: true,
      formatter: "{c}",
      fontSize: 20,
    },
    data: [],
    links: [],
    itemStyle: { color: "LightSkyBlue" },
    lineStyle: { width: 5, color: "lightblack" },
    symbolSize: 50,
    emphasis: {
      lineStyle: { width: 10, color: "#ff0000" },
    },
  },
};

let option1 = {
  tooltip: {
    formatter: function (ele) {
      const data = ele.data;
      switch (ele.dataType) {
        case "node":
          return `节点：${data.name}`;
        case "edge":
          return `边：${data.source} <-> ${data.target}，权重：${data.value}`;
      }
    },
  },
  series: {
    type: "graph",
    layout: "none",
    draggable: true,
    roam: true,
    label: {
      show: true,
      fontSize: 24,
      fontWeight: 700,
      color: "#666",
    },
    edgeLabel: {
      show: true,
      formatter: "{c}",
      fontSize: 20,
    },
    data: [],
    links: [],
    itemStyle: { color: "LightSkyBlue" },
    lineStyle: { width: 5, color: "LightBlack" },
    symbolSize: 50,
    emphasis: {
      lineStyle: { width: 10, color: "#ff0000" },
    },
  },
};

// 弹窗封装
const msgBox = (tip, reg, regMsg) => {
  return new Promise((resolve) => {
    ElMessageBox.prompt(tip, "Tip", {
      confirmButtonText: "OK",
      cancelButtonText: "Cancel",
      inputPattern: reg,
      inputErrorMessage: regMsg,
    }).then((v) => {
      ElMessage({
        type: "success",
        message: `输入成功`,
      });
      resolve(v.value);
    });
  });
};

onMounted(() => {
  // 第一个图
  chart = echarts.init(document.querySelector("#graph"));
  chart.on("click", async function (ele) {
    switch (ele.dataType) {
      case "node": {
        const v = await msgBox("请输入节点", /^[A-Z]$/, "请只输入一个大写字母");
        nodes.value.forEach((obj) => {
          if (obj.name == ele.data.name) {
            obj.name = v;
          }
        });
        option.series.data.forEach((obj) => {
          if (obj.name == ele.data.name) {
            obj.name = v;
          }
        });
        option.series.links.forEach((obj) => {
          if (obj.source == ele.data.name) {
            obj.source = v;
          }
          if (obj.target == ele.data.name) {
            obj.target = v;
          }
        });
        break;
      }
      case "edge": {
        const v = await msgBox(
          "请输入权重",
          /^[0-9]*[1-9][0-9]*$/,
          "请输入正整数"
        );
        option.series.links.forEach((obj) => {
          if (
            (obj.source == ele.data.source && obj.target == ele.data.target) ||
            (obj.source == ele.data.target && obj.target == ele.data.source)
          ) {
            obj.value = v;
          }
        });
        break;
      }
    }
    chart.setOption(option);
  });

  // 第二个图
  chart1 = echarts.init(document.querySelector("#graph1"));
  chart1.on("click", async function (ele) {
    switch (ele.dataType) {
      case "node": {
        const v = await msgBox("请输入节点", /^[A-Z]$/, "请只输入一个大写字母");
        nodes.value.forEach((obj) => {
          if (obj.name == ele.data.name) {
            obj.name = v;
          }
        });
        option.series.data.forEach((obj) => {
          if (obj.name == ele.data.name) {
            obj.name = v;
          }
        });
        option.series.links.forEach((obj) => {
          if (obj.source == ele.data.name) {
            obj.source = v;
          }
          if (obj.target == ele.data.name) {
            obj.target = v;
          }
        });
        break;
      }
      case "edge": {
        const v = await msgBox(
          "请输入权重",
          /^[0-9]*[1-9][0-9]*$/,
          "请输入正整数"
        );
        option.series.links.forEach((obj) => {
          if (
            (obj.source == ele.data.source && obj.target == ele.data.target) ||
            (obj.source == ele.data.target && obj.target == ele.data.source)
          ) {
            obj.value = v;
          }
        });
        break;
      }
    }
    chart1.setOption(option);
  });
});

// 1) 收到子组件传来的路径后就启动自动轮播
const handleLujingUpdated = (LJArray) => {
  LJ.value = LJArray;
  lujing = LJ.value;
  console.log("收到路径:", lujing);

};

// 2) 获取并修改子组件传来的表单数据
const getInput = (f) => {
  chart.clear();
  num = f.count;
  nodes = ref([]);
  links = ref([]);
  option.series.data = [];
  option.series.links = [];

  option1.series.data = [];
  option1.series.links = [
    { source: "A", target: "C", value: "1" },
    { source: "C", target: "A", value: "1" },
    { source: "B", target: "C", value: "5" },
    { source: "C", target: "B", value: "5" },
    { source: "B", target: "E", value: "3" },
    { source: "E", target: "B", value: "3" },
    { source: "C", target: "F", value: "4" },
    { source: "F", target: "C", value: "4" },
    { source: "D", target: "F", value: "2" },
    { source: "F", target: "D", value: "2" },
  ];

  // 生成节点坐标（示例）
  !(function () {
    const nRow = num < 8 ? Math.ceil(num / 2) : 4;
    const nCol = num / nRow;
    let yCoord = 100;
    for (let i = 0; i < nCol; i++) {
      let xCoord = i % 2 == 0 ? 0 : 50;
      for (let j = 0; j < nRow; j++) {
        const index = i * nRow + j;
        xCoord += 100;
        nodes.value.push({ name: f.node[index], x: xCoord, y: yCoord });
        if (index + 1 >= num) {
          break;
        }
      }
      yCoord += 100;
    }
  })();

  // 配置 links
  f.link.forEach((link) => {
    links.value.push({ source: link[0], target: link[1], value: link[2] });
    links.value.push({ source: link[1], target: link[0], value: link[2] });
  });

  // 配置 option 的 data
  nodes.value.forEach((node) => {
    option.series.data.push({
      name: node.name,
      x: node.x,
      y: node.y,
    });
  });
  // 可选：给 option1.series.data 做同样处理
  nodes.value.forEach((node) => {
    option1.series.data.push({
      name: node.name,
      x: node.x,
      y: node.y,
    });
  });

  // 配置 option 的 links
  links.value.forEach((link) =>
    option.series.links.push({
      source: link.source,
      target: link.target,
      value: link.value,
      lineStyle: { color: "lightblack" },
    })
  );

  // 最后渲染
  chart.setOption(option);
  // 如果需要渲染 chart1，可以启用
  // chart1.setOption(option1);
};

// 3) 获取并利用子组件传来的后端数据
const getResp = (resp) => {
  response = resp;
  const { nodeSelected, path, valueTable } = response;
  const valueResult = valueTable[valueTable.length - 1];
  let count = 0;

  // 清空数组
  outputTable.length = 0;

  // 将后端返回的数据推入 outputTable（示例）
  for (let i = 0; i < num; i++) {
    if (String.fromCharCode(65 + i) == nodeSelected.value) {
      continue;
    }
    outputTable.push({
      name: nodes.value[i].name,
      value: valueResult[i],
      path: path[count].join("->"),
    });
    count += 1;
  }
};

// -------------------------
// i：当前演示到哪条路径
let i = 0;

// 用于自动播放的定时器引用
let autoPlayInterval = null;

function highlightAllEdges() {
  // 1) 重置所有连线为黑色（看你需求）
  option.series.links.forEach(link => {
    link.lineStyle = { color: "Black" };
  });
  // 2) 将路径上的所有连线标记为绿色
  lujing.forEach(link => {
    const [n1, n2] = [link[0], link[1]];
    option.series.links.forEach((link) => {
      if (
        (link.source === n1 && link.target === n2) ||
        (link.source === n2 && link.target === n1)
      ) {
        link.lineStyle = { color: "#19CAAD" };
      }
    });
  });
  
  // 3) 更新图
  chart.setOption(option);
}


/** 
 * 开始自动轮播 
 */
const startAutoPlay = () => {
  if (autoPlayInterval) return;
  i = 0;
  autoPlayInterval = setInterval(() => {
    next();
    if (i > lujing.length - 1) {
        highlightAllEdges();
       stopAutoPlay();
    }
  }, 2000);
};

/** 
 * 停止自动轮播
 */
const stopAutoPlay = () => {
    clearInterval(autoPlayInterval);
    autoPlayInterval = null;
};

/** 
 * 上一步 
 */
const pre = () => {
  // 如果当前不是第0条，就往回一步
  if (i > 0) {
    i--;
  }
  // 执行着色等逻辑
  drawEdgeAndDescribe();
};

/** 
 * 下一步 
 */
const next = () => {
  drawEdgeAndDescribe();
  // 如果还没到最后一条，则 i++
  if (i <= lujing.length - 1) {
    i++;
  }
};

/**
 * 根据当前 i 进行边的着色与文字描述
 */
function drawEdgeAndDescribe() {
  let temp = document.querySelector("#describe");
  // 若当前 i 超出范围，直接返回
  if (!lujing[i]) return;

  // 显示提示
  temp.innerHTML = `最小权值的边为 ${lujing[i]}`;

  // 先重置所有线段的颜色
  option.series.links.forEach((link) => {
    link.lineStyle = { color: "Black" };
    delete link.tempColor; // 清除临时标记
  });

  // 再把当前要着色的这条边标记
  const [n1, n2] = [lujing[i][0], lujing[i][1]]; 
  // 例如 lujing[i] = "AC"，n1='A', n2='C'
  option.series.links.forEach((link) => {
    if (
      (link.source === n1 && link.target === n2) ||
      (link.source === n2 && link.target === n1)
    ) {
      link.tempColor = "#19CAAD"; 
    }
  });

  // 恢复标记的颜色
  option.series.links.forEach((link) => {
    if (link.tempColor) {
      link.lineStyle.color = link.tempColor;
    }
  });

  // 更新图表
  chart.setOption(option);
}
</script>

<template>
  <main>
    <div id="inputFormAndOutput">
      <!-- 子组件表单 -->
      <dijkstraForm
        @getInputForm="getInput"
        @getResponse="getResp"
        @lujingUpdated="handleLujingUpdated"
        @startAutoPlay="startAutoPlay"
      />

      <!-- 输出卡片 -->
      <el-card
        style="
          max-width: 500px;
          border: 1px solid #666;
          width: 500px;
          margin-top: 20px;
          height: 200px;
        "
      >
        <template #header>切换过程/注释</template>
        <div id="Switch">
          <el-button-group>
            <!-- 上一步 -->
            <el-button
              type="primary"
              :icon="ArrowLeft"
              @click="pre"
            />
            <!-- 下一步 -->
            <el-button type="primary" @click="next">
              <el-icon class="el-icon--right"><ArrowRight /></el-icon>
            </el-button>
          </el-button-group>

          <!-- 你可以加一个“停止轮播”按钮手动终止 -->
          <!--
          <el-button type="danger" @click="stopAutoPlay" style="margin-left: 10px;">
            停止轮播
          </el-button>
          -->
        </div>
        <div id="describe" style="font-size: 30px">
          此时最小权值的边为
        </div>
        <div id="graph1"></div>
      </el-card>
    </div>

    <!-- 主图 -->
    <div id="graph"></div>
  </main>
</template>

<style scoped>
main {
  display: flex;
  width: 1240px;
  margin: 0 auto;
}

#inputFormAndOutput {
  padding: 0 20px;
}

#graph {
  width: 720px;
  height: 550px;
  border: 1px solid #666;
  border-radius: 8px;
}

#Switch {
  height: 40px;
  margin-bottom: 5px;
}

.el-icon--right {
  margin-left: 5px;
}
</style>

<style>
.el-message-box {
  width: 250px;
}
</style>
