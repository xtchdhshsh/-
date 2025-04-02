package comlisanshuxue.lisan.Bipartite.controller;

import comlisanshuxue.lisan.Bipartite.pojo.BipartiteMatching;
import comlisanshuxue.lisan.Bipartite.pojo.Graph;
import comlisanshuxue.lisan.Bipartite.service.BipartiteService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.util.Arrays;
import java.util.List;
import java.util.stream.Collectors;

@RestController
@RequestMapping("/bipartite")
public class BipartiteController {

    @Autowired
    private BipartiteService bipartiteService;

    @GetMapping("/findAllMaxMatching")
    public BipartiteMatching findAllMaxMatching(
            @RequestParam int U,
            @RequestParam int V,
            @RequestParam String edges
    ) {
        List<List<Integer>> edgesList = parseEdges(edges);

        Graph graph = new Graph(U, V, edgesList);

        return bipartiteService.findAllMaxMatching(graph);
    }

    private List<List<Integer>> parseEdges(String edges) {
        return Arrays.stream(edges.split(";"))
                .map(edge -> Arrays.stream(edge.split(","))
                        .map(Integer::parseInt)
                        .collect(Collectors.toList()))
                .collect(Collectors.toList());
    }

}
