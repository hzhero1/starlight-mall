package ltd.starlight.mall.controller.mall;

import ltd.starlight.mall.common.Constants;
import ltd.starlight.mall.common.ServiceResultEnum;
import ltd.starlight.mall.common.StarlightMallException;
import ltd.starlight.mall.controller.vo.StarlightMallOrderDetailVO;
import ltd.starlight.mall.controller.vo.StarlightMallShoppingCartItemVO;
import ltd.starlight.mall.controller.vo.StarlightMallUserVO;
import ltd.starlight.mall.service.StarlightMallOrderService;
import ltd.starlight.mall.service.StarlightMallShoppingCartService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.util.CollectionUtils;
import org.springframework.util.StringUtils;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;

import javax.annotation.Resource;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpSession;
import java.util.List;

@Controller
public class OrderController {

        @Resource
        StarlightMallShoppingCartService starlightMallShoppingCartService;

        @Resource
        StarlightMallOrderService starlightMallOrderService;

        @GetMapping("/saveOrder")
        public String saveOrder(HttpSession httpSession) {
                StarlightMallUserVO user = (StarlightMallUserVO) httpSession.getAttribute(Constants.MALL_USER_SESSION_KEY);
                List<StarlightMallShoppingCartItemVO> myShoppingCartItems = starlightMallShoppingCartService.getMyShoppingCartItems(user.getUserId());
                if (StringUtils.isEmpty(user.getAddress().trim())) {
                        //无收货地址
                        StarlightMallException.fail(ServiceResultEnum.NULL_ADDRESS_ERROR.getResult());
                }
                if (CollectionUtils.isEmpty(myShoppingCartItems)) {
                        //购物车中无数据则跳转至错误页
                        StarlightMallException.fail(ServiceResultEnum.SHOPPING_ITEM_ERROR.getResult());
                }
                //保存订单并返回订单号
                String saveOrderResult = starlightMallOrderService.saveOrder(user, myShoppingCartItems);
                //跳转到订单详情页
                return "redirect:/orders/" + saveOrderResult;
        }

        @GetMapping("/orders/{orderNo}")
        public String orderDetailPage(HttpServletRequest request, @PathVariable("orderNo") String orderNo, HttpSession httpSession) {
                StarlightMallUserVO user = (StarlightMallUserVO) httpSession.getAttribute(Constants.MALL_USER_SESSION_KEY);
                StarlightMallOrderDetailVO orderDetailVO = starlightMallOrderService.getOrderDetailByOrderNo(orderNo, user.getUserId());
                if (orderDetailVO == null) {
                        return "error/error_5xx";
                }
                request.setAttribute("orderDetailVO", orderDetailVO);
                return "mall/order-detail";
        }
}
