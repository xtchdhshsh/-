package comlisanshuxue.lisan.chuandibibao;

import lombok.Data;

import java.util.ArrayList;
import java.util.List;

@Data
class Warshall {

	private List<Vertex> vertexList;
	private int[][] matrix = null;
	private int maxSize = 20;
	private List<int[][]> jieguo = new ArrayList<>();

	public Warshall(int maxSize) {
		vertexList = new ArrayList<Vertex>(maxSize);
		matrix = new int[maxSize][maxSize];
		this.maxSize = maxSize;

	}

	// 增加顶点
	public void addVertex(String label) {
		if (isFull())
			throw new ArrayIndexOutOfBoundsException("数组已经满了");
		vertexList.add(new Vertex(label));
	}

	private boolean isFull() {
		return vertexList.size() == maxSize;
	}

	// 增加边
	public void addEdge(int start, int end) {
		int size = getSize();
		if (start >= size || end >= size)
			throw new ArrayIndexOutOfBoundsException();

		this.matrix[start][end] = 1;
	}

	// 执行算法
	public void doWarshall() {
		int len = getSize();
//		int[][] s= new int[len][len];
//		for(int i=0;i<len;i++){
//			for(int j=0;j<len;j++){
//				s[i][j] = matrix[i][j];
//				System.out.print(s[i][j]);
//			}
//			System.out.println();
//		}
//		System.out.println("输出s数组的值");
		// 考察每一行
		for (int x = 0; x < len; x++) {
//			this.jieguo.get(x) = new int[n][n];

			// 考察行中的每个单元
			for (int y = 0; y < len; y++) {
				if (matrix[x][y] == 1) {
					// 考察列x的每一个单元
					for (int z = 0; z < len; z++) {
						if (matrix[z][x] == 1) {
							matrix[z][y] = 1;

						}
					}
				}
			}
			//System.out.println("第"+x);
			this.printMatrix(); //打印每一步
//			for(int i=0;i<len;i++){
//				for(int j=0;j<len;j++){
//					s[i][j] = matrix[i][j];
//					System.out.print(s[i][j]);
//				}
//				System.out.println();
//			}
//			System.out.println("s素组拼装之后的结果");
			//this.jieguo.add(s);
		}
	}

	private int getSize() {
		return this.vertexList.size();
	}

	// 打印邻接矩阵
	public void printMatrix() {
		int [][] s= new int[getSize()][getSize()];
		System.out.println();
		int len = getSize();
		for (int i = 0; i < len; i++) {
			for (int k = 0; k < len; k++) {
				s[i][k] = matrix[i][k];
				//System.out.print(matrix[i][k]);
			}
			//System.out.println();
		}
		this.jieguo.add(s);
	}

	// 赋值
	public String fuzhiMatrix() {
		String re = "";
		int len = getSize();
		for (int i = 0; i < len; i++) {
			for (int k = 0; k < len; k++) {
				if(matrix[i][k]==1){
					re+=i+" "+k+",";

				}
			}

		}
		re = re.substring(0,re.length()-1);
		return re;
	}


}