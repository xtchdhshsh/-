<script setup>
import * as echarts from 'echarts';
import axios from 'axios';
import { onMounted, reactive, ref } from 'vue';
import { ElMessage, ElMessageBox } from 'element-plus';
import dijkstraForm from '@/components/MSTForm.vue';
import {
  ArrowLeft,
  ArrowRight,
  Delete,
  Edit,
  Share,
} from '@element-plus/icons-vue'

// let lujing=['AC', 'CF', 'DF', 'BC', 'BE'];
let lujing
let redLinks = ref([]); // 用于标记哪些线段应该被着色为红色
let num = ref();
let nodes = ref([]);
let links = ref([]);
let response = {};
let LJ = ref([]);
let outputTable = reactive([]);
let chart;
let chart1;
let option = {
    // 悬停详情
    tooltip: {
        formatter: function (ele) {
            const data = ele.data;
            switch(ele.dataType){
                case 'node': return `节点：${data.name}`;
                case 'edge': return `边：${data.source} <-> ${data.target}，权重：${data.value}`;
            }
        }
    },
    series: {
        type: 'graph',
        layout: 'none',
        draggable: true,
        roam: true,
        // 节点label
        label: {
            show: true,
            fontSize: 24,
            fontWeight:700,
            color: '#666'
        },
        // 连线label
        edgeLabel: {
            show: true,
            formatter: '{c}',
            fontSize: 20,
        },
        data: [],
        links: [],
        itemStyle: { color: 'LightSkyBlue' },
        lineStyle: { width: 5, color: 'lightblack' },
        // 节点大小
        symbolSize:50,
        // 邻接强调
        emphasis: {
            // focus: 'adjacency',
            lineStyle: { width: 10, color: '#ff0000' }
        },
    },
};

let option1 = {
    // 悬停详情
    tooltip: {
        formatter: function (ele) {
            const data = ele.data;
            switch(ele.dataType){
                case 'node': return `节点：${data.name}`;
                case 'edge': return `边：${data.source} <-> ${data.target}，权重：${data.value}`;
            }
        }
    },
    series: {
        type: 'graph',
        layout: 'none',
        draggable: true,
        roam: true,
        // 节点label
        label: {
            show: true,
            fontSize: 24,
            fontWeight:700,
            color: '#666'
        },
        // 连线label
        edgeLabel: {
            show: true,
            formatter: '{c}',
            fontSize: 20,
        },
        data: [],
        links: [],
        itemStyle: { color: 'LightSkyBlue' },
        lineStyle: { width: 5, color: 'LightBlack' },
        // 节点大小
        symbolSize:50,
        // 邻接强调
        emphasis: {
            // focus: 'adjacency',
            lineStyle: { width: 10, color: '#ff0000' }
        },
    },
};


  // 打印 lujing 数组


// 更改弹窗
const msgBox = (tip, reg, regMsg) => {
    return new Promise(resolve => {
        ElMessageBox.prompt(tip, 'Tip', {
            confirmButtonText: 'OK',
            cancelButtonText: 'Cancel',
            inputPattern: reg,
            inputErrorMessage: regMsg,
        })
        .then(v => {
            ElMessage({
                type: 'success',
                message: `输入成功`,
            });
            resolve(v.value);
        })
    })
}

