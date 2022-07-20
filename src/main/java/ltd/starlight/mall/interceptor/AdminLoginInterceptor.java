package ltd.starlight.mall.interceptor;

import org.springframework.lang.Nullable;
import org.springframework.stereotype.Component;
import org.springframework.web.servlet.HandlerInterceptor;
import org.springframework.web.servlet.ModelAndView;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

@Component
public class AdminLoginInterceptor implements HandlerInterceptor {
    @Override
    public boolean preHandle(HttpServletRequest request, HttpServletResponse response,
                             Object handler) throws Exception {
        System.out.println("进入拦截器...");
        if (request.getSession().getAttribute("loginUserName") == null) {
            request.getSession().setAttribute("errorMsg","请登录");
            response.sendRedirect("/admin/login");
            System.out.println("未登录，拦截成功...");
            return false;
        } else {
            request.getSession().removeAttribute("errorMsg");
            System.out.println("已登录，验证通过");
            return true;
        }
    }

    @Override
    public void postHandle(HttpServletRequest request, HttpServletResponse response, Object handler,
                           @Nullable ModelAndView modelAndView) throws Exception {
    }

    @Override
    public void afterCompletion(HttpServletRequest request, HttpServletResponse response, Object handler,
                                @Nullable Exception ex) throws Exception {
    }

}
