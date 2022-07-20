package ltd.starlight.mall.service.Impl;

import ltd.starlight.mall.dao.AdminUserMapper;
import ltd.starlight.mall.entity.AdminUser;
import ltd.starlight.mall.service.AdminUserService;
import ltd.starlight.mall.util.MD5Util;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import javax.annotation.Resource;

@Service
public class AdminUserServiceImpl implements AdminUserService {
    @Resource
    private AdminUserMapper adminUserMapper;

    @Override
    public AdminUser login (String userName, String password){
        String passwordMD5 = MD5Util.MD5Encode(password, "UTF-8");
        return adminUserMapper.login(userName, passwordMD5);
    }

    @Override
    public AdminUser getUserDetailById(Integer userId){
        return adminUserMapper.getUserDetailById(userId);
    }

    @Override
    public Boolean updatePassword(Integer userId, String originalPassword, String newPassword){
        String originalPasswordMD5 = MD5Util.MD5Encode(originalPassword, "UTF-8");
        String newPasswordMD5 = MD5Util.MD5Encode(newPassword, "UTF-8");
        return adminUserMapper.updatePassword(userId, originalPasswordMD5, newPasswordMD5);
    }
}
