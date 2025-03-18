package comlisanshuxue.lisan.djstl;

import comlisanshuxue.lisan.dto.Djs;
import comlisanshuxue.lisan.vo.Djsvo;
import io.swagger.annotations.Api;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RestController;

import java.util.Arrays;

@RestController
@Api(tags = "dijkstra")
public  class DijkstraShort {
	private static final int MAX_DISTANCE = 1000; // 无限距离，用于初始化距离
	
//	public static void main(String[] args) {
//		int [][] jieshou = new int[7][7];
//		int max = MAX_DISTANCE;
////		int[][] graph = {
////				{0, 10, max, 4, max, max},
////				{10, 0, 8, 2, 6, max},
////				{max, 8, 10, 15, 1, 5},
////				{4, 2,  15, 0, 6, max},
////				{max, 6, 1, 6, 0, 12},
////				{max, max, 5, max, 12, 0}};
//		int[][] graph = {
//				{0, 10, max, 4, max, max,123},
//				{10, 0, 8, 2, 6, max,12},
//				{max, 8, 0, 15, 1, 5,33},
//				{4, 2,  15, 0, 6, max,max},
//				{max, 6, 1, 6, 0, 12,9},
//				{max, max, 5, max, 12, 0,8},
//				{21, max, 5, 5, 12,7,0}};
//
//		System.out.println("输入起始节点：");
//		@SuppressWarnings("resource")
//		Scanner in = new Scanner(System.in);
//
//		int startNode = Integer.parseInt(in.nextLine());
//		//int[] dist = dijkstra(graph, startNode,7);
//		jieshou = dijkstra(graph,startNode,7);
//		for (int[] ints : jieshou) {
//			for (int anInt : ints) {
//				System.out.print(anInt+"\t");
//			}
//			System.out.println();
//		}
//		//System.out.println(Arrays.toString(dist));
//	}
//
//	/**
//	 * Dijkstra算法，其调用了findMinDistNode方法
//	 * @param graph 进行查找的图，这里用二维矩阵实现
//	 * @param startNode 起始节点
//	 * @return 返回起始节点到其他节点的最短路径距离列表
//	 */
//	public static int[][] dijkstra(int[][] graph, int startNode,int x) {
//		int [][] jieguo = new int[x][x];
//		int cishu = 0;
//		short ch = 'a';
//		//char[] vertexList = {'A', 'B', 'C', 'D', 'E', 'F','G'};
//		char[] vertexList =new char[x];
//		for(int i=0;i<x;i++){
//			vertexList[i] = (char)(ch+i);
//		}
//
//
//		String[] findProcess = new String[x]; // 用于打印最短路径的字符串数组
//		findProcess[0] = String.valueOf(vertexList[startNode]); // 直接将开始节点赋给findProcess[0]
//		int[] processDist = new int[x]; // 用于打印最短路径距离的整型数组（和findProcess搭配打印）
//		processDist[0] = 0;
//
//		int vertexNumbers = graph.length;
//		System.out.println("长度为"+vertexNumbers);
//		int[] dist = new int[vertexNumbers]; // 当下startNode到每个顶点的最短距离
//		Arrays.fill(dist, MAX_DISTANCE); // 将初始距离都设为最大值
//		dist[startNode] = 0; // startNode和自己的距离为0
//		boolean[] visited = new boolean[vertexNumbers]; // 该列表标记顶点是否被放入最终最短路径中
//
//		// 第一个for循环用于找到minNode，将其加入最终的最短路径
//		for (int i = 0; i < vertexNumbers; i++) {
//			int minNode = findMinDistNode(dist, visited); // 未被访问且startNode能达到的最短距离的顶点
//			visited[minNode] = true; // 将此顶点加入已访问列表，保证之后循环findMinDistNode方法时不再访问该顶点，因为已经确定将该点加入最终最短路径了
//
//			if (i == 0) ; // 开始节点到开始节点不进行任何操作
//			else {
//			    findProcess[i] = findProcess[i-1] + " -> " + vertexList[minNode]; // 开始节点到每次确定下的minNode之间的路径
//			}
//			processDist[i] = dist[minNode]; // 开始节点到每次确定下的minNode之间的距离
//
//			// 第二个for循环以minNode顶点为新的开始点进行后续节点的查找，保证从startNode到后续节点的距离为当下最小距离
//			for (int j = 0; j < vertexNumbers; j++) {
//				/*
//				 *  minNode和后续节点j满足几个条件：
//				 *  1、后续节点j未被访问过
//				 *  2、在图中minNode和后续节点距离不为0，即不是同一个节点
//				 *  3、minNode和startNode距离不为最大值
//				 *  4、startNode到minNode的最短距离(dist[minNode])加minNode和后续节点的距离小于startNode直接到后续节点的最短距离(dist[j])
//				 */
//				if (!visited[j] && graph[minNode][j] != 0 && dist[minNode] != MAX_DISTANCE
//						&& dist[minNode] + graph[minNode][j] < dist[j]) {
//					dist[j] = dist[minNode] + graph[minNode][j]; // 更新dist列表，保证dist[j]是目前为止的最小值
//
//				}
//
//
//			}
//			System.out.println("赋值一下");
//			for(int ia=0;ia<vertexNumbers;ia++){
//				jieguo[cishu][ia] = dist[ia];
//			}
//			cishu++;
//
//		}
//		System.out.println("起始节点到各节点的最短路径及距离如下：");
//		for (int i = 1; i < 7; i++) {
//			System.out.println(findProcess[i] + " 的最短距离为 " + processDist[i]);
//		}
//		return jieguo;
//	}
//
//	// 从dist[]中其余未访问的节点中找到startNode能到达的距离最小的顶点
//	public static int findMinDistNode(int[] dist, boolean[] visited) {
//		int minDist = MAX_DISTANCE;
//		int minNode = -1;
//		for (int i = 0; i < dist.length; i++) {
//			// 若距离为MAX_DISTANCE，则说明startNode无法到达该顶点
//			if (!visited[i] && dist[i] < minDist) {
//				minDist = dist[i]; // 更新minDist，保证最后得出的是可达到的距离最短的顶点
//				minNode = i; // 更新minNode
//			}
//		}
//		return minNode;
//	}
//就这里 生成路径在哪里

