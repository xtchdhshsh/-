package comlisanshuxue.lisan.Assignment.entity;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class AssignmentProblem {
    private Long id;
    private Long assignmentId;
    private Long problemId;
    private Integer points;
}