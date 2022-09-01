package ltd.starlight.mall.dao;

import ltd.starlight.mall.entity.StarlightMallOrder;
import ltd.starlight.mall.util.PageQueryUtil;
import org.apache.ibatis.annotations.Param;

import java.util.List;

public interface StarlightMallOrderMapper {
    int deleteByPrimaryKey(Long orderId);

    int insert(StarlightMallOrder record);

    int insertSelective(StarlightMallOrder record);

    StarlightMallOrder selectByPrimaryKey(Long orderId);

    StarlightMallOrder selectByOrderNo(String orderNo);

    int updateByPrimaryKeySelective(StarlightMallOrder record);

    int updateByPrimaryKey(StarlightMallOrder record);

    List<StarlightMallOrder> findStarlightMallOrderList(PageQueryUtil pageUtil);

    int getTotalStarlightMallOrders(PageQueryUtil pageUtil);

    List<StarlightMallOrder> selectByPrimaryKeys(@Param("orderIds") List<Long> orderIds);

    int checkOut(@Param("orderIds") List<Long> orderIds);

    int closeOrder(@Param("orderIds") List<Long> orderIds, @Param("orderStatus") int orderStatus);

    int checkDone(@Param("orderIds") List<Long> asList);
}