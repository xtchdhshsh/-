package comlisanshuxue.lisan.dto;

import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.Data;
import org.springframework.stereotype.Component;

@Component
@Data
public class Djs {
    @JsonProperty("powerTable")
    public int[][] graph;
    @JsonProperty("num")
    public int shuliang;
    @JsonProperty("nodeSelected")
    public int qidian;

}
