package comlisanshuxue.lisan.Assignment.Controller;

import comlisanshuxue.lisan.Assignment.Service.AssignmentService;
import comlisanshuxue.lisan.Assignment.entity.Result;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

@RestController
@RequestMapping("/api/classes")
public class ClassController {

    @Autowired
    private AssignmentService assignmentService;

    @GetMapping
    public Result getAllClasses() {
        // 执行查询
        List<String> classes = assignmentService.getAllClasses();
        return Result.success(classes);
    }
}
