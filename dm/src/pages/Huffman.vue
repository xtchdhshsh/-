<script setup>
import * as echarts from 'echarts';
import { reactive, ref, onMounted } from 'vue';
import axios from '@/utlis/axios';

const TIME_PERSTEP = 1000; // 每一步的时间

let num = 0;
const ruleForm = ref();
let inputForm = reactive({string: '', strArr: [],wordStat:  false, spaceStat: true, othersStat: true});
const rules = reactive({string:[{required: true, message: '请输入数据', trigger: 'blur'}]});
let currentStep = ref(-1);
let process = new Array();
const outputTable = reactive([]);
let WPL = ref();
let chart;
// 根节点或叶子节点的标签样式
let specialLabelRich = {
    align: 'center', // 文字对齐
    rich: {
        value: {
            fontSize: 20,
            fontWeight: 700,
            height: 80
        },
        name: {
            fontSize: 20,
            fontWeight: 700,
            height: 0
        }
    }
};
let data = {
    name: 'Step',
    value: 0,
    children: [],
    // 隐去根节点到子节点的连线
    lineStyle: { color: 'rgba(0,0,0,0)' }
};
let option = {
    // 工具栏
    toolbox:{
        show: true,
        itemSize: 20, // icon大小
        itemGap: 20, // icon间隔
        top: '1%',
        right:'1%',
        feature: {
            // 上一步
            myBack: {
                title: 'back',
                icon: 'path://M669.6 849.6c8.8 8 22.4 7.2 30.4-1.6s7.2-22.4-1.6-30.4l-309.6-280c-8-7.2-8-17.6 0-24.8l309.6-270.4c8.8-8 9.6-21.6 2.4-30.4-8-8.8-21.6-9.6-30.4-2.4L360.8 480.8c-27.2 24-28 64-0.8 88.8l309.6 280z',
                onclick: () => {
                    if(currentStep.value > 0) {
                        drawProcess(--currentStep.value);
                    }
                }
            },
            // 下一步
            myNext: {
                title: 'next',
                icon: 'path://M642.174 504.594c7.99 7.241 7.897 17.58-0.334 24.782L332.62 799.945c-8.867 7.759-9.766 21.236-2.007 30.103 7.758 8.867 21.236 9.766 30.103 2.007l309.221-270.569c27.429-24 27.792-64.127 0.89-88.507L360.992 192.192c-8.73-7.912-22.221-7.248-30.133 1.482-7.912 8.73-7.248 22.222 1.482 30.134l309.833 280.786z',
                onclick: () => {
                    if(currentStep.value < num - 1) { 
                        drawProcess(++currentStep.value); 
                    }
                }
            },
            // 重写restore
            myRestore: {
                title: 'restore',
                icon: 'path://M943.8 484.1c-17.5-13.7-42.8-10.7-56.6 6.8-5.7 7.3-8.5 15.8-8.6 24.4h-0.4c-0.6 78.3-26.1 157-78 223.3-124.9 159.2-356 187.1-515.2 62.3-31.7-24.9-58.2-54-79.3-85.9h77.1c22.4 0 40.7-18.3 40.7-40.7v-3c0-22.4-18.3-40.7-40.7-40.7H105.5c-22.4 0-40.7 18.3-40.7 40.7v177.3c0 22.4 18.3 40.7 40.7 40.7h3c22.4 0 40.7-18.3 40.7-40.7v-73.1c24.2 33.3 53 63.1 86 89 47.6 37.3 101 64.2 158.9 79.9 55.9 15.2 113.5 19.3 171.2 12.3 57.7-7 112.7-24.7 163.3-52.8 52.5-29 98-67.9 135.3-115.4 37.3-47.6 64.2-101 79.9-158.9 10.2-37.6 15.4-76 15.6-114.6h-0.1c-0.3-11.6-5.5-23.1-15.5-30.9zM918.7 135.2h-3c-22.4 0-40.7 18.3-40.7 40.7V249c-24.2-33.3-53-63.1-86-89-47.6-37.3-101-64.2-158.9-79.9-55.9-15.2-113.5-19.3-171.2-12.3-57.7 7-112.7 24.7-163.3 52.8-52.5 29-98 67.9-135.3 115.4-37.3 47.5-64.2 101-79.9 158.8-10.2 37.6-15.4 76-15.6 114.6h0.1c0.2 11.7 5.5 23.2 15.4 30.9 17.5 13.7 42.8 10.7 56.6-6.8 5.7-7.3 8.5-15.8 8.6-24.4h0.4c0.6-78.3 26.1-157 78-223.3 124.9-159.2 356-187.1 515.2-62.3 31.7 24.9 58.2 54 79.3 85.9h-77.1c-22.4 0-40.7 18.3-40.7 40.7v3c0 22.4 18.3 40.7 40.7 40.7h177.3c22.4 0 40.7-18.3 40.7-40.7V175.8c0.1-22.3-18.2-40.6-40.6-40.6z',
                onclick: () => {
                    option.series.data = [];
                    option.toolbox.show = false;
                    chart.setOption(option);
                    option.toolbox.show = true;
                }
            },
            saveAsImage: {
                show: true,
                type:'png',
                name: 'download',
                title:'save',
            },
        },
    },
    tooltip: {
        trigger: 'item',
        triggerOn: 'mousemove',
        formatter: function(params){
            let tip = '';
            if(params){
                if(!params.data.children){
                    tip += params.marker + '字符：' + params.name + '<br/>';
                    tip += params.marker + '权重：' + params.value + '<br/>';
                    // Huffman编码
                    if(currentStep.value == num - 1){
                        for(let i = 0; i < num; i++) {
                            if(params.name == outputTable[i].name){
                                tip += params.marker + '编码：' + outputTable[i].code;
                            }
                        }
                    }
                } else if(params.name == 'Step'){
                    tip += params.marker + '当前步骤：' + params.value;
                }
            }
            return tip;
        }
    },
    series: {
        type: 'tree',
        roam: true, // 只平移不缩放
        layout: "orthogonal", // 垂直树布局
        orient: "vertical", // 从上到下方向
        data: [],
        symbolSize: 40,
        edgeShape: 'polyline', // 折线
        initialTreeDepth: -1, // 初始展开层数，-1表示所有层
        itemStyle:{ color: 'LightGrey' },
        lineStyle: { width: 5 },
        label: {
            position: 'inside',
            fontSize: 20,
            fontWeight: 700,
            formatter: '{c}'
        },
        leaves: {},
        lineStyle: { color: '#19CAAD' },
        // 显示父级节点
        emphasis: { focus: 'ancestor' }
    }
};

