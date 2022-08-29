package ltd.starlight.mall.service;

import ltd.starlight.mall.entity.Carousel;
import ltd.starlight.mall.util.PageQueryUtil;
import ltd.starlight.mall.util.PageResult;
import ltd.starlight.mall.controller.vo.StarlightMallIndexCarouselVO;

import java.util.List;

public interface StarlightMallCarouselService {
    /**
     * 查询后台管理系统轮播图分页数据
     *
     * @param pageUtil
     * @return
     */
    PageResult getCarouselPage(PageQueryUtil pageUtil);

    /**
     * 新增一条轮播图记录
     *
     * @param carousel
     * @return
     */
    String addCarousel(Carousel carousel);

    /**
     * 修改一条轮播图记录
     *
     * @param carousel
     * @return
     */
    String updateCarousel(Carousel carousel);

    /**
     * 根据主键查询轮播图记录
     *
     * @param id
     * @return
     */
    Carousel getCarouselById(Integer id);

    /**
     * 批量删除轮播图记录
     *
     * @param ids
     * @return
     */
    Boolean deleteBatch(Integer[] ids);

    List<StarlightMallIndexCarouselVO> getCarouselsForIndex(int number);
}
