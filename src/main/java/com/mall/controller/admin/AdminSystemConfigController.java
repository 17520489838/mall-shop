package com.mall.controller.admin;

import com.mall.common.result.Result;
import com.mall.entity.SysConfig;
import com.mall.service.SysConfigService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import org.springframework.web.bind.annotation.*;

import java.util.List;

/**
 * 管理后台 - 系统配置接口
 */
@RestController
@RequestMapping("/v1/admin/config")
@Tag(name = "管理后台系统配置接口")
public class AdminSystemConfigController {

    private final SysConfigService sysConfigService;

    public AdminSystemConfigController(SysConfigService sysConfigService) {
        this.sysConfigService = sysConfigService;
    }

    @GetMapping
    @Operation(summary = "获取所有系统配置")
    public Result<List<SysConfig>> getConfigs() {
        return Result.success(sysConfigService.getAllConfigs());
    }

    @PutMapping
    @Operation(summary = "批量更新系统配置")
    public Result<String> updateConfigs(@RequestBody List<SysConfig> configs) {
        sysConfigService.updateConfig(configs);
        return Result.success("保存成功");
    }
}
