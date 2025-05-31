package comlisanshuxue.lisan.Captcha;

import com.google.code.kaptcha.impl.DefaultKaptcha;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.MediaType;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.bind.annotation.RestController;
import org.apache.commons.codec.binary.Base64;

import javax.imageio.ImageIO;
import javax.servlet.http.HttpServletRequest;
import java.awt.image.BufferedImage;
import java.io.ByteArrayOutputStream;
import java.util.HashMap;
import java.util.Map;

@RestController
public class getcaptcha {

    @Autowired
    private DefaultKaptcha captchaProducer;

    @GetMapping(value = "/captcha", produces = MediaType.APPLICATION_JSON_VALUE)
    @ResponseBody
    public Map<String, String> defaultKaptcha(){
        Map<String, String> result = new HashMap<>();

        try {
            // 生成验证码字符串
            String verifyCode = captchaProducer.createText();

            // 生成验证码图片
            BufferedImage challenge = captchaProducer.createImage(verifyCode);

            // 将图片转换为Base64编码
            ByteArrayOutputStream imgOutputStream = new ByteArrayOutputStream();
            ImageIO.write(challenge, "jpg", imgOutputStream);

            byte[] captchaBytes = imgOutputStream.toByteArray();
            String base64Image = "data:image/jpeg;base64," + Base64.encodeBase64String(captchaBytes);

            // 返回结果
            result.put("verifyCode", verifyCode);
            result.put("message", "success");
            result.put("image", base64Image);
        } catch (Exception e) {
            result.put("code", "500");
            result.put("message", "验证码生成失败: " + e.getMessage());
        }

        return result;
    }
}