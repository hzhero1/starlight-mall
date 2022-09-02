package ltd.starlight.mall.service;

import ltd.starlight.mall.controller.vo.StarlightMallOrderDetailVO;
import ltd.starlight.mall.controller.vo.StarlightMallOrderItemVO;
import ltd.starlight.mall.controller.vo.StarlightMallShoppingCartItemVO;
import ltd.starlight.mall.controller.vo.StarlightMallUserVO;
import ltd.starlight.mall.entity.StarlightMallOrder;
import ltd.starlight.mall.util.PageQueryUtil;
import ltd.starlight.mall.util.PageResult;

import java.util.List;

public interface StarlightMallOrderService {
    /**
     * 后台分页
     *
     * @param pageUtil
     * @return
     */
    PageResult getStarlightMallOrdersPage(PageQueryUtil pageUtil);

    /**
     * 订单信息修改
     *
     * @param starlightMallOrder
     * @return
     */
    String updateOrderInfo(StarlightMallOrder starlightMallOrder);

    /**
     * 配货
     *
     * @param ids
     * @return
     */
    String checkDone(Long[] ids);

    /**
     * 出库
     *
     * @param ids
     * @return
     */
    String checkOut(Long[] ids);

    /**
     * 关闭订单
     *
     * @param ids
     * @return
     */
    String closeOrder(Long[] ids);

    /**
     * 保存订单
     *
     * @param user
     * @param myShoppingCartItems
     * @return
     */
    String saveOrder(StarlightMallUserVO user, List<StarlightMallShoppingCartItemVO> myShoppingCartItems);

    /**
     * 获取订单详情
     *
     * @param orderNo
     * @param userId
     * @return
     */
    StarlightMallOrderDetailVO getOrderDetailByOrderNo(String orderNo, Long userId);

    /**
     * 获取订单详情
     *
     * @param orderNo
     * @return
     */
    StarlightMallOrder getStarlightMallOrderByOrderNo(String orderNo);

    /**
     * 我的订单列表
     *
     * @param pageUtil
     * @return
     */
    PageResult getMyOrders(PageQueryUtil pageUtil);

    /**
     * 手动取消订单
     *
     * @param orderNo
     * @param userId
     * @return
     */
    String cancelOrder(String orderNo, Long userId);

    /**
     * 确认收货
     *
     * @param orderNo
     * @param userId
     * @return
     */
    String finishOrder(String orderNo, Long userId);

    String paySuccess(String orderNo, int payType);

    List<StarlightMallOrderItemVO> getOrderItems(Long id);
}
