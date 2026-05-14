package com.mall.dao;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.mall.entity.UmsAdmin;
import org.apache.ibatis.annotations.Param;
import org.apache.ibatis.annotations.Select;
import org.apache.ibatis.annotations.Update;

/**
 * 管理员DAO
 */
public interface UmsAdminDao extends BaseMapper<UmsAdmin> {

    @Select("SELECT * FROM ums_admin WHERE username = #{username} AND deleted = 0")
    UmsAdmin selectByUsername(@Param("username") String username);

    @Update("UPDATE ums_admin SET last_login_time = NOW() WHERE id = #{adminId}")
    int updateLoginTime(@Param("adminId") Long adminId);
}
