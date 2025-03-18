package comlisanshuxue.lisan.MingTi;

import java.util.ArrayList;

public class Proposition {
	String formula;
	//val为变量值,truth为真值
	int[][] va = new int[16][4];
	int[][] middle=new int[16][10];//存储中间过程
	int[] truth = new int[16];
	ArrayList<String> va_name=new ArrayList<String>();//用于存储中间过程变量名的数组
	int num=0;
	Proposition(String formula) {
		this.formula=formula;
		int count=0;
		for (int i = 0; i < 2; i++) {
			for (int j = 0; j < 2; j++) {
				for (int k = 0; k < 2; k++) {
					for (int l = 0; l < 2; l++) {
						this.va[count][0] = i;
						this.va[count][1] = j;
						this.va[count][2] = k;
						this.va[count][3] = l;
						count++;
					}
				}
			}
		}
	}
}