onMounted(() => {
    // ECharts 配置
    chart = echarts.init(document.querySelector('#graph'));

    // 更改节点名称或权值
    chart.on('click', async function (ele) {
        switch(ele.dataType){
            case 'node': {
                const v = await msgBox('请输入节点', /^[A-Z]$/, '请只输入一个大写字母');
                nodes.value.forEach(obj => {if(obj.name == ele.data.name) {  obj.name = v; }})
                option.series.data
                .forEach(obj => {if(obj.name == ele.data.name) {  obj.name = v; }})
                option.series.links
                .forEach(obj => {
                    if(obj.source == ele.data.name) { obj.source = v; } 
                    if(obj.target == ele.data.name) { obj.target = v; } 
                })
                break;
            }
            case 'edge': {
                const v = await msgBox('请输入权重', /^[0-9]*[1-9][0-9]*$/, '请输入正整数');
                option.series.links
                .forEach(obj => {
                    if(obj.source == ele.data.source && obj.target == ele.data.target
                        || obj.source == ele.data.target && obj.target == ele.data.source
                    ) { obj.value = v; } 
                })
                break;
            }
        }
        chart.setOption(option);
    });
// 
     // ECharts 配置
     chart1 = echarts.init(document.querySelector('#graph1'));

// 更改节点名称或权值
chart1.on('click', async function (ele) {
    switch(ele.dataType){
        case 'node': {
            const v = await msgBox('请输入节点', /^[A-Z]$/, '请只输入一个大写字母');
            nodes.value.forEach(obj => {if(obj.name == ele.data.name) {  obj.name = v; }})
            option.series.data
            .forEach(obj => {if(obj.name == ele.data.name) {  obj.name = v; }})
            option.series.links
            .forEach(obj => {
                if(obj.source == ele.data.name) { obj.source = v; } 
                if(obj.target == ele.data.name) { obj.target = v; } 
            })
            break;
        }
        case 'edge': {
            const v = await msgBox('请输入权重', /^[0-9]*[1-9][0-9]*$/, '请输入正整数');
            option.series.links
            .forEach(obj => {
                if(obj.source == ele.data.source && obj.target == ele.data.target
                    || obj.source == ele.data.target && obj.target == ele.data.source
                ) { obj.value = v; } 
            })
            break;
        }
    }
    chart1.setOption(option);
});

})
//获取子组件传来数据
const handleLujingUpdated = (LJArray) => {
  LJ.value = LJArray;
  lujing = LJ.value
   console.log(lujing); // 打印 lujing 数组
};

// 获取并修改子组件传来的表单数据
const getInput = f =>{
    chart.clear();
    num = f.count;
    nodes = ref([]);
    links = ref([]);
    option.series.data = [];
    option.series.links = [];


    option1.series.data = [];
    option1.series.links=[
       {source: 'A', target: 'C', value: '1'},
       {source: 'C', target: 'A', value: '1'},
       {source: 'B', target: 'C', value: '5'},
       {source: 'C', target: 'B', value: '5'},
       {source: 'B', target: 'E', value: '3'},
       {source: 'E', target: 'B', value: '3'},
       {source: 'C', target: 'F', value: '4'},
       {source: 'F', target: 'C', value: '4'},
       {source: 'D', target: 'F', value: '2'},
       {source: 'F', target: 'D', value: '2'}
    ]
    // 生成节点坐标
    !function(){
        const nRow = num < 8 ? Math.ceil(num / 2) : 4;
        const nCol = num / nRow;
        let yCoord = 100;
        for (let i = 0; i < nCol; i++) {
            let xCoord = i % 2 == 0 ? 0 : 50;
            for (let j = 0; j < nRow; j++) {
                const index = i * nRow + j;
                xCoord += 100;
                nodes.value.push({"name": f.node[index], 'x': xCoord, 'y': yCoord});
                if(index + 1 >= num) { break; }
            }
            yCoord += 100;
        }
    }();
    f.link.forEach(link => {
        links.value.push({source: link[0], target: link[1], value: link[2]});
        links.value.push({source: link[1], target: link[0], value: link[2]});
    })
    // 配置data
    nodes.value.forEach(node => option.series.data.push({
        name: node.name,
        x: node.x,
        y: node.y
    }))
    nodes.value.forEach(node => option1.series.data.push({
        name: node.name,
        x: node.x,
        y: node.y
    }))
    // 配置links
    links.value.forEach(link => option.series.links.push({
        source: link.source,
        target: link.target,
        value: link.value,
        lineStyle: { color:'lightblack' }
    }))

    
    chart.setOption(option);
    // chart1.setOption(option1);
}

// 获取并利用子组件传来的后端数据
const getResp = resp => {
    response = resp;
    const { nodeSelected, path, valueTable } = response;
    const valueResult = valueTable[valueTable.length - 1];
    let count = 0;
    // 清空数组
    outputTable.length = 0;
    for (let i = 0; i < num; i++) {
        console.log( String.fromCharCode(65 + i));
        if(String.fromCharCode(65 + i) == nodeSelected.value) { continue; }
        outputTable.push({name: nodes.value[i].name, value: valueResult[i], path: path[count].join("->")});
        count += 1;
    }
};