	@PostMapping("/dijkstra")
	public Djsvo dijies(@RequestBody Djs d) {
		Djsvo djsvo = new Djsvo();
		int[][] g = d.getGraph();

		int shuliang = d.getShuliang();
		int [][] graph = new int[shuliang][shuliang];
		int [][] jieshou = new int[shuliang][shuliang];
			int max = MAX_DISTANCE;
			for(int i = 0;i<shuliang;i++){
				for(int j=0;j<shuliang;j++){
					//todo 判断是否为无穷 用if
					if(g[i][j]==-1){
						graph[i][j] = max;
						continue;
					}
					graph[i][j] = g[i][j];
					System.out.print(graph[i][j]);
				}
				System.out.println("llllll");
			}
//			int[][] graph = {
//					{0, 4, max, 4, max, max,123},
//					{10, 0, 8, 2, 6, max,12},
//					{max, 8, 0, 15, 1, 5,33},
//					{4, 2,  15, 0, 6, max,max},
//					{max, 6, 1, 6, 0, 12,9},
//					{max, max, 5, max, 12, 0,8},
//					{21, max, 5, 5, 12,7,0}};

			System.out.println("输入起始节点：");
			//@SuppressWarnings("resource")
//			Scanner in = new Scanner(System.in);
//
//			int startNode = Integer.parseInt(in.nextLine());
			int startNode = d.getQidian();
			//int[] dist = dijkstra(graph, startNode,7);
			djsvo = dijkstra(graph,startNode,shuliang);
			for (int[] ints : djsvo.getJieguo()) {
				for (int anInt : ints) {
					System.out.print(anInt+"\t");
				}
				System.out.println();
			}
			//System.out.println(Arrays.toString(dist));
		DijkstraAlgorithm dijkstraAlgorithm = new DijkstraAlgorithm();
		String[] jieg = dijkstraAlgorithm.FindLuJing(graph, shuliang);
		djsvo.setLujing(jieg);
		return djsvo;
		}


