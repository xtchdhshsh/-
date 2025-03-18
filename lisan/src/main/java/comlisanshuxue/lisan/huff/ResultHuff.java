package comlisanshuxue.lisan.huff;

import lombok.Data;
import org.apache.commons.lang3.StringUtils;
import org.springframework.stereotype.Component;

import java.util.ArrayList;

import static comlisanshuxue.lisan.huff.HuffmanTree.addDouble;

@Data
@Component
class ResultHuff{

    ArrayList<HuffmanNode[]> guocheng;
    String bianma;//编码
    Double daiquan;//带权
    String daiquanguocheng;//带权过程

    ResultHuff(){bianma="";daiquanguocheng="";}
    public void CalCodes(HuffmanNode root, String code) {

        if (root == null) return;

        if (root.left == null && root.right == null) {
            this.setBianma(this.getBianma()+root.data+":"+root.frequency+":"+code+" ");
            return;
        }

        CalCodes(root.left, code + "0");
        CalCodes(root.right, code + "1");
    }

    public Double calculateWPL(HuffmanNode root, int depth) {
        if (root == null) return 0.0;

        if (root.left == null && root.right == null) {
            setDaiquanguocheng(getDaiquanguocheng()+root.frequency+"*"+depth+"+");
            return root.frequency * depth;
        }

        Double re = addDouble(calculateWPL(root.left, depth + 1),calculateWPL(root.right, depth + 1));

        if(depth==0){
            daiquan=re;
            daiquanguocheng=daiquanguocheng.substring(0,daiquanguocheng.length()-1)+"="+daiquan;
        }

        return re;
    }
}