let i=0;

const pre = () => {

    let temp = document.querySelector('#describe')
   temp.innerHTML=`最小权值的边为${lujing[i]}`
    // 标记需要保持绿色的线段
    option.series.links.forEach(link => {
        // 如果线段是 'C-F' 或 'F-C'，则标记为绿色
        if ((link.source === LJ.value[i][0] && link.target === LJ.value[i][1]) || 
           (link.source === LJ.value[i][1] && link.target === LJ.value[i][0])) {
            link.tempColor = 'Black'; // 临时属性，用于标记颜色
        }
    });

    // 重置所有线段的颜色为黑色
    option.series.links.forEach(link => {
        link.lineStyle = { color: 'Black' };
    });

    // 恢复标记为绿色的线段的颜色
    option.series.links.forEach(link => {
        if (link.tempColor) { // 检查是否有临时颜色标记
            link.lineStyle.color = link.tempColor; // 恢复绿色

        }
    });
    if(i>0&&i<=5){
        i--;
    }

    // 更新图表
    chart.setOption(option);
};


const next = () => {

    let temp = document.querySelector('#describe')
   temp.innerHTML=`最小权值的边为${lujing[i]}`
    // 标记需要保持绿色的线段
    option.series.links.forEach(link => {
        // 如果线段是 'C-F' 或 'F-C'，则标记为绿色
        if ((link.source === LJ.value[i][0] && link.target === LJ.value[i][1]) || 
           (link.source === LJ.value[i][1] && link.target === LJ.value[i][0])) {
            link.tempColor = '#19CAAD'; // 临时属性，用于标记颜色
        }
    });

    // 重置所有线段的颜色为黑色
    option.series.links.forEach(link => {
        link.lineStyle = { color: 'Black' };
    });

    // 恢复标记为绿色的线段的颜色
    option.series.links.forEach(link => {
        if (link.tempColor) { // 检查是否有临时颜色标记
            link.lineStyle.color = link.tempColor; // 恢复绿色

        }
    });
    if(i>=0&&i<5){
        i++;
    }
    if(i==5){
        i--
    }

    // 更新图表
    chart.setOption(option);
};

</script>
<!-- A-B:6,A-C:1,A-D:5,B-C:3,B-E:5,C-D:5,C-E:6,C-F:4,D-F:2 -->
<template>
    <main>
        <div id="inputFormAndOutput">
            <!-- 输入表单 -->
            <dijkstraForm @getInputForm="getInput" @getResponse="getResp"  @lujingUpdated="handleLujingUpdated" />
            <!-- 输出表格 -->
            
            <el-card style="max-width: 450px;border: 1px solid #666;width: 400px;margin-top: 20px;height: 200px;">
                <template #header>切换过程/注释
                
                </template>
                <div id="Switch"> 

                    <el-button-group>
                     <el-button type="primary" :icon="ArrowLeft" @click="pre"></el-button>
                     <el-button type="primary" @click="next">
                     <el-icon class="el-icon--right"><ArrowRight /></el-icon>
                    </el-button>

                     </el-button-group>

                </div>
                    <div id="describe" style="font-size: 30px;">
                        此时最小权值的边为{{ i }}
                    </div>
                <div id="graph1"></div>
            </el-card>
             
        </div>
   
        <div id="graph">
            
        </div>
    </main>
</template>

<style scoped>
main{
    display: flex;
}

#inputFormAndOutput{
    padding: 0 20px;
}

.outputTable{
    width: 450px;
    height: 300px;
    border: 1px solid #666;
    border-radius: 8px;
    margin-top: 5px;
}

#graph {
    width: 800px;
    height: 550px;
    border: 1px solid #666;
    border-radius: 8px;
}


#graph1{
    width: 360px;
    height: 0px;
    border: 1px solid #666;
    border-radius: 8px;
}
#Switch{
    height:40px;
}
#left{

    float:left
}
#right{

    float:left
}
</style>

<style>
  .el-message-box {
    width: 250px;
  }
</style>