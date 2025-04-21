package comlisanshuxue.lisan.interceptor;

import comlisanshuxue.lisan.Utils.JWTUtils;
import org.springframework.stereotype.Component;
import org.springframework.web.servlet.HandlerInterceptor;
import org.springframework.web.servlet.ModelAndView;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import static comlisanshuxue.lisan.Utils.JWTUtils.validateToken;

@Component
public class LoginCheckinterceptor implements HandlerInterceptor {
    @Override
    public boolean preHandle(HttpServletRequest request, HttpServletResponse response, Object handler) throws Exception {

        String url = request.getRequestURI();
        if (url.contains("login") || url.contains("Register") || url.contains("TeacherLogin") || url.contains("TeacherRegister")) {
            return true;
        }

        String jwt = request.getHeader("token");
        if (JWTUtils.validateToken(jwt)&&jwt!=null) return true;
        else {
            response.getWriter().write("not login");
            return false;
        }
    }

    @Override
    public void postHandle(HttpServletRequest request, HttpServletResponse response, Object handler, ModelAndView modelAndView) throws Exception {
    }

    @Override
    public void afterCompletion(HttpServletRequest request, HttpServletResponse response, Object handler, Exception ex) throws Exception {
    }
}
