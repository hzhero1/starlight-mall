package ltd.starlight.mall.dao;

import ltd.starlight.mall.entity.MallUser;
import ltd.starlight.mall.util.PageQueryUtil;
import org.apache.ibatis.annotations.Param;

import java.util.List;

public interface MallUserMapper {

    int insertSelective(MallUser record);

    MallUser selectByLoginName(String loginName);

    MallUser selectByLoginNameAndPasswd(@Param("loginName") String loginName, @Param("password") String passwd);

    MallUser selectByPrimaryKey(Long userId);

    int updateByPrimaryKeySelective(MallUser record);

    List<MallUser> findMallUserList(PageQueryUtil pageUtil);

    int getTotalMallUsers(PageQueryUtil pageUtil);

    int lockUserBatch(@Param("ids") Integer[] ids, @Param("lockStatus") int lockStatus);
}
