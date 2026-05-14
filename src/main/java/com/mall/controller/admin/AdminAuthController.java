package com.mall.controller.admin;

import com.mall.common.result.Result;
import com.mall.common.utils.CurrentUserUtils;
import com.mall.dto.LoginDTO;
import com.mall.entity.UmsAdmin;
import com.mall.entity.UmsMenu;
import com.mall.service.UmsAdminService;
import com.mall.vo.LoginVO;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import org.springframework.web.bind.annotation.*;

import javax.validation.Valid;
import java.util.List;

@RestController
@RequestMapping("/v1/admin/auth")
@Tag(name = "管理后台认证接口")
public class AdminAuthController {

    private final UmsAdminService adminService;

    public AdminAuthController(UmsAdminService adminService) {
        this.adminService = adminService;
    }

    @PostMapping("/login")
    @Operation(summary = "管理员登录")
    public Result<LoginVO> login(@Valid @RequestBody LoginDTO dto) {
        return Result.success(adminService.login(dto));
    }

    @GetMapping("/info")
    @Operation(summary = "获取管理员信息")
    public Result<?> getAdminInfo() {
        Long adminId = CurrentUserUtils.getUserId();
        UmsAdmin admin = adminService.getCurrentAdmin(adminId);
        List<String> permissions = adminService.getAdminPermissions(adminId);
        List<UmsMenu> menus = adminService.getAdminMenus(adminId);
        return Result.success(new java.util.HashMap<String, Object>() {{
            put("admin", admin);
            put("permissions", permissions);
            put("menus", menus);
        }});
    }

    @GetMapping("/menus")
    @Operation(summary = "获取管理员菜单")
    public Result<List<UmsMenu>> getMenus() {
        return Result.success(adminService.getAdminMenus(CurrentUserUtils.getUserId()));
    }

    @PostMapping("/logout")
    @Operation(summary = "管理员登出")
    public Result<Void> logout() {
        return Result.success();
    }
}