		/**
		 * Dijkstra算法，其调用了findMinDistNode方法
		 * @param graph 进行查找的图，这里用二维矩阵实现
		 * @param startNode 起始节点
		 * @return 返回起始节点到其他节点的最短路径距离列表
		 */
		public static Djsvo dijkstra(int[][] graph, int startNode, int x) {
			Djsvo djsvo = new Djsvo();
			int [][] jieguo = new int[x][x];
			String[] lujing = new String[x-1];
			int cishu = 0;
			short ch = 'a';
			//char[] vertexList = {'A', 'B', 'C', 'D', 'E', 'F','G'};
			char[] vertexList =new char[x];
			for(int i=0;i<x;i++){
				vertexList[i] = (char)(ch+i);
			}

			String[] findProcess = new String[x]; // 用于打印最短路径的字符串数组
			findProcess[0] = String.valueOf(vertexList[startNode]); // 直接将开始节点赋给findProcess[0]
			int[] processDist = new int[x]; // 用于打印最短路径距离的整型数组（和findProcess搭配打印）
			processDist[0] = 0;

			int vertexNumbers = graph.length;
			System.out.println("长度为"+vertexNumbers);
			int[] dist = new int[vertexNumbers]; // 当下startNode到每个顶点的最短距离
			Arrays.fill(dist, MAX_DISTANCE); // 将初始距离都设为最大值
			dist[startNode] = 0; // startNode和自己的距离为0
			boolean[] visited = new boolean[vertexNumbers]; // 该列表标记顶点是否被放入最终最短路径中

			// 第一个for循环用于找到minNode，将其加入最终的最短路径
			for (int i = 0; i < vertexNumbers; i++) {
				int minNode = findMinDistNode(dist, visited); // 未被访问且startNode能达到的最短距离的顶点
				visited[minNode] = true; // 将此顶点加入已访问列表，保证之后循环findMinDistNode方法时不再访问该顶点，因为已经确定将该点加入最终最短路径了

				if (i == 0) ; // 开始节点到开始节点不进行任何操作
				else {
					findProcess[i] = findProcess[i-1] + " -> " + vertexList[minNode]; // 开始节点到每次确定下的minNode之间的路径
				}
				processDist[i] = dist[minNode]; // 开始节点到每次确定下的minNode之间的距离

				// 第二个for循环以minNode顶点为新的开始点进行后续节点的查找，保证从startNode到后续节点的距离为当下最小距离
				for (int j = 0; j < vertexNumbers; j++) {
					/*
					 *  minNode和后续节点j满足几个条件：
					 *  1、后续节点j未被访问过
					 *  2、在图中minNode和后续节点距离不为0，即不是同一个节点
					 *  3、minNode和startNode距离不为最大值
					 *  4、startNode到minNode的最短距离(dist[minNode])加minNode和后续节点的距离小于startNode直接到后续节点的最短距离(dist[j])
					 */
					if (!visited[j] && graph[minNode][j] != 0 && dist[minNode] != MAX_DISTANCE
							&& dist[minNode] + graph[minNode][j] < dist[j]) {
						dist[j] = dist[minNode] + graph[minNode][j]; // 更新dist列表，保证dist[j]是目前为止的最小值

					}


				}
				System.out.println("赋值一下");
				for(int ia=0;ia<vertexNumbers;ia++){
					jieguo[cishu][ia] = dist[ia];
				}
				cishu++;

			}
			System.out.println("起始节点到各节点的最短路径及距离如下：");
			String dange = "";
			for (int i = 1; i < x; i++) {
				dange = "";
				char zuihouyiwei = findProcess[i].charAt(findProcess[i].length()-1);
				if(zuihouyiwei>='a'||zuihouyiwei<='z'){
					for(int j = 0;j<findProcess[i].length();j++){
						if(findProcess[i].charAt(j)>='a'&&findProcess[i].charAt(j)<='z'){
							dange+=findProcess[i].charAt(j);
							dange = dange.toUpperCase();
						}
					}
				}
				dange+=":";
				dange+=processDist[i];
				lujing[zuihouyiwei-'a'-1] = dange;
				System.out.println(findProcess[i] + " 的最短距离为 " + processDist[i]);
			}
			System.out.println("单个路径:"+lujing[4]);
			djsvo.setJieguo(jieguo);
			djsvo.setLujing(lujing);
			return djsvo;
		}

