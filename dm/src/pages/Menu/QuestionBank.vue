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
        style="width: 100%"
        :header-cell-style="{ textAlign: 'center' }"
      >
        <el-table-column prop="id" label="ID" width="80" align="center" />
        <el-table-column prop="type1" label="分类" width="80" align="center" />
        <el-table-column label="题型" width="80" align="center">
          <template #default="{ row }">{{
            row.type === "choice" ? "选择" : "填空"
          }}</template>
        </el-table-column>
        <el-table-column prop="formula" label="公式 / 说明" />
        <el-table-column
          prop="create_time"
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
      console.log(data);
  
      console.log(data[0]);
      list.value = data.map((raw) => {
        const type1 = raw.type
        const json = JSON.parse(raw.content);
        const dt = new Date(raw.create_time)
        const short = dt.toLocaleString('zh-CN', {
          year:   'numeric',
          month:  '2-digit',
          day:    '2-digit',
          hour:   '2-digit',
          minute: '2-digit'
        })
        return {
          id: raw.id,
          type1: type1,
          type: json.type || "fill",
          formula: json.formula || "—",
          create_time: short
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
    if (row.type1 === 'set') {
      previewUrl.value = `/answerSat/${row.id}`
    } else if (row.type1 === 'warshall') {
      previewUrl.value = `/answerWarshall/${row.id}`
    } else if(row.type1 === '真值表') {
      previewUrl.value = `/answer/${row.id}`
    }else if(row.type1 === 'dijkstra') {
      previewUrl.value = `/answerDijkstra/${row.id}`
    }else if(row.type1 === 'mst') {
      previewUrl.value = `/answerMST/${row.id}`
    } else if(row.type1 === 'huffman') {
      previewUrl.value = `/answerHuffman/${row.id}`
    } else  if(row.type1 === 'bipartite') {
      previewUrl.value = `/answerBipartite/${row.id}`
    } else if(row.type1 === 'sorting') {
      previewUrl.value = `/answerSorting/${row.id}`
    }else {
      previewUrl.value = `/answer/${row.id}`
    }
    previewVisible.value = true;
  }
  
  const qrVisible = ref(false);
  const qrLink = ref("");
  function openQr(row) {
    if (row.type1 === 'set') {
      qrLink.value = `${window.location.origin}/answerSat/${row.id}`;
    } else if(row.type1 === 'warshall') {
      qrLink.value = `${window.location.origin}/answerWarshall/${row.id}`;
    } else if(row.type1 === '真值表') {
      qrLink.value = `${window.location.origin}/answer/${row.id}`;
    }else if(row.type1 === 'dijkstra') {
      qrLink.value = `${window.location.origin}/answerDijkstra/${row.id}`;
    }else if(row.type1 === 'mst') {
      qrLink.value = `${window.location.origin}/answerMST/${row.id}`;
    }else if(row.type1 === 'huffman') {
      qrLink.value = `${window.location.origin}/answerHuffman/${row.id}`;
    } else if(row.type1 === 'bipartite') {
      qrLink.value = `${window.location.origin}/answerBipartite/${row.id}`;
    } else if(row.type1 === 'sorting') {
      qrLink.value = `${window.location.origin}/answerSorting/${row.id}`;
    } else {
      qrLink.value = `${window.location.origin}/answer/${row.id}`;
    }
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
    