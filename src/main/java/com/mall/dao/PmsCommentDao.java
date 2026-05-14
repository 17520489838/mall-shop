package com.mall.dao;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.mall.entity.PmsComment;
import org.apache.ibatis.annotations.Param;
import org.apache.ibatis.annotations.Select;

/**
 * 评价DAO
 */
public interface PmsCommentDao extends BaseMapper<PmsComment> {

    @Select("SELECT AVG(rating) FROM pms_comment WHERE product_id = #{productId} AND status = 1 AND deleted = 0")
    Double selectAvgRatingByProductId(@Param("productId") Long productId);

    @Select("SELECT COUNT(*) FROM pms_comment WHERE product_id = #{productId} AND status = 1 AND deleted = 0")
    Long selectCountByProductId(@Param("productId") Long productId);
}
