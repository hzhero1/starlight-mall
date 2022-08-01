package ltd.starlight.mall.service.Impl;

import ltd.starlight.mall.common.ServiceResultEnum;
import ltd.starlight.mall.dao.CarouselMapper;
import ltd.starlight.mall.entity.Carousel;
import ltd.starlight.mall.service.StarLightMallCarouselService;
import ltd.starlight.mall.util.PageQueryUtil;
import ltd.starlight.mall.util.PageResult;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class StarLightMallCarouselServiceImpl implements StarLightMallCarouselService {

    @Autowired
    CarouselMapper carouselMapper;

    @Override
    public PageResult getCarouselPage(PageQueryUtil pageUtil) {
        int totalCount = carouselMapper.getTotalCarousels();
        List<Carousel> result = carouselMapper.selectCarouselList(pageUtil);
        return new PageResult(result, totalCount, pageUtil.getLimit(), pageUtil.getPage());
    }

    @Override
    public Carousel getCarouselById(Integer id) {
        return carouselMapper.selectByPrimaryKey(id);
    }

    @Override
    public String addCarousel(Carousel carousel) {
        if (carouselMapper.insertSelective(carousel) > 0) {
            return ServiceResultEnum.SUCCESS.getResult();
        }
        return ServiceResultEnum.DB_ERROR.getResult();
    }

    @Override
    public String updateCarousel(Carousel carousel) {
        Carousel result = carouselMapper.selectByPrimaryKey(carousel.getCarouselId());
        if (result == null) return ServiceResultEnum.DATA_NOT_EXIST.getResult();
        if (carouselMapper.updateByPrimaryKeySelective(carousel) > 0) {
            return ServiceResultEnum.SUCCESS.getResult();
        }
        return ServiceResultEnum.DB_ERROR.getResult();
    }

    @Override
    public Boolean deleteBatch(Integer[] ids) {
        if (ids.length < 1) return false;
        return carouselMapper.deleteBatch(ids) > 0;
    }
}