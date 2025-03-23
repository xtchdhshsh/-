package comlisanshuxue.lisan.Bipartite.service;

import comlisanshuxue.lisan.Bipartite.pojo.BipartiteMatching;
import comlisanshuxue.lisan.Bipartite.pojo.Graph;
import org.springframework.stereotype.Service;

import java.util.*;

@Service
public class BipartiteService {
    public BipartiteMatching findAllMaxMatching(Graph graph) {
        BipartiteGraph bg = new BipartiteGraph(
                graph.getU(),
                graph.getV(),
                graph.getEdges()
        );

        // 最大
        List<Map<Integer, Integer>> allMatching = bg.findAllMaxMatching();

        // 完备
        List<Map<Integer, Integer>> perfectMatchings = bg.findPerfectMatchings();

        BipartiteMatching result = new BipartiteMatching();
        result.setAllMatching(allMatching);
        result.setPerfectMatchings(perfectMatchings.isEmpty() ? null : perfectMatchings);
        result.setHasPerfectMatching(!perfectMatchings.isEmpty());

        return result;
    }


    private static class BipartiteGraph {
        private final int U;
        private final int V;
        private final List<Integer>[] graph;
        private int maxMatchingSize;

        public BipartiteGraph(int U, int V, List<List<Integer>> edges) {
            this.U = U;
            this.V = V;
            this.graph = new ArrayList[U];
            Arrays.setAll(graph, i -> new ArrayList<>());

            for (List<Integer> edge : edges) {
                if (edge.size() != 2) continue;
                int u = edge.get(0);
                int v = edge.get(1);
                if (u >= 0 && u < U && v >= 0 && v < V) {
                    graph[u].add(v);
                }
            }

            this.maxMatchingSize = calculateMaxMatching();
        }

        //大小
        private int calculateMaxMatching() {
            int[] pairU = new int[U];
            int[] pairV = new int[V];
            int[] dist = new int[U];
            Arrays.fill(pairU, -1);
            Arrays.fill(pairV, -1);

            int result = 0;
            while (bfs(pairU, pairV, dist)) {
                for (int u = 0; u < U; u++) {
                    if (pairU[u] == -1 && dfs(u, pairU, pairV, dist)) {
                        result++;
                    }
                }
            }
            return result;
        }

        private boolean bfs(int[] pairU, int[] pairV, int[] dist) {
            Queue<Integer> queue = new LinkedList<>();
            Arrays.fill(dist, -1);

            for (int u = 0; u < U; u++) {
                if (pairU[u] == -1) {
                    dist[u] = 0;
                    queue.add(u);
                }
            }

            boolean found = false;
            while (!queue.isEmpty()) {
                int u = queue.poll();
                for (int v : graph[u]) {
                    if (pairV[v] == -1) {
                        found = true;
                    } else if (dist[pairV[v]] == -1) {
                        dist[pairV[v]] = dist[u] + 1;
                        queue.add(pairV[v]);
                    }
                }
            }
            return found;
        }

        private boolean dfs(int u, int[] pairU, int[] pairV, int[] dist) {
            for (int v : graph[u]) {
                if (pairV[v] == -1 || (dist[pairV[v]] == dist[u] + 1 && dfs(pairV[v], pairU, pairV, dist))) {
                    pairU[u] = v;
                    pairV[v] = u;
                    return true;
                }
            }
            dist[u] = -1;
            return false;
        }

        // 回溯
        public List<Map<Integer, Integer>> findAllMaxMatching() {
            List<Map<Integer, Integer>> allMatching = new ArrayList<>();
            backtrack(0, new HashMap<>(), new boolean[V], allMatching);
            return allMatching;
        }

        private void backtrack(int u,
                               Map<Integer, Integer> currentMatching,
                               boolean[] usedV,
                               List<Map<Integer, Integer>> allMatching) {
            // 剪枝
            if (currentMatching.size() + (U - u) < maxMatchingSize) return;

            if (u == U) {
                if (currentMatching.size() == maxMatchingSize) {
                    allMatching.add(new HashMap<>(currentMatching));
                }
                return;
            }

            backtrack(u + 1, currentMatching, usedV, allMatching);

            for (int v : graph[u]) {
                if (!usedV[v]) {
                    Map<Integer, Integer> newMatching = new HashMap<>(currentMatching);
                    boolean[] newUsedV = Arrays.copyOf(usedV, V);

                    newMatching.put(u, v);
                    newUsedV[v] = true;

                    backtrack(u + 1, newMatching, newUsedV, allMatching);
                }
            }
        }

        // 查找所有完备匹配
        public List<Map<Integer, Integer>> findPerfectMatchings() {
            List<Map<Integer, Integer>> allMatching = findAllMaxMatching();
            int smallerSetSize = Math.min(U, V);

            List<Map<Integer, Integer>> perfectMatchings = new ArrayList<>();
            for (Map<Integer, Integer> matching : allMatching) {
                if (matching.size() == smallerSetSize) {
                    perfectMatchings.add(matching);
                }
            }

            return perfectMatchings;
        }
    }
}
