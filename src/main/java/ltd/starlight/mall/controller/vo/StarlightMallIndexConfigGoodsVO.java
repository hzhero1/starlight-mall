package ltd.starlight.mall.controller.vo;

import lombok.Getter;
import lombok.Setter;

@Setter
@Getter
public class StarlightMallIndexConfigGoodsVO {
    private Long goodsId;

    private String goodsName;

    private String goodsIntro;

    private String goodsCoverImg;

    private Integer sellingPrice;

    private String tag;
}
