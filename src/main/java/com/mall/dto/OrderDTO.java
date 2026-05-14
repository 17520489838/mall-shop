package com.mall.dto;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;

import javax.validation.constraints.Min;
import javax.validation.constraints.NotNull;
import java.util.List;

@Data
@Schema(description = "创建订单参数")
public class OrderDTO {

    @NotNull(message = "收货地址不能为空")
    @Schema(description = "收货地址ID", required = true)
    private Long addressId;

    @Schema(description = "支付方式: 1-在线支付, 2-货到付款")
    private Integer payType = 1;

    @Schema(description = "订单备注")
    private String remark;

    @Schema(description = "购物车项ID列表(为空则购买所有选中)")
    private List<Long> cartItemIds;

    // 以下为立即购买(直接下单)字段 — 不为空时跳过购物车
    @Schema(description = "直接购买: 商品ID")
    private Long productId;

    @Min(value = 1, message = "数量不能少于1")
    @Schema(description = "直接购买: 数量")
    private Integer quantity;

    @Schema(description = "直接购买: 规格ID")
    private Long specId;
}
