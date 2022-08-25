package ltd.starlight.mall.service;


import ltd.starlight.mall.entity.GoodsCategory;
import ltd.starlight.mall.util.PageQueryUtil;
import ltd.starlight.mall.util.PageResult;
import ltd.starlight.mall.vo.StarlightMallIndexCategoryVO;

import java.util.List;

public interface StarlightMallCategoryService {

    PageResult getCategoriesPage(PageQueryUtil pageUtil);

    String addCategory(GoodsCategory goodsCategory);

    String updateGoodsCategory(GoodsCategory goodsCategory);

    GoodsCategory getGoodsCategoryById(Long id);

    Boolean deleteBatch(Integer[] ids);
    /**
     * 根据parentId和level获取分类列表
     *
     * @param parentIds
     * @param categoryLevel
     * @return
     */
    List<GoodsCategory> selectByLevelAndParentIdsAndNumber(List<Long> parentIds, int categoryLevel);

    List<StarlightMallIndexCategoryVO> getCategoriesForIndex();

}
