package ltd.starlight.mall.dao;

import ltd.starlight.mall.entity.User;
import ltd.starlight.mall.util.PageQueryUtil;

import java.util.List;

public interface UserMapper {

    /**
     * 返回分页数据列表
     *
     * @param pageUtil
     * @return
     */
    List<User> findUsers(PageQueryUtil pageUtil);

    /**
     * 返回数据总数
     *
     * @param pageUtil
     * @return
     */
    int getTotalUser(PageQueryUtil pageUtil);
}
