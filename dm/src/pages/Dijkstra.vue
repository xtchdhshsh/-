<script setup>
import * as echarts from 'echarts';
import { onMounted, reactive, ref, watch } from 'vue';
import dijkstraForm from '@/components/DijkstraForm.vue';

const NUM_LINE_GRAPH = 5; // 带权图每一行最大节点数
const INFINITY = 1000; // 表示路径权值无穷大的值（前后端交互）
const TIME_PERSTEP = 3000; // 每一步的时间

let num = 0;
let nodes = ref([]);
let links = ref([]);
let response = {}; // 后端信息
let currentStep = ref(-1); // 当前步数
let pathStack = new Array(); // 最短路径栈
let backStack = new Array(); // 后退栈
let process = reactive([]); // 过程记录
let pathCurrent = new Array(); // 当前最短路径
let outputTable = reactive([]); // 输出表格
const carousel = ref(); // 过程信息走马灯
let dialogFormVisible = ref(); // 改变坐标对话框
let chart;
let option = {
    // 工具栏
    toolbox:{
        show: true,
        itemSize: 20, // icon大小
        itemGap: 20, // icon间隔
        top: '1%',
        right:'1%',
        feature: {
            // 更改坐标
            myCoord: {
                title: 'coord',
                icon: 'path://M7 28v65h65v-5H12V28H7z',
                onclick: async () => {
                    dialogFormVisible.value = true;
                }
            },
            // 上一步
            myBack: {
                title: 'back',
                icon: 'path://M669.6 849.6c8.8 8 22.4 7.2 30.4-1.6s7.2-22.4-1.6-30.4l-309.6-280c-8-7.2-8-17.6 0-24.8l309.6-270.4c8.8-8 9.6-21.6 2.4-30.4-8-8.8-21.6-9.6-30.4-2.4L360.8 480.8c-27.2 24-28 64-0.8 88.8l309.6 280z',
                onclick: () => {
                    if(currentStep.value > 0) { 
                        backStack.push(pathStack.pop());
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
                        pathStack.push(backStack.pop());
                        drawProcess(++currentStep.value); 
                    }
                }
            },
            restore: {
                show: true
            },
            saveAsImage: {
                show: true,
                type:'png',
                name: 'download',
                title:'save',
            },
        },
    },
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
            fontWeight: 700,
            color: 'black'
        },
        // 连线label
        edgeLabel: {
            show: true,
            formatter: '{c}',
            fontSize: 20,
        },
        data: [],
        links: [],
        itemStyle: { color: 'LightGrey' },
        lineStyle: { width: 5, color: 'LightBlack' },
        symbolSize: 50, // 节点大小
        // 邻接强调
        emphasis: {
            focus: 'adjacency',
            lineStyle: { width: 10, color: 'LightBlack' }
        },
    },
};

onMounted(() => {
    // ECharts 配置
    chart = echarts.init(document.querySelector('#graph'));
})

