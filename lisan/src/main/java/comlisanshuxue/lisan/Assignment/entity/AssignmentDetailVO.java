package comlisanshuxue.lisan.Assignment.entity;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.List;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class AssignmentDetailVO {
    private Long assignmentId;
    private String title;
    private String description;
    private Long createdAt;
    private Long deadline;
    private Integer totalPoints;
    private String classname;
    private List<ProblemVO> problems;
}