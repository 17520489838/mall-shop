package com.mall.service;

import com.mall.dto.LoginDTO;
import com.mall.entity.UmsAdmin;
import com.mall.entity.UmsMenu;
import com.mall.entity.UmsRole;
import com.mall.vo.DashboardVO;
import com.mall.vo.LoginVO;

import java.util.List;

/**
 * 管理员服务接口
 */
public interface UmsAdminService {

    LoginVO login(LoginDTO dto);

    UmsAdmin getCurrentAdmin(Long adminId);

    List<UmsMenu> getAdminMenus(Long adminId);

    List<String> getAdminPermissions(Long adminId);

    DashboardVO getDashboardData();

    List<UmsRole> listRoles();

    void createRole(UmsRole role);

    void updateRole(UmsRole role);

    void deleteRole(Long id);

    List<UmsMenu> listMenus();

    void createMenu(UmsMenu menu);

    void updateMenu(UmsMenu menu);

    void deleteMenu(Long id);

    List<Long> getRoleMenuIds(Long roleId);

    void updateRoleMenus(Long roleId, List<Long> menuIds);
}
