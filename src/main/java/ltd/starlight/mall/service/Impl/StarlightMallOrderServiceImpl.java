package ltd.starlight.mall.service.Impl;

import ltd.starlight.mall.common.*;
import ltd.starlight.mall.controller.vo.*;
import ltd.starlight.mall.dao.StarlightMallGoodsMapper;
import ltd.starlight.mall.dao.StarlightMallOrderItemMapper;
import ltd.starlight.mall.dao.StarlightMallOrderMapper;
import ltd.starlight.mall.dao.StarlightMallShoppingCartItemMapper;
import ltd.starlight.mall.entity.StarlightMallGoods;
import ltd.starlight.mall.entity.StarlightMallOrder;
import ltd.starlight.mall.entity.StarlightMallOrderItem;
import ltd.starlight.mall.entity.StockNumDTO;
import ltd.starlight.mall.service.StarlightMallOrderService;
import ltd.starlight.mall.util.BeanUtil;
import ltd.starlight.mall.util.NumberUtil;
import ltd.starlight.mall.util.PageQueryUtil;
import ltd.starlight.mall.util.PageResult;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.util.CollectionUtils;
import org.springframework.util.StringUtils;

import java.util.*;
import java.util.function.Function;
import java.util.stream.Collectors;

import static java.util.stream.Collectors.groupingBy;

@Service
public class StarlightMallOrderServiceImpl implements StarlightMallOrderService {

    @Autowired
    StarlightMallGoodsMapper starlightMallGoodsMapper;

    @Autowired
    StarlightMallShoppingCartItemMapper starlightMallShoppingCartItemMapper;

    @Autowired
    StarlightMallOrderMapper starlightMallOrderMapper;

    @Autowired
    StarlightMallOrderItemMapper starlightMallOrderItemMapper;

    @Override
    @Transactional
    public String saveOrder(StarlightMallUserVO user, List<StarlightMallShoppingCartItemVO> myShoppingCartItems) {
        List<Long> itemIdList = myShoppingCartItems.stream().map(StarlightMallShoppingCartItemVO::getCartItemId).collect(Collectors.toList());
        List<Long> goodsIds = myShoppingCartItems.stream().map(StarlightMallShoppingCartItemVO::getGoodsId).collect(Collectors.toList());
        List<StarlightMallGoods> goodsList = starlightMallGoodsMapper.selectByPrimaryKeys(goodsIds);
        List<StarlightMallGoods> goodsListNotSelling = goodsList.stream().filter(StarlightMallGoods -> StarlightMallGoods
                .getGoodsSellStatus() != Constants.SELL_STATUS_UP).collect(Collectors.toList());
        if (!CollectionUtils.isEmpty(goodsListNotSelling)) {
            StarlightMallException.fail(goodsListNotSelling.get(0).getGoodsName() + "已下架，无法生成订单");
        }
        Map<Long, StarlightMallGoods> starlightMallGoodsMap = goodsList.stream().collect(Collectors
                .toMap(StarlightMallGoods::getGoodsId, Function.identity(), (entity1, entity2) -> entity1));

        for (StarlightMallShoppingCartItemVO myShoppingCartItem : myShoppingCartItems) {
            if (!starlightMallGoodsMap.containsKey(myShoppingCartItem.getGoodsId())) {
                StarlightMallException.fail(ServiceResultEnum.SHOPPING_ITEM_ERROR.getResult());
            }
            if (myShoppingCartItem.getGoodsCount() > starlightMallGoodsMap.get(myShoppingCartItem.getGoodsId()).getStockNum()) {
                StarlightMallException.fail(ServiceResultEnum.SHOPPING_ITEM_COUNT_ERROR.getResult());
            }
        }
        //删除购物项
        if (!CollectionUtils.isEmpty(itemIdList) && !CollectionUtils.isEmpty(goodsIds) && !CollectionUtils.isEmpty(goodsList)) {
            if (starlightMallShoppingCartItemMapper.deleteBatch(itemIdList) > 0) {
                List<StockNumDTO> stockNumDTOS = BeanUtil.copyList(myShoppingCartItems, StockNumDTO.class);
                if (starlightMallGoodsMapper.updateStockNum(stockNumDTOS) < 1) {
                    StarlightMallException.fail(ServiceResultEnum.SHOPPING_ITEM_COUNT_ERROR.getResult());
                }
                //生成订单号
                String orderNo = NumberUtil.genOrderNo();
                int priceTotal = 0;

                StarlightMallOrder starlightMallOrder = new StarlightMallOrder();
                Date createTime = new Date();
                starlightMallOrder.setOrderNo(orderNo);
                starlightMallOrder.setCreateTime(createTime);
                starlightMallOrder.setUserId(user.getUserId());
                starlightMallOrder.setUserAddress(user.getAddress());
                for (StarlightMallShoppingCartItemVO myShoppingCartItem : myShoppingCartItems) {
                    priceTotal += myShoppingCartItem.getSellingPrice() * myShoppingCartItem.getGoodsCount();
                }
                if (priceTotal < 1) {
                    StarlightMallException.fail(ServiceResultEnum.ORDER_PRICE_ERROR.getResult());
                }
                starlightMallOrder.setTotalPrice(priceTotal);
                String extraInfo = "";
                starlightMallOrder.setExtraInfo(extraInfo);
                if (starlightMallOrderMapper.insertSelective(starlightMallOrder) > 0) {
                    List<StarlightMallOrderItem> starlightMallOrderItems = new ArrayList<>();
                    for (StarlightMallShoppingCartItemVO myShoppingCartItem : myShoppingCartItems) {
                        StarlightMallOrderItem starlightMallOrderItem = new StarlightMallOrderItem();
                        BeanUtil.copyProperties(myShoppingCartItem, starlightMallOrderItem);
                        starlightMallOrderItem.setOrderId(starlightMallOrder.getOrderId());
                        starlightMallOrderItems.add(starlightMallOrderItem);
                    }
                    if (starlightMallOrderItemMapper.insertBatch(starlightMallOrderItems) > 0) {
                        return orderNo;
                    }
                }
            }
        }
        StarlightMallException.fail(ServiceResultEnum.SHOPPING_ITEM_ERROR.getResult());
        return ServiceResultEnum.SHOPPING_ITEM_ERROR.getResult();
    }

