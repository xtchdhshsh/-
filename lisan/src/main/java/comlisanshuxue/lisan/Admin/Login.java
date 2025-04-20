package comlisanshuxue.lisan.Admin;

import comlisanshuxue.lisan.Mapper.UserMapper;
import comlisanshuxue.lisan.Utils.JWTUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
public class Login {

    @Autowired
    private UserMapper userMapper;

    //学生登录
    @PostMapping("/login")
    public String login(String username, String password)
    {
        String result = userMapper.login(username, password);
        System.out.println("收到请求，用户名：" + username);
        if (result == null)
            return "login fail";
        else{
            return JWTUtils.getToken(username,0);
        }
    }

    //教师登录
    @PostMapping("/TeacherLogin")
    public String TeacherLogin(String username, String password)
    {
        String result = userMapper.TeacherLogin(username, password);
        if (result == null)
            return "login fail";
        else{
            return JWTUtils.getToken(username,1);
        }
    }

    //学生注册
    @PostMapping("/Register")
    public String register(String username, String password)
    {
        int result = userMapper.register(username, password);
        if(result==0) return "注册失败";
        else return "注册成功";
    }
    //学生注册
    @PostMapping("/TeacherRegister")
    public String TeacherRegister(String username, String password)
    {
        int result = userMapper.TeacherRegister(username, password);
        if(result==0) return "注册失败";
        else return "注册成功";
    }
}
