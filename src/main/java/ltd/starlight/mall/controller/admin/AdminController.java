package ltd.starlight.mall.controller.admin;

import ltd.starlight.mall.entity.AdminUser;
import ltd.starlight.mall.service.AdminUserService;
import ltd.starlight.mall.service.Impl.AdminUserServiceImpl;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.*;
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
        if (StringUtils.isEmpty(verifyCode)) {
            session.setAttribute("errorMsg", "验证码不能为空");
            return "admin/login";
        }
        if (StringUtils.isEmpty(userName) && StringUtils.isEmpty(password)) {
            session.setAttribute("errorMsg", "用户名和密码不能为空");
            return "admin/login";
        }

        String captchaCode = session.getAttribute("verifyCode").toString();
        if (!verifyCode.equals(captchaCode)) {
            session.setAttribute("errorMsg", "验证码输入错误");
            return "admin/login";
        }
        AdminUser adminUser = adminUserService.login(userName, password);
        if (adminUser != null) {
            session.setAttribute("loginUserName", adminUser.getLoginUserName());
            session.setAttribute("adminUserId", adminUser.getAdminUserId());
            return "redirect:/admin/index";
        } else session.setAttribute("errorMsg", "用户名或密码错误，请重试");
        return "admin/login";
    }

    @GetMapping("/profile")
    public String profile(HttpServletRequest request) {
        Integer userId = (int) request.getSession().getAttribute("adminUserId");
        AdminUser adminUser = adminUserService.getUserDetailById(userId);
        if (adminUser == null) return "admin/login";
        request.setAttribute("path", "profile");
        request.setAttribute("loginUserName", adminUser.getLoginUserName());
        request.setAttribute("nickName", adminUser.getNickName());
        return "admin/profile";
    }

    @PostMapping("/profile/password")
    @ResponseBody
    public String passwordUpdate(HttpServletRequest request, @RequestParam("originalPassword") String originalPassword,
                                 @RequestParam("newPassword") String newPassword){
        if(StringUtils.isEmpty(originalPassword) || StringUtils.isEmpty(newPassword)){
            return "密码不能为空";
        }
        Integer userId = (int) request.getSession().getAttribute("adminUserId");
        if(adminUserService.updatePassword(userId, originalPassword, newPassword)!=null){
            request.getSession().removeAttribute("loginUserName");
            request.getSession().removeAttribute("adminUserId");
            request.getSession().removeAttribute("errorMsg");
            return "success";
        }
        return "修改失败";
    }

}
