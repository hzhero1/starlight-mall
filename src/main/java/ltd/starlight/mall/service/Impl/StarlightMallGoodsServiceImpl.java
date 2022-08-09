package ltd.starlight.mall.service.Impl;

import ltd.starlight.mall.common.ServiceResultEnum;
import ltd.starlight.mall.dao.StarlightMallGoodsMapper;
import ltd.starlight.mall.entity.StarlightMallGoods;
import ltd.starlight.mall.service.StarlightMallGoodsService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

@Service
public class StarlightMallGoodsServiceImpl implements StarlightMallGoodsService {

    @Autowired
    private StarlightMallGoodsMapper goodsMapper;

    @Override
    public String addStarlightMallGoods(StarlightMallGoods goods){
        if(goodsMapper.insertSelective(goods)>0){
            return ServiceResultEnum.SUCCESS.getResult();
        }
        return ServiceResultEnum.DB_ERROR.getResult();
    }
}
