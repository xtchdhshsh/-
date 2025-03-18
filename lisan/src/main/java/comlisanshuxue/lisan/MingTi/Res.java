package comlisanshuxue.lisan.MingTi;

import lombok.Data;

import java.util.ArrayList;

@Data
public class Res {
    int[][] data;//返回值
    ArrayList<String> va;//返回字符
    Res(int[][] data, ArrayList<String> va){
        this.data=data;
        this.va=va;
    }

}//返回类
