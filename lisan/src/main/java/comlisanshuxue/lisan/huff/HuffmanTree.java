package comlisanshuxue.lisan.huff;

import org.apache.commons.lang3.StringUtils;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RestController;

import java.math.BigDecimal;
import java.util.*;

import static java.lang.Double.sum;

@RestController
public class HuffmanTree {

    @PostMapping("/huffman")
    public ResultHuff huff(@RequestBody HuffDto hd) {
        String inputArr = hd.getInputArr();

        ArrayList<HuffmanNode[]> huffmanNodes = new ArrayList<>();

        PriorityQueue<HuffmanNode> minHeap = new PriorityQueue<>();

        String[] split = inputArr.split("~");

        int length = split.length;//变量个数

        for(int i=0;i<length;i++){
            String[] s = split[i].split("`");
            String character = String.valueOf(s[0]);
            Double frequency = Double.parseDouble(s[1]);
            minHeap.add(new HuffmanNode(character,frequency));
        }

        while (minHeap.size() > 1) {

            HuffmanNode[] jieshou = new HuffmanNode[minHeap.size()];
            int ak = 0;
            for (HuffmanNode huffmanNode : minHeap) {
                jieshou[ak++] = huffmanNode;
            }
            huffmanNodes.add(jieshou);

            HuffmanNode left = minHeap.poll();
            HuffmanNode right = minHeap.poll();
            HuffmanNode parent = new HuffmanNode(left.data+"_"+right.data, addDouble(left.frequency,right.frequency));
            parent.left = left;
            parent.right = right;
            minHeap.add(parent);

        }

        HuffmanNode[] last = new HuffmanNode[]{minHeap.poll()};
        huffmanNodes.add(last);
        HuffmanNode root = last[0];//根节点

        ResultHuff resultHuff = new ResultHuff();

        resultHuff.CalCodes(root, "");

        resultHuff.calculateWPL(root, 0);

        resultHuff.setGuocheng(huffmanNodes);

        return resultHuff;
    }

    //不丢精度的Double加法
    public static double addDouble(double m1, double m2) {
        BigDecimal p1 = new BigDecimal(Double.toString(m1));
        BigDecimal p2 = new BigDecimal(Double.toString(m2));
        return p1.add(p2).doubleValue();
    }

}