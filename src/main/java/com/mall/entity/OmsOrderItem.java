package com.mall.entity;

import com.baomidou.mybatisplus.annotation.*;
import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;

import java.math.BigDecimal;
import java.time.LocalDateTime;

@Data
@TableName("oms_order_item")
@Schema(description = "订单商品")
public class OmsOrderItem {

    @TableId(type = IdType.AUTO)
    @Schema(description = "ID")
    private Long id;

    @Schema(description = "订单ID")
    private Long orderId;

    @Schema(description = "订单编号")
    private String orderNo;

    @Schema(description = "商品ID")
    private Long productId;

    @Schema(description = "商品名称")
    private String productName;

    @Schema(description = "商品图片")
    private String productPic;

    @Schema(description = "商品单价")
    private BigDecimal productPrice;

    @Schema(description = "购买数量")
    private Integer productQuantity;

    @Schema(description = "商品规格")
    private String productSku;

    @Schema(description = "小计金额")
    private BigDecimal subtotal;

    @TableField(fill = FieldFill.INSERT)
    @Schema(description = "创建时间")
    private LocalDateTime createdAt;
}
