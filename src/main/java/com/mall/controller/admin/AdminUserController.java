package com.mall.controller.admin;

import com.mall.common.result.Result;
import com.mall.entity.UmsMenu;
import com.mall.entity.UmsRole;
import com.mall.service.UmsAdminService;
import com.mall.service.UmsUserService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/v1/admin/users")
@Tag(name = "管理后台用户接口")
public class AdminUserController {

    private final UmsUserService userService;
    private final UmsAdminService adminService;

    public AdminUserController(UmsUserService userService, UmsAdminService adminService) {
        this.userService = userService;
        this.adminService = adminService;
    }

    // ========== 用户管理 ==========

    @GetMapping
    @Operation(summary = "用户列表")
    public Result<?> listUsers(@RequestParam(defaultValue = "1") Integer pageNum,
                               @RequestParam(defaultValue = "20") Integer pageSize,
                               @RequestParam(required = false) String keyword) {
        return Result.success(userService.listUsers(pageNum, pageSize, keyword));
    }

    @PutMapping("/{id}/status")
    @Operation(summary = "启用/禁用用户")
    public Result<String> toggleUserStatus(@PathVariable Long id) {
        userService.toggleUserStatus(id);
        return Result.success("操作成功");
    }

    // ========== 角色管理 ==========

    @GetMapping("/roles")
    @Operation(summary = "角色列表")
    public Result<List<UmsRole>> listRoles() {
        return Result.success(adminService.listRoles());
    }

    @PostMapping("/roles")
    @Operation(summary = "新增角色")
    public Result<String> createRole(@RequestBody UmsRole role) {
        adminService.createRole(role);
        return Result.success("新增成功");
    }

    @PutMapping("/roles/{id}")
    @Operation(summary = "更新角色")
    public Result<String> updateRole(@PathVariable Long id, @RequestBody UmsRole role) {
        role.setId(id);
        adminService.updateRole(role);
        return Result.success("更新成功");
    }

    @DeleteMapping("/roles/{id}")
    @Operation(summary = "删除角色")
    public Result<String> deleteRole(@PathVariable Long id) {
        adminService.deleteRole(id);
        return Result.success("删除成功");
    }

    // ========== 角色-菜单关联 ==========

    @GetMapping("/roles/{id}/menus")
    @Operation(summary = "获取角色菜单ID列表")
    public Result<List<Long>> getRoleMenuIds(@PathVariable Long id) {
        return Result.success(adminService.getRoleMenuIds(id));
    }

    @PutMapping("/roles/{id}/menus")
    @Operation(summary = "更新角色菜单权限")
    public Result<String> updateRoleMenus(@PathVariable Long id, @RequestBody List<Long> menuIds) {
        adminService.updateRoleMenus(id, menuIds);
        return Result.success("权限更新成功");
    }

    // ========== 菜单管理 ==========

    @GetMapping("/menus")
    @Operation(summary = "菜单列表")
    public Result<?> listMenus() {
        return Result.success(adminService.listMenus());
    }

    @PostMapping("/menus")
    @Operation(summary = "新增菜单")
    public Result<String> createMenu(@RequestBody UmsMenu menu) {
        adminService.createMenu(menu);
        return Result.success("新增成功");
    }

    @PutMapping("/menus/{id}")
    @Operation(summary = "更新菜单")
    public Result<String> updateMenu(@PathVariable Long id, @RequestBody UmsMenu menu) {
        menu.setId(id);
        adminService.updateMenu(menu);
        return Result.success("更新成功");
    }

    @DeleteMapping("/menus/{id}")
    @Operation(summary = "删除菜单")
    public Result<String> deleteMenu(@PathVariable Long id) {
        adminService.deleteMenu(id);
        return Result.success("删除成功");
    }
}
