<template>
  <el-dialog
    :title="isEdit ? '修改作业' : '布置作业'"
    v-model="dialogVisible"
    width="70%"
    :before-close="handleClose"
    :close-on-click-modal="false"
  >
    <el-form :model="form" ref="formRef" label-width="100px" v-loading="loading">
      <el-row :gutter="20">
        <el-col :span="12">
          <el-form-item label="作业标题" prop="title" required>
            <el-input v-model="form.title" placeholder="请输入作业标题"></el-input>
          </el-form-item>
        </el-col>
        <el-col :span="12">
           <el-form-item label="选择班级" prop="classname" required>
             <el-select v-model="form.classname" placeholder="请选择班级" style="width: 100%;">
               <el-option v-for="c in classes" :key="c" :label="c" :value="c"></el-option>
             </el-select>
           </el-form-item>
        </el-col>
      </el-row>

      <el-form-item label="作业描述" prop="description">
        <el-input v-model="form.description" type="textarea" :rows="2" placeholder="请输入作业描述"></el-input>
      </el-form-item>
      
      <el-form-item label="截止时间" prop="deadline" required>
        <el-date-picker
          v-model="form.deadline"
          type="datetime"
          placeholder="选择截止日期时间"
          style="width: 100%;"
        ></el-date-picker>
      </el-form-item>

      <el-divider>选择题目</el-divider>

      <el-table :data="allProblems" ref="problemsTable" @selection-change="handleSelectionChange" height="250px">
        <el-table-column type="selection" width="55" />
        <el-table-column prop="id" label="题目ID" width="80" />
        <el-table-column prop="content" label="题目内容" />
        <el-table-column label="分值" width="150">
           <template #default="{ row }">
              <el-input-number v-model="row.points" :min="0" :max="100" size="small" controls-position="right" />
           </template>
        </el-table-column>
      </el-table>

    </el-form>

    <template #footer>
      <span class="dialog-footer">
        <el-button @click="handleClose">取 消</el-button>
        <el-button type="primary" @click="handleSubmit" :loading="submitLoading">确 定</el-button>
      </span>
    </template>
  </el-dialog>
</template>

<script setup>
import { ref, watch, onMounted, defineProps, defineEmits } from 'vue';
import { ElMessage } from 'element-plus';
import axios from '@/utlis/axios';

const props = defineProps({
  assignmentId: {
    type: Number,
    default: null,
  },
});

const emit = defineEmits(['close']);

const isEdit = ref(false);
const dialogVisible = ref(true);
const loading = ref(false);
const submitLoading = ref(false);

const form = ref({
  title: '',
  description: '',
  classname: '',
  deadline: '',
  problems: [],
});
const formRef = ref(null);

const allProblems = ref([]);
const selectedProblems = ref([]);
const classes = ref(["离散22级物联网1班", "离散22级物联网2班"]); 

// 加载题目列表 (假数据，需要替换成真实接口)
async function loadAllProblems() {
    // TODO: 调用后端接口获取所有题目
    allProblems.value = [
        { id: 1, content: "证明：(P→Q) ⇔ (¬P∨Q)", points: 10 },
        { id: 2, content: "求(A∩B)∪C的真值表", points: 10 },
        { id: 3, content: "什么是哈密顿图？", points: 10 },
        { id: 4, content: "迪杰斯特拉算法的应用场景", points: 10 },
    ];
}

// 加载作业详情（用于编辑）
async function loadAssignmentDetail() {
  if (!props.assignmentId) return;
  loading.value = true;
  try {
    const response = await axios.get(`/api/assignments/${props.assignmentId}`);
    const data = response.data.data;
    form.value = {
        title: data.title,
        description: data.description,
        classname: data.classname,
        deadline: new Date(data.deadline),
        problems: data.problems,
    };
    
    // 回显选中的题目和分数
    const problemIds = data.problems.map(p => p.problemId);
    allProblems.value.forEach(p => {
        if(problemIds.includes(p.id)) {
            const problemData = data.problems.find(item => item.problemId === p.id);
            if (problemData) {
                p.points = problemData.points;
            }
        }
    });

    // 延迟以确保DOM更新完毕
    setTimeout(() => {
        allProblems.value.forEach(p => {
            if(problemIds.includes(p.id)) {
                this.$refs.problemsTable.toggleRowSelection(p, true);
            }
        });
    }, 100);

  } catch (error) {
    ElMessage.error('加载作业详情失败');
  } finally {
    loading.value = false;
  }
}

// 提交表单
async function handleSubmit() {
  try {
    await formRef.value.validate();
  } catch (error) {
    ElMessage.warning('请填写完整的作业信息');
    return;
  }
  
  if (!form.value.deadline) {
    ElMessage.warning('请选择截止时间');
    return;
  }

  if (selectedProblems.value.length === 0) {
      ElMessage.warning('请至少选择一个题目');
      return;
  }

  submitLoading.value = true;
  const payload = {
    ...form.value,
    deadline: form.value.deadline ? form.value.deadline.getTime() : null,
    problems: selectedProblems.value.map(p => ({
        problemId: p.id,
        points: p.points || 0,
    })),
  };

  try {
    if (isEdit.value) {
      await axios.put(`/api/assignments/${props.assignmentId}`, payload);
      ElMessage.success('作业修改成功');
    } else {
      await axios.post('/api/assignments', payload);
      ElMessage.success('作业布置成功');
    }
    emit('close', true); // true表示需要刷新列表
  } catch (error) {
    ElMessage.error(isEdit.value ? '修改失败' : '布置失败');
  } finally {
    submitLoading.value = false;
  }
}

function handleSelectionChange(selection) {
    selectedProblems.value = selection;
}

function handleClose() {
  emit('close');
}

watch(() => props.assignmentId, (newId) => {
    isEdit.value = newId !== null;
    if (isEdit.value) {
        loadAssignmentDetail();
    }
}, { immediate: true });

onMounted(() => {
    loadAllProblems();
})

</script>

<style scoped>
.dialog-footer {
  text-align: right;
}
</style> 