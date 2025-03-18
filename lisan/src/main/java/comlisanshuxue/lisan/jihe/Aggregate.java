package comlisanshuxue.lisan.jihe;

import java.util.HashSet;
import java.util.Scanner;
 
public class Aggregate {
 
    public static void main(String[] args) {
//        为四个集合开辟内存空间，方便后续赋值
        HashSet<Integer> result = new HashSet<>();
        HashSet<Integer> A = new HashSet<>();
        HashSet<Integer> B = new HashSet<>();
        HashSet<Integer> C = new HashSet<>();
        System.out.println("请先输入A、B、C每个集合元素的个数，然后依次输入集合中的元素.");
//        获取控制台输入
        Scanner scanner = new Scanner(System.in);
//        获取A、B、C集合的元素个数
        int a = scanner.nextInt();
        int b = scanner.nextInt();
        int c = scanner.nextInt();
//        为集合A赋值
        for (int i = 0;i < a;i++){
            int i1 = scanner.nextInt();
            A.add(i1);
        }
//        为集合B赋值
        for (int j = 0;j < b;j++){
            int j1 = scanner.nextInt();
            B.add(j1);
        }
//        为集合C赋值
        for (int k = 0;k < c;k++){
            int k1 = scanner.nextInt();
            C.add(k1);
        }
//        查看A、B、C集合中的元素
        System.out.println("A=" + A.toString());
        System.out.println("B=" + B.toString());
        System.out.println("C=" + C.toString());
//        清除result中的元素
        result.clear();
//        将A集合中的元素放入result中
        result.addAll(A);
//        去除A中所有所有不在B中的元素
        result.retainAll(B);
        System.out.println("交集=" + result);
        result.clear();
//        把A、B集合中过的元素都加进去，Set集合不会重复
        result.addAll(A);
        result.addAll(B);
        System.out.println("并集=" + result);
        result.clear();
        result.addAll(A);
//        清除A中所有属于B的元素
        result.removeAll(B);
        System.out.println("差集=" + result);
        result.clear();
        result.addAll(C);
        result.removeAll(A);
        System.out.println("补集=" + result);
    }
}