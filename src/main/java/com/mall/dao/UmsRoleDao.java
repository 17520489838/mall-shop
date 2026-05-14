package com.mall.dao;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.mall.entity.UmsRole;
import org.apache.ibatis.annotations.Param;
import org.apache.ibatis.annotations.Select;

import java.util.List;

/**
 * 角色DAO
 */
public interface UmsRoleDao extends BaseMapper<UmsRole> {

    @Select("SELECT r.* FROM ums_role r " +
            "JOIN ums_admin_role ar ON r.id = ar.role_id " +
            "WHERE ar.admin_id = #{adminId}")
    List<UmsRole> selectByAdminId(@Param("adminId") Long adminId);
}
