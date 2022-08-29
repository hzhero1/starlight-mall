package ltd.starlight.mall.service;


import ltd.starlight.mall.controller.vo.SearchPageCategoryVO;
import ltd.starlight.mall.entity.GoodsCategory;
import ltd.starlight.mall.util.PageQueryUtil;
import ltd.starlight.mall.util.PageResult;
import ltd.starlight.mall.controller.vo.StarlightMallIndexCategoryVO;

import java.util.List;

public interface StarlightMallCategoryService {

    PageResult getCategoriesPage(PageQueryUtil pageUtil);

    String addCategory(GoodsCategory goodsCategory);

    String updateGoodsCategory(GoodsCategory goodsCategory);

    GoodsCategory getGoodsCategoryById(Long id);

    Boolean deleteBatch(Integer[] ids);

    List<GoodsCategory> selectByLevelAndParentIdsAndNumber(List<Long> parentIds, int categoryLevel);

    List<StarlightMallIndexCategoryVO> getCategoriesForIndex();

    SearchPageCategoryVO getCategoriesForSearch(Long categoryId);


}
