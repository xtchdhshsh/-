package comlisanshuxue.lisan.MingTi;

import java.util.ArrayList;
import java.util.Arrays;

public class NormalForm {
    String form;
    int count0 = 0;
    int count1 = 0;

    Proposition fo;
    Translation tr;

    NormalForm(String form) {
        this.form = form;
        fo = new Proposition(form);
        tr = new Translation(form);
    }

    void calculate() {
        tr.operate(fo);
        tr.CalValName(fo);
        for(int i=0;i<16;i++){
            if (fo.truth[i] == 0)
                count0++;
            else
                count1++;
        }
    }

    //	void table() {
//		String a="";
//		if(tr.judge()==2) {
//			System.out.println(tr.va[0]+" "+tr.va[1]+" "+tr.pfr);
//			for(int i=0;i<4;i++) {
//				System.out.println(fo.va[1][i*4]+" "+fo.va[2][i*4]+"   "+fo.truth[i*4]);
//
//			}
//		}
//		if(tr.judge()==3) {
//			System.out.println(tr.va[0]+" "+tr.va[1]+" "+tr.va[2]+" "+tr.pfr);
//			for(int i=0;i<8;i++) {
//				System.out.println(fo.va[1][i*2]+" "+fo.va[2][i*2]+" "+fo.va[3][i*2]+"   "+fo.truth[i*2]);
//
//			}
//		}
//		if(tr.judge()==4) {
//			System.out.println(tr.va[0]+" "+tr.va[1]+" "+tr.va[2]+" "+tr.va[3]+" "+tr.pfr);
//			for(int i=0;i<16;i++) {
//				System.out.println(fo.va[1][i]+" "+fo.va[2][i]+" "+fo.va[3][i]+" "+fo.va[4][i]+"   "+fo.truth[i]);
//
//			}
//		}
//
//	}
    int[][] table() {
        int va_num=tr.count3; //变量个数
        int num1= (int) Math.pow(2,4-va_num);
        int num2= (int) Math.pow(2,va_num);
        int [][]a=new int[num2][fo.num];
        for(int i=0;i<num2;i++)
        {
            if (va_num >= 0) System.arraycopy(fo.va[i * num1], 0, a[i], 0, va_num);
            if (fo.num - va_num >= 0) System.arraycopy(fo.middle[i * num1], 0, a[i], va_num, fo.num - va_num);
        }
        return a;
    }

    ArrayList<String> GetValName(){return fo.va_name;}

    /*
    void pdnf() {
        //this.calculate();
        count1=count1/2;
        count1--;
        System.out.println("主析取范式为:");
        for(int i=0;i<8;i++) {
            if(fo.truth[i*2]==1) {
                System.out.print("(");
                if(fo.va[1][i*2]==0) {
                    System.out.print("﹁"+tr.va[0]);
                }
                else {
                    System.out.print(tr.va[0]);
                }
                System.out.print("∧");
                if(fo.va[2][i*2]==0) {
                    System.out.print("﹁"+tr.va[1]);
                }
                else {
                    System.out.print(tr.va[1]);
                }
                System.out.print("∧");
                if(fo.va[3][i*2]==0) {
                    System.out.print("﹁"+tr.va[2]);
                }
                else {
                    System.out.print(tr.va[2]);
                }
                System.out.print(")");

                if(count1>0) {
                    System.out.print("∨");
                    count1--;
                }
            }
        }
        System.out.println();
    }*/
    String pdnf() {
        //this.calculate();
        String re = "";
        count1 = count1 / 2;
        count1--;
        System.out.println("主析取范式为:");
        for (int i = 0; i < 8; i++) {
            if (fo.truth[i * 2] == 1) {
                //System.out.print("(");
                re += "(";
                if (fo.va[0][i * 2] == 0) {
                    //System.out.print("﹁"+tr.va[0]);
                    re += "﹁" + tr.va[0];
                } else {
                    //System.out.print(tr.va[0]);
                    re += tr.va[0];
                }
                //System.out.print("∧");
                re += "∧";
                if (fo.va[2][i * 2] == 0) {
                    //System.out.print("﹁"+tr.va[1]);
                    re += "﹁" + tr.va[1];
                } else {
                    //System.out.print(tr.va[1]);
                    re += tr.va[1];
                }
                //System.out.print("∧");
                re += "∧";
                if (fo.va[3][i * 2] == 0) {
                    //System.out.print("﹁"+tr.va[2]);
                    re += "﹁" + tr.va[2];
                } else {
                    //System.out.print(tr.va[2]);
                    re += tr.va[2];
                }
                //System.out.print(")");
                re += ")";
                if (count1 > 0) {
                    //System.out.print("∨");
                    re += "∨";
                    count1--;
                }
            }
        }
        return re;
    }

