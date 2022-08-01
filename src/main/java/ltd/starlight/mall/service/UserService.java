package ltd.starlight.mall.service;

import ltd.starlight.mall.dao.UserMapper;
import ltd.starlight.mall.entity.User;
import ltd.starlight.mall.util.PageQueryUtil;
import ltd.starlight.mall.util.PageResult;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class UserService {
    @Autowired
    private UserMapper userDao;

    public PageResult getUserPage(PageQueryUtil pageUtil) {
        // 当前页码中的数据列表
        List<User> users = userDao.findUsers(pageUtil);
        // 数据总条数，用于计算分页数据
        int total = userDao.getTotalUser(pageUtil);
        // 分页信息封装
        PageResult pageResult = new PageResult(users, total, pageUtil.getLimit(), pageUtil.getPage());
        return pageResult;
    }
}
