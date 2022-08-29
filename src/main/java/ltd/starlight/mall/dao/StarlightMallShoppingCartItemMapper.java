package ltd.starlight.mall.dao;

import ltd.starlight.mall.entity.StarlightMallShoppingCartItem;
import org.apache.ibatis.annotations.Param;

import java.util.List;

public interface StarlightMallShoppingCartItemMapper {
    int deleteByPrimaryKey(Long cartItemId);

    int insert(StarlightMallShoppingCartItem record);

    int insertSelective(StarlightMallShoppingCartItem record);

    StarlightMallShoppingCartItem selectByPrimaryKey(Long cartItemId);

    StarlightMallShoppingCartItem selectByUserIdAndGoodsId(@Param("starlightMallUserId") Long starlightMallUserId, @Param("goodsId") Long goodsId);

    List<StarlightMallShoppingCartItem> selectByUserId(@Param("starlightMallUserId") Long starlightMallUserId, @Param("number") int number);

    int selectCountByUserId(Long starlightMallUserId);

    int updateByPrimaryKeySelective(StarlightMallShoppingCartItem record);

    int updateByPrimaryKey(StarlightMallShoppingCartItem record);

    int deleteBatch(List<Long> ids);
}
