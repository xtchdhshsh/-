//package comlisanshuxue.lisan.chuandibibao;
//
//public class WarshallTest {
//
////	public static void main(String[] args) {
////
////		Warshall w = new Warshall(8);
////		w.addVertex("a");
////		w.addVertex("b");
////		w.addVertex("c");
////		w.addVertex("d");
////		w.addVertex("e");
////		w.addVertex("f");
////		w.addVertex("g");
////
////
////
////		w.addEdge(0, 2);
////		w.addEdge(1, 0);
////		w.addEdge(1, 4);
////		w.addEdge(3, 4);
////		w.addEdge(4, 2);
////		w.addEdge(2, 5);
////		w.addEdge(5, 6);
////
////		w.printMatrix();
////
////		w.doWarshall();
////
////		w.printMatrix();
////	}
//
//	public static void main(String[] args)  {
//
//		String re = "";
//		Warshall w = new Warshall(8);
//		w.addVertex("a");
//		w.addVertex("b");
//		w.addVertex("c");
//		w.addVertex("d");
//		w.addVertex("e");
//		w.addVertex("f");
//		w.addVertex("g");
//
//		String s = "0 2,1 0,1 4,3 4,4 2,2 5,5 6";
//		String[] split = s.split(",");
//		for(int i=0;i<split.length;i++) {
//            String[] split2 = split[i].split(" ");
//            w.addEdge(Integer.parseInt(split2[0]), Integer.parseInt(split2[1]));
//        }
//
////		w.addEdge(0, 2);
////		w.addEdge(1, 0);
////		w.addEdge(1, 4);
////		w.addEdge(3, 4);
////		w.addEdge(4, 2);
////		w.addEdge(2, 5);
////		w.addEdge(5, 6);
//
//		w.printMatrix();
//
//		w.doWarshall(num);
//
//		w.printMatrix();
//
//		re = w.fuzhiMatrix();
//		System.out.println("最后的结果");
//		System.out.println(re);
//	}
//}
//