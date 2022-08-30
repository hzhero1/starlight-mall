package ltd.starlight.mall.service;

import ltd.starlight.mall.controller.vo.StarlightMallShoppingCartItemVO;
import ltd.starlight.mall.entity.StarlightMallShoppingCartItem;
import org.apache.ibatis.annotations.Param;

import javax.servlet.http.HttpSession;
import java.util.List;

public interface StarlightMallShoppingCartService {
    String addStarlightMallCartItem(StarlightMallShoppingCartItem starlightMallShoppingCartItem);

    List<StarlightMallShoppingCartItemVO> getMyShoppingCartItems(Long starlightMallUserId);

    String updateStarlightMallCartItem(StarlightMallShoppingCartItem StarlightMallShoppingCartItem);

    Boolean deleteById(Long StarlightMallShoppingCartItemId, HttpSession httpSession);
}
