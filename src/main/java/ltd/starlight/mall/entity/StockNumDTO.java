package ltd.starlight.mall.entity;

import lombok.Getter;
import lombok.Setter;

/**
 * 库存修改所需实体
 */
@Getter
@Setter
public class StockNumDTO {

    private Long goodsId;

    private Integer goodsCount;

}
