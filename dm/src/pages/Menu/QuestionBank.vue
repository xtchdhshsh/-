<template>
    <div class="page-wrapper">
      <h2 class="title">题库管理</h2>
  
      <div class="toolbar">
        <el-input
          v-model="query"
          placeholder="按公式关键字搜索"
          clearable
          @keyup.enter="load"
          style="width: 240px"
        />
        <el-button type="primary" @click="load" icon="Refresh">刷新</el-button>
      </div>
  
      <el-table
        :data="list"
        border
        style="width: 100%; margin-top: 20px"
        :header-cell-style="{ textAlign: 'center' }"
      >
        <el-table-column prop="id" label="ID" width="80" align="center" />
        <el-table-column label="题型" width="80" align="center">
          <template #default="{ row }">{{
            row.type === "fill" ? "填空" : "选择"
          }}</template>
        </el-table-column>
        <el-table-column prop="formula" label="公式 / 说明" />
        <el-table-column
          prop="createdAt"
          label="创建时间"
          width="180"
          align="center"
        />
        <el-table-column label="操作" width="260" align="center">
          <template #default="{ row }">
            <el-button size="small" @click="preview(row)" type="primary" plain
              >预览</el-button
            >
            <el-button size="small" @click="openQr(row)" plain>二维码</el-button>
            <el-button size="small" type="danger" plain @click="handleDelete(row.id)"
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
  
      <el-dialog
        v-model="previewVisible"
        title="题目预览"
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
  
      <el-dialog
        v-model="qrVisible"
        title="题目二维码"
        width="320px"
        :close-on-click-modal="false"
      >
        <div class="qr-box">
          <qrcode-vue :value="qrLink" :size="256" />
          <p class="qr-link">{{ qrLink }}</p>
        </div>
      </el-dialog>
    </div>
  </template>
    
    <script setup>
  import { ref, onMounted } from "vue";
  import axios from "@/utlis/axios";
  import QrcodeVue from "qrcode.vue";
  import { ElMessage, ElMessageBox } from "element-plus";
  
  const list = ref([]);
  const page = ref(1);
  const pageSize = 10;
  const total = ref(0);
  const query = ref("");
  
  async function load() {
    try {
      const { data } = await axios.get("/api/share/list");
  
      list.value = data.map((raw) => {
        const json = JSON.parse(raw.content);
        return {
          id: raw.id,
          type: json.type || "fill",
          formula: json.formula || "—",
          createdAt: raw.createTime
            ? new Date(raw.createTime).toLocaleString()
            : "—",
        };
      });
      total.value = list.value.length;
    } catch (e) {
      console.error(e);
      ElMessage.error("加载题目失败");
    }
  }
  
  onMounted(load);
  
  const previewVisible = ref(false);
  const previewUrl = ref("");
  function preview(row) {
    previewUrl.value = `/answer/${row.id}`;
    previewVisible.value = true;
  }
  
  const qrVisible = ref(false);
  const qrLink = ref("");
  function openQr(row) {
    qrLink.value = `${window.location.origin}/answer/${row.id}`;
    qrVisible.value = true;
  }
  
  function handleDelete(id) {
    ElMessageBox.confirm(`确定删除题目 #${id} 吗？`, "提示", {
      type: "warning",
    })
      .then(() => axios.delete(`/api/share/${id}`))
      .then(() => {
        ElMessage.success("删除成功");
        load();
      })
      .catch(() => {});
  }
  </script>
    
    <style scoped>
  .page-wrapper {
    padding: 0 20px;
    max-width: 1200px;
    /* margin: auto; */
  }
  .title {
    font-size: 32px;
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
  .qr-box {
    display: flex;
    flex-direction: column;
    align-items: center;
    gap: 12px;
  }
  .qr-link {
    word-break: break-all;
    text-align: center;
    font-size: 12px;
    color: #666;
  }
  </style>
    