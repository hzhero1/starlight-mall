package ltd.starlight.mall.controller.admin;

import ltd.starlight.mall.common.ServiceResultEnum;
import ltd.starlight.mall.entity.Carousel;
import ltd.starlight.mall.service.Impl.StarLightMallCarouselServiceImpl;
import ltd.starlight.mall.util.PageQueryUtil;
import ltd.starlight.mall.util.PageResult;
import ltd.starlight.mall.util.Result;
import ltd.starlight.mall.util.ResultGenerator;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.util.StringUtils;
import org.springframework.web.bind.annotation.*;

import javax.servlet.http.HttpServletRequest;
import java.util.Map;
import java.util.Objects;

@Controller
@RequestMapping("/admin/carousels")
public class StarLightMallCarouselController {

    @Autowired
    StarLightMallCarouselServiceImpl starLightMallCarouselService;

    @GetMapping
    public String carouselPage(HttpServletRequest request) {
        request.setAttribute("path", "starlight_mall_carousel");
        return "admin/starlight_mall_carousel";
    }

    @RequestMapping(value = "/list", method = RequestMethod.GET)
    @ResponseBody
    public Result list(@RequestParam Map<String, Object> param) {
        if (StringUtils.isEmpty(param.get("page")) || StringUtils.isEmpty(param.get("limit"))) {
            return ResultGenerator.genFailResult("参数异常！");
        }
        PageResult pageResult = starLightMallCarouselService.getCarouselPage(new PageQueryUtil(param));
        return ResultGenerator.genSuccessResult(pageResult);
    }

    @RequestMapping(value = "/add", method = RequestMethod.POST)
    @ResponseBody
    public Result add(@RequestBody Carousel carousel) {
        if (StringUtils.isEmpty(carousel.getCarouselUrl()) || Objects.isNull(carousel.getCarouselRank())) {
            return ResultGenerator.genFailResult("参数异常！");
        }
        String result = starLightMallCarouselService.addCarousel(carousel);
        if (ServiceResultEnum.SUCCESS.getResult().equals(result)) {
            return ResultGenerator.genSuccessResult();
        } else {
            return ResultGenerator.genFailResult(result);
        }
    }

    @RequestMapping(value = "/update", method = RequestMethod.POST)
    @ResponseBody
    public Result update(@RequestBody Carousel carousel) {
        if (StringUtils.isEmpty(carousel.getCarouselUrl()) || Objects.isNull(carousel.getCarouselRank())) {
            return ResultGenerator.genFailResult("参数异常！");
        }
        String result = starLightMallCarouselService.updateCarousel(carousel);
        if (ServiceResultEnum.SUCCESS.getResult().equals(result)) {
            return ResultGenerator.genSuccessResult();
        } else {
            return ResultGenerator.genFailResult(result);
        }
    }

    @RequestMapping(value = "/info/{id}", method = RequestMethod.GET)
    @ResponseBody
    public Result info(@PathVariable("id") Integer id ) {
        Carousel carousel = starLightMallCarouselService.getCarouselById(id);
        if(carousel==null) return ResultGenerator.genFailResult(ServiceResultEnum.DATA_NOT_EXIST.getResult());
        return ResultGenerator.genSuccessResult(carousel);
    }

    @RequestMapping(value = "/delete", method = RequestMethod.POST)
    @ResponseBody
    public Result delete(@RequestBody Integer[] ids ) {
        if(starLightMallCarouselService.deleteBatch(ids)){
            return ResultGenerator.genSuccessResult();
        }
        return ResultGenerator.genFailResult("删除失败");
    }

}
