package com.mall.dao;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.mall.entity.PmsProduct;
import org.apache.ibatis.annotations.Param;
import org.apache.ibatis.annotations.Select;
import org.apache.ibatis.annotations.Update;

import java.util.List;

/**
 * 商品DAO
 */
public interface PmsProductDao extends BaseMapper<PmsProduct> {

    @Select("SELECT * FROM pms_product WHERE status = 1 AND deleted = 0 ORDER BY sales DESC LIMIT #{limit}")
    List<PmsProduct> selectHotProducts(@Param("limit") int limit);

    @Select("SELECT * FROM pms_product WHERE status = 1 AND deleted = 0 AND is_new = 1 ORDER BY created_at DESC LIMIT #{limit}")
    List<PmsProduct> selectNewProducts(@Param("limit") int limit);

    @Select("SELECT * FROM pms_product WHERE status = 1 AND deleted = 0 AND is_recommend = 1 ORDER BY sort_order ASC LIMIT #{limit}")
    List<PmsProduct> selectRecommendProducts(@Param("limit") int limit);

    @Update("UPDATE pms_product SET sales = sales + #{quantity} WHERE id = #{productId}")
    int addSales(@Param("productId") Long productId, @Param("quantity") int quantity);

    @Update("UPDATE pms_product SET stock = stock - #{quantity} WHERE id = #{productId} AND stock >= #{quantity}")
    int reduceStock(@Param("productId") Long productId, @Param("quantity") int quantity);

    @Select("SELECT * FROM pms_product WHERE stock <= 10 AND deleted = 0 ORDER BY stock ASC LIMIT 10")
    List<PmsProduct> selectLowStockProducts();
}
