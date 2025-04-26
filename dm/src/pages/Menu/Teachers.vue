<script setup>
import { ref, onMounted } from 'vue'

const dataList = ref([])

// 发布作业相关
const homeworkDialogVisible = ref(false)
const homeworkContent = ref('')

// 发布公告相关
const noticeDialogVisible = ref(false)
const noticeContent = ref('')

const fetchData = async () => {
  try {
    dataList.value = []
  } catch (err) {
    console.error('加载教师数据失败', err)
  }
}

const publishHomework = () => {
  if (!homeworkContent.value.trim()) {
    ElMessage.warning('作业内容不能为空')
    return
  }
  console.log('发布作业内容:', homeworkContent.value)
  homeworkDialogVisible.value = false
  homeworkContent.value = ''
  ElMessage.success('作业发布成功')
}

const publishNotice = () => {
  if (!noticeContent.value.trim()) {
    ElMessage.warning('公告内容不能为空')
    return
  }
  console.log('发布公告内容:', noticeContent.value)
  noticeDialogVisible.value = false
  noticeContent.value = ''
  ElMessage.success('公告发布成功')
}

onMounted(() => {
  fetchData()
})
</script>

<template>
  <div class="page">
    <h2>教师管理</h2>

    <div class="button-group">
      <el-button type="primary" @click="homeworkDialogVisible = true">发布作业</el-button>
      <el-button type="success" @click="noticeDialogVisible = true">发布公告</el-button>
    </div>

    <div v-if="dataList.length === 0" class="nodata" />
    <ul v-else>
      <li v-for="(item, index) in dataList" :key="index">{{ item }}</li>
    </ul>

    <!-- 发布作业弹窗 -->
    <el-dialog v-model="homeworkDialogVisible" title="发布作业" width="500px">
      <el-input
        v-model="homeworkContent"
        type="textarea"
        placeholder="请输入作业内容"
        :rows="5"
      />
      <template #footer>
        <el-button @click="homeworkDialogVisible = false">取消</el-button>
        <el-button type="primary" @click="publishHomework">发布</el-button>
      </template>
    </el-dialog>

    <!-- 发布公告弹窗 -->
    <el-dialog v-model="noticeDialogVisible" title="发布公告" width="500px">
      <el-input
        v-model="noticeContent"
        type="textarea"
        placeholder="请输入公告内容"
        :rows="5"
      />
      <template #footer>
        <el-button @click="noticeDialogVisible = false">取消</el-button>
        <el-button type="success" @click="publishNotice">发布</el-button>
      </template>
    </el-dialog>
  </div>
</template>

<style scoped>
.page {
  padding: 0 20px;
  max-width: 1200px;
}
h2 {
  padding-bottom: 16px;
  font-size: 28px;
}
.button-group {
  margin-bottom: 20px;
}
.nodata {
  margin-top: 20px;
  color: #888;
}
</style>
