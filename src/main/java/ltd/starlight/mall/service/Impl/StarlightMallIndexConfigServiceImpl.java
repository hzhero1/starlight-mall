package ltd.starlight.mall.service.Impl;

import ltd.starlight.mall.common.ServiceResultEnum;
import ltd.starlight.mall.dao.IndexConfigMapper;
import ltd.starlight.mall.dao.StarlightMallGoodsMapper;
import ltd.starlight.mall.entity.IndexConfig;
import ltd.starlight.mall.entity.StarlightMallGoods;
import ltd.starlight.mall.service.StarlightMallIndexConfigService;
import ltd.starlight.mall.util.BeanUtil;
import ltd.starlight.mall.util.PageQueryUtil;
import ltd.starlight.mall.util.PageResult;
import ltd.starlight.mall.controller.vo.StarlightMallIndexConfigGoodsVO;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.util.CollectionUtils;

import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

@Service
public class StarlightMallIndexConfigServiceImpl implements StarlightMallIndexConfigService {
    @Autowired
    private IndexConfigMapper indexConfigMapper;

    @Autowired
    private StarlightMallGoodsMapper goodsMapper;

    @Override
    public PageResult getConfigsPage(PageQueryUtil pageUtil) {
        List<IndexConfig> indexConfigs = indexConfigMapper.findIndexConfigList(pageUtil);
        int total = indexConfigMapper.getTotalIndexConfigs(pageUtil);
        PageResult pageResult = new PageResult(indexConfigs, total, pageUtil.getLimit(), pageUtil.getPage());
        return pageResult;
    }

    @Override
    public String addIndexConfig(IndexConfig indexConfig) {
        if (indexConfigMapper.insertSelective(indexConfig) > 0) {
            return ServiceResultEnum.SUCCESS.getResult();
        }
        return ServiceResultEnum.DB_ERROR.getResult();
    }

    @Override
    public String updateIndexConfig(IndexConfig indexConfig) {
        IndexConfig temp = indexConfigMapper.selectByPrimaryKey(indexConfig.getConfigId());
        if (temp == null) {
            return ServiceResultEnum.DATA_NOT_EXIST.getResult();
        }
        if (indexConfigMapper.updateByPrimaryKeySelective(indexConfig) > 0) {
            return ServiceResultEnum.SUCCESS.getResult();
        }
        return ServiceResultEnum.DB_ERROR.getResult();
    }

    @Override
    public List<StarlightMallIndexConfigGoodsVO> getConfigGoodsForIndex(int configType, int number) {
        List<StarlightMallIndexConfigGoodsVO> starlightMallIndexConfigGoodsVOS = new ArrayList<>(number);
        List<IndexConfig> indexConfigs = indexConfigMapper.findIndexConfigsByTypeAndNum(configType, number);
        if (!CollectionUtils.isEmpty(indexConfigs)) {
            List<Long> goodsIds = indexConfigs.stream().map(IndexConfig::getGoodsId).collect(Collectors.toList());
            List<StarlightMallGoods> starlightMallGoods = goodsMapper.selectByPrimaryKeys(goodsIds);
            starlightMallIndexConfigGoodsVOS = BeanUtil.copyList(starlightMallGoods, StarlightMallIndexConfigGoodsVO.class);
            for (StarlightMallIndexConfigGoodsVO starlightMallIndexConfigGoodsVO: starlightMallIndexConfigGoodsVOS) {
                String goodsName = starlightMallIndexConfigGoodsVO.getGoodsName();
                String goodsIntro = starlightMallIndexConfigGoodsVO.getGoodsIntro();
                if (goodsName.length() >30){
                    goodsName = goodsName.substring(0,30)+"...";
                    starlightMallIndexConfigGoodsVO.setGoodsName(goodsName);
                }
                if(goodsIntro.length()>22){
                    goodsIntro = goodsIntro.substring(0,22)+"...";
                    starlightMallIndexConfigGoodsVO.setGoodsIntro(goodsIntro);
                }
            }
        }
        return starlightMallIndexConfigGoodsVOS;
    }

    @Override
    public Boolean deleteBatch(Long[] ids) {
        if (ids.length < 1) {
            return false;
        }
        //删除数据
        return indexConfigMapper.deleteBatch(ids) > 0;
    }
}
