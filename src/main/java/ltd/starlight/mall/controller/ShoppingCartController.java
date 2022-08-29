package ltd.starlight.mall.controller;

import ltd.starlight.mall.common.Constants;
import ltd.starlight.mall.common.ServiceResultEnum;
import ltd.starlight.mall.entity.StarlightMallShoppingCartItem;
import ltd.starlight.mall.service.StarlightMallShoppingCartService;
import ltd.starlight.mall.util.Result;
import ltd.starlight.mall.util.ResultGenerator;
import ltd.starlight.mall.controller.vo.StarlightMallUserVO;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.ResponseBody;

import javax.annotation.Resource;
import javax.servlet.http.HttpSession;

@Controller
public class ShoppingCartController {

    @Resource
    private StarlightMallShoppingCartService starlightMallShoppingCartService;

    @PostMapping("/shop-cart")
    @ResponseBody
    public Result addStarlightMallShoppingCartItem(@RequestBody StarlightMallShoppingCartItem starlightMallShoppingCartItem,
                                                 HttpSession httpSession) {
        StarlightMallUserVO user = (StarlightMallUserVO) httpSession.getAttribute(Constants.MALL_USER_SESSION_KEY);
        starlightMallShoppingCartItem.setUserId(user.getUserId());
        String saveResult = starlightMallShoppingCartService.addStarlightMallCartItem(starlightMallShoppingCartItem);
        //添加成功
        if (ServiceResultEnum.SUCCESS.getResult().equals(saveResult)) {
            return ResultGenerator.genSuccessResult();
        }
        //添加失败
        return ResultGenerator.genFailResult(saveResult);
    }

}
