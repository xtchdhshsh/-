package comlisanshuxue.lisan.MingTi;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.HashMap;

public class Translation {
	String pfr;
	char[] op;//命题公式
	char[] op1;//后缀表达式
	char[] op2;//后缀表达式-符号栈
	char[] va=new char[4];//变量储存
	int[] variable=new int[4];
	int count1=-1;//op1计数器
	int count2=-1;//op2计数器
	int count3=0;//va计数器
	LogicalOperation lg = new LogicalOperation();

	Translation(String pfr){
		this.pfr=pfr;
		op=pfr.toCharArray();
		op1=new char[op.length];
		op2=new char[op.length];
		judge();
		BuildOp1();
	}

	void BuildOp1() {
		//转化为后缀表达式,遍历op数组；
		//优先级为 ﹁  ∧  ∨  →  ↔
		//样例p∨(q∨﹁r∧s)
		//后缀表达式pqr﹁s∧vv
		HashMap<Character, Integer> map = new HashMap<>();
		map.put('﹁', 5);
		map.put('∧', 4);
		map.put('∨', 3);
		map.put('→', 2);
		map.put('↔', 1);
		map.put('(', 0);//左括号是为了方便比较
		for (char c : op) {
			if (c >= 'a' && c <= 'z') {
				count1++;
				op1[count1] = c;
			} else if (c == '(') {
				count2++;
				op2[count2] = c;
			} else if (c == ')') {
				while (op2[count2] != '(') {
					count1++;
					op1[count1] = op2[count2];
					count2--;
				}
				count2--;
			}
			else {
				if (count2 != -1 && map.get(op2[count2]) >= map.get(c)) {
					while (count2 != -1 && map.get(op2[count2]) > map.get(c)) {
						count1++;
						op1[count1] = op2[count2];
						count2--;
					}
				}
				count2++;
				op2[count2] = c;
			}
		}//循环结束

		while(count2>=0) {
			count1++;
			op1[count1]=op2[count2];
			count2--;
		}//后缀表达式完成
	}

	void judge() {//存储变量
		int len=pfr.length();
		for(int i=0;i<len;i++)
		{
			char ch=pfr.charAt(i);
			if(ch>='a'&&ch<='z'){
				int flag=1;
				for(int j=0;j<count3;j++) {
                    if (va[j] == ch) {
						flag = 0;
						break;
					}
				}
				if(flag==1) va[count3++]=ch;
			}
		}
	}
	void operate(Proposition fo) {
		for(int line=0;line<16;line++) {
			int num=-1;
			int middlenum=0;
			for(int i=0;i<=count1;i++) {
				if(op1[i]>='a'&&op1[i]<='z') {
					num++;
					for(int k=0;k<count3;k++)
						if(va[k]==op1[i]) variable[num]=fo.va[line][k];
				}
				else if(op1[i]=='﹁') {
					variable[num]=lg.nagation(variable[num]);
					fo.middle[line][middlenum++]=variable[num];
				}
				else if(op1[i]=='∨') {
					variable[num-1]=lg.disjunction(variable[num-1], variable[num]);
					fo.middle[line][middlenum++]=variable[num-1];
					num--;
				}
				else if(op1[i]=='∧') {
					variable[num-1]=lg.conjunction(variable[num-1], variable[num]);
					fo.middle[line][middlenum++]=variable[num-1];
					num--;
				}
				else if(op1[i]=='→') {
					variable[num-1]=lg.implication(variable[num-1], variable[num]);
					fo.middle[line][middlenum++]=variable[num-1];
					num--;
				}
				else if(op1[i]=='↔') {
					variable[num-1]=lg.equivalence(variable[num-1], variable[num]);
					fo.middle[line][middlenum++]=variable[num-1];
					num--;
				}
			}
			fo.truth[line]=variable[0];
		}
	}
	void CalValName(Proposition fo){
		String[] v=new String[4];
		for(int i=0;i<count3;i++) v[i]=String.valueOf(va[i]);
		for(int i=0;i<count3;i++) fo.va_name.add(String.valueOf(va[i]));
		fo.num+=count3;
		int num=-1;
		for(int i=0;i<=count1;i++) {
			if(op1[i]>='a'&&op1[i]<='z') {
				num++;
				for(int k=0;k<count3;k++)
					if(va[k]==op1[i]) v[num]= String.valueOf(va[k]);
			}
			else if(op1[i]=='﹁') {
				v[num]="﹁"+v[num];
				fo.va_name.add(v[num]);
				fo.num++;
			}
			else if(op1[i]=='∨') {
				v[num-1]=v[num-1]+"∨"+v[num];
				fo.va_name.add(v[num-1]);
				num--;
				fo.num++;
			}
			else if(op1[i]=='∧') {
				v[num-1]=v[num-1]+"∧"+v[num];
				fo.va_name.add(v[num-1]);
				num--;
				fo.num++;
			}
			else if(op1[i]=='→') {
				v[num-1]=v[num-1]+"→"+v[num];
				fo.va_name.add(v[num-1]);
				num--;
				fo.num++;
			}
			else if(op1[i]=='↔') {
				v[num-1]=v[num-1]+"↔"+v[num];
				fo.va_name.add(v[num-1]);
				num--;
				fo.num++;
			}
		}
	}
}