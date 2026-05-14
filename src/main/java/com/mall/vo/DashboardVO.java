package com.mall.vo;


import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;

import java.math.BigDecimal;
import java.util.List;
import java.util.Map;

@Data
@Schema(description = "仪表盘数据")
public class DashboardVO {

    @Schema(description = "今日订单数")
    private Long todayOrderCount;

    @Schema(description = "今日销售额")
    private BigDecimal todaySalesAmount;

    @Schema(description = "总订单数")
    private Long totalOrderCount;

    @Schema(description = "总用户数")
    private Long totalUserCount;

    @Schema(description = "总商品数")
    private Long totalProductCount;

    @Schema(description = "近7日订单趋势")
    private List<Map<String, Object>> orderTrend;

    @Schema(description = "热销商品TOP10")
    private List<Map<String, Object>> hotProducts;

    @Schema(description = "订单状态分布")
    private List<Map<String, Object>> orderStatusDistribution;
}
