<script setup>
import { ref, watch } from 'vue'

const algorithms = [
  { name: '冒泡排序', value: 'bubble' },
  { name: '选择排序', value: 'selection' },
  { name: '插入排序', value: 'insertion' },
  { name: '快速排序', value: 'quick' },
  { name: '归并排序', value: 'merge' }
]

const selectedAlgorithm = ref('bubble') 
const arraySize = ref(10)               
const sortingSpeed = ref(800)       

// 用户输入
const userInput = ref('')             
const originalInput = ref('')         
const isInputActive = ref(false)       

const isSorting = ref(false)            
const isPaused = ref(false)          

// 重置
const generatedArray = ref([])      
const resetCount = ref(0)          

const array = ref([])                 
const comparingIndices = ref([])  
const swapHistory = ref([])            

let sortingGenerator = null    
const mergeSortInfo = ref([])  // 归并排序文字解释


// 生成随机数组
function generateArray(size) {
  const newArr = Array.from({ length: size }, (_, i) => ({
    id: i,
    value: Math.floor(Math.random() * 100) + 1
  }))
  array.value = [...newArr]
  generatedArray.value = [...newArr]
  resetCount.value = 0
  swapHistory.value = []
  comparingIndices.value = []
  isInputActive.value = false
}

// 重置数组内容
const shouldStop = ref(false)
async function resetArray() {
  shouldStop.value = true     // 终止排序
  
  if (sortingGenerator) {
    sortingGenerator = null
  }

  if (isInputActive.value && userInput.value !== '') {
    const vals = originalInput.value.split(',').map(v => Number(v.trim()))
    array.value = vals.map((val, i) => ({ id: i, value: val }))
    userInput.value = originalInput.value
    resetCount.value = 0
  } else {
    isInputActive.value = false
    // 第一次重置：恢复为初始生成的数组
    if (resetCount.value === 0 && generatedArray.value.length > 0) {
      array.value = [...generatedArray.value]
      resetCount.value++    // 准备第二次重置：生成新的数组
    } else {
      generateArray(arraySize.value)
    }
  }

  isSorting.value = false
  isPaused.value = false
  comparingIndices.value = []
  swapHistory.value = []
  mergeSortInfo.value = []
}

// 当数组大小变化时自动重置
watch(arraySize, () => {
  if (!isInputActive.value) {
    resetArray()
  }
}, { immediate: true })

// 用户输入处理
function InputConfirm() {
  const vals = userInput.value.split(',').map(v => v.trim())

  const nums = []
  for (const v of vals) {
    const n = Number(v)
    if (isNaN(n) || !Number.isInteger(n) || n <= 0) {
      ElMessage.error(`无效数字：[${v}]，请输入正整数`)
      return
    }
    nums.push(n)
  }

  // 构建排序数组
  array.value = nums.map((val, i) => ({ id: i, value: val }))
  swapHistory.value = []
  comparingIndices.value = []
  sortingGenerator = null
  isInputActive.value = true
  originalInput.value = userInput.value.trim()
}

// 延迟函数
function sleep(ms) {
  return new Promise(resolve => setTimeout(resolve, ms))
}

// 保存交换信息
function recordSwap(i, j) {
  const val1 = array.value[i].value
  const val2 = array.value[j].value
  swapHistory.value.push({ index1: i, index2: j, value1: val1, value2: val2 })
}
// 执行数组交换操作
function swap(i, j) {
  if (i === j) {
    return    // 下标相同不返回交换信息
  }
  const temp = array.value[i]
  array.value[i] = array.value[j]
  array.value[j] = temp
  recordSwap(i, j)
}

// 开始排序
async function startSorting() {
  isSorting.value = true
  isPaused.value = false
  shouldStop.value = false

  const algo = sortingAlgorithms[selectedAlgorithm.value]()
  sortingGenerator = algo

  for await (const _ of algo) {
    if (shouldStop.value) {
      break
    }
    while (isPaused.value) {
      if (shouldStop.value) {
        break
      }
      await sleep(100)
    }
    await sleep(sortingSpeed.value)
  }

  // 排序完成或被终止
  isSorting.value = false
  sortingGenerator = null
  comparingIndices.value = []
}

// 暂停排序
function pauseSorting() {
  isPaused.value = true
}

// 恢复排序
function resumeSorting() {
  isPaused.value = false
}

// 单步执行排序
async function stepSorting() {
  if (isSorting.value && !sortingGenerator) {
    return
  }

  if (!sortingGenerator) {
    sortingGenerator = sortingAlgorithms[selectedAlgorithm.value]()
  }

  const next = await sortingGenerator.next()
  if (next.done) {
    sortingGenerator = null
    comparingIndices.value = []
  }
}

