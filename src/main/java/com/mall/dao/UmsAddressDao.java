package com.mall.dao;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.mall.entity.UmsAddress;
import org.apache.ibatis.annotations.Param;
import org.apache.ibatis.annotations.Update;

/**
 * 地址DAO
 */
public interface UmsAddressDao extends BaseMapper<UmsAddress> {

    @Update("UPDATE ums_address SET is_default = 0 WHERE user_id = #{userId} AND is_default = 1")
    int clearDefault(@Param("userId") Long userId);
}
