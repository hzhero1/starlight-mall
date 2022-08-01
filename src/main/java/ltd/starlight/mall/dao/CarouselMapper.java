package ltd.starlight.mall.dao;

import ltd.starlight.mall.entity.Carousel;
import ltd.starlight.mall.util.PageQueryUtil;
import org.apache.ibatis.annotations.Param;

import java.util.List;

public interface CarouselMapper {

    int deleteByPrimaryKey(Integer carouselId);

    int insert(Carousel record);

    int insertSelective(Carousel record);

    Carousel selectByPrimaryKey(Integer carouselId);

    int updateByPrimaryKeySelective(Carousel record);

    int updateByPrimaryKey(Carousel record);

    List<Carousel> selectCarouselList(PageQueryUtil pageUtil);

    int getTotalCarousels();

    int deleteBatch(Integer[] ids);

    List<Carousel> selectCarouselsByNum(@Param("number") int number);
}