// 排序算法生成
const sortingAlgorithms = {
  *bubble() {
    let n = array.value.length
    for (let i = 0; i < n - 1; i++) {
      for (let j = 0; j < n - i - 1; j++) {
        comparingIndices.value = [j, j + 1]
        yield
        if (array.value[j].value > array.value[j + 1].value) {
          swap(j, j + 1)
        }
      }
    }
    comparingIndices.value = []
  },

  *selection() {
    let n = array.value.length
    for (let i = 0; i < n; i++) {
      let minIndex = i
      for (let j = i + 1; j < n; j++) {
        comparingIndices.value = [minIndex, j]
        yield
        if (array.value[j].value < array.value[minIndex].value) {
          minIndex = j
        }
      }
      if (minIndex !== i) {
        swap(i, minIndex)
      }
    }
    comparingIndices.value = []
  },

  *insertion() {
    let n = array.value.length
    for (let i = 1; i < n; i++) {
      let j = i
      while (j > 0 && array.value[j - 1].value > array.value[j].value) {
        comparingIndices.value = [j - 1, j]
        yield
        swap(j, j - 1)
        j--
      }
    }
    comparingIndices.value = []
  },

  *quick() {
    // 分区
    function* partition(arr, low, high) {
      let pivot = arr[high].value
      let i = low - 1
      for (let j = low; j < high; j++) {
        comparingIndices.value = [j, high]
        yield
        if (arr[j].value < pivot) {
          i++
          swap(i, j)
        }
      }
      swap(i + 1, high)
      yield i + 1
      return i + 1
    }

    // 递归
    function* quickSort(arr, low, high) {
      if (low < high) {
        const pGen = partition(arr, low, high)
        let pResult = pGen.next()
        while (!pResult.done) {
          yield pResult.value
          pResult = pGen.next()
        }
        const p = pResult.value
        yield* quickSort(arr, low, p - 1)
        yield* quickSort(arr, p + 1, high)
      }
    }

    yield* quickSort(array.value, 0, array.value.length - 1)
    comparingIndices.value = []
  },

  *merge() {
    function* mergeSort(arr, l, r) {
      if (l >= r) {
        mergeSortInfo.value.push(`跳过子数组 [${l}, ${r}]`)
        yield
        return
      }

      const m = Math.floor((l + r) / 2)
      mergeSortInfo.value.push(`拆分为 [${l}, ${m}] 和 [${m + 1}, ${r}]`)
      yield* mergeSort(arr, l, m)
      yield* mergeSort(arr, m + 1, r)

      mergeSortInfo.value.push(`合并 [${l}, ${m}] 和 [${m + 1}, ${r}]`)
      yield

      const left = array.value.slice(l, m + 1)
      const right = array.value.slice(m + 1, r + 1)
      let i = 0, j = 0, k = l

      while (i < left.length && j < right.length) {
        comparingIndices.value = [left[i].id, right[j].id]
        mergeSortInfo.value.push(`比较 ${left[i].value} 和 ${right[j].value}`)
        yield

        if (left[i].value <= right[j].value) {
          array.value[k] = left[i]
          i++
        } else {
          array.value[k] = right[j]
          j++
        }
        k++
      }
      while (i < left.length) {
        array.value[k] = left[i]
        i++
        k++
      }
      while (j < right.length) {
        array.value[k] = right[j]
        j++
        k++
      }

      mergeSortInfo.value.push(`合并完成 [${l}, ${r}]`)
      yield
    }

    yield* mergeSort(array.value, 0, array.value.length - 1)
    comparingIndices.value = []
  }
}
</script>