onMounted(() => {
    // ECharts 配置
    chart = echarts.init(document.querySelector('#huffmanTree'));
    // 根节点标签
    data.label = JSON.parse(changeSpecialLabel(data));
    // 叶子节点标签
    option.series.leaves.label = JSON.parse(changeSpecialLabel(option.series.leaves));
})

// 修改根节点或叶子节点的标签 
const changeSpecialLabel = obj => {
    const formatterArr = JSON.stringify(obj) == JSON.stringify(data) ? [ '{name|{b}}', '{value|{c}}' ] : [ '{value|{c}}', '{name|{b}}' ];
    const color = JSON.stringify(obj) == JSON.stringify(data) ? '#F4606C' : 'LightSkyBlue';
    specialLabelRich.formatter = formatterArr.join('\n');
    specialLabelRich.rich.name.color = color;
    specialLabelRich.rich.value.color = color;
    return JSON.stringify(specialLabelRich);
}

// 修改outputTable单元格样式，先到先得原则
const cellStyle = ({column, rowIndex}) => {
    if (column.label === 'Huffman编码'){ return { 'text-align': 'center', color: '#F4606C' }}
    if(column){ return { 'text-align': 'center' }}
}

// 处理输入信息
const transformInput = async () => {
    if(inputForm.wordStat == false){
        // 输入为数组格式
        inputForm.strArr = 
            inputForm.string.replace(/[\r\n]/g,"").split(',').map(s => s = [...s.split(' ')]).map(arr => {
                // 过滤因空格带来的元素
                return arr.filter(chr => chr != '');})
    }else{
        // 输入一段文字
        let strArr = [];
        const charJudge = [inputForm.spaceStat == true ? 'T' : 'F', inputForm.othersStat == true ? 'T' : 'F'].join("");
        // 纯字母、数字，只含空格，只忽略空格，全部字符四种情况
        const judgeChar = s =>{
            const regChar = /[A-Z]|[1-9]/;
            switch(charJudge){
                case 'FF': if(regChar.test(s)){ return true; } break;
                case 'TF': if(regChar.test(s) || s == ' '){ return true; } break;
                case 'FT': if(s != ' '){ return true } break;
                case 'TT': return true;
            }
            return false;
        }
        strArr = new Map();
        for(let s of inputForm.string.replace(/[\r\n]/g,"").toUpperCase().split('')){ 
            if(judgeChar(s)){ strArr.set(s, strArr.has(s) ? strArr.get(s) + 1 : 1); }
        }
        inputForm.strArr = Array.from(strArr); 
    }
    num = inputForm.strArr.length; 
}

