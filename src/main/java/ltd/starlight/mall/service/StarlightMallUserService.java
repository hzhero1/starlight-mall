package ltd.starlight.mall.service;

import javax.servlet.http.HttpSession;

public interface StarlightMallUserService {

    String register(String loginName, String password);

    String login(String loginName, String passwordMD5, HttpSession httpSession);
}
