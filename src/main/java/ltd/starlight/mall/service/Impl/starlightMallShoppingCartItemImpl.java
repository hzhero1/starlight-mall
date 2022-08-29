package ltd.starlight.mall.service.Impl;

import ltd.starlight.mall.common.Constants;
import ltd.starlight.mall.common.ServiceResultEnum;
import ltd.starlight.mall.dao.StarlightMallGoodsMapper;
import ltd.starlight.mall.dao.StarlightMallShoppingCartItemMapper;
import ltd.starlight.mall.entity.StarlightMallGoods;
import ltd.starlight.mall.entity.StarlightMallShoppingCartItem;
import ltd.starlight.mall.service.StarlightMallShoppingCartService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

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
}
