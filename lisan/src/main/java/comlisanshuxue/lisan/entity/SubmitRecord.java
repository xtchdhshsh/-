package comlisanshuxue.lisan.entity;

import lombok.Data;

@Data
public class SubmitRecord {
    private Long id;
    private String studentId;
    private Long questionId;
    private String answer;
    private Boolean isCorrect;
    private String submitTime;
} 