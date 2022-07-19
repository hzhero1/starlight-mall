package ltd.starlight.mall.entity;

import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class AdminUser {
    private Integer adminUserId;

    private String loginUserName;

    private String loginPassword;

    private String nickName;

    private Byte locked;
}
