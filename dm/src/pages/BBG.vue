<script setup>
import * as echarts from "echarts";
import { onMounted, reactive, ref, watch ,computed} from "vue";
import BBGForm from "@/components/BBGForm.vue";

const INFINITY = 1000;
const TIME_PERSTEP = 3000;

let num1 = 0;
let num2 = 0;
let num = 0; // 节点总数
let nodes1 = ref([]);
let nodes2 = ref([]);
let nodes = ref([]); // 最终把 node1、node2 的坐标都放这
let links = ref([]);
const isAll = ref(false);
const isPerfect = ref(false);
let dialogFormVisible = ref();
let chart;
const allMatchingCount = ref(0);
const perfectMatchingCount = ref(0);
const allMatching = ref([]);
const perfectMatching = ref([]);
const hasPerfect = ref(false);

const selectedMaxMatchingIndex = ref(-1);
const selectedperfectMatchingIndex = ref(-1);

let option = {
  // 工具栏
  toolbox: {
    show: true,
    itemSize: 20, // icon大小
    itemGap: 20, // icon间隔
    top: "1%",
    right: "1%",
    feature: {
      // 更改坐标
      myCoord: {
        title: "coord",
        icon: "path://M7 28v65h65v-5H12V28H7z",
        onclick: async () => {
          dialogFormVisible.value = true;
        },
      },
      restore: {
        show: true,
      },
      saveAsImage: {
        show: true,
        type: "png",
        name: "download",
        title: "save",
      },
    },
  },
  // 悬停详情
  tooltip: {
    formatter: function (ele) {
      const data = ele.data;
      switch (ele.dataType) {
        case "node":
          return `节点：${data.name}`;
        case "edge":
          return `边：${data.source} <-> ${data.target}`;
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
      color: "white",
    },
    data: [],
    links: [],
    itemStyle: { color: "LightGrey" },
    lineStyle: { width: 5, color: "black" },
    symbolSize: 50,
    emphasis: {
      focus: "adjacency",
      lineStyle: { width: 10, color: "black" },
    },
  },
};

const getInput = (f) => {
  chart.clear();

  // 初始化节点和边数据
  num1 = f.count1;
  num2 = f.count2;
  num = num1 + num2;
  nodes1.value = f.node1;
  nodes2.value = f.node2;
  links.value = f.link;

  // 清空旧数据
  option.series.data = [];
  option.series.links = [];
  nodes.value = [];

  const LEFT_X = 100,
    RIGHT_X = 600,
    START_Y = 50,
    GAP_Y = 80;

  // 定义节点，确保name唯一
  // 统一给节点加个前缀确保不冲突
  nodes1.value.forEach((name, i) => {
    nodes.value.push({ name: "L_" + name, x: LEFT_X, y: START_Y + i * GAP_Y });
  });
  nodes2.value.forEach((name, i) => {
    nodes.value.push({ name: "R_" + name, x: RIGHT_X, y: START_Y + i * GAP_Y });
  });

  // 添加节点到 option
  nodes.value.forEach((node) => {
    option.series.data.push({
      name: node.name,
      x: node.x,
      y: node.y,
      itemStyle: {
        color: node.name.startsWith("L_") ? "#5470C6" : "#91CC75",
      },
    });
  });

  // 添加边到 option
  links.value.forEach((link) => {
    console.log("link", link),
      option.series.links.push({
        source: "L_" + link[0],
        target: "R_" + link[1],
        lineStyle: { color: "black" },
      });
  });

  chart.setOption(option, true);
};

function updateMatchingType(payload) {
  isAll.value = payload.isAll;
  isPerfect.value = payload.isPerfect;
}

const getResponse = (res) => {
  allMatchingCount.value = res.allMatchingCount;
  allMatching.value = res.allMatching;
  perfectMatchingCount.value = res.perfectMatchingCount;
  perfectMatching.value = res.perfectMatching;
  hasPerfect.value = res.hasPerfect;
  console.log("res", res);
};

const indexToChar = (index) => {
  return String.fromCharCode(index + "A".charCodeAt(0));
};

// 监听选中最大匹配的索引变化
// 监听选中最大匹配索引变化，更新已绘制边的颜色
watch(
  selectedMaxMatchingIndex,
  (newVal) => {
    if (allMatching.value.length > 0) {
      const matching = allMatching.value[newVal];
      console.log("matching", matching);
      // 恢复所有边为默认样式
      option.series.links.forEach((link) => {
        link.lineStyle = { color: "black", width: 5 };
      });

      // 针对选中的匹配，对应的边设置为红色
      matching.forEach((item) => {
        const source = "L_" + indexToChar(item.node1);
        const target = "R_" + indexToChar(item.node2);
        // 找到对应的边
        const link = option.series.links.find(
          (l) => l.source === source && l.target === target
        );
        if (link) {
          link.lineStyle = { color: "red", width: 8 };
        }
      });
      // 更新图表的边样式（只更新 series.links 部分）
      chart.setOption({ series: { links: option.series.links } });
    }
  },
  { immediate: true }
);


// 监听选中完备匹配的索引变化
watch (
  selectedperfectMatchingIndex,
  (newVal) => {
    if (perfectMatching.value.length > 0) {
      const matching = perfectMatching.value[newVal];

      // 恢复所有边为默认样式
      option.series.links.forEach((link) => {
        link.lineStyle = { color: "black", width: 5 };
      });

      // 针对选中的匹配，对应的边设置为红色
      matching.forEach((item) => {
        const source = "L_" + indexToChar(item.node1);
        const target = "R_" + indexToChar(item.node2);
        // 找到对应的边
        const link = option.series.links.find(
          (l) => l.source === source && l.target === target
        );
        if (link) {
          link.lineStyle = { color: "red", width: 8 };
        }
      });
      // 更新图表的边样式（只更新 series.links 部分）
      chart.setOption({ series: { links: option.series.links } });
    }
  },
  { immediate: true }
)

const selectedMatchingIndexComputed = computed({
  get() {
    return selectedMaxMatchingIndex.value === -1 ? null : selectedMaxMatchingIndex.value;
  },
  set(val) {
    selectedMaxMatchingIndex.value = val === null ? -1 : val;
  }
});

const selectedperfectMatchingIndexComputed = computed({
  get() {
    return selectedperfectMatchingIndex.value === -1 ? null : selectedperfectMatchingIndex.value;
  },
  set(val) {
    selectedperfectMatchingIndex.value = val === null ? -1 : val;
  }
});


onMounted(() => {
  // ECharts 配置
  chart = echarts.init(document.getElementById("graph"));
});
</script>

<template>
  <main>
    <div id="inputFormAndOutput">
      <!-- 子组件 -->
      <BBGForm
        @getInputForm="getInput"
        @getResponse="getResponse"
        @updateMatchingType="updateMatchingType"
      />

      <div
        style="
          max-width: 500px;
          border: 1px solid #666;
          width: 500px;
          margin-top: 20px;
          height: 200px;
        "
        class="outputArea"
      >
        <div class="Section" v-if="isAll">
          一共有 {{ allMatchingCount }} 种最大匹配
          <br />

          <el-select
            v-model="selectedMatchingIndexComputed"
            placeholder="请选择匹配"
            style="margin: 10px 0"
          >
            <el-option
              v-for="(matching, index) in allMatching"
              :key="index"
              :label="`匹配 ${index + 1}`"
              :value="index"
            />
          </el-select>
        </div>
        <div class="Section" v-if="isPerfect">
          <div v-if="hasPerfect">
            一共有 {{ perfectMatchingCount }} 种完备匹配
            <br />
            <el-select
              v-model="selectedperfectMatchingIndexComputed"
              placeholder="请选择匹配"
              style="margin: 10px 0"
            >
              <el-option
                v-for="(matching, index) in perfectMatching"
                :key="index"
                :label="`匹配 ${index + 1}`"
                :value="index"
              />
            </el-select>
          </div>
          <div v-else>没有完备匹配</div>
        </div>
      </div>
    </div>

    <div class="process">
      <div id="graph"></div>
    </div>
  </main>
</template>

<style scoped>
main {
  display: flex;
  width: 1240px;
  margin: 0 auto;
}

#inputFormAndOutput {
  padding: 0 15px;
}

.outputArea {
  padding: 15px;
  max-height: 175px;
  overflow-y: auto;
  border-radius: 8px;
  background-color: #f9f9f9;
  margin-top: 20px;
}

#graph {
  width: 700px;
  height: 550px;
  border: 1px solid #666;
  border-radius: 8px;
}
</style>
