<template>
    <div class="page-wrapper">
      <h2 class="title">作业提交管理</h2>
  
      <!-- 搜索栏 -->
      <div class="toolbar">
        <el-input
          v-model="query"
          placeholder="按学生/题目关键词搜索"
          clearable
          @keyup.enter="load"
          style="width: 260px"
        />
        <el-button type="primary" @click="load" icon="Refresh">刷新</el-button>
      </div>
  
      <!-- 提交列表 -->
      <el-table
        :data="list"
        border
        style="width: 100%"
        :header-cell-style="{ textAlign: 'center' }"
      >
        <el-table-column prop="id" label="提交ID" width="90" align="center" />
        <el-table-column prop="student" label="学生" width="140" />
        <el-table-column
          prop="questionId"
          label="题目ID"
          width="90"
          align="center"
        />
        <el-table-column prop="score" label="得分" width="80" align="center" />
        <el-table-column
          prop="submitAt"
          label="提交时间"
          width="180"
          align="center"
        />
        <el-table-column label="操作" width="160" align="center">
          <template #default="{ row }">
            <el-button size="small" @click="preview(row)" type="primary" plain
              >详情</el-button
            >
            <el-button size="small" type="danger" plain @click="deleteSub(row)"
              >删除</el-button
            >
          </template>
        </el-table-column>
      </el-table>
  
      <el-pagination
        v-if="total > pageSize"
        v-model:current-page="page"
        :page-size="pageSize"
        :total="total"
        layout="prev, pager, next"
        @current-change="load"
        class="pager"
      />
  
      <!-- 详情弹窗 -->
      <el-dialog
        v-model="previewVisible"
        title="提交详情"
        width="90vw"
        :close-on-click-modal="false"
      >
        <iframe
          v-if="previewVisible"
          :src="previewUrl"
          frameborder="0"
          style="width: 100%; height: 70vh"
        />
      </el-dialog>
    </div>
  </template>
    
    <script setup>
  import { ref, onMounted } from "vue";
  import axios from "@/utlis/axios";
  import { ElMessage, ElMessageBox } from "element-plus";
  import dayjs from "dayjs";
  
  const list = ref([]);
  const page = ref(1);
  const pageSize = 10;
  const total = ref(0);
  const query = ref("");
  
  async function load() {
    try {
      const { data } = await axios.get("/submission/list", {
        params: { page: page.value, size: pageSize, q: query.value },
      });
      list.value = data.records.map((r) => ({
        id: r.id,
        student: r.studentName,
        questionId: r.questionId,
        score: r.score ?? "-",
        submitAt: dayjs(r.submitTime).format("YYYY-MM-DD HH:mm"),
      }));
      total.value = data.total;
    } catch (e) {
      console.error(e);
      ElMessage.error("加载提交列表失败");
    }
  }
  
  onMounted(load);
  
  /* 详情预览 */
  const previewVisible = ref(false);
  const previewUrl = ref("");
  function preview(row) {
    previewUrl.value = `/submission/${row.id}`;
    previewVisible.value = true;
  }
  
  /* 删除提交 */
  async function deleteSub(row) {
    ElMessageBox.confirm(`确定删除提交 #${row.id} 吗？`, "提示", {
      type: "warning",
    })
      .then(() => axios.delete(`/submission/${row.id}`))
      .then(() => {
        ElMessage.success("删除成功");
        load();
      })
      .catch(() => {});
  }
  </script>
    
    <style scoped>
  .page-wrapper {
    padding: 24px;
    max-width: 1100px;
    margin: auto;
  }
  .title {
    font-size: 22px;
    font-weight: 600;
    margin-bottom: 16px;
  }
  .toolbar {
    display: flex;
    align-items: center;
    gap: 12px;
    margin-bottom: 12px;
  }
  .pager {
    margin-top: 16px;
    display: flex;
    justify-content: center;
  }
  </style>
    