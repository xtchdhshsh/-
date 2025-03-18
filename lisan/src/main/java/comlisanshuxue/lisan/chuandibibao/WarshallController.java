package comlisanshuxue.lisan.chuandibibao;

import comlisanshuxue.lisan.dto.Warshalldto;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import io.swagger.models.auth.In;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

@RestController
@Api(tags = "Warshall")
public class WarshallController {

//	public static void main(String[] args) {
//
//		Warshall w = new Warshall(8);
//		w.addVertex("a");
//		w.addVertex("b");
//		w.addVertex("c");
//		w.addVertex("d");
//		w.addVertex("e");
//		w.addVertex("f");
//		w.addVertex("g");
//
//
//
//		w.addEdge(0, 2);
//		w.addEdge(1, 0);
//		w.addEdge(1, 4);
//		w.addEdge(3, 4);
//		w.addEdge(4, 2);
//		w.addEdge(2, 5);
//		w.addEdge(5, 6);
//
//		w.printMatrix();
//
//		w.doWarshall();
//
//		w.printMatrix();
//	}

    @ApiOperation("计算Warshall")
    @PostMapping("/warshall")
    public List<int[][]> war(@RequestBody Warshalldto war)  {
        Integer num = war.getNum();
        String shizi = war.getStringMatrix();
        System.out.println(num+shizi+"aaaaaaaaaaaaaaa");
        String re = "";
        short ch = 'a';
        Warshall w = new Warshall(num);
        for(int i=0;i<num;i++){
            w.addVertex(String.valueOf(ch+i));
        }
//        w.addVertex("a");
//        w.addVertex("b");
//        w.addVertex("c");
//        w.addVertex("d");
//        w.addVertex("e");
//        w.addVertex("f");
//        w.addVertex("g");

        //String s = "0 2,1 0,1 4,3 4,4 2,2 5,5 6";
        String[] split = shizi.split(",");
        for(int i=0;i<split.length;i++) {
            String[] split2 = split[i].split(" ");
            w.addEdge(Integer.parseInt(split2[0]), Integer.parseInt(split2[1]));
        }

//		w.addEdge(0, 2);
//		w.addEdge(1, 0);
//		w.addEdge(1, 4);
//		w.addEdge(3, 4);
//		w.addEdge(4, 2);
//		w.addEdge(2, 5);
//		w.addEdge(5, 6);

        //w.printMatrix();

        w.doWarshall();

        //w.printMatrix();
        for(int q = 0;q<w.getJieguo().size();q++){
            int[][] ints = w.getJieguo().get(q);
            for(int k=0;k<num;k++){
                for(int c=0;c<num;c++){
                    System.out.print(ints[k][c]);
                }
                System.out.println();
            }
            System.out.println("每一个");
        }

        re = w.fuzhiMatrix();
        System.out.println("最后的结果");
        System.out.println(re);
        return w.getJieguo();
    }
}
