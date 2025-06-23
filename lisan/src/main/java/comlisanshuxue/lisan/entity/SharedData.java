package comlisanshuxue.lisan.entity;

import lombok.Data;

import java.time.LocalDateTime;

@Data
public class SharedData {
    private Long id;
    private String type;//新增了题目类型type
    private String content; // 存储JSON格式的内容数据
    private LocalDateTime create_time;
}