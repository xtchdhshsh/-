package comlisanshuxue.lisan.Bipartite.pojo;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.List;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class Graph {
    private int U; // 左侧顶点数
    private int V; // 右侧顶点数
    private List<List<Integer>> edges; // 边的列表

}
