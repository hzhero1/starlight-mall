package ltd.starlight.mall.service;

import ltd.starlight.mall.entity.IndexConfig;
import ltd.starlight.mall.util.PageQueryUtil;
import ltd.starlight.mall.util.PageResult;
import ltd.starlight.mall.vo.StarlightMallIndexConfigGoodsVO;
import org.springframework.stereotype.Service;

import java.util.List;

public interface StarlightMallIndexConfigService {

    PageResult getConfigsPage(PageQueryUtil pageUtil);

    String addIndexConfig(IndexConfig indexConfig);

    String updateIndexConfig(IndexConfig indexConfig);

    /**
     * 返回固定数量的首页配置商品对象(首页调用)
     *
     * @param number
     * @return
     */
    List<StarlightMallIndexConfigGoodsVO> getConfigGoodsForIndex(int configType, int number);

    Boolean deleteBatch(Long[] ids);
}
