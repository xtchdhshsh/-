package comlisanshuxue.lisan.controller;

import comlisanshuxue.lisan.entity.SharedData;
import comlisanshuxue.lisan.service.ShareService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import java.util.List;

@RestController
@RequestMapping("/api/share")
public class ShareController {
    @Autowired
    private ShareService shareService;

    //更新了接口传入的参数，新添加了type
    @PostMapping
    public Long createShare(@RequestBody SharedData sharedData) {
        return shareService.saveSharedData(sharedData);
    }

    // 新增接口：根据id和type获取答案
    @GetMapping("/{id}/{type}/answer")
    public ResponseEntity<String> getAnswer(@PathVariable Long id, @PathVariable String type) {
        String answer = shareService.getAnswerByIdAndType(id, type);
        return answer != null ? ResponseEntity.ok(answer) : ResponseEntity.notFound().build();
    }

    // 新增接口：根据type获取题目列表
    @GetMapping("/list/{type}")
    public List<SharedData> listByType(@PathVariable String type) {
        return shareService.findByType(type);
    }

    @GetMapping("/{id}")
    public SharedData getShare(@PathVariable Long id) {
        return shareService.getSharedData(id);
    }

    @GetMapping("/list")
    public List<SharedData> list() {
        return shareService.findAll();
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Void> deleteShare(@PathVariable Long id) {
        boolean deleted = shareService.deleteSharedData(id);
        return deleted ? ResponseEntity.ok().build() : ResponseEntity.notFound().build();
    }
}