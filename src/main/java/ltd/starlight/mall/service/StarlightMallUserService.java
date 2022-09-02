package ltd.starlight.mall.service;

import ltd.starlight.mall.controller.vo.StarlightMallUserVO;
import ltd.starlight.mall.entity.MallUser;
import ltd.starlight.mall.util.PageQueryUtil;
import ltd.starlight.mall.util.PageResult;

import javax.servlet.http.HttpSession;

public interface StarlightMallUserService {

    PageResult getStarlightMallUsersPage(PageQueryUtil pageUtil);

    String register(String loginName, String password);

    String login(String loginName, String passwordMD5, HttpSession httpSession);

    StarlightMallUserVO updateUserInfo(MallUser mallUser, HttpSession httpSession);

    Boolean lockUsers(Integer[] ids, int lockStatus);
}
