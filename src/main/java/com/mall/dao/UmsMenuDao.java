package com.mall.dao;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.mall.entity.UmsMenu;
import org.apache.ibatis.annotations.Param;
import org.apache.ibatis.annotations.Select;

import java.util.List;

/**
 * 菜单DAO
 */
public interface UmsMenuDao extends BaseMapper<UmsMenu> {

    @Select("SELECT DISTINCT m.* FROM ums_menu m " +
            "JOIN ums_role_menu rm ON m.id = rm.menu_id " +
            "JOIN ums_admin_role ar ON rm.role_id = ar.role_id " +
            "WHERE ar.admin_id = #{adminId} AND m.status = 1 " +
            "ORDER BY m.sort_order ASC")
    List<UmsMenu> selectByAdminId(@Param("adminId") Long adminId);

    @Select("SELECT DISTINCT m.permission FROM ums_menu m " +
            "JOIN ums_role_menu rm ON m.id = rm.menu_id " +
            "JOIN ums_admin_role ar ON rm.role_id = ar.role_id " +
            "WHERE ar.admin_id = #{adminId} AND m.permission IS NOT NULL")
    List<String> selectPermissionsByAdminId(@Param("adminId") Long adminId);
}
