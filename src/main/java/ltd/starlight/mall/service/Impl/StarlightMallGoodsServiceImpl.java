package ltd.starlight.mall.service.Impl;

import ltd.starlight.mall.common.Constants;
import ltd.starlight.mall.common.ServiceResultEnum;
import ltd.starlight.mall.common.StarlightMallCategoryLevelEnum;
import ltd.starlight.mall.controller.vo.SearchPageCategoryVO;
import ltd.starlight.mall.controller.vo.StarlightMallSearchGoodsVO;
import ltd.starlight.mall.dao.GoodsCategoryMapper;
import ltd.starlight.mall.dao.StarlightMallGoodsMapper;
import ltd.starlight.mall.entity.GoodsCategory;
import ltd.starlight.mall.entity.StarlightMallGoods;
import ltd.starlight.mall.service.StarlightMallGoodsService;
import ltd.starlight.mall.util.BeanUtil;
import ltd.starlight.mall.util.PageQueryUtil;
import ltd.starlight.mall.util.PageResult;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.util.CollectionUtils;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Date;
import java.util.List;

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

    @Override
    public String updateStarlightMallGoods(StarlightMallGoods goods) {
        StarlightMallGoods temp = goodsMapper.selectByPrimaryKey(goods.getGoodsId());
        if (temp == null) {
            return ServiceResultEnum.DATA_NOT_EXIST.getResult();
        }
        goods.setUpdateTime(new Date());
        if (goodsMapper.updateByPrimaryKeySelective(goods) > 0) {
            return ServiceResultEnum.SUCCESS.getResult();
        }
        return ServiceResultEnum.DB_ERROR.getResult();
    }

    @Override
    public StarlightMallGoods getStarlightMallGoodsById(Long id) {
        return goodsMapper.selectByPrimaryKey(id);
    }

    @Override
    public PageResult getStarlightMallGoodsPage(PageQueryUtil pageUtil) {
        List<StarlightMallGoods> goodsList = goodsMapper.findStarlightMallGoodsList(pageUtil);
        int total = goodsMapper.getTotalStarlightMallGoods(pageUtil);
        PageResult pageResult = new PageResult(goodsList, total, pageUtil.getLimit(), pageUtil.getPage());
        return pageResult;
    }

    @Override
    public Boolean batchUpdateSellStatus(Long[] ids, int sellStatus) {
        return goodsMapper.batchUpdateSellStatus(ids, sellStatus) > 0;
    }

    @Override
    public PageResult searchStarlightMallGoods(PageQueryUtil pageUtil) {
        List<StarlightMallGoods> goodsList = goodsMapper.findStarlightMallGoodsListBySearch(pageUtil);
        int total = goodsMapper.getTotalStarlightMallGoodsBySearch(pageUtil);
        List<StarlightMallSearchGoodsVO> starlightMallSearchGoodsVOS = new ArrayList<>();
        if (!CollectionUtils.isEmpty(goodsList)) {
            starlightMallSearchGoodsVOS = BeanUtil.copyList(goodsList, StarlightMallSearchGoodsVO.class);
            for (StarlightMallSearchGoodsVO starlightMallSearchGoodsVO : starlightMallSearchGoodsVOS) {
                String goodsName = starlightMallSearchGoodsVO.getGoodsName();
                String goodsIntro = starlightMallSearchGoodsVO.getGoodsIntro();
                // 字符串过长导致文字超出的问题
                if (goodsName.length() > 28) {
                    goodsName = goodsName.substring(0, 28) + "...";
                    starlightMallSearchGoodsVO.setGoodsName(goodsName);
                }
                if (goodsIntro.length() > 30) {
                    goodsIntro = goodsIntro.substring(0, 30) + "...";
                    starlightMallSearchGoodsVO.setGoodsIntro(goodsIntro);
                }
            }
        }
        PageResult pageResult = new PageResult(starlightMallSearchGoodsVOS, total, pageUtil.size(), pageUtil.getPage());
        return pageResult;
    }

}
