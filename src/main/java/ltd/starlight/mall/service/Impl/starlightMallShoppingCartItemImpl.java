package ltd.starlight.mall.service.Impl;

import ltd.starlight.mall.common.Constants;
import ltd.starlight.mall.common.ServiceResultEnum;
import ltd.starlight.mall.controller.vo.StarlightMallShoppingCartItemVO;
import ltd.starlight.mall.controller.vo.StarlightMallUserVO;
import ltd.starlight.mall.dao.StarlightMallGoodsMapper;
import ltd.starlight.mall.dao.StarlightMallShoppingCartItemMapper;
import ltd.starlight.mall.entity.StarlightMallGoods;
import ltd.starlight.mall.entity.StarlightMallShoppingCartItem;
import ltd.starlight.mall.service.StarlightMallShoppingCartService;
import ltd.starlight.mall.util.BeanUtil;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.util.CollectionUtils;

import javax.servlet.http.HttpSession;
import java.util.*;
import java.util.function.Function;
import java.util.stream.Collectors;

@Service
public class starlightMallShoppingCartItemImpl implements StarlightMallShoppingCartService {

    @Autowired
    private StarlightMallShoppingCartItemMapper starlightMallShoppingCartItemMapper;

    @Autowired
    private StarlightMallGoodsMapper starlightMallGoodsMapper;

    @Override
    public String addStarlightMallCartItem(StarlightMallShoppingCartItem starlightMallShoppingCartItem) {
        StarlightMallShoppingCartItem temp = starlightMallShoppingCartItemMapper.selectByUserIdAndGoodsId(starlightMallShoppingCartItem
                .getUserId(), starlightMallShoppingCartItem.getGoodsId());
        if (temp != null) {
            return "购物车中已存在该物品";
        }
        StarlightMallGoods starlightMallGoods = starlightMallGoodsMapper.selectByPrimaryKey(starlightMallShoppingCartItem.getGoodsId());
        if (starlightMallGoods == null) {
            return ServiceResultEnum.GOODS_NOT_EXIST.getResult();
        }
        int totalItem = starlightMallShoppingCartItemMapper.selectCountByUserId(starlightMallShoppingCartItem.getUserId()) + 1;
        if (starlightMallShoppingCartItem.getGoodsCount() > Constants.SHOPPING_CART_ITEM_LIMIT_NUMBER) {
            return ServiceResultEnum.SHOPPING_CART_ITEM_LIMIT_NUMBER_ERROR.getResult();
        }
        if (totalItem > Constants.SHOPPING_CART_ITEM_TOTAL_NUMBER) {
            return ServiceResultEnum.SHOPPING_CART_ITEM_TOTAL_NUMBER_ERROR.getResult();
        }
        if (starlightMallShoppingCartItemMapper.insertSelective(starlightMallShoppingCartItem) > 0) {
            return ServiceResultEnum.SUCCESS.getResult();
        }
        return ServiceResultEnum.DB_ERROR.getResult();
    }

    @Override
    public List<StarlightMallShoppingCartItemVO> getMyShoppingCartItems(Long starlightMallUserId) {
        List<StarlightMallShoppingCartItemVO> starlightMallShoppingCartItemVOS = new ArrayList<>();
        List<StarlightMallShoppingCartItem> starlightMallShoppingCartItems = starlightMallShoppingCartItemMapper
                .selectByUserId(starlightMallUserId, Constants.SHOPPING_CART_ITEM_TOTAL_NUMBER);
        if (!CollectionUtils.isEmpty(starlightMallShoppingCartItems)) {
            //查询商品信息并做数据转换
            List<Long> starlightMallGoodsIds = starlightMallShoppingCartItems.stream().map(StarlightMallShoppingCartItem::getGoodsId).collect(Collectors.toList());
            List<StarlightMallGoods> starlightMallGoods = starlightMallGoodsMapper.selectByPrimaryKeys(starlightMallGoodsIds);
            Map<Long, StarlightMallGoods> starlightMallGoodsMap = new HashMap<>();
            if (!CollectionUtils.isEmpty(starlightMallGoods)) {
                starlightMallGoodsMap = starlightMallGoods.stream().collect(Collectors.toMap(StarlightMallGoods::getGoodsId, Function.identity(), (entity1, entity2) -> entity1));
            }
            for (StarlightMallShoppingCartItem starlightMallShoppingCartItem : starlightMallShoppingCartItems) {
                StarlightMallShoppingCartItemVO starlightMallShoppingCartItemVO = new StarlightMallShoppingCartItemVO();
                BeanUtil.copyProperties(starlightMallShoppingCartItem, starlightMallShoppingCartItemVO);
                if (starlightMallGoodsMap.containsKey(starlightMallShoppingCartItem.getGoodsId())) {
                    StarlightMallGoods starlightMallGoodsTemp = starlightMallGoodsMap.get(starlightMallShoppingCartItem.getGoodsId());
                    starlightMallShoppingCartItemVO.setGoodsCoverImg(starlightMallGoodsTemp.getGoodsCoverImg());
                    String goodsName = starlightMallGoodsTemp.getGoodsName();
                    // 字符串过长导致文字超出的问题
                    if (goodsName.length() > 28) {
                        goodsName = goodsName.substring(0, 28) + "...";
                    }
                    starlightMallShoppingCartItemVO.setGoodsName(goodsName);
                    starlightMallShoppingCartItemVO.setSellingPrice(starlightMallGoodsTemp.getSellingPrice());
                    starlightMallShoppingCartItemVOS.add(starlightMallShoppingCartItemVO);
                }
            }
        }
        return starlightMallShoppingCartItemVOS;
    }

    @Override
    public String updateStarlightMallCartItem(StarlightMallShoppingCartItem starlightMallShoppingCartItem) {
        StarlightMallShoppingCartItem starlightMallShoppingCartItemUpdate = starlightMallShoppingCartItemMapper
                .selectByPrimaryKey(starlightMallShoppingCartItem.getCartItemId());
        if (starlightMallShoppingCartItemUpdate == null) {
            return ServiceResultEnum.DATA_NOT_EXIST.getResult();
        }
        //超出单个商品的最大数量
        if (starlightMallShoppingCartItem.getGoodsCount() > Constants.SHOPPING_CART_ITEM_LIMIT_NUMBER) {
            return ServiceResultEnum.SHOPPING_CART_ITEM_LIMIT_NUMBER_ERROR.getResult();
        }
        starlightMallShoppingCartItemUpdate.setGoodsCount(starlightMallShoppingCartItem.getGoodsCount());
        starlightMallShoppingCartItemUpdate.setUpdateTime(new Date());
        //修改记录
        if (starlightMallShoppingCartItemMapper.updateByPrimaryKeySelective(starlightMallShoppingCartItemUpdate) > 0) {
            return ServiceResultEnum.SUCCESS.getResult();
        }
        return ServiceResultEnum.DB_ERROR.getResult();
    }

    @Override
    public Boolean deleteById(Long starlightMallShoppingCartItemId, HttpSession session) {
        //todo userId不同不能删除
        StarlightMallUserVO starlightMallUserVO = (StarlightMallUserVO) session.getAttribute(Constants.MALL_USER_SESSION_KEY);
        if (!starlightMallUserVO.getUserId().equals(starlightMallShoppingCartItemMapper.selectByPrimaryKey(starlightMallShoppingCartItemId).getUserId())){
            return false;
        }
        return starlightMallShoppingCartItemMapper.deleteByPrimaryKey(starlightMallShoppingCartItemId) > 0;
    }
}