    @Override
    public StarlightMallOrderDetailVO getOrderDetailByOrderNo(String orderNo, Long userId) {
        StarlightMallOrder starlightMallOrder = starlightMallOrderMapper.selectByOrderNo(orderNo);
        if (starlightMallOrder == null) {
            StarlightMallException.fail(ServiceResultEnum.ORDER_NOT_EXIST_ERROR.getResult());
        }
        //验证是否是当前userId下的订单，否则报错
        if (!userId.equals(starlightMallOrder.getUserId())) {
            StarlightMallException.fail(ServiceResultEnum.NO_PERMISSION_ERROR.getResult());
        }
        List<StarlightMallOrderItem> orderItems = starlightMallOrderItemMapper.selectByOrderId(starlightMallOrder.getOrderId());
        //获取订单项数据
        if (CollectionUtils.isEmpty(orderItems)) {
            StarlightMallException.fail(ServiceResultEnum.ORDER_ITEM_NOT_EXIST_ERROR.getResult());
        }
        List<StarlightMallOrderItemVO> starlightMallOrderItemVOS = BeanUtil.copyList(orderItems, StarlightMallOrderItemVO.class);
        StarlightMallOrderDetailVO starlightMallOrderDetailVO = new StarlightMallOrderDetailVO();
        BeanUtil.copyProperties(starlightMallOrder, starlightMallOrderDetailVO);
        starlightMallOrderDetailVO.setOrderStatusString(StarlightMallOrderStatusEnum.getStarlightMallOrderStatusEnumByStatus(starlightMallOrderDetailVO.getOrderStatus()).getName());
        starlightMallOrderDetailVO.setPayTypeString(PayTypeEnum.getPayTypeEnumByType(starlightMallOrderDetailVO.getPayType()).getName());
        starlightMallOrderDetailVO.setStarlightMallOrderItemVOS(starlightMallOrderItemVOS);
        return starlightMallOrderDetailVO;
    }

