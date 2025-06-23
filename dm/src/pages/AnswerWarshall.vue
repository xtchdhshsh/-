<template>
  <div v-if="loaded" class="wrapper">
    <h2 class="title">Warshall 传递闭包</h2>

    <!-- =========== 矩阵区域 =========== -->
    <div :class="layoutClass" class="matrix-zone">
      <!-- 原矩阵 -->
      <section class="panel">
        <h3>原始矩阵&nbsp;({{ n }}×{{ n }})</h3>
        <table class="matrix">
          <tr v-for="(row, r) in src" :key="r">
            <td v-for="(cell, c) in row" :key="c">{{ cell }}</td>
          </tr>
        </table>
      </section>

      <!-- 填空矩阵 -->
      <section class="panel">
        <h3>请填写闭包矩阵</h3>
        <table class="matrix editable">
          <tr v-for="(row, r) in ans" :key="r">
            <td v-for="(_, c) in row" :key="c">
              <input
                  v-model="ans[r][c]"
                  class="cell"
                  maxlength="1"
                  @input="fix(r,c)"
              />
            </td>
          </tr>
        </table>
      </section>
    </div>

    <el-button type="primary" :loading="posting" @click="submit">
      提交答案
    </el-button>
  </div>

  <!-- 简单 loading -->
  <div v-else class="loading">题目加载中…</div>
</template>

<script setup>
import { ref, onMounted, computed } from 'vue'
import { useRoute, useRouter } from 'vue-router'
import axios from '@/utlis/axios'
import { ElMessage } from 'element-plus'

/* ---------- 状态 ---------- */
const route   = useRoute()
const router  = useRouter()
const loaded  = ref(false)
const posting = ref(false)

const src = ref([])        // 原矩阵
const ans = ref([])        // 填空
const n   = ref(0)         // 阶数

/* ---------- 加载题目 ---------- */
onMounted(async () => {
  try {
    const { data } = await axios.get(`/api/share/${route.params.id}`)
    const payload  = JSON.parse(data.content || '{}')

    if (!Array.isArray(payload.matrix)) {
      throw new Error('数据格式错误')
    }

    src.value = payload.matrix
    n.value   = payload.matrix.length
    ans.value = Array.from({ length: n.value }, () =>
        Array.from({ length: n.value }, () => '')
    )
    loaded.value = true
  } catch (e) {
    console.error(e)
    ElMessage.error('加载失败')
    router.replace('/')
  }
})

/* ---------- 输入校正 ---------- */
function fix (r, c) {
  ans.value[r][c] = ans.value[r][c] === '1' ? '1' : '0'
}

/* ---------- 提交 ---------- */
async function submit () {
  // 必须全部填完
  if (ans.value.flat().some(v => v !== '0' && v !== '1')) {
    ElMessage.warning('所有格子只能填 0 或 1')
    return
  }
  posting.value = true
  try {
    await axios.post('/api/answer', {
      questionId: route.params.id,
      answer: ans.value.map(row => row.map(Number))
    })
    ElMessage.success('提交成功！')
    router.back()
  } catch (e) {
    console.error(e)
    ElMessage.error('提交失败')
  } finally {
    posting.value = false
  }
}

/* ---------- 布局判断 ---------- */
const layoutClass = computed(() => (n.value <= 7 ? 'side' : 'stack'))
</script>

<style scoped>
/* ===== 通用 ===== */
.wrapper{
  /* 1 rem 左右留白，最大 1200px，且整体水平居中 */
  margin: 0 auto;
  padding: 0 1rem;
  max-width: 1200px;
  text-align: center;
}
.title{ margin: 1.2rem 0 2rem; }

/* ===== 布局切换 ===== */
.matrix-zone{
  --gap: 2.5rem;            /* 两张面板之间的间距 */
  display: flex;
  gap: var(--gap);
  justify-content: center;  /* 保证始终居中 */
  align-items: flex-start;
  flex-wrap: wrap;          /* 大矩阵时自动折行到下一行 */
}
.matrix-zone.stack{         /* > 7 阶时本就折行，但此处显式设置方向 */
  flex-direction: column;
  align-items: center;
  gap: 1.8rem;
}

/* ===== 面板 ===== */
.panel h3{ margin-bottom: .5rem; }

/* ===== 矩阵 ===== */
.matrix{
  border-collapse: collapse;
  margin: 0 auto;          /* 再次保证表格本身水平居中 */
}

/* 让大小随窗口宽度自动缩放，最小 38px，最大 60px */
.matrix td{
  --sz: clamp(38px, 4.3vw, 60px);
  width:  var(--sz);
  height: var(--sz);
  border: 1px solid #666;
  font-size: 1rem;
  text-align: center;
}

.matrix.editable td{ padding: 0; }
.cell{
  width: 100%; height: 100%;
  border: none; outline: none;
  text-align: center;
  font-size: 1rem;
}

/* ===== 按钮 ===== */
.el-button{                 /* 让按钮居中显示 */
  display: block;
  margin: 2rem auto 0;
  padding: 0 2.5rem;
}

/* ===== loading ===== */
.loading{ text-align:center; padding:3rem 0; color:#666; }

</style>
