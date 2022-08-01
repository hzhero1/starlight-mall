package ltd.starlight.mall.controller;


import ltd.starlight.mall.service.UserService;
import ltd.starlight.mall.util.PageQueryUtil;
import ltd.starlight.mall.util.PageResult;
import ltd.starlight.mall.util.Result;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.util.StringUtils;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import java.util.Map;

@RestController
@RequestMapping("/users")
public class PageTestController {
    @Autowired
    private UserService userService;

    /**
     * 分页功能测试
     */
    @RequestMapping(value = "/list", method = RequestMethod.GET)
    public Result list(@RequestParam Map<String, Object> params) {
        Result result = new Result();
        if (StringUtils.isEmpty(params.get("page")) || StringUtils.isEmpty(params.get("limit"))) {
            // 返回错误码
            result.setResultCode(500);
            // 错误信息
            result.setMessage("参数异常！");
            return result;
        }
        // 封装查询参数
        PageQueryUtil queryParamList = new PageQueryUtil(params);
        // 查询并封装分页结果集
        PageResult userPage = userService.getUserPage(queryParamList);
        // 返回成功码
        result.setResultCode(200);
        result.setMessage("查询成功");
        // 返回分页数据
        result.setData(userPage);
        return result;
    }
}
