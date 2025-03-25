package comlisanshuxue.lisan.Bipartite.controller;

import comlisanshuxue.lisan.Bipartite.pojo.BipartiteMatching;
import comlisanshuxue.lisan.Bipartite.pojo.Graph;
import comlisanshuxue.lisan.Bipartite.service.BipartiteService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/bipartite")
public class BipartiteController {

    @Autowired
    private BipartiteService bipartiteService;

    @RequestMapping("/findAllMaxMatching")
    public BipartiteMatching findAllMaxMatching(Graph graph) {

        return bipartiteService.findAllMaxMatching(graph);
    }

}
