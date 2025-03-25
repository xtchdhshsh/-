package comlisanshuxue.lisan.Bipartite.pojo;

import java.util.List;
import java.util.Map;

public class BipartiteMatching {
    private List<Map<Integer, Integer>> allMatching; // 所有最大匹配
    private List<Map<Integer, Integer>> perfectMatchings; // 所有完备匹配
    private boolean hasPerfectMatching; // 是否存在完备匹配

    public List<Map<Integer, Integer>> getAllMatching() {
        return allMatching;
    }

    public void setAllMatching(List<Map<Integer, Integer>> allMatching) {
        this.allMatching = allMatching;
    }

    public List<Map<Integer, Integer>> getPerfectMatchings() {
        return perfectMatchings;
    }

    public void setPerfectMatchings(List<Map<Integer, Integer>> perfectMatchings) {
        this.perfectMatchings = perfectMatchings;
    }

    public boolean isHasPerfectMatching() {
        return hasPerfectMatching;
    }

    public void setHasPerfectMatching(boolean hasPerfectMatching) {
        this.hasPerfectMatching = hasPerfectMatching;
    }
}