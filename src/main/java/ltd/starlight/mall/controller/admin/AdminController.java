package ltd.starlight.mall.controller.admin;

import ltd.starlight.mall.entity.AdminUser;
import ltd.starlight.mall.service.AdminUserService;
import ltd.starlight.mall.service.Impl.AdminUserServiceImpl;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.thymeleaf.util.StringUtils;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpSession;

@Controller
@RequestMapping("/admin")
public class AdminController {
    @Autowired
    private AdminUserServiceImpl adminUserService;

    @GetMapping("/index")
    public String index(HttpServletRequest request, HttpSession session) {
        request.setAttribute("path", "index");
        return "admin/index";
    }

    @GetMapping({"/login"})
    public String login() {
        return "admin/login";
    }

    @PostMapping("/login")
    public String login(@RequestParam("userName") String userName,
                        @RequestParam("password") String password,
                        @RequestParam("verifyCode") String verifyCode, HttpSession session, HttpServletRequest request) {
        if(StringUtils.isEmpty(verifyCode)){
            session.setAttribute("errorMsg", "验证码不能为空");
            return "admin/login";
        }
        if(StringUtils.isEmpty(userName) && StringUtils.isEmpty(password)){
            session.setAttribute("errorMsg", "用户名和密码不能为空");
            return "admin/login";
        }

        String captchaCode = session.getAttribute("verifyCode").toString();
        if(!verifyCode.equals(captchaCode)){
            session.setAttribute("errorMsg", "验证码输入错误");
            return "admin/login";
        }
        AdminUser adminUser = adminUserService.login(userName, password);
        if(adminUser!=null){
            session.setAttribute("loginUser", adminUser.getLoginUserName());
            session.setAttribute("loginUserId", adminUser.getAdminUserId());
            return "redirect:/admin/index";
        } else session.setAttribute("errorMsg", "用户名或密码错误，请重试");
        return "admin/login";
    }
}
