package comlisanshuxue.lisan.Utils;

import io.jsonwebtoken.JwtBuilder;
import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.SignatureAlgorithm;

import static javax.crypto.Cipher.SECRET_KEY;

public class JWTUtils {

    //JWT加密key
    private static final String SECRET_KEY = "SZTU@2025.$#";

    //获取JWT
    public static String getToken(String username,Integer IsTeacher) {
        return  Jwts.builder()
                .claim("username", username)
                .claim("IsTeacher", IsTeacher)
                .signWith(SignatureAlgorithm.HS512, SECRET_KEY)
                .compact();
    }

    // 验证token  合法性
    private static boolean validateToken(String authToken) {
        try {
            Jwts.parser().setSigningKey(SECRET_KEY).parseClaimsJws(authToken);
            return true;
        } catch (Exception e) {
            System.out.println("无效的token：" + authToken);
        }
        return false;
    }
}
