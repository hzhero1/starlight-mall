package ltd.starlight.mall.dao;

import ltd.starlight.mall.entity.StarlightMallOrderItem;
import org.apache.ibatis.annotations.Param;

import java.util.List;

public interface StarlightMallOrderItemMapper {
    int deleteByPrimaryKey(Long orderItemId);

    int insert(StarlightMallOrderItem record);

    int insertSelective(StarlightMallOrderItem record);

    StarlightMallOrderItem selectByPrimaryKey(Long orderItemId);

    /**
     * 根据订单id获取订单项列表
     *
     * @param orderId
     * @return
     */
    List<StarlightMallOrderItem> selectByOrderId(Long orderId);

    /**
     * 根据订单ids获取订单项列表
     *
     * @param orderIds
     * @return
     */
    List<StarlightMallOrderItem> selectByOrderIds(@Param("orderIds") List<Long> orderIds);

    /**
     * 批量insert订单项数据
     *
     * @param orderItems
     * @return
     */
    int insertBatch(@Param("orderItems") List<StarlightMallOrderItem> orderItems);

    int updateByPrimaryKeySelective(StarlightMallOrderItem record);

    int updateByPrimaryKey(StarlightMallOrderItem record);
}