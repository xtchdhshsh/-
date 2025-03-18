<script setup>
import { reactive, ref, onMounted } from 'vue';
import axios from '@/utlis/axios';

const emit = defineEmits(["getInputForm", "getResponse","lujingUpdated"]); //定义宏事件
const inputForm = reactive({ count: 0, node: [], link: '' }); // 输入表单
const transferForm = reactive({ count: 0, node: [], link: [] }); // 组件传输表单
let nodeSelected = ref();
// let LJ = ['AC', 'CF', 'DF', 'BC', 'BE'];
  let LJ ;
const minCount = 3;
const ruleForm = ref();
const rules = reactive({
  count: [{ required: true }],
  node: [{ required: true, message: '请输入节点', trigger: 'change' }],
  link: [{ required: true, message: '请输入路径信息', trigger: 'blur' }]
});

// 根据节点个数显示节点信息
const showNode = n => {
  inputForm.node = new Array();
  for (let i = 0; i < n; i++) {
    inputForm.node.push(String.fromCharCode(65 + i));
  }
}



// 提交表单信息
const submitForm = async ruleForm => {
  if (!ruleForm) return;
  await ruleForm.validate(valid => {
    if (valid) {
      transferForm.count = inputForm.count;
      transferForm.node = inputForm.node;
      // 解析路径信息
      !function(){
        transferForm.link = [];
        // 去除空格和换行
        let str = inputForm.link.replace(/[ ]|[\r\n]/g,"");
        str = str.split(',');
        str = str.map( s => {
          s = s.split(':');
          transferForm.link.push([...s[0].split('-'), s[1]]);
        });
      }();

      

        axios.post(
          "/scs",
          {
              num: inputForm.count,
              powerTable: createPowerTable()
          }
        )
        .then( response => {
          console.log(response.data)
             LJ = response.data;})
  
      // 传送表单信息
      emit("getInputForm", transferForm);
      emit("lujingUpdated", LJ);
    }
  })
    // console.log(inputForm.node)
    // console.log(createPowerTable())


}

// 重置输入框
const resetForm = ruleForm => {
  if (!ruleForm) return;
  ruleForm.resetFields();
  showNode(minCount);
}

// 生成权重表格
const createPowerTable = () => {
  let powerTable = new Array();
  inputForm.node.forEach(col => {
    let rowArr = new Array();
    inputForm.node.forEach(row => {
      if(col == row){
        rowArr.push(0);
      } else {
        let power = -1;
        const len = transferForm.link.length;
        for (let i = 0; i < len; i++) {
          const link = transferForm.link[i];
          if(col == link[0] && row == link[1] || col == link[1] && row == link[0]){
            power = +link[2];
            break;
          }
        }
        rowArr.push(power);
      }
    })
    powerTable.push(rowArr);
  })
  return powerTable;
}

const searchPath = () => {
  const response = {
    jieguo:[
      [0,10,1000,4,1000,1000],
      [0,6,1000,4,10,1000],
      [0,6,14,4,10,1000],
      [0,6,11,4,10,22],
      [0,6,11,4,10,16],
      [0,6,11,4,10,16]],
    lujing:['ADB:6', 'ADEC:11', 'AD:4', 'ADE:10', 'ADECF:16']
  }
  // axios.post(
  //       "http://localhost:8080/scs",
  //       {
  //           num: inputForm.count,
  //           powerTable: createPowerTable()
  //       }
  //   )
  // .then( response => {
  //   console.log(response.data)
  //     let LJ = response.data;})
      
      let { jieguo: valueTable, lujing: result } = response;
      let resp = { };
      let path = new Array();
      let nodeStep = new Array();
      let pathSort = new Array();
      let count = 0;
      // 分割result
      result = result.map(r => r = r.split(":"));
      result.forEach(r => { r[0] = [...r[0]]; pathSort.push(+r[1]); });
      // 最短路径值排序
      pathSort.sort((a, b) => a - b);
      // 生成每步经过节点的数组并对最短路径排序
      nodeStep.push(nodeSelected.value);
      while(result.length != 0){
        const lenR = result.length;
        for(let i = 0; i < lenR; i++){
          if(result[i][1] == pathSort[inputForm.count - 1 - lenR]){
            const lenRI = result[i][0].length;
            path.push(result[i]);
            nodeStep.push(result[i][0][lenRI - 1]);
            result.splice(i, 1);
            break;
          }
        }
        count += 1;
        if(count > 20){ break; }
      }      
      // 传送后端数据
      emit("getResponse", resp = {
        nodeSelected: nodeSelected,
        path: path,
        valueTable: valueTable,
        nodeStep: nodeStep
      });
    // })
};

onMounted(() => {
  // 节点信息初始化
  showNode(minCount);
})
</script>

<template>
    <!-- 输入表单 -->
    <el-form ref="ruleForm" :model="inputForm" :rules="rules" class="inputArea">
        <!-- 基本信息 -->
      <el-form-item label="节点个数" prop="count">
        <el-input-number v-model="inputForm.count" :min="minCount" :max="20" @change="showNode" />
      </el-form-item>
      <el-form-item label="节点信息" prop="node">
        <el-input v-model.trim="inputForm.node" />
      </el-form-item>
      <el-form-item label="路径信息" prop="link">
        <el-input v-model.trim="inputForm.link" type="textarea" class="elTextarea" placeholder="A-B:3, A-C:5,&#10B-C:1" />
      </el-form-item>
      <!-- 按钮 -->
      <div class="buttons">
      <el-form-item>
      <el-button type="primary" @click="submitForm(ruleForm)">生成</el-button>
      <el-button @click="resetForm(ruleForm)">重置</el-button>
      </el-form-item>
      
      </div>
  </el-form>
</template>
  
<style scoped>
.inputArea{
    width: 400px;
    padding: 15px;
    border: 1px solid #666;
    border-radius: 8px;
}
:deep(.elTextarea textarea){
  height: 100px;
  resize: none;
}
.buttons{
  display: flex;
  justify-content: space-between;
  padding: 0 10px;
}
.selectNode{
  width: 100px;
  margin: 0 8px;
}
</style>