// 过程信息转换
const transformProcess = nodes => {
    return nodes.map(node => {
        let newNode = { name: node.data, value: node.frequency };
        // 递归
        if (node.left || node.right) {
            newNode.children = [];
            if (node.left) { newNode.children.push(transformProcess([node.left])[0]); }
            if (node.right) { newNode.children.push(transformProcess([node.right])[0]); }
        }
        return newNode;
    });
}

// 每一步过程
const drawProcess = i => {
    data.value = i;
    data.children = transformProcess(process[i]);
    option.series.data = [data];
    chart.setOption(option);
}

// 开始运算
const compute = async ruleForm => {
    if (!ruleForm) return;
    await ruleForm.validate(valid => {
        if (valid) {
            // 输入信息处理
            transformInput();
            // 前后端交互
            axios.post(
                "/huffman",
                {
                    inputArr: inputForm.strArr.map(arr => arr.join("`")).join("~")
                }
            ).then( response => {
                let { guocheng: processArr, bianma: outputTableArr, daiquanguocheng: WPLString } = response.data;
                // 重置
                currentStep.value = -1;
                outputTable.length = 0;
                process.length = 0;
                process = [...processArr];
                // 排序
                process.forEach(p => p.sort((a, b) => a.frequency - b.frequency));
                WPL = ref();
                // 绘图
                for (let i = 0; i < num; i++) {
                    setTimeout(() => {
                        drawProcess(i);
                        currentStep.value += 1;
                        // 输出表格
                        if(i == num - 1){ 
                            outputTableArr = outputTableArr.split(" ").filter(arr => arr != '');
                            outputTableArr.forEach(str => {
                                const [na, power, code] = str.split(":");
                                outputTable.push({name: na, power: power, code: code});
                                WPL.value = "WPL = " + WPLString;
                            });
                        }
                    }, i * TIME_PERSTEP);
                }
            })
        }
    })
}
</script>

<template>
    <main>
        <div class="inputAndOutput">
            <!-- 输入表单 -->
            <el-form ref="ruleForm" :model="inputForm" :rules="rules" class="inputArea">
                <el-form-item prop="string">
                    <el-input  v-model="inputForm.string" type="textarea" class="elTextarea" placeholder="请输入指定格式或一段文字" />
                </el-form-item>
                <el-form-item>
                    <div>
                        <p class="inputTip">格式如下：<br>A 28, B 36,<br>C 24, D 12</p>
                        <el-form-item label="文字"><el-switch inline-prompt active-text="开启" inactive-text="关闭" v-model="inputForm.wordStat" /></el-form-item>
                        <el-form-item label="非字母、数字字符："></el-form-item>
                        <el-form-item label="空格"><el-switch inline-prompt active-text="统计" inactive-text="忽略" v-model="inputForm.spaceStat" /></el-form-item>
                        <el-form-item label="其他"><el-switch inline-prompt active-text="统计" inactive-text="忽略" v-model="inputForm.othersStat" /></el-form-item>
                        <el-button type="success" @click="compute(ruleForm)">开始运算</el-button>
                    </div>
                </el-form-item>
            </el-form>
            <!-- 输出表格 -->
            <el-table :data="outputTable" class="outputTable"
            :header-cell-style="{'text-align':'center'}"
            :cell-style="cellStyle">
                <el-table-column fixed prop="name" label="字符" width="100" />
                <el-table-column prop="power" label="权重" width="100" />
                <el-table-column prop="code" label="Huffman编码" style="width: 100%;" />
                <template #append>
                    <div class="WPL">{{WPL}}</div>
                </template>
            </el-table>
        </div>
        <!-- Huffman树演示 -->
        <div id="huffmanTree"></div>
    </main>
</template>

<style scoped>
main{
    display: flex;
    width: 1240px;
    margin: 0 auto;
}
.inputAndOutput {
    width: 350px;
    margin: 0 10px;
}
.inputArea{
    display: flex;
    border: 1px solid #666;
    border-radius: 8px;
    padding: 5px 10px;
}
.elTextarea{
    width: 200px;
    margin: 10px;
    margin-left: 0;
}
:deep(.elTextarea textarea){
    height: 240px;
    resize: none;
}
.inputTip{
    color: #19CAAD;
}
.outputTable{
    height: 300px;
    border: 1px solid #666;
    border-radius: 8px;
    margin-top: 10px;
}
.WPL{
    width: 340px;
    font-size: 16px;
    margin: 5px;
    color: #F4606C;
    word-wrap: break-word;
}
#huffmanTree {
    width: 850px;
    height: 600px;
    border: 1px solid #666;
    border-radius: 8px;
}
</style>