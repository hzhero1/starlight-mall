package ltd.starlight.mall.controller.admin;


import ltd.starlight.mall.common.ServiceResultEnum;
import ltd.starlight.mall.common.StarlightMallCategoryLevelEnum;
import ltd.starlight.mall.entity.GoodsCategory;
import ltd.starlight.mall.entity.StarlightMallGoods;
import ltd.starlight.mall.service.Impl.StarlightMallCategoryServiceImpl;
import ltd.starlight.mall.service.StarlightMallCategoryService;
import ltd.starlight.mall.service.StarlightMallGoodsService;
import ltd.starlight.mall.util.Result;
import ltd.starlight.mall.util.ResultGenerator;
import org.springframework.stereotype.Controller;
import org.springframework.util.CollectionUtils;
import org.springframework.util.StringUtils;
import org.springframework.web.bind.annotation.*;

import javax.annotation.Resource;
import javax.servlet.http.HttpServletRequest;
import java.util.Collections;
import java.util.List;
import java.util.Objects;

@Controller
@RequestMapping("/admin/goods")
public class StarlightMallGoodsController {
    @Resource
    private StarlightMallCategoryService starlightMallCategoryService;

    @Resource
    private StarlightMallGoodsService starlightMallGoodsService;

    @GetMapping("/edit")
    public String edit(HttpServletRequest request) {
        request.setAttribute("path", "edit");
        //查询所有的一级分类
        List<GoodsCategory> firstLevelCategories = starlightMallCategoryService
                .selectByLevelAndParentIdsAndNumber(Collections.singletonList(0L),
                StarlightMallCategoryLevelEnum.LEVEL_ONE.getLevel());
        if (!CollectionUtils.isEmpty(firstLevelCategories)) {
            //查询一级分类列表中第一个实体的所有二级分类
            List<GoodsCategory> secondLevelCategories = starlightMallCategoryService.
                    selectByLevelAndParentIdsAndNumber(Collections.singletonList(firstLevelCategories.get(0)
                                    .getCategoryId()), StarlightMallCategoryLevelEnum.LEVEL_TWO.getLevel());
            if (!CollectionUtils.isEmpty(secondLevelCategories)) {
                //查询二级分类列表中第一个实体的所有三级分类
                List<GoodsCategory> thirdLevelCategories = starlightMallCategoryService
                        .selectByLevelAndParentIdsAndNumber(Collections.singletonList(secondLevelCategories.get(0)
                                .getCategoryId()), StarlightMallCategoryLevelEnum.LEVEL_THREE.getLevel());
                request.setAttribute("firstLevelCategories", firstLevelCategories);
                request.setAttribute("secondLevelCategories", secondLevelCategories);
                request.setAttribute("thirdLevelCategories", thirdLevelCategories);
                request.setAttribute("path", "goods-edit");
                return "admin/starlight_mall_goods_edit";
            }
        }
        return "error/error_5xx";
    }

    @RequestMapping(value = "/add", method = RequestMethod.POST)
    @ResponseBody
    public Result save(@RequestBody StarlightMallGoods starlightMallGoods) {
        if (StringUtils.isEmpty(starlightMallGoods.getGoodsName())
                || StringUtils.isEmpty(starlightMallGoods.getGoodsIntro())
                || StringUtils.isEmpty(starlightMallGoods.getTag())
                || Objects.isNull(starlightMallGoods.getOriginalPrice())
                || Objects.isNull(starlightMallGoods.getGoodsCategoryId())
                || Objects.isNull(starlightMallGoods.getSellingPrice())
                || Objects.isNull(starlightMallGoods.getStockNum())
                || Objects.isNull(starlightMallGoods.getGoodsSellStatus())
                || StringUtils.isEmpty(starlightMallGoods.getGoodsCoverImg())
                || StringUtils.isEmpty(starlightMallGoods.getGoodsDetailContent())) {
            return ResultGenerator.genFailResult("参数异常！");
        }
        String result = starlightMallGoodsService.addStarlightMallGoods(starlightMallGoods);
        if (ServiceResultEnum.SUCCESS.getResult().equals(result)) {
            return ResultGenerator.genSuccessResult();
        } else {
            return ResultGenerator.genFailResult(result);
        }
    }

}
