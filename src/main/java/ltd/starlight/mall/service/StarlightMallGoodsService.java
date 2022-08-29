package ltd.starlight.mall.service;

import ltd.starlight.mall.entity.StarlightMallGoods;
import ltd.starlight.mall.util.PageQueryUtil;
import ltd.starlight.mall.util.PageResult;

public interface StarlightMallGoodsService {

    String addStarlightMallGoods(StarlightMallGoods goods);

    String updateStarlightMallGoods(StarlightMallGoods goods);

    StarlightMallGoods getStarlightMallGoodsById(Long id);

    PageResult getStarlightMallGoodsPage(PageQueryUtil pageUtil);

    Boolean batchUpdateSellStatus(Long[] ids,int sellStatus);

    PageResult searchStarlightMallGoods(PageQueryUtil pageUtil);

}
