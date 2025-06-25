<template>
  <div class="page-wrapper">
    <!-- 头部区域 -->
    <div class="header">
      <div class="breadcrumb">
        首页 > 课程管理 > <b>作业管理</b>
      </div>
    </div>

    <!-- 功能栏 -->
    <div class="actions-bar">
      <el-form :inline="true" class="filter-form">
        <el-form-item label="选择作业">
          <el-select v-model="selectedHomework" placeholder="我布置的作业" style="width: 200px;" @change="load" clearable>
            <el-option label="我布置的作业" value=""></el-option>
          </el-select>
        </el-form-item>
        <el-form-item label="选择班级">
          <el-select v-model="selectedClass" placeholder="全部班级" style="width: 200px;" @change="load" clearable>
            <el-option label="全部班级" value=""></el-option>
            <el-option v-for="c in classes" :key="c" :label="c" :value="c"></el-option>
          </el-select>
        </el-form-item>
        <el-form-item>
          <el-button type="primary" icon="Plus" @click="handleCreate">布置作业</el-button>
        </el-form-item>
        <el-form-item>
          <el-button type="success" icon="Download" @click="handleExport">导出Excel</el-button>
        </el-form-item>
      </el-form>
    </div>

    <!-- 作业列表 -->
    <el-table :data="homeworkList" border v-loading="loading">
      <el-table-column prop="title" label="作业内容" />
      <el-table-column label="已提交" width="100" align="center">
        <template #default="{ row }">
          {{ row.submittedCount }}
        </template>
      </el-table-column>
      <el-table-column label="未提交" width="100" align="center">
        <template #default="{ row }">
          {{ row.totalStudents - row.submittedCount }}
        </template>
      </el-table-column>
      <el-table-column prop="classname" label="班级" width="180" align="center" />
      <el-table-column prop="assignTime" label="布置时间" width="180" align="center" />
      <el-table-column prop="deadline" label="截止时间" width="180" align="center" />
      <el-table-column label="操作" width="220" align="center">
        <template #default="{ row }">
          <el-button type="primary" size="small" @click="handleEdit(row.assignmentId)">修改</el-button>
          <el-button type="danger" size="small" @click="handleDelete(row.assignmentId)">删除</el-button>
        </template>
      </el-table-column>
    </el-table>

    <!-- 分页 -->
    <el-pagination
      v-if="total > page.pageSize"
      v-model:current-page="page.pageNum"
      :page-size="page.pageSize"
      :total="total"
      layout="total, prev, pager, next"
      @current-change="handlePageChange"
      class="pager"
    />

    <!-- 作业表单弹窗 -->
    <AssignmentForm
      v-if="formVisible"
      :assignment-id="currentAssignmentId"
      @close="handleCloseForm"
    />
  </div>
</template>

<script setup>
import { ref, onMounted, reactive } from "vue";
import { ElMessage, ElMessageBox } from "element-plus";
import axios from "@/utlis/axios"; 
import AssignmentForm from '@/components/AssignmentForm.vue';

// --- 数据区 ---
const loading = ref(true);
const homeworkList = ref([]);
const selectedClass = ref("");
const selectedHomework = ref("");
const classes = ref([]); 
const total = ref(0);
const page = reactive({
  pageNum: 1,
  pageSize: 10,
});

const formVisible = ref(false);
const currentAssignmentId = ref(null);

// --- 方法区 ---

// 加载作业列表
async function load() {
  loading.value = true;
  try {
    const params = {
      pageNum: page.pageNum,
      pageSize: page.pageSize,
      classname: selectedClass.value || null,
    };
    const response = await axios.get("/api/assignments", { params });
    const data = response.data.data;
    homeworkList.value = data.list.map(item => ({
      ...item,
      assignTime: new Date(item.assignTime).toLocaleString(),
      deadline: new Date(item.deadline).toLocaleString(),
    }));
    total.value = data.total;
  } catch (e) {
    ElMessage.error("加载作业列表失败");
  } finally {
    loading.value = false;
  }
}

// 加载班级列表
async function loadClasses() {
    // try {
    //     const response = await axios.get("/api/classes");
    //     classes.value = response.data.data;
    // } catch (e) {
    //     ElMessage.error("加载班级列表失败");
    // }
    // 暂时使用假数据
    classes.value = ["离散22级物联网1班", "离散22级物联网2班"];
}

// 分页变化
function handlePageChange(currentPage) {
  page.pageNum = currentPage;
  load();
}

// 删除作业
function handleDelete(id) {
  ElMessageBox.confirm("确定要删除这项作业吗？", "提示", {
    confirmButtonText: "确定",
    cancelButtonText: "取消",
    type: "warning",
  }).then(async () => {
    try {
      await axios.delete(`/api/assignments/${id}`);
      ElMessage.success("删除成功");
      load(); // 重新加载数据
    } catch (e) {
      ElMessage.error("删除失败");
    }
  }).catch(() => {});
}

// 导出Excel
async function handleExport() {
  try {
    const response = await axios.get('/api/assignments/export', {
      params: {
        classname: selectedClass.value || null,
      },
      responseType: 'blob', // 关键：期望响应是二进制数据
    });

    // 从响应头中获取文件名
    const disposition = response.headers['content-disposition'];
    let filename = '作业列表.xlsx';
    if (disposition && disposition.includes('attachment')) {
      const filenameRegex = /filename[^;=\n]*=((['"]).*?\2|[^;\n]*)/;
      const matches = filenameRegex.exec(disposition);
      if (matches != null && matches[1]) {
        filename = decodeURIComponent(matches[1].replace(/['"]/g, ''));
      }
    }

    // 创建下载链接
    const url = window.URL.createObjectURL(new Blob([response.data]));
    const link = document.createElement('a');
    link.href = url;
    link.setAttribute('download', filename);
    document.body.appendChild(link);
    link.click();

    // 清理
    document.body.removeChild(link);
    window.URL.revokeObjectURL(url);
    ElMessage.success('导出成功！');

  } catch (error) {
    ElMessage.error('导出失败，请重试');
  }
}

// 打开新增表单
function handleCreate() {
  currentAssignmentId.value = null;
  formVisible.value = true;
}

// 打开编辑表单
function handleEdit(id) {
  currentAssignmentId.value = id;
  formVisible.value = true;
}

// 关闭表单
function handleCloseForm(refresh = false) {
  formVisible.value = false;
  if (refresh) {
    load();
  }
}

// 初始化加载
onMounted(() => {
  load();
  loadClasses();
});
</script>

<style scoped>
.page-wrapper {
  padding: 0 20px;
  max-width: 100%;
}
.header {
  margin-bottom: 16px;
}
.breadcrumb {
  font-size: 14px;
  color: #606266;
}
.actions-bar {
  display: flex;
  justify-content: space-between;
  align-items: center;
  background-color: #f5f7fa;
  padding: 12px 20px;
  border-radius: 4px;
  margin-bottom: 20px;
}
.filter-form .el-form-item {
  margin-bottom: 0;
}
.actions-bar .buttons {
  display: flex;
  align-items: center;
  gap: 12px;
}
.pager {
  margin-top: 16px;
  display: flex;
  justify-content: center;
}
</style> 