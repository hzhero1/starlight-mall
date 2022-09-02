package ltd.starlight.mall.dao;

import ltd.starlight.mall.entity.StarlightMallGoods;
import ltd.starlight.mall.entity.StockNumDTO;
import ltd.starlight.mall.util.PageQueryUtil;
import org.apache.ibatis.annotations.Param;

import java.util.List;

public interface StarlightMallGoodsMapper {

    int insertSelective(StarlightMallGoods record);

    StarlightMallGoods selectByPrimaryKey(Long goodsId);

    List<StarlightMallGoods> selectByPrimaryKeys(List<Long> goodsIds);

    int updateByPrimaryKeySelective(StarlightMallGoods record);

    List<StarlightMallGoods> findStarlightMallGoodsList(PageQueryUtil pageUtil);

    int getTotalStarlightMallGoods(PageQueryUtil pageUtil);

    int batchUpdateSellStatus(@Param("goodsIds")Long[] goodsIds, @Param("sellStatus") int sellStatus);

    List<StarlightMallGoods> findStarlightMallGoodsListBySearch(PageQueryUtil pageUtil);

    int getTotalStarlightMallGoodsBySearch(PageQueryUtil pageUtil);

    int updateStockNum(@Param("stockNumDTOS") List<StockNumDTO> stockNumDTOS);
}
