package comlisanshuxue.lisan.Assignment.entity;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class AssignmentVO {
    private Long assignmentId;
    private String title;
    private String classname;
    private Integer submittedCount;
    private Integer totalStudents;
    private Long assignTime;
    private Long deadline;
}