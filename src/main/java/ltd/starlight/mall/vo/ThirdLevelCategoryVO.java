
package ltd.starlight.mall.vo;

import lombok.Getter;
import lombok.Setter;

import java.io.Serializable;

/**
 * 首页分类数据VO(第三级)
 */
@Getter
@Setter
public class ThirdLevelCategoryVO implements Serializable {

    private Long categoryId;

    private Byte categoryLevel;

    private String categoryName;

}