<template>
  <div class="sorting-visualizer">
    <h1>排序算法可视化</h1>

    <!-- 控制台 -->
    <div class="controls">
      <div class="control-group">
        <label>算法:</label>
        <el-select 
          v-model="selectedAlgorithm" 
          placeholder="选择算法" 
          size="small" 
          :disabled="isSorting" 
          style="width: 90px;"
        >
          <el-option 
            v-for="algo in algorithms" 
            :key="algo.value" 
            :label="algo.name" 
            :value="algo.value" 
          />
        </el-select>
      </div>

      <div class="control-group">
        <label>大小:</label>
        <el-slider 
          v-model="arraySize" 
          :min="5" :max="20" :step="1" show-input size="small" 
          style="width: 200px;" 
          :disabled="isSorting" 
        />
      </div>

      <div class="control-group">
        <label>速度:</label>
        <el-slider 
          v-model="sortingSpeed" 
          :min="500" :max="2000" :step="100" show-input size="small" 
          style="width: 200px;" 
          :disabled="isSorting" 
        />
      </div>

      <div class="control-group">
        <label>输入数组:</label>
        <input
          v-model="userInput"
          :disabled="isSorting"
          @keyup.enter="InputConfirm"
          placeholder="如: 5,8,1,3 (注意为英文逗号)"
          style="width: 200px; padding: 4px 8px; border-radius: 4px; border: 1px solid #ccc;"
        />
        <el-button type="success" @click="InputConfirm" :disabled="false">确认输入</el-button>
      </div>

      <div class="buttons">
        <el-button type="primary" size="small" @click="startSorting" :disabled="isSorting">完整排序</el-button>
        <el-button type="warning" size="small" @click="stepSorting">单步交换</el-button>
        <el-button type="info" size="small" @click="resetArray" :disabled="false">重置</el-button>
        <el-button type="success" size="small" @click="pauseSorting" :disabled="!isSorting || isPaused">暂停</el-button>
        <el-button type="success" size="small" @click="resumeSorting" :disabled="!isSorting || !isPaused">继续</el-button>
      </div>
    </div>

    <!-- 可视化窗口 -->
    <div class="visualization">
      <div v-for="(item, index) in array" :key="item.id">
        <div
          class="bar"
          :class="{ comparing: comparingIndices.includes(index) }"
          :style="{
            height: `${item.value * 3}px`,
            width: `calc(${100 / array.length}% - 1.5rem)`,
            left: `calc(${index * (100 / array.length)}%)`,
            transition: isSorting ? 'left 0.3s ease, height 0.3s ease' : 'none'
          }"
        >
          <div class="bar-value">{{ item.value }}</div>
        </div>
        <div 
          class="bar-index"
          :style="{
            width: `calc(${100 / array.length}% - 1rem)`,
            left: `calc(${index * (100 / array.length)}%)`,
          }"
        >{{ index }}</div>
      </div>
      
    </div>

    <!-- 排序信息 -->
    <el-card v-if="selectedAlgorithm === 'merge'" class="swap-history">
      <template #header>
        <h3 style="text-align: center;">合并记录</h3>
      </template>

      <el-scrollbar height="300px">
        <el-timeline>
          <el-timeline-item v-for="(log, index) in mergeSortInfo" :key="index">
            {{ log }}
          </el-timeline-item>
        </el-timeline>
      </el-scrollbar>
    </el-card>

    <el-card class="swap-history" v-else>
      <template #header>
        <h3 style="text-align: center;">交换记录</h3>
      </template>

      <el-table :data="swapHistory" style="width: 100%;" height="400">
        <el-table-column prop="step" label="步骤" min-width="20%">
          <template #default="scope">
            {{ scope.$index + 1 }}
          </template>
        </el-table-column>
        <el-table-column label="交换位置" min-width="30%">
          <template #default="scope">
            {{ scope.row.index1 }} ↔ {{ scope.row.index2 }}
          </template>
        </el-table-column>
        <el-table-column label="交换值" min-width="30%">
          <template #default="scope">
            {{ scope.row.value1 }} ↔ {{ scope.row.value2 }}
          </template>
        </el-table-column>
      </el-table>
    </el-card>
  </div>
</template>

<style scoped>
.sorting-visualizer {
  max-width: 75%;
  margin: 3rem auto;
  padding: 2rem;
  background-color: #fff;
  border: 0.1rem solid #ccc;
  border-radius: 2rem;
}

h1 {
  text-align: center;
  margin-bottom: 2rem;
}

.controls {
  display: flex;
  flex-wrap: wrap;
  gap: 2rem;
  justify-content: center;
  margin-bottom: 2rem;
}
.control-group {
  display: flex;
  align-items: center;
  gap: 12px;
}
.buttons {
  display: flex;
  gap: 1.5rem;
  justify-content: center;
  margin-left: 1rem;
}

.visualization {
  position: relative;
  height: 20rem;
  /* border: 1px solid #ddd; */
  margin-bottom: 60px;
}
.bar {
  position: absolute;
  bottom: 0;
  background-color: #409EFF;
  text-align: center;
  border-radius: 8px 8px 0 0;
}
.bar.comparing {
  background-color: #F56C6C;
}
.bar-value {
  position: absolute;
  top: -30px;
  width: 100%;
  text-align: center;
  color: black;
  font-size: 16px;
}
.bar-index {
  position: absolute;
  bottom: -36px;
  text-align: center;
}

.swap-history {
  margin-top: 20px;
  padding: 10px 20px;
  border-radius: 8px;
}
</style>
