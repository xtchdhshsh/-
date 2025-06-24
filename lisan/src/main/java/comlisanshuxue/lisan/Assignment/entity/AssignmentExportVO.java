package comlisanshuxue.lisan.Assignment.entity;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class AssignmentExportVO {
    private Long assignmentId;
    private String title;
    private String classname;
    private Long createdAt;
    private Long deadline;
    private Integer submittedCount;
    private Integer totalStudents;

}
