package ltd.starlight.mall.controller.mall;

import ltd.starlight.mall.common.Constants;
import ltd.starlight.mall.common.ServiceResultEnum;
import ltd.starlight.mall.common.StarlightMallException;
import ltd.starlight.mall.controller.vo.StarlightMallShoppingCartItemVO;
import ltd.starlight.mall.controller.vo.StarlightMallUserVO;
import ltd.starlight.mall.entity.StarlightMallShoppingCartItem;
import ltd.starlight.mall.service.StarlightMallShoppingCartService;
import ltd.starlight.mall.util.Result;
import ltd.starlight.mall.util.ResultGenerator;
import org.springframework.lang.UsesSunMisc;
import org.springframework.stereotype.Controller;
import org.springframework.util.CollectionUtils;
import org.springframework.web.bind.annotation.*;

import javax.annotation.Resource;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpSession;
import java.util.List;

@Controller
public class ShoppingCartController {

    @Resource
    StarlightMallShoppingCartService starlightMallShoppingCartService;

    @GetMapping("/shop-cart")
    public String cartListPage(HttpServletRequest request,
                               HttpSession httpSession) {
        StarlightMallUserVO user = (StarlightMallUserVO) httpSession.getAttribute(Constants.MALL_USER_SESSION_KEY);
        int itemsTotal = 0;
        int priceTotal = 0;
        List<StarlightMallShoppingCartItemVO> myShoppingCartItems = starlightMallShoppingCartService.getMyShoppingCartItems(user.getUserId());
        if (!CollectionUtils.isEmpty(myShoppingCartItems)) {
            //购物项总数
            itemsTotal = myShoppingCartItems.stream().mapToInt(StarlightMallShoppingCartItemVO::getGoodsCount).sum();
            if (itemsTotal < 1) {
                StarlightMallException.fail("购物项不能为空");
            }
            //总价
            for (StarlightMallShoppingCartItemVO starlightMallShoppingCartItemVO : myShoppingCartItems) {
                priceTotal += starlightMallShoppingCartItemVO.getGoodsCount() * starlightMallShoppingCartItemVO.getSellingPrice();
            }
            if (priceTotal < 1) {
                StarlightMallException.fail("购物项价格异常");
            }
        }
        request.setAttribute("itemsTotal", itemsTotal);
        request.setAttribute("priceTotal", priceTotal);
        request.setAttribute("myShoppingCartItems", myShoppingCartItems);
        return "mall/cart";
    }

    @PostMapping("/shop-cart")
    @ResponseBody
    public Result addStarlightMallShoppingCartItem(@RequestBody StarlightMallShoppingCartItem starlightMallShoppingCartItem,
                                                 HttpSession httpSession) {
        StarlightMallUserVO user = (StarlightMallUserVO) httpSession.getAttribute(Constants.MALL_USER_SESSION_KEY);
        starlightMallShoppingCartItem.setUserId(user.getUserId());
        String result = starlightMallShoppingCartService.addStarlightMallCartItem(starlightMallShoppingCartItem);
        if (ServiceResultEnum.SUCCESS.getResult().equals(result)) {
            return ResultGenerator.genSuccessResult();
        }
        return ResultGenerator.genFailResult(result);
    }

    @PutMapping("/shop-cart")
    @ResponseBody
    public Result updateStarlightMallShoppingCartItem(@RequestBody StarlightMallShoppingCartItem starlightMallShoppingCartItem,
                                                   HttpSession httpSession) {
        StarlightMallUserVO user = (StarlightMallUserVO) httpSession.getAttribute(Constants.MALL_USER_SESSION_KEY);
        starlightMallShoppingCartItem.setUserId(user.getUserId());
        String updateResult = starlightMallShoppingCartService.updateStarlightMallCartItem(starlightMallShoppingCartItem);
        //修改成功
        if (ServiceResultEnum.SUCCESS.getResult().equals(updateResult)) {
            return ResultGenerator.genSuccessResult();
        }
        //修改失败
        return ResultGenerator.genFailResult(updateResult);
    }

    /**
     * 删除购物项
     */
    @DeleteMapping("/shop-cart/{starlightMallShoppingCartItemId}")
    @ResponseBody
    public Result updateStarlightMallShoppingCartItem(@PathVariable("starlightMallShoppingCartItemId") Long starlightMallShoppingCartItemId,
                                                   HttpSession session) {
        StarlightMallUserVO user = (StarlightMallUserVO) session.getAttribute(Constants.MALL_USER_SESSION_KEY);
        Boolean deleteResult = starlightMallShoppingCartService.deleteById(starlightMallShoppingCartItemId,session );
        //删除成功
        if (deleteResult) {
            return ResultGenerator.genSuccessResult();
        }
        //删除失败
        return ResultGenerator.genFailResult(ServiceResultEnum.OPERATE_ERROR.getResult());
    }
}
