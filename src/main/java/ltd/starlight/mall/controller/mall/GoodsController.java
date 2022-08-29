package ltd.starlight.mall.controller.mall;

import ltd.starlight.mall.common.Constants;
import ltd.starlight.mall.controller.vo.SearchPageCategoryVO;
import ltd.starlight.mall.controller.vo.StarlightMallGoodsDetailVO;
import ltd.starlight.mall.entity.StarlightMallGoods;
import ltd.starlight.mall.service.StarlightMallCategoryService;
import ltd.starlight.mall.service.StarlightMallGoodsService;
import ltd.starlight.mall.util.BeanUtil;
import ltd.starlight.mall.util.PageQueryUtil;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.util.StringUtils;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestParam;

import javax.servlet.http.HttpServletRequest;
import java.util.Map;

@Controller
public class GoodsController {

    @Autowired
    StarlightMallGoodsService starlightMallGoodsService;

    @Autowired
    StarlightMallCategoryService starlightMallCategoryService;

    @GetMapping({"/search", "/search.html"})
    public String searchPage(@RequestParam Map<String, Object> params, HttpServletRequest request) {
        if (StringUtils.isEmpty(params.get("page"))) {
            params.put("page", 1);
        }
        params.put("limit", Constants.GOODS_SEARCH_PAGE_LIMIT);
        //封装分类数据
        if (params.containsKey("goodsCategoryId") && !StringUtils.isEmpty(params.get("goodsCategoryId") + "")) {
            Long categoryId = Long.valueOf(params.get("goodsCategoryId") + "");
            SearchPageCategoryVO searchPageCategoryVO = starlightMallCategoryService.getCategoriesForSearch(categoryId);
            if (searchPageCategoryVO != null) {
                request.setAttribute("goodsCategoryId", categoryId);
                request.setAttribute("searchPageCategoryVO", searchPageCategoryVO);
            }
        }
        //封装参数供前端回显
        if (params.containsKey("orderBy") && !StringUtils.isEmpty(params.get("orderBy") + "")) {
            request.setAttribute("orderBy", params.get("orderBy") + "");
        }
        String keyword = "";
        //对keyword做过滤
        if (params.containsKey("keyword") && !StringUtils.isEmpty((params.get("keyword") + "").trim())) {
            keyword = params.get("keyword") + "";
        }
        request.setAttribute("keyword", keyword);
        params.put("keyword", keyword);
        //封装商品数据
        PageQueryUtil pageUtil = new PageQueryUtil(params);
        request.setAttribute("pageResult", starlightMallGoodsService.searchStarlightMallGoods(pageUtil));
        return "mall/search";
    }

    @GetMapping("/goods/detail/{goodsId}")
    public String detailPage(@PathVariable("goodsId") Long goodsId, HttpServletRequest request) {
        if (goodsId < 1) {
            return "error/error_5xx";
        }
        StarlightMallGoods goods = starlightMallGoodsService.getStarlightMallGoodsById(goodsId);
        if (goods == null) {
            return "error/error_404";
        }
        StarlightMallGoodsDetailVO goodsDetailVO = new StarlightMallGoodsDetailVO();
        BeanUtil.copyProperties(goods, goodsDetailVO);
        request.setAttribute("goodsDetail", goodsDetailVO);
        return "mall/detail";
    }
}
