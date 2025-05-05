<template>
    <div>
      <h2>查看题目</h2>
  
      <el-table :data="displayTable" border>
        <el-table-column
          v-for="(h, i) in headers"
          :key="i"
          :label="h"
          align="center"
        >
          <template #default="{ row, $index }">
            <template v-if="isFill && row[i] === null">
              <el-input
                v-model="editableData[$index][i]"
                size="small"
                style="width: 80px"
              />
            </template>
            <template v-else-if="isChoice && isBlankPos($index, i)">
              {{ markOf($index, i) }}
            </template>
            <template v-else>{{ row[i] }}</template>
          </template>
        </el-table-column>
      </el-table>
  
      <div v-if="isChoice" class="choice-wrapper">
        <el-radio-group v-model="chosen">
          <el-radio
            v-for="opt in options"
            :key="opt.key"
            :label="opt.key"
            class="choice-radio"
          >
            {{ opt.key }}.&nbsp;{{ spaced(opt.val) }}
          </el-radio>
        </el-radio-group>
      </div>
  
      <div style="margin-top: 20px; text-align: center">
        <el-button type="primary" @click="submit">提交答案</el-button>
      </div>
    </div>
  </template>
  
  <script setup>
  import { ref, onMounted, computed } from "vue";
  import { useRoute } from "vue-router";
  import axios from "@/utlis/axios";
  import { ElMessage } from "element-plus";
  
  const route = useRoute();
  const headers = ref([]);
  const editableData = ref([]);
  
  const questionType = ref("fill");
  const blanksPos = ref([]);
  const options = ref([]);
  const chosen = ref("");
  
  const spaced = (v) => v.split("").join(" ");
  
  const keyOf = (r, c) => `${r}-${c}`;
  const isBlankPos = (r, c) => blanksPos.value.includes(keyOf(r, c));
  const markOf = (r, c) => {
    const idx = blanksPos.value.indexOf(keyOf(r, c));
    return idx > -1 ? `?${idx + 1}` : "";
  };
  
  const isFill = computed(() => questionType.value === "fill");
  const isChoice = computed(() => questionType.value === "choice");
  const displayTable = computed(() => editableData.value);
  
  onMounted(async () => {
    const { id } = route.params;
    try {
      const { data: resp } = await axios.get(`/api/share/${id}`);
      const content =
        typeof resp.content === "string" ? JSON.parse(resp.content) : resp;
  
      questionType.value = content.type || "fill";
      headers.value = content.headers;
  
      if (content.type === "fill") {
        editableData.value = content.data.map((r) => [...r]);
      } else {
        blanksPos.value = content.blanks || [];
        options.value = content.options || [];
        editableData.value = content.fullTable.map((r) => [...r]);
      }
    } catch (e) {
      console.error(e);
      ElMessage.error("题目加载失败");
    }
  });
  
  function submit() {
    if (isFill.value) {
      console.log("提交填空题: ", editableData.value);
      ElMessage.success("答案已提交");
      return;
    }
    if (!chosen.value) {
      ElMessage.info("请选择一个选项");
      return;
    }
    const rightOpt = options.value.find((o) => o.right);
    if (!rightOpt) {
      ElMessage.error("题目数据缺少正确答案");
      return;
    }
    if (chosen.value === rightOpt.key) {
      ElMessage.success("回答正确!");
    } else {
      ElMessage.error(`回答错误，正确答案是 ${rightOpt.key}`);
    }
  }
  </script>
  
  <style scoped>
  .banner {
    display: flex;
    justify-content: center;
    min-width: 1240px;
  }
  .leftbanner {
    width: 600px;
    margin: auto;
  }
  .decoration {
    position: relative;
    width: 450px;
    height: 200px;
    margin-top: 100px;
  }
  .tip {
    font-size: 20px;
    padding-top: 10px;
    margin-left: 10px;
  }
  .tipbut {
    margin-top: 10px;
    margin-left: 20px;
  }
  .enter {
    margin-top: 30px;
    margin-left: 80px;
    height: 30px;
    width: 220px;
    padding: 0 6px;
  }
  .submit {
    height: 32px;
    width: 60px;
    margin-left: 10px;
  }
  .new-question {
    height: 32px;
    margin-left: 10px;
  }
  .table-container {
    margin-left: 20px;
    width: 100%;
    height: 500px;
    margin-top: 20px;
    margin-bottom: 20px;
    overflow-x: auto;
  }
  .el-table {
    width: auto;
  }
  
  .choice-wrapper {
    display: flex;
    justify-content: center;
    gap: 48px;
    margin-top: 16px;
  }
  .choice-radio {
    display: flex;
    align-items: center;
    white-space: nowrap;
  }
  </style>
  