    /*
    void pcnf() {
        count0=count0/2;
        count0--;
        System.out.println("主合取范式为:");
        for(int i=0;i<8;i++) {
            if(fo.truth[i*2]==0) {
                System.out.print("(");
                if(fo.va[1][i*2]==0) {
                    System.out.print(tr.va[0]);
                }
                else {
                    System.out.print("﹁"+tr.va[0]);
                }
                System.out.print("∨");
                if(fo.va[2][i*2]==0) {
                    System.out.print(tr.va[1]);
                }
                else {
                    System.out.print("﹁"+tr.va[1]);
                }
                System.out.print("∨");
                if(fo.va[3][i*2]==0) {
                    System.out.print(tr.va[2]);
                }
                else {
                    System.out.print("﹁"+tr.va[2]);
                }
                System.out.print(")");

                if(count0>0) {
                    System.out.print("∧");
                    count0--;
                }
            }
        }
        System.out.println();
    }
    */
    String pcnf() {
        String re = "";
        count0 = count0 / 2;
        count0--;
        System.out.println("主合取范式为:");
        for (int i = 0; i < 8; i++) {
            if (fo.truth[i * 2] == 0) {
                //System.out.print("(");
                re += "(";
                if (fo.va[1][i * 2] == 0) {
                    //System.out.print(tr.va[0]);
                    re += tr.va[0];
                } else {
                    //System.out.print("﹁"+tr.va[0]);
                    re += "﹁" + tr.va[0];
                }
                //System.out.print("∨");
                re += "∨";
                if (fo.va[2][i * 2] == 0) {
                    //System.out.print(tr.va[1]);
                    re += tr.va[1];
                } else {
                    //System.out.print("﹁"+tr.va[1]);
                    re += "﹁" + tr.va[1];
                }
                //System.out.print("∨");
                re += "∨";
                if (fo.va[3][i * 2] == 0) {
                    //System.out.print(tr.va[2]);
                    re += tr.va[2];
                } else {
                    //System.out.print("﹁"+tr.va[2]);
                    re += "﹁" + tr.va[2];
                }
                //System.out.print(")");
                re += ")";
                if (count0 > 0) {
                    //System.out.print("∧");
                    re += "∧";
                    count0--;
                }
            }
        }
        return re;

    }

    /*
        void comeTrue() {
            System.out.println("成真赋值为:");
            for(int i=0;i<8;i++) {
                if(fo.truth[i*2]==1) {
                    System.out.print(fo.va[1][i*2]+" "+fo.va[2][i*2]+" "+fo.va[3][i*2]);
                    System.out.print("   ");
                }
            }
            System.out.println();
        }

     */
    String comeTrue() {
        StringBuilder re = new StringBuilder();
        System.out.println("成真赋值为:");
        int va_num=tr.count3; //变量个数
        int num1= (int) Math.pow(2,4-va_num);
        int num2= (int) Math.pow(2,va_num);
        for(int i=0;i<num2;i++) {
            for(int j=0;j<va_num;j++) {
                if(fo.truth[i*num1]==1){
                    if(i!=0) re.append(",");
                    re.append(Arrays.toString(fo.va[i * num1]));
                    if(j!=va_num-1) re.append(" ");
                }
            }
        }
        System.out.println(re);
        return re.toString();
    }

    /*
        void aFalse() {
            System.out.println("成假赋值为:");
            for (int i = 0; i < 8; i++) {
                if (fo.truth[i * 2] == 0) {
                    System.out.print(fo.va[1][i * 2] + " " + fo.va[2][i * 2] + " " + fo.va[3][i * 2]);
                    System.out.print("   ");
                }
            }
            System.out.println();
        }

     */
    String aFalse() {
        String re="";
        int k = 0;
        System.out.println("成假赋值为:");
        for (int i = 0; i < 8; i++) {
            if (fo.truth[i * 2] == 0) {

                System.out.print(fo.va[1][i * 2] + " " + fo.va[2][i * 2] + " " + fo.va[3][i * 2]);
                if(k==0){
                    re+=fo.va[1][i * 2] + " " + fo.va[2][i * 2] + " " + fo.va[3][i * 2];
                    k++;
                }else {
                    re+=","+fo.va[1][i * 2] + " " + fo.va[2][i * 2] + " " + fo.va[3][i * 2];
                }
                System.out.print("   ");
            }
        }
        System.out.println();
        return re;
    }


}