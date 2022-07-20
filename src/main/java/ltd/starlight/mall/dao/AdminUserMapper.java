package ltd.starlight.mall.dao;

import ltd.starlight.mall.entity.AdminUser;
import org.apache.ibatis.annotations.Param;
import org.springframework.boot.autoconfigure.kafka.KafkaProperties;

public interface AdminUserMapper {

    AdminUser login(@Param("userName") String userName, @Param("password") String password);

    AdminUser getUserDetailById(@Param("userId") Integer userId);

    Boolean updatePassword(@Param("userId") Integer userId, @Param("originalPassword") String originalPassword,
                           @Param("newPassword") String newPassword);

    Boolean updateName(@Param("userId") Integer userId, @Param("loginUserName") String loginUserName,
                       @Param("nickName") String nickName);
}
