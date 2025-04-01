<script setup>
import { ref, onMounted } from 'vue'
import axios from '@/utlis/axios' // 你自定义的 axios 封装

// 用于存放后端返回的表头与表数据
const tableHeaders = ref([]) // ["p", "q", "p→q", ...]
const tableData = ref([])    // 二维数组 [[0,0,1], [0,1,1], [1,0,0], [1,1,1], ...]

onMounted(() => {
  const enter = document.querySelector('.enter')
  const submit = document.querySelector('.submit')

  // 1）插入符号按钮
  // 这里假设与前面的写法一致：页面上有若干 .tipbut
  for (let i = 2; i <= 7; i++) {
    const tipButton = document.querySelector(`.tipbut:nth-child(${i})`)
    if (!tipButton) continue
    tipButton.addEventListener('click', () => {
      const cursorPosition = enter.selectionStart
      const startText = enter.value.substring(0, cursorPosition)
      const endText = enter.value.substring(cursorPosition)
      const textToInsert = tipButton.value
      enter.value = startText + textToInsert + endText

      // 设置光标位置
      enter.setSelectionRange(
        cursorPosition + textToInsert.length,
        cursorPosition + textToInsert.length
      )
      enter.focus()
    })
  }

  // 2）提交按钮：向后端请求真值表
  submit.addEventListener('click', () => {
    const formula = enter.value.trim()
    if (!formula) return
    axios.get(`/zhenzhi/${formula}`)
      .then(res => {
        const resp = res.data
        tableHeaders.value = resp.va || []
        tableData.value = resp.data || []
        console.log(resp)
      })
      .catch(err => {
        console.error('请求异常:', err)
      })
  })
})
</script>

<template>
  <div class="banner">
    <!-- 左侧输入区 -->
    <el-card style="min-width: 580px; width: 550px" class="leftbanner">
      <template #header>
        <div class="card-header title">
          <span>命题公式真值表</span>
        </div>
      </template>

      <el-card style="min-width: 430px; height: 200px" class="decoration">
        <div class="tip">请输入命题公式:</div>

        <!-- 逻辑符号按钮 -->
        <el-button type="button" class="tipbut" value="∨">∨</el-button>
        <el-button type="button" class="tipbut" value="∧">∧</el-button>
        <el-button type="button" class="tipbut" value="→">→</el-button>
        <el-button type="button" class="tipbut" value="﹁">﹁</el-button>
        <el-button type="button" class="tipbut" value="↔">↔</el-button>
        <el-button type="button" class="tipbut" value="()">()</el-button>

        <!-- 输入框 + 提交按钮 -->
        <input
          type="text"
          placeholder="例如：(p∨q)->r∨s"
          class="enter"
        />
        <el-button type="primary submit" class="submit">提交</el-button>
      </el-card>
    </el-card>

    <!-- 右侧显示表格 - 外层容器控制滚动和自适应 -->
    <div class="table-container">
      <el-table :data="tableData" border style="width: auto" height="500" :header-cell-style="{ textAlign: 'center' , backgroundColor: '#000', color: '#fff'}">
        <!-- 动态生成列：根据后端返回的表头数组 -->
        <el-table-column
          v-for="(header, hIndex) in tableHeaders"
          :key="hIndex"
          :label="header"
          :align="center"
        >
          <!-- 单元格的具体展示：row[hIndex] -->
          <template #default="{ row }">
            {{ row[hIndex] }}
          </template>
        </el-table-column>
      </el-table>
    </div>
  </div>
</template>

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
  width: 450px;
  height: 200px;
  margin-top: 100px;
  position: relative;
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
} 
.submit {
  height: 32px;
  width: 60px;
  margin-left: 10px;
}

/* 让表格容器可在内容过宽时滚动，实现“大小自适应” */
.table-container {
  margin-left: 20px;      /* 右侧表格和左侧卡片分开一些 */
  border: 0 solid;
  overflow-x: auto;      /* 如果表格过宽，则出现横向滚动条 */
  /* 如果希望固定最大高度，也可加: max-height: 400px; overflow-y: auto; */
  width: 100%;           /* 让表格容器宽度自适应 */
  height: 500px;         /* 固定高度 */
  margin-top: 20px;
  margin-bottom: 20px;
}

.el-table {
  width: auto;           /* 让表格宽度自适应 */
}
/* .el-table .cell {
  white-space: nowrap;
} */

</style>
