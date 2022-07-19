package ltd.starlight.mall.service;

import ltd.starlight.mall.entity.AdminUser;

public interface AdminUserService {

    AdminUser login(String userName, String password);

}