// 获取并修改子组件传来的表单数据
const getInput = f =>{
    chart.clear();
    num = f.count;
    nodes = ref([]);
    links = ref([]);
    // 清空
    option.series.data = [];
    option.series.links = [];
    // 生成节点坐标
    !function(){
        const nRow = num < 2 * NUM_LINE_GRAPH ? Math.ceil(num / 2) : NUM_LINE_GRAPH;
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
    // 配置links
    links.value.forEach(link => option.series.links.push({
        source: link.source,
        target: link.target,
        value: link.value,
        lineStyle: { color:'lightblack' }
    }))
    chart.setOption(option);
}

// 画最短路径
const drawShortestPath = () => {
    option.series.links.forEach(link => {
        // 和已有重复不生效！！！（大概）
        link.lineStyle.color = 'Black';
        pathStack.forEach(shortestPath => {
            if(shortestPath[0] == link.source && shortestPath[1] == link.target
                || shortestPath[0] == link.target && shortestPath[1] == link.source){
                link.lineStyle.color = '#19CAAD';
            }
        });
    });
}

// 每一步
const drawProcess = i => {
    const { path, valueTable, nodeStep } = response;
    option.series.data.forEach((d, index) => {
        const value = valueTable[i][index];
        d.itemStyle = {};
        // 已勘测节点
        if(value < INFINITY){
            d.itemStyle.color = 'LightSkyBlue';
            // 当前节点
            if(nodeStep[i] == d.name){
                d.itemStyle.color = '#F4606C';
                // 最短路径
                if(i != 0){
                    const sp = path.filter(p => p[0][p[0].length - 1] == d.name);
                    const shortestPath = sp[0][0];
                    const len = shortestPath.length - 1;
                    for (let j = 0; j < len; j++) {
                        const link = [shortestPath[j], shortestPath[j + 1]];
                        if(!pathStack){
                            pathStack.push(link);
                        } else {
                            const setStack = new Set();
                            pathStack.forEach(p => setStack.add(p.join("")));
                            if(!setStack.has(link.join(""))) { pathStack.push(link); }
                        }
                    }
                }
                drawShortestPath();
            }
        }
    });
    chart.setOption(option);
}

// 记录过程
const recordProcess = i => {
    const { path, valueTable, nodeStep } = response;
    let processObj = { currentNode: {}, accessibleNode: [] };
    option.series.data.forEach((d, index) => {
        const value = valueTable[i][index];
        if(value < INFINITY){
            if(nodeStep[i] == d.name){
                // 记录当前节点
                let pathFin = i == 0 ? 'A' : path[i - 1][0].join("->");
                processObj.currentNode = { name: d.name, path: pathFin, pathValue: value };
            } else {
                // 记录新检测节点
                let nodeFin = new Set();
                for (let j = 0; j <= i; j++) { nodeFin.add(nodeStep[j]); }
                links.value.forEach(link => {
                    if(link.source == nodeStep[i] && link.target == d.name && !nodeFin.has(d.name)){
                        let state = i == 0 || value != valueTable[i - 1][index] ? 'change' : 'remain';
                        let node = { name: d.name, path: '', pathValue: value, state: state };
                        processObj.accessibleNode.push(node);
                    }
                });
            }
        }
    })
    // 可经过节点的路径
    processObj.accessibleNode.forEach(obj => {
        let pathFin = '';
        const index = obj.name.charCodeAt() - 65;
        switch(obj.state){
            case 'change': { pathFin = processObj.currentNode.path + `->${obj.name}`; pathCurrent[index] = pathFin; break; }
            case 'remain': { pathFin = pathCurrent[index]; break; }
        }
        obj.path = pathFin;
    });
    if(process.length < num){ process.push(processObj); }
}

// 获取并利用子组件传来的后端数据
const getResp = resp => {
    response = resp;
    const { path, nodeStep } = response;
    // 重置
    outputTable.length = 0;
    process.length = 0;
    currentStep.value = -1;
    pathStack = [];
    backStack = [];
    pathCurrent = new Array(num);
    // 记录过程信息
    for(let i = 0; i < num; i++) { recordProcess(i); }
    // 绘图
    for(let i = 0; i < num; i++) {
        setTimeout(() => {
            drawProcess(i);
            currentStep.value += 1;
            // 输出表格
            if(i != 0){ outputTable.push({name: nodeStep[i], value: path[i - 1][1], path: path[i - 1][0].join("->")}); }
        }, i * TIME_PERSTEP);
    }
};

// 更改坐标重新渲染
const changeCoord = () => {
    const reg = /^[0-9]*[1-9][0-9]*$/;
    let regResult = 1;
    // 配置data
    nodes.value.forEach(node => {
        if(!reg.test(node.x) || !reg.test(node.y)){
            regResult = 0;
            alert(`${node.name}点请填写正确信息！`);
        } else {
            option.series.data.forEach(d => {
                if(node.name == d.name){
                    d.x = node.x;
                    d.y = node.y;
                }
            })
        }
    })
    if(regResult){
        chart.setOption(option);
        dialogFormVisible.value = false;
    }
}

// 自动轮播过程信息
watch(currentStep, newValue => { carousel.value.setActiveItem(newValue); });
</script>

<template>
    <main>
        <div id="inputFormAndOutput">
            <!-- 输入表单 -->
            <dijkstraForm @getInputForm="getInput" @getResponse="getResp" />
            <!-- 输出表格 -->
            <el-table :data="outputTable" class="outputTable" 
            :header-cell-style="{'text-align':'center'}"
            :cell-style="{'text-align':'center'}">
                <el-table-column fixed prop="name" label="目标点" width="100" />
                <el-table-column prop="value" label="最短路径值" width="120" />
                <el-table-column prop="path" label="最短路径" style="width: 100%;" />
            </el-table>
        </div>
        <div class="process">
            <!-- dijkstra演示 -->
            <div id="graph"></div>
            <!-- 过程信息 -->
            <div id="processText">
                <el-carousel ref="carousel" indicator-position="none" :autoplay="false">
                    <el-carousel-item v-for="(p, i) in process">
                        <div class="container">
                            <!-- 查找节点 -->
                            <p>第{{i + 1}}步：
                                <span style="color: LightBlue;">从{{p.currentNode.name}}点出发，</span>
                                <span v-if="p.accessibleNode.length">可到达{{p.accessibleNode.map(n => n.name).join("、")}}点</span>
                                <span v-else>没有可到达点</span>
                            </p>
                            <!-- 查找节点的信息 -->
                            <div v-for="n in p.accessibleNode">
                                <p><span style="color: #19CAAD">{{n.name}}点：</span>
                                    <span v-if="n.state == 'change'">找到更短的路径，更新信息，</span>
                                    <span v-else>没找到更短的路径，保留原来的信息，</span>
                                    当前最短路径为{{n.path}}，长度为{{n.pathValue}}
                                </p>
                            </div>
                            <!-- 最短路径信息 -->
                            <p v-if="i > 0">
                                <span style="color: #F4606C;">已到达{{p.currentNode.name}}点，</span>
                                确定{{response.nodeSelected}}点到{{p.currentNode.name}}点最短路径为{{p.currentNode.path}}，长度为{{p.currentNode.pathValue}}
                            </p>
                        </div>
                    </el-carousel-item>
                </el-carousel>
            </div>
        </div>
    </main>
    <!-- 更改坐标对话框 -->
    <el-dialog v-model="dialogFormVisible" title="更改节点坐标" width="300" draggable>
        <p style="color: #F4606C;">注：x取值范围100~600，y取值100~500</p>
        <el-form v-for="n in nodes" :key="n.name">
            <el-form-item class="elDialogItem">
                <span class="elDialogNode">{{n.name}} :</span><span>x : <input v-model=n.x></span><span>y : <input v-model=n.y></span>
            </el-form-item>                
        </el-form>
        <template #footer>
            <div class="dialog-footer">
                <el-button @click="dialogFormVisible = false">Cancel</el-button>
                <el-button type="primary" @click="changeCoord">Confirm</el-button>
            </div>
        </template>
    </el-dialog>
</template>

<style scoped>
main{
    display: flex;
    width: 1240px;
    margin: 0 auto;
}
#inputFormAndOutput{
    padding: 0 15px;
}
/* 输出表格 */
.outputTable{
    width: 400px;
    height: 300px;
    border: 1px solid #666;
    border-radius: 8px;
    margin-top: 5px;
}
/* dijkstra演示 */
#graph {
    width: 800px;
    height: 400px;
    border: 1px solid #666;
    border-radius: 8px;
}
/* 过程信息走马灯 */
#processText{
    height: 200px;
    border: 1px solid #666;
    border-radius: 8px;
    margin-top: 5px;
}
.el-carousel{
    width: 800px;
    height: 200px;
}
.el-carousel__item{
    height: 200px;
    padding: 10px 75px;
}
.el-carousel__item .container{
    width: 650px;
    height: 180px;
    overflow: auto;
    font-size: 20px;
}
/* 更改坐标对话框 */
.elDialogItem{
    margin-top: 5px;
}
.elDialogItem span{
    width: 80px;
    margin: 0 10px;
}
.elDialogItem .elDialogNode{
    width: 30px;
    margin: 0;
}
.elDialogItem span input{
    width: 60px;
    border: 1px solid #666;
}
</style>