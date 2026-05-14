package com.mall.controller.admin;

import com.mall.common.result.Result;
import com.mall.service.UmsAdminService;
import com.mall.vo.DashboardVO;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/v1/admin/statistics")
@Tag(name = "管理后台数据统计接口")
public class AdminStatisticsController {

    private final UmsAdminService adminService;

    public AdminStatisticsController(UmsAdminService adminService) {
        this.adminService = adminService;
    }

    @GetMapping("/dashboard")
    @Operation(summary = "仪表盘数据")
    public Result<DashboardVO> getDashboard() {
        return Result.success(adminService.getDashboardData());
    }
}
