package comlisanshuxue.lisan.shengchengshu;

import lombok.Data;
import org.apache.commons.lang3.CharUtils;
import org.springframework.stereotype.Component;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RestController;

@RestController
public class prim {

    @PostMapping("/scs")
    public String[] scs(@RequestBody scsDto s) {
        char[] vertex = new char[s.num];
        for (int i = 0; i < s.num; i++) {
            vertex[i] = (char) (65 + i);
        }
        //char[] vertex = {'A', 'B', 'C', 'D', 'E', 'F', 'G'};
        int num = vertex.length;
        int[][] powerTable = s.getPowerTable();
        int[][] edge = powerTable.clone();
//        int[][] edge = {//10000表示两个顶点不通
//                {10000, 5, 7, 10000, 10000, 10000, 2},
//                {5, 10000, 10000, 9, 10000, 10000, 3},
//                {7, 10000, 10000, 10000, 8, 10000, 10000},
//                {10000, 9, 10000, 10000, 10000, 4, 10000},
//                {10000, 10000, 8, 10000, 10000, 5, 4},
//                {10000, 10000, 10000, 4, 5, 10000, 6},
//                {2, 3, 10000, 10000, 4, 6, 10000}};
        int length = edge.length;
        for(int i=0;i<length;i++){
            for (int j=0;j<length;j++){
                if(edge[i][j]==0||edge[i][j]==-1){
                    edge[i][j] = 10000;
                }
            }
        }
        MGraph mGraph = new MGraph(num);
        MinTree minTree = new MinTree();
        minTree.creatGraph(mGraph, num, vertex, edge);
        minTree.showGraph(mGraph);
        String[] prim = minTree.prim(mGraph, 0, num - 1);

        return prim;
    }
}

@Component
@Data
class scsDto{
    int[][] powerTable;
    int num;
}
 
class MGraph {
    int num; //顶点的个数
    char[] vertex; //顶点的元素
    int[][] edge; //存放边
 
    public MGraph(int num) { //构造器
        this.num = num;
        vertex = new char[num];
        edge = new int[num][num];
    }
}
 
class MinTree {
    //创建图
    public void creatGraph(MGraph mGraph, int num, char[] vertex, int[][] edge) {
        for (int i = 0; i < num; i++) {
            mGraph.vertex[i] = vertex[i]; //添加每个顶点的名字
            for (int j = 0; j < num; j++) {
                mGraph.edge[i][j] = edge[i][j]; //添加各个城市之间直接相连的边的权值
            }
        }
    }
 
    //普利姆算法
    public String[] prim(MGraph mGraph, int node,int changdu) {
        String[] jieguo = new String[changdu];
        int[] vis = new int[mGraph.num]; //用于标记遍历过的节点
        vis[node] = 1; //将当前节点标记为1表示已访问
        //用来记录最小路径中两个节点的下标
        int index1 = -1;
        int index2 = -1;
        for (int k = 1; k < mGraph.num; k++) { //若有n个节点 则有n - 1 条边
            int minSide = Integer.MAX_VALUE; //遍历与之比较 找到最小的路径
            for (int i = 0; i < mGraph.num; i++) {
                for (int j = 0; j < mGraph.num; j++) {
                    if (vis[i] == 1 && vis[j] == 0 && mGraph.edge[i][j] < minSide) {
                        minSide = mGraph.edge[i][j]; //最短边上的权值
                        //最短边的两个顶点
                        index1 = i;
                        index2 = j;
                    }
                }
            }
            //从第内层for循环出来后，表示找到一条最短边
            System.out.println("边<" + mGraph.vertex[index1] + "," + mGraph.vertex[index2] + ">权值：" + minSide);
            jieguo[k-1] = String.valueOf(mGraph.vertex[index1])+String.valueOf(mGraph.vertex[index2]);
            vis[index2] = 1; //将最内层与已访问节点路径最短的节点设置为已访问
        }
        for (String s : jieguo) {
            System.out.println("结果:"+s);
        }
        return jieguo;
    }
 
    //输出初始图
    public void showGraph(MGraph mGraph) {
        for (int[] arr : mGraph.edge) {
            for (int element : arr) {
                System.out.print(element + " ");
            }
            System.out.println();
        }
    }
}
 