		// 从dist[]中其余未访问的节点中找到startNode能到达的距离最小的顶点
		public static int findMinDistNode(int[] dist, boolean[] visited) {
			int minDist = MAX_DISTANCE;
			int minNode = -1;
			for (int i = 0; i < dist.length; i++) {
				// 若距离为MAX_DISTANCE，则说明startNode无法到达该顶点
				if (!visited[i] && dist[i] < minDist) {
					minDist = dist[i]; // 更新minDist，保证最后得出的是可达到的距离最短的顶点
					minNode = i; // 更新minNode
				}
			}
			return minNode;
		}


//	public static void huoqulujing(int[][] s){
//		int max = MAX_DISTANCE;
//		int [][] graph = new int[7][7];
////		int[][] graph = {
////				{0, 10, max, 4, max, max},
////				{10, 0, 8, 2, 6, max},
////				{max, 8, 10, 15, 1, 5},
////				{4, 2,  15, 0, 6, max},
////				{max, 6, 1, 6, 0, 12},
////				{max, max, 5, max, 12, 0}};
//
//
//
//		for(int i =0;i<7;i++){
//			for(int j=0;j<7;j++){
//				if(s[i][j]== Integer.MAX_VALUE){
//					graph[i][j] = max;
//				}else {
//					graph[i][j]  = s[i][j];
//				}
//
//			}
//		}
//
//
//
////		int[][] graph = {
////				{0, 10, max, 4, max, max,0},
////				{10, 0, 8, 2, 6, max,0},
////				{max, 8, 10, 15, 1, 5,0},
////				{4, 2,  15, 0, 6, max,0},
////				{max, 6, 1, 6, 0, 12,9},
////				{max, max, 5, max, 12, 0,8},
////				{0, max, 5, 5, 12,7,8}};
//
//		System.out.println("输入起始节点：");
//		@SuppressWarnings("resource")
//		Scanner in = new Scanner(System.in);
//
//		int startNode = Integer.parseInt(in.nextLine());
//		int[] dist = dijkstra(graph, startNode);
//		System.out.println(Arrays.toString(dist));
//	}
//
//	/**
//	 * Dijkstra算法，其调用了findMinDistNode方法
//	 * @param graph 进行查找的图，这里用二维矩阵实现
//	 * @param startNode 起始节点
//	 * @return 返回起始节点到其他节点的最短路径距离列表
//	 */
//	public static int[] dijkstra(int[][] graph, int startNode) {
//		char[] vertexList = {'A', 'B', 'C', 'D', 'E', 'F','G'};
//
//		String[] findProcess = new String[7]; // 用于打印最短路径的字符串数组
//		findProcess[0] = String.valueOf(vertexList[startNode]); // 直接将开始节点赋给findProcess[0]
//		int[] processDist = new int[7]; // 用于打印最短路径距离的整型数组（和findProcess搭配打印）
//		processDist[0] = 0;
//
//		int vertexNumbers = graph.length;
//		int[] dist = new int[vertexNumbers]; // 当下startNode到每个顶点的最短距离
//		Arrays.fill(dist, MAX_DISTANCE); // 将初始距离都设为最大值
//		dist[startNode] = 0; // startNode和自己的距离为0
//		boolean[] visited = new boolean[vertexNumbers]; // 该列表标记顶点是否被放入最终最短路径中
//
//		// 第一个for循环用于找到minNode，将其加入最终的最短路径
//		for (int i = 0; i < vertexNumbers; i++) {
//			int minNode = findMinDistNode(dist, visited); // 未被访问且startNode能达到的最短距离的顶点
//			visited[minNode] = true; // 将此顶点加入已访问列表，保证之后循环findMinDistNode方法时不再访问该顶点，因为已经确定将该点加入最终最短路径了
//
//			if (i == 0) ; // 开始节点到开始节点不进行任何操作
//			else {
//				findProcess[i] = findProcess[i-1] + " -> " + vertexList[minNode]; // 开始节点到每次确定下的minNode之间的路径
//			}
//			processDist[i] = dist[minNode]; // 开始节点到每次确定下的minNode之间的距离
//
//			// 第二个for循环以minNode顶点为新的开始点进行后续节点的查找，保证从startNode到后续节点的距离为当下最小距离
//			for (int j = 0; j < vertexNumbers; j++) {
//				/*
//				 *  minNode和后续节点j满足几个条件：
//				 *  1、后续节点j未被访问过
//				 *  2、在图中minNode和后续节点距离不为0，即不是同一个节点
//				 *  3、minNode和startNode距离不为最大值
//				 *  4、startNode到minNode的最短距离(dist[minNode])加minNode和后续节点的距离小于startNode直接到后续节点的最短距离(dist[j])
//				 */
//				if (!visited[j] && graph[minNode][j] != 0 && dist[minNode] != MAX_DISTANCE
//						&& dist[minNode] + graph[minNode][j] < dist[j]) {
//					dist[j] = dist[minNode] + graph[minNode][j]; // 更新dist列表，保证dist[j]是目前为止的最小值
//				}
//			}
//		}
//		System.out.println("起始节点到各节点的最短路径及距离如下：");
//		for (int i = 1; i < 7; i++) {
//			System.out.println(findProcess[i] + " 的最短距离为 " + processDist[i]);
//		}
//		return dist;
//	}
//
//	// 从dist[]中其余未访问的节点中找到startNode能到达的距离最小的顶点
//	public static int findMinDistNode(int[] dist, boolean[] visited) {
//		int minDist = MAX_DISTANCE;
//		int minNode = -1;
//		for (int i = 0; i < dist.length; i++) {
//			// 若距离为MAX_DISTANCE，则说明startNode无法到达该顶点
//			if (!visited[i] && dist[i] < minDist) {
//				minDist = dist[i]; // 更新minDist，保证最后得出的是可达到的距离最短的顶点
//				minNode = i; // 更新minNode
//			}
//		}
//		return minNode;
//	}
}

