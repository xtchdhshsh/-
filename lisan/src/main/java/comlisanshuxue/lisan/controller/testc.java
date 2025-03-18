package comlisanshuxue.lisan.controller;

import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
public class testc {

    @RequestMapping("/test")
    public void test(){
        System.out.println("test");
    }
}
