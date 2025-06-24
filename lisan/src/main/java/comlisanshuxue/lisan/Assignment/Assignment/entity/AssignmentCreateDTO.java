package comlisanshuxue.lisan.Assignment.entity;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.List;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class AssignmentCreateDTO {
    private String title;
    private String description;
    private Long deadline;
    private String classname;
    private List<ProblemDTO> problems;

    @Data
    public static class ProblemDTO {
        private Long problemId;
        private Integer points;
    }
}