package comlisanshuxue.lisan.djstl;

import org.apache.commons.lang3.StringUtils;

import java.util.*;

public class DijkstraAlgorithm {

    private static final int INF = Integer.MAX_VALUE;

    public static String[] dijkstra(int[][] graph, int source) {
        int vertices = graph.length;
        int[] dist = new int[vertices]; // 存储从源点到每个顶点的最短距离
        boolean[] visited = new boolean[vertices]; // 记录顶点是否已被访问
        int[] previous = new int[vertices]; // 记录每个顶点的前驱顶点
        String[] paths = new String[vertices]; // 存储每个顶点的路径结果

        // 初始化距离数组、访问数组、前驱数组和路径数组
        Arrays.fill(dist, INF);
        Arrays.fill(previous, -1);
        dist[source] = 0;
        Arrays.fill(paths, "");

        for (int i = 0; i < vertices - 1; i++) {
            int u = minDistance(dist, visited);
            visited[u] = true;

            // 更新与u相邻顶点的距离
            for (int v = 0; v < vertices; v++) {
                if (!visited[v] && graph[u][v] != -1 && dist[u] != INF && dist[u] + graph[u][v] < dist[v]) {
                    dist[v] = dist[u] + graph[u][v];
                    previous[v] = u;
                }
            }
        }

        // 生成路径结果
        for (int i = 0; i < vertices; i++) {
            paths[i] = getPath(previous, i) + " (" + dist[i] + ")";
        }

        // 打印最短路径
        printSolution(paths);
        return paths;
    }

    // 找到未访问顶点中距离最小的顶点
    private static int minDistance(int[] dist, boolean[] visited) {
        int min = INF;
        int minIndex = -1;

        for (int v = 0; v < dist.length; v++) {
            if (!visited[v] && dist[v] <= min) {
                min = dist[v];
                minIndex = v;
            }
        }

        return minIndex;
    }

    // 获取从源点到目标顶点的路径
    private static String getPath(int[] previous, int target) {
        if (previous[target] == -1) {
            return (char) ('A' + target) + "";
        }
        return getPath(previous, previous[target]) + " -> " + (char) ('A' + target);
    }

    // 打印最短路径结果
    private static void printSolution(String[] paths) {
        System.out.println("顶点\t路径");
        for (int i = 0; i < paths.length; i++) {
            System.out.println((char) ('A' + i) + "\t" + paths[i]);
        }
    }

    public String[] FindLuJing(int[][] graph,int geshu) {

        String[] jieguo = new String[geshu-1];
        String[] jieshou = new String[geshu];
        String dange = "";
//        int[][] graph = {
//                {0, 10, -1, 4, -1, -1},
//                {10, 0, 8, 2, 6, -1},
//                {-1, 8, 0, -1, 1, 5},
//                {4, 2, -1, 0, 6, -1},
//                {-1, 6, 1, 6, 0, 12},
//                {-1, -1, 5, -1, 12, 0}
//        };

        jieshou  = dijkstra(graph, 0);
        System.out.println("输出数组");
       for (int i = 1;i<geshu;i++){
           dange = "";
           for(int j=0;j<jieshou[i].length();j++){
               if(jieshou[i].charAt(j)>='A' && jieshou[i].charAt(j)<='Z'){
                   dange+=jieshou[i].charAt(j);
               }
           }
           dange+=":";
           dange += StringUtils.substringBetween(jieshou[i],"(",")");
           jieguo[i-1] = dange;
       }
        for (String s : jieguo) {
            System.out.println("结果的"+s);
        }
        return jieguo;
    }
}