package comlisanshuxue.lisan.dto;

import io.swagger.models.auth.In;
import lombok.Data;
import org.springframework.stereotype.Component;

import javax.annotation.PostConstruct;
import java.util.List;

@Component
@Data
public class jihedto {
    public List<Integer> a;
    public List<Integer> b;
    public List<Integer> c;
    public String shizi;



}
