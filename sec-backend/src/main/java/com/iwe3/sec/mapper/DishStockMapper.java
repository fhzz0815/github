package com.iwe3.sec.mapper;

import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;
import com.iwe3.sec.entity.DishStockEntity;
import java.util.List;

/**
 * dish_stock 表的数据访问接口
 */
@Mapper
public interface DishStockMapper {

    /** 分页查询列表 */
    List<DishStockEntity> selectList(@Param("query") DishStockEntity query);

    /** 根据ID查询 */
    DishStockEntity selectById(@Param("id") Long id);

    /** 根据门店和菜品查询 */
    DishStockEntity selectByStoreAndDish(@Param("storeId") Long storeId, @Param("dishId") Long dishId);

    /** 扣减库存（带乐观锁，防止超卖） */
    int decreaseStock(@Param("storeId") Long storeId, @Param("dishId") Long dishId,
                      @Param("quantity") Integer quantity);

    /** 回滚库存（取消订单时恢复已扣减的库存） */
    int increaseStock(@Param("storeId") Long storeId, @Param("dishId") Long dishId,
                      @Param("quantity") Integer quantity);

    /** 新增 */
    int insert(DishStockEntity entity);

    /** 修改 */
    int update(DishStockEntity entity);

    /** 根据ID删除 */
    int deleteById(@Param("id") Long id);
}
