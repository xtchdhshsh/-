<template>
  <div class="answer-sat-page">
    <h2 class="page-title">集合运算答题</h2>

    <el-card class="question-card">
      <div class="question-line"><strong>集合 A：</strong> {{ sets.a.join(', ') || '∅' }}</div>
      <div class="question-line"><strong>集合 B：</strong> {{ sets.b.join(', ') || '∅' }}</div>
      <div class="question-line"><strong>集合 C：</strong> {{ sets.c.join(', ') || '∅' }}</div>
      <div class="question-line"><strong>运算式：</strong> {{ formula }}</div>
    </el-card>

    <el-card class="answer-card">
      <div class="answer-prompt"><strong>请输入运算结果：</strong></div>
      <el-input
          v-model="studentAnswer"
          placeholder="用逗号分隔元素，例如：1,2,3"
          clearable
      />
      <el-button type="primary" @click="submitAnswer" :loading="submitting" class="submit-btn">
        提交答案
      </el-button>
    </el-card>
  </div>
</template>

<script setup>
import { ref, onMounted } from 'vue'
import { useRoute, useRouter } from 'vue-router'
import axios from '@/utlis/axios'
import { ElMessage } from 'element-plus'

const route = useRoute()
const router = useRouter()

const sets = ref({ a: [], b: [], c: [] })
const formula = ref('')
const studentAnswer = ref('')
const submitting = ref(false)

onMounted(async () => {
  const id = route.params.id
  try {
    const { data } = await axios.get(`/api/share/${id}`)
    const payload = JSON.parse(data.content)
    sets.value = payload.sets || { a: [], b: [], c: [] }
    formula.value = payload.formula || ''
  } catch (err) {
    ElMessage.error('加载题目失败，返回首页')
    router.replace('/')
  }
})

async function submitAnswer() {
  if (!studentAnswer.value.trim()) {
    ElMessage.info('请输入你的答案')
    return
  }
  const arr = studentAnswer.value
      .split(',')
      .map(s => s.trim())
      .filter(s => s !== '')
  submitting.value = true
  try {
    await axios.post('/api/answer', {
      questionId: route.params.id,
      answer: arr
    })
    ElMessage.success('提交成功')
    router.back()
  } catch {
    ElMessage.error('提交失败')
  } finally {
    submitting.value = false
  }
}
</script>

<style scoped>
.answer-sat-page {
  max-width: 600px;
  margin: 40px auto;
  display: flex;
  flex-direction: column;
  gap: 20px;
}

.page-title {
  text-align: center;
  font-size: 24px;
  margin-bottom: 16px;
}

.question-card,
.answer-card {
  padding: 16px;
}

.question-line {
  margin-bottom: 8px;
  font-size: 16px;
}

.answer-prompt {
  margin-bottom: 8px;
  font-size: 16px;
}

.submit-btn {
  margin-top: 12px;
  display: block;
}
</style>