    /**
     * 我的订单列表
     */
    public PageResult getMyOrders(PageQueryUtil pageUtil) {
        int total = starlightMallOrderMapper.getTotalStarlightMallOrders(pageUtil);
        List<StarlightMallOrder> starlightMallOrders = starlightMallOrderMapper.findStarlightMallOrderList(pageUtil);
        List<StarlightMallOrderListVO> orderListVOS = new ArrayList<>();
        if (total > 0) {
            //数据转换 将实体类转成vo
            orderListVOS = BeanUtil.copyList(starlightMallOrders, StarlightMallOrderListVO.class);
            //设置订单状态中文显示值
            for (StarlightMallOrderListVO starlightMallOrderListVO : orderListVOS) {
                starlightMallOrderListVO.setOrderStatusString(StarlightMallOrderStatusEnum.getStarlightMallOrderStatusEnumByStatus(starlightMallOrderListVO.getOrderStatus()).getName());
            }
            List<Long> orderIds = starlightMallOrders.stream().map(StarlightMallOrder::getOrderId).collect(Collectors.toList());
            if (!CollectionUtils.isEmpty(orderIds)) {
                List<StarlightMallOrderItem> orderItems = starlightMallOrderItemMapper.selectByOrderIds(orderIds);
                Map<Long, List<StarlightMallOrderItem>> itemByOrderIdMap = orderItems.stream().collect(Collectors.groupingBy(StarlightMallOrderItem::getOrderId));
                for (StarlightMallOrderListVO starlightMallOrderListVO : orderListVOS) {
                    //封装每个订单列表对象的订单项数据
                    if (itemByOrderIdMap.containsKey(starlightMallOrderListVO.getOrderId())) {
                        List<StarlightMallOrderItem> orderItemListTemp = itemByOrderIdMap.get(starlightMallOrderListVO.getOrderId());
                        //将StarlightMallOrderItem对象列表转换成StarlightMallOrderItemVO对象列表
                        List<StarlightMallOrderItemVO> starlightMallOrderItemVOS = BeanUtil.copyList(orderItemListTemp, StarlightMallOrderItemVO.class);
                        starlightMallOrderListVO.setStarlightMallOrderItemVOS(starlightMallOrderItemVOS);
                    }
                }
            }
        }
        PageResult pageResult = new PageResult(orderListVOS, total, pageUtil.getLimit(), pageUtil.getPage());
        return pageResult;
    }

    @Override
    public StarlightMallOrder getStarlightMallOrderByOrderNo(String orderNo) {
        return starlightMallOrderMapper.selectByOrderNo(orderNo);
    }

    @Override
    public String paySuccess(String orderNo, int payType) {
        StarlightMallOrder starlightMallOrder = starlightMallOrderMapper.selectByOrderNo(orderNo);
        if (starlightMallOrder != null) {
            //订单状态判断 非待支付状态下不进行修改操作
            if (starlightMallOrder.getOrderStatus().intValue() != StarlightMallOrderStatusEnum.ORDER_PRE_PAY.getOrderStatus()) {
                return ServiceResultEnum.ORDER_STATUS_ERROR.getResult();
            }
            starlightMallOrder.setOrderStatus((byte) StarlightMallOrderStatusEnum.ORDER_PAID.getOrderStatus());
            starlightMallOrder.setPayType((byte) payType);
            starlightMallOrder.setPayStatus((byte) PayStatusEnum.PAY_SUCCESS.getPayStatus());
            starlightMallOrder.setPayTime(new Date());
            starlightMallOrder.setUpdateTime(new Date());
            if (starlightMallOrderMapper.updateByPrimaryKeySelective(starlightMallOrder) > 0) {
                return ServiceResultEnum.SUCCESS.getResult();
            } else {
                return ServiceResultEnum.DB_ERROR.getResult();
            }
        }
        return ServiceResultEnum.ORDER_NOT_EXIST_ERROR.getResult();
    }

    @Override
    public PageResult getStarlightMallOrdersPage(PageQueryUtil pageUtil) {
        List<StarlightMallOrder> starlightMallOrders = starlightMallOrderMapper.findStarlightMallOrderList(pageUtil);
        int total = starlightMallOrderMapper.getTotalStarlightMallOrders(pageUtil);
        PageResult pageResult = new PageResult(starlightMallOrders, total, pageUtil.getLimit(), pageUtil.getPage());
        return pageResult;
    }

    @Override
    @Transactional
    public String updateOrderInfo(StarlightMallOrder starlightMallOrder) {
        StarlightMallOrder temp = starlightMallOrderMapper.selectByPrimaryKey(starlightMallOrder.getOrderId());
        //不为空且orderStatus>=0且状态为出库之前可以修改部分信息
        if (temp != null && temp.getOrderStatus() >= 0 && temp.getOrderStatus() < 3) {
            temp.setTotalPrice(starlightMallOrder.getTotalPrice());
            temp.setUserAddress(starlightMallOrder.getUserAddress());
            temp.setUpdateTime(new Date());
            if (starlightMallOrderMapper.updateByPrimaryKeySelective(temp) > 0) {
                return ServiceResultEnum.SUCCESS.getResult();
            }
            return ServiceResultEnum.DB_ERROR.getResult();
        }
        return ServiceResultEnum.DATA_NOT_EXIST.getResult();
    }

