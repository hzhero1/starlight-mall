package ltd.starlight.mall.controller.vo;

import lombok.Getter;
import lombok.Setter;

import java.io.Serializable;

@Getter
@Setter
public class StarlightMallIndexCarouselVO implements Serializable {
    private static final long serialVersionUID = -2070742051167758758L;

    private String carouselUrl;

    private String redirectUrl;
}
