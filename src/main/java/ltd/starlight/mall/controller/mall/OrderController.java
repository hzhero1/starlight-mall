package ltd.starlight.mall.controller.mall;

import ltd.starlight.mall.common.Constants;
import ltd.starlight.mall.common.ServiceResultEnum;
import ltd.starlight.mall.common.StarlightMallException;
import ltd.starlight.mall.common.StarlightMallOrderStatusEnum;
import ltd.starlight.mall.controller.vo.StarlightMallOrderDetailVO;
import ltd.starlight.mall.controller.vo.StarlightMallShoppingCartItemVO;
import ltd.starlight.mall.controller.vo.StarlightMallUserVO;
import ltd.starlight.mall.entity.StarlightMallOrder;
import ltd.starlight.mall.service.StarlightMallOrderService;
import ltd.starlight.mall.service.StarlightMallShoppingCartService;
import ltd.starlight.mall.util.PageQueryUtil;
import ltd.starlight.mall.util.Result;
import ltd.starlight.mall.util.ResultGenerator;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.util.CollectionUtils;
import org.springframework.util.StringUtils;
import org.springframework.web.bind.annotation.*;

import javax.annotation.Resource;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpSession;
import java.util.List;
import java.util.Map;

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

        @GetMapping("/orders")
        public String orderListPage(@RequestParam Map<String, Object> params, HttpServletRequest request, HttpSession httpSession) {
                StarlightMallUserVO user = (StarlightMallUserVO) httpSession.getAttribute(Constants.MALL_USER_SESSION_KEY);
                params.put("userId", user.getUserId());
                if (StringUtils.isEmpty(params.get("page"))) {
                        params.put("page", 1);
                }
                params.put("limit", Constants.ORDER_SEARCH_PAGE_LIMIT);
                //封装我的订单数据
                PageQueryUtil pageUtil = new PageQueryUtil(params);
                request.setAttribute("orderPageResult", starlightMallOrderService.getMyOrders(pageUtil));
                request.setAttribute("path", "orders");
                return "mall/my-orders";
        }

        @GetMapping("/selectPayType")
        public String selectPayType(HttpServletRequest request, @RequestParam("orderNo") String orderNo, HttpSession httpSession) {
                StarlightMallUserVO user = (StarlightMallUserVO) httpSession.getAttribute(Constants.MALL_USER_SESSION_KEY);
                StarlightMallOrder starlightMallOrder = starlightMallOrderService.getStarlightMallOrderByOrderNo(orderNo);
                //判断订单userId
                if (!user.getUserId().equals(starlightMallOrder.getUserId())) {
                        StarlightMallException.fail(ServiceResultEnum.NO_PERMISSION_ERROR.getResult());
                }
                //判断订单状态
                if (starlightMallOrder.getOrderStatus().intValue() != StarlightMallOrderStatusEnum.ORDER_PRE_PAY.getOrderStatus()) {
                        StarlightMallException.fail(ServiceResultEnum.ORDER_STATUS_ERROR.getResult());
                }
                request.setAttribute("orderNo", orderNo);
                request.setAttribute("totalPrice", starlightMallOrder.getTotalPrice());
                return "mall/pay-select";
        }

        @GetMapping("/payPage")
        public String payOrder(HttpServletRequest request, @RequestParam("orderNo") String orderNo, HttpSession httpSession, @RequestParam("payType") int payType) {
                StarlightMallUserVO user = (StarlightMallUserVO) httpSession.getAttribute(Constants.MALL_USER_SESSION_KEY);
                StarlightMallOrder starlightMallOrder = starlightMallOrderService.getStarlightMallOrderByOrderNo(orderNo);
                //判断订单userId
                if (!user.getUserId().equals(starlightMallOrder.getUserId())) {
                        StarlightMallException.fail(ServiceResultEnum.NO_PERMISSION_ERROR.getResult());
                }
                //判断订单状态
                if (starlightMallOrder.getOrderStatus().intValue() != StarlightMallOrderStatusEnum.ORDER_PRE_PAY.getOrderStatus()) {
                        StarlightMallException.fail(ServiceResultEnum.ORDER_STATUS_ERROR.getResult());
                }
                request.setAttribute("orderNo", orderNo);
                request.setAttribute("totalPrice", starlightMallOrder.getTotalPrice());
                if (payType == 1) {
                        return "mall/alipay";
                } else {
                        return "mall/wxpay";
                }
        }

        @GetMapping("/paySuccess")
        @ResponseBody
        public Result paySuccess(@RequestParam("orderNo") String orderNo, @RequestParam("payType") int payType) {
                String payResult = starlightMallOrderService.paySuccess(orderNo, payType);
                if (ServiceResultEnum.SUCCESS.getResult().equals(payResult)) {
                        return ResultGenerator.genSuccessResult();
                } else {
                        return ResultGenerator.genFailResult(payResult);
                }
        }

        @PutMapping("/orders/{orderNo}/cancel")
        @ResponseBody
        public Result cancelOrder(@PathVariable("orderNo") String orderNo, HttpSession httpSession) {
                StarlightMallUserVO user = (StarlightMallUserVO) httpSession.getAttribute(Constants.MALL_USER_SESSION_KEY);
                String cancelOrderResult = starlightMallOrderService.cancelOrder(orderNo, user.getUserId());
                if (ServiceResultEnum.SUCCESS.getResult().equals(cancelOrderResult)) {
                        return ResultGenerator.genSuccessResult();
                } else {
                        return ResultGenerator.genFailResult(cancelOrderResult);
                }
        }

        @PutMapping("/orders/{orderNo}/finish")
        @ResponseBody
        public Result finishOrder(@PathVariable("orderNo") String orderNo, HttpSession httpSession) {
                StarlightMallUserVO user = (StarlightMallUserVO) httpSession.getAttribute(Constants.MALL_USER_SESSION_KEY);
                String finishOrderResult = starlightMallOrderService.finishOrder(orderNo, user.getUserId());
                if (ServiceResultEnum.SUCCESS.getResult().equals(finishOrderResult)) {
                        return ResultGenerator.genSuccessResult();
                } else {
                        return ResultGenerator.genFailResult(finishOrderResult);
                }
        }
}
