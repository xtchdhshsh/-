package comlisanshuxue.lisan.studentmanager;

import comlisanshuxue.lisan.Mapper.ExperimentMapper;
import comlisanshuxue.lisan.Utils.JWTUtils;
import comlisanshuxue.lisan.classmaneger.Result;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.RequestHeader;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.ArrayList;
import java.util.Map;


@RestController
public class studentmanager {

    @Autowired
    private ExperimentMapper experimentMapper;
    @RequestMapping("/getexperiment")
    public String getexperiment(@RequestHeader("token") String token){
        String teacher = JWTUtils.getUsername(token);
        ArrayList<String> re = new ArrayList<>(experimentMapper.getexperiment(teacher));
        return re.toString();
    }

    @RequestMapping("/getmark")
    public String getmark(@RequestHeader("token") String token,String experiment){
        String teacher = JWTUtils.getUsername(token);
        ArrayList<String> re = new ArrayList<>();
        for (Map<String, String> map : experimentMapper.getmark(teacher,experiment)){
            re.add(map.values().toString());
        }
        return re.toString();
    }
}
