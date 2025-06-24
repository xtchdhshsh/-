package comlisanshuxue.lisan.Assignment.entity;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class Page {
    private Integer pageNum = 1;
    private Integer pageSize = 10;
}
