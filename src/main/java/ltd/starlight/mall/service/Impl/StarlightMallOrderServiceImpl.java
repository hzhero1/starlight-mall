package ltd.starlight.mall.service.Impl;

import ltd.starlight.mall.common.*;
import ltd.starlight.mall.controller.vo.StarlightMallOrderDetailVO;
import ltd.starlight.mall.controller.vo.StarlightMallOrderItemVO;
import ltd.starlight.mall.controller.vo.StarlightMallShoppingCartItemVO;
import ltd.starlight.mall.controller.vo.StarlightMallUserVO;
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
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.util.CollectionUtils;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.Map;
import java.util.function.Function;
import java.util.stream.Collectors;

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

            if (myShoppingCartItem.getGoodsCount() > starlightMallGoodsMap.get(user.getUserId()).getStockNum()) {
                StarlightMallException.fail(ServiceResultEnum.SHOPPING_ITEM_COUNT_ERROR.getResult());
            }
        }
        //删除购物项
        if (!CollectionUtils.isEmpty(itemIdList) && !CollectionUtils.isEmpty(goodsIds) && !CollectionUtils.isEmpty(goodsList)) {
            if (starlightMallShoppingCartItemMapper.deleteBatch(itemIdList) > 0) {
                List<StockNumDTO> stockNumDTOS = BeanUtil.copyList(myShoppingCartItems, StockNumDTO.class);
                if (starlightMallGoodsMapper.updateStockNumber(stockNumDTOS) < 1) {
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
}