    @Override
    public List<StarlightMallOrderItemVO> getOrderItems(Long id) {
        StarlightMallOrder starlightMallOrder = starlightMallOrderMapper.selectByPrimaryKey(id);
        if (starlightMallOrder != null) {
            List<StarlightMallOrderItem> orderItems = starlightMallOrderItemMapper.selectByOrderId(starlightMallOrder.getOrderId());
            //获取订单项数据
            if (!CollectionUtils.isEmpty(orderItems)) {
                List<StarlightMallOrderItemVO> starlightMallOrderItemVOS = BeanUtil.copyList(orderItems, StarlightMallOrderItemVO.class);
                return starlightMallOrderItemVOS;
            }
        }
        return null;
    }

    @Override
    public String checkDone(Long[] ids) {
        //查询所有的订单 判断状态 修改状态和更新时间
        List<StarlightMallOrder> orders = starlightMallOrderMapper.selectByPrimaryKeys(Arrays.asList(ids));
        String errorOrderNos = "";
        if (!CollectionUtils.isEmpty(orders)) {
            for (StarlightMallOrder starlightMallOrder : orders) {
                if (starlightMallOrder.getIsDeleted() == 1) {
                    errorOrderNos += starlightMallOrder.getOrderNo() + " ";
                    continue;
                }
                if (starlightMallOrder.getOrderStatus() != 1) {
                    errorOrderNos += starlightMallOrder.getOrderNo() + " ";
                }
            }
            if (StringUtils.isEmpty(errorOrderNos)) {
                //订单状态正常 可以执行配货完成操作 修改订单状态和更新时间
                if (starlightMallOrderMapper.checkDone(Arrays.asList(ids)) > 0) {
                    return ServiceResultEnum.SUCCESS.getResult();
                } else {
                    return ServiceResultEnum.DB_ERROR.getResult();
                }
            } else {
                //订单此时不可执行出库操作
                if (errorOrderNos.length() > 0 && errorOrderNos.length() < 100) {
                    return errorOrderNos + "订单的状态不是支付成功无法执行出库操作";
                } else {
                    return "你选择了太多状态不是支付成功的订单，无法执行配货完成操作";
                }
            }
        }
        //未查询到数据 返回错误提示
        return ServiceResultEnum.DATA_NOT_EXIST.getResult();
    }

    @Override
    public String checkOut(Long[] ids) {
        //查询所有的订单 判断状态 修改状态和更新时间
        List<StarlightMallOrder> orders = starlightMallOrderMapper.selectByPrimaryKeys(Arrays.asList(ids));
        String errorOrderNos = "";
        if (!CollectionUtils.isEmpty(orders)) {
            for (StarlightMallOrder starlightMallOrder : orders) {
                if (starlightMallOrder.getIsDeleted() == 1) {
                    errorOrderNos += starlightMallOrder.getOrderNo() + " ";
                    continue;
                }
                if (starlightMallOrder.getOrderStatus() != 1 && starlightMallOrder.getOrderStatus() != 2) {
                    errorOrderNos += starlightMallOrder.getOrderNo() + " ";
                }
            }
            if (StringUtils.isEmpty(errorOrderNos)) {
                //订单状态正常 可以执行出库操作 修改订单状态和更新时间
                if (starlightMallOrderMapper.checkOut(Arrays.asList(ids)) > 0) {
                    return ServiceResultEnum.SUCCESS.getResult();
                } else {
                    return ServiceResultEnum.DB_ERROR.getResult();
                }
            } else {
                //订单此时不可执行出库操作
                if (errorOrderNos.length() > 0 && errorOrderNos.length() < 100) {
                    return errorOrderNos + "订单的状态不是支付成功或配货完成无法执行出库操作";
                } else {
                    return "你选择了太多状态不是支付成功或配货完成的订单，无法执行出库操作";
                }
            }
        }
        //未查询到数据 返回错误提示
        return ServiceResultEnum.DATA_NOT_EXIST.getResult();
    }

    @Override
    public String closeOrder(Long[] ids) {
        //查询所有的订单 判断状态 修改状态和更新时间
        List<StarlightMallOrder> orders = starlightMallOrderMapper.selectByPrimaryKeys(Arrays.asList(ids));
        String errorOrderNos = "";
        if (!CollectionUtils.isEmpty(orders)) {
            for (StarlightMallOrder starlightMallOrder : orders) {
                // isDeleted=1 一定为已关闭订单
                if (starlightMallOrder.getIsDeleted() == 1) {
                    errorOrderNos += starlightMallOrder.getOrderNo() + " ";
                    continue;
                }
                //已关闭或者已完成无法关闭订单
                if (starlightMallOrder.getOrderStatus() == 4 || starlightMallOrder.getOrderStatus() < 0) {
                    errorOrderNos += starlightMallOrder.getOrderNo() + " ";
                }
            }
            if (StringUtils.isEmpty(errorOrderNos)) {
                //订单状态正常 可以执行关闭操作 修改订单状态和更新时间
                if (starlightMallOrderMapper.closeOrder(Arrays.asList(ids), StarlightMallOrderStatusEnum.ORDER_CLOSED_BY_JUDGE.getOrderStatus()) > 0) {
                    return ServiceResultEnum.SUCCESS.getResult();
                } else {
                    return ServiceResultEnum.DB_ERROR.getResult();
                }
            } else {
                //订单此时不可执行关闭操作
                if (errorOrderNos.length() > 0 && errorOrderNos.length() < 100) {
                    return errorOrderNos + "订单不能执行关闭操作";
                } else {
                    return "你选择的订单不能执行关闭操作";
                }
            }
        }
        //未查询到数据 返回错误提示
        return ServiceResultEnum.DATA_NOT_EXIST.getResult();
    }

    @Override
    public String cancelOrder(String orderNo, Long userId) {
        StarlightMallOrder starlightMallOrder = starlightMallOrderMapper.selectByOrderNo(orderNo);
        if (starlightMallOrder != null) {
            //验证是否是当前userId下的订单，否则报错
            if (!userId.equals(starlightMallOrder.getUserId())) {
                StarlightMallException.fail(ServiceResultEnum.NO_PERMISSION_ERROR.getResult());
            }
            //订单状态判断
            if (starlightMallOrder.getOrderStatus().intValue() == StarlightMallOrderStatusEnum.ORDER_SUCCESS.getOrderStatus()
                    || starlightMallOrder.getOrderStatus().intValue() == StarlightMallOrderStatusEnum.ORDER_CLOSED_BY_MALLUSER.getOrderStatus()
                    || starlightMallOrder.getOrderStatus().intValue() == StarlightMallOrderStatusEnum.ORDER_CLOSED_BY_EXPIRED.getOrderStatus()
                    || starlightMallOrder.getOrderStatus().intValue() == StarlightMallOrderStatusEnum.ORDER_CLOSED_BY_JUDGE.getOrderStatus()) {
                return ServiceResultEnum.ORDER_STATUS_ERROR.getResult();
            }
            if (starlightMallOrderMapper.closeOrder(Collections.singletonList(starlightMallOrder.getOrderId()), StarlightMallOrderStatusEnum.ORDER_CLOSED_BY_MALLUSER.getOrderStatus()) > 0) {
                return ServiceResultEnum.SUCCESS.getResult();
            } else {
                return ServiceResultEnum.DB_ERROR.getResult();
            }
        }
        return ServiceResultEnum.ORDER_NOT_EXIST_ERROR.getResult();
    }

    @Override
    public String finishOrder(String orderNo, Long userId) {
        StarlightMallOrder starlightMallOrder = starlightMallOrderMapper.selectByOrderNo(orderNo);
        if (starlightMallOrder != null) {
            //验证是否是当前userId下的订单，否则报错
            if (!userId.equals(starlightMallOrder.getUserId())) {
                return ServiceResultEnum.NO_PERMISSION_ERROR.getResult();
            }
            //订单状态判断 非出库状态下不进行修改操作
            if (starlightMallOrder.getOrderStatus().intValue() != StarlightMallOrderStatusEnum.ORDER_EXPRESS.getOrderStatus()) {
                return ServiceResultEnum.ORDER_STATUS_ERROR.getResult();
            }
            starlightMallOrder.setOrderStatus((byte) StarlightMallOrderStatusEnum.ORDER_SUCCESS.getOrderStatus());
            starlightMallOrder.setUpdateTime(new Date());
            if (starlightMallOrderMapper.updateByPrimaryKeySelective(starlightMallOrder) > 0) {
                return ServiceResultEnum.SUCCESS.getResult();
            } else {
                return ServiceResultEnum.DB_ERROR.getResult();
            }
        }
        return ServiceResultEnum.ORDER_NOT_EXIST_ERROR.getResult();
    }
}
