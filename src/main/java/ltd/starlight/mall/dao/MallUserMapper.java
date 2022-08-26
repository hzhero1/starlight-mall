package ltd.starlight.mall.dao;

import ltd.starlight.mall.entity.MallUser;
import org.apache.ibatis.annotations.Param;

public interface MallUserMapper {

    int insertSelective(MallUser record);

    MallUser selectByLoginName(String loginName);

    MallUser selectByLoginNameAndPasswd(@Param("loginName") String loginName, @Param("password") String passwd);
}
