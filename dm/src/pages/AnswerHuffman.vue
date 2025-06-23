<script setup>
import { ref, reactive, onMounted } from 'vue'
import { useRoute, useRouter } from 'vue-router'
import axios         from '@/utlis/axios'
import { ElMessage } from 'element-plus'

const route   = useRoute()
const router  = useRouter()
const loaded  = ref(false)

const table = reactive([])   
const n     = ref(0)

onMounted(async () => {
  try {
    const { data } = await axios.get(`/api/share/${route.params.id}`)
    let p = typeof data.content === 'string' ? JSON.parse(data.content) : data.content
    if (data.type !== 'huffman' && p.type !== 'huffman') throw new Error('类型错误')

    p.table.forEach(row => table.push({ ...row, code:'' }))
    n.value = table.length
    loaded.value = true
  } catch (e) {
    console.error(e)
    ElMessage.error('题目加载失败'); router.back()
  }
})

async function submit () {
  if (table.some(r => !/^[01]+$/.test(r.code))) {
    ElMessage.warning('每个编码只能填 0/1，且不能为空')
    return
  }
  try {
    await axios.post('/api/answer', {
      questionId : route.params.id,
      answer     : table.map(r => ({ ch:r.ch, code:r.code }))
    })
    ElMessage.success('提交成功'); router.back()
  } catch(e){
    console.error(e); ElMessage.error('提交失败')
  }
}
</script>

<template>
  <div v-if="loaded" class="wrap">
    <h2 class="title">Huffman 编码</h2>

    <!-- 字符 + 权重 + 输入编码 -->
    <el-table :data="table" class="tbl"
              :header-cell-style="{'text-align':'center'}"
              :cell-style="{'text-align':'center'}">
      <el-table-column prop="ch"    label="字符" width="120"/>
      <el-table-column prop="w"     label="权重" width="120"/>
      <el-table-column label="你的编码">
        <template #default="{ row }">
          <el-input v-model="row.code" placeholder="如 1010" maxlength="20"/>
        </template>
      </el-table-column>
    </el-table>

    <el-button type="primary" class="btn" @click="submit">提交答案</el-button>
  </div>

  <div v-else class="loading">题目加载中…</div>
</template>

<style scoped>
.wrap   { 
    max-width: 620px; 
    margin: 20px auto; 
}
.title  { 
    text-align:center; 
    margin-bottom:14px; 
}
.tbl    { 
    margin-bottom: 16px; 
}
.btn    { 
    display:block; 
    margin:0 auto;
 }
.loading{ 
    text-align:center; 
    padding:60px 0; 
    color:#666 
}
</style>
