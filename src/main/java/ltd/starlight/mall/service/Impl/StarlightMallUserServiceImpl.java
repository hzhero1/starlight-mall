package ltd.starlight.mall.service.Impl;


import ltd.starlight.mall.common.Constants;
import ltd.starlight.mall.common.ServiceResultEnum;
import ltd.starlight.mall.dao.MallUserMapper;
import ltd.starlight.mall.dao.StarlightMallShoppingCartItemMapper;
import ltd.starlight.mall.entity.MallUser;
import ltd.starlight.mall.service.StarlightMallUserService;
import ltd.starlight.mall.util.*;
import ltd.starlight.mall.controller.vo.StarlightMallUserVO;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.thymeleaf.util.StringUtils;

import javax.servlet.http.HttpSession;
import java.util.List;

@Service
public class StarlightMallUserServiceImpl implements StarlightMallUserService {

    @Autowired
    MallUserMapper mallUserMapper;

    @Autowired
    StarlightMallShoppingCartItemMapper starlightMallShoppingCartItemMapper;

    @Override
    public String register(String loginName, String password) {
        if (mallUserMapper.selectByLoginName(loginName) != null) {
            return ServiceResultEnum.SAME_LOGIN_NAME_EXIST.getResult();
        }
        MallUser registerUser = new MallUser();
        registerUser.setLoginName(loginName);
        registerUser.setNickName(loginName);
        String passwordMD5 = MD5Util.MD5Encode(password, "UTF-8");
        registerUser.setPasswordMd5(passwordMD5);
        if (mallUserMapper.insertSelective(registerUser) > 0) {
            return ServiceResultEnum.SUCCESS.getResult();
        }
        return ServiceResultEnum.DB_ERROR.getResult();
    }

    @Override
    public String login(String loginName, String passwordMD5, HttpSession httpSession) {
        MallUser user = mallUserMapper.selectByLoginNameAndPasswd(loginName, passwordMD5);
        if (user != null && httpSession != null) {
            if (user.getLockedFlag() == 1) {
                return ServiceResultEnum.LOGIN_USER_LOCKED.getResult();
            }
            //昵称太长 影响页面展示
            if (user.getNickName() != null && user.getNickName().length() > 7) {
                String tempNickName = user.getNickName().substring(0, 7) + "..";
                user.setNickName(tempNickName);
            }
            StarlightMallUserVO starlightMallUserVO = new StarlightMallUserVO();
            BeanUtil.copyProperties(user, starlightMallUserVO);
            starlightMallUserVO.setShopCartItemCount(starlightMallShoppingCartItemMapper.selectCountByUserId(user.getUserId()));
            httpSession.setAttribute(Constants.MALL_USER_SESSION_KEY, starlightMallUserVO);
            return ServiceResultEnum.SUCCESS.getResult();
        }
        return ServiceResultEnum.LOGIN_ERROR.getResult();
    }

    @Override
    public StarlightMallUserVO updateUserInfo(MallUser mallUser, HttpSession httpSession) {
        StarlightMallUserVO userTemp = (StarlightMallUserVO) httpSession.getAttribute(Constants.MALL_USER_SESSION_KEY);
        MallUser userFromDB = mallUserMapper.selectByPrimaryKey(userTemp.getUserId());
        if (userFromDB != null) {
            if (!StringUtils.isEmpty(mallUser.getNickName())) {
                userFromDB.setNickName(StarlightMallUtils.cleanString(mallUser.getNickName()));
            }
            if (!StringUtils.isEmpty(mallUser.getAddress())) {
                userFromDB.setAddress(StarlightMallUtils.cleanString(mallUser.getAddress()));
            }
            if (!StringUtils.isEmpty(mallUser.getIntroduceSign())) {
                userFromDB.setIntroduceSign(StarlightMallUtils.cleanString(mallUser.getIntroduceSign()));
            }
            if (mallUserMapper.updateByPrimaryKeySelective(userFromDB) > 0) {
                StarlightMallUserVO starlightMallUserVO = new StarlightMallUserVO();
                userFromDB = mallUserMapper.selectByPrimaryKey(mallUser.getUserId());
                BeanUtil.copyProperties(userFromDB, starlightMallUserVO);
                httpSession.setAttribute(Constants.MALL_USER_SESSION_KEY, starlightMallUserVO);
                return starlightMallUserVO;
            }
        }
        return null;
    }

    @Override
    public PageResult getStarlightMallUsersPage(PageQueryUtil pageUtil) {
        List<MallUser> mallUsers = mallUserMapper.findMallUserList(pageUtil);
        int total = mallUserMapper.getTotalMallUsers(pageUtil);
        PageResult pageResult = new PageResult(mallUsers, total, pageUtil.getLimit(), pageUtil.getPage());
        return pageResult;
    }

    @Override
    public Boolean lockUsers(Integer[] ids, int lockStatus) {
        if (ids.length < 1) {
            return false;
        }
        return mallUserMapper.lockUserBatch(ids, lockStatus) > 0;
    }
}
