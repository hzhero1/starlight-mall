package ltd.starlight.mall.controller.mall;

import ltd.starlight.mall.common.Constants;
import ltd.starlight.mall.common.IndexConfigTypeEnum;
import ltd.starlight.mall.service.StarlightMallCarouselService;
import ltd.starlight.mall.service.StarlightMallCategoryService;
import ltd.starlight.mall.service.StarlightMallIndexConfigService;
import ltd.starlight.mall.vo.StarlightMallIndexCarouselVO;
import ltd.starlight.mall.vo.StarlightMallIndexCategoryVO;
import ltd.starlight.mall.vo.StarlightMallIndexConfigGoodsVO;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;

import javax.servlet.http.HttpServletRequest;
import java.util.List;

@Controller
public class IndexController {

    @Autowired
    StarlightMallCarouselService starlightMallCarouselService;

    @Autowired
    StarlightMallCategoryService starlightMallCategoryService;

    @Autowired
    StarlightMallIndexConfigService starlightMallIndexConfigService;

    @GetMapping({"/index", "/", "/index.html"})
    public String indexPage(HttpServletRequest request) {
        List<StarlightMallIndexCarouselVO> carousels = starlightMallCarouselService.getCarouselsForIndex(Constants.INDEX_CAROUSEL_NUMBER);
        List<StarlightMallIndexCategoryVO> categories = starlightMallCategoryService.getCategoriesForIndex();
        List<StarlightMallIndexConfigGoodsVO> hotGoodses = starlightMallIndexConfigService.getConfigGoodsForIndex(IndexConfigTypeEnum
                .INDEX_GOODS_HOT.getType(), Constants.INDEX_GOODS_HOT_NUMBER);
        List<StarlightMallIndexConfigGoodsVO> newGoodses = starlightMallIndexConfigService.getConfigGoodsForIndex(IndexConfigTypeEnum
                .INDEX_GOODS_NEW.getType(), Constants.INDEX_GOODS_NEW_NUMBER);
        List<StarlightMallIndexConfigGoodsVO> recommendGoodses = starlightMallIndexConfigService.getConfigGoodsForIndex(IndexConfigTypeEnum
                .INDEX_GOODS_RECOMMOND.getType(), Constants.INDEX_GOODS_RECOMMOND_NUMBER);

        request.setAttribute("carousels", carousels);
        request.setAttribute("categories", categories);
        request.setAttribute("hotGoodses", hotGoodses);//热销商品
        request.setAttribute("newGoodses", newGoodses);//新品
        request.setAttribute("recommendGoodses", recommendGoodses);//推荐商品

        return "mall/index";
    }
}
