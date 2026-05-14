package com.mall.entity;

import com.baomidou.mybatisplus.annotation.*;
import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.List;

@Data
@TableName("oms_order")
@Schema(description = "订单")
public class OmsOrder {

    @TableId(type = IdType.AUTO)
    @Schema(description = "订单ID")
    private Long id;

    @Schema(description = "订单编号")
    private String orderNo;

    @Schema(description = "用户ID")
    private Long userId;

    @Schema(description = "订单总金额")
    private BigDecimal totalAmount;

    @Schema(description = "应付金额")
    private BigDecimal payAmount;

    @Schema(description = "运费")
    private BigDecimal freightAmount;

    @Schema(description = "优惠金额")
    private BigDecimal discountAmount;

    @Schema(description = "支付方式: 1-在线支付, 2-货到付款")
    private Integer payType;

    @Schema(description = "订单状态: 0-待付款, 1-已付款, 2-已发货, 3-已完成, 4-已取消, 5-已退款")
    private Integer status;

    @Schema(description = "物流公司")
    private String deliveryCompany;

    @Schema(description = "物流单号")
    private String deliveryNo;

    @Schema(description = "发货时间")
    private LocalDateTime deliveryTime;

    @Schema(description = "收货人姓名")
    private String receiveName;

    @Schema(description = "收货人电话")
    private String receivePhone;

    @Schema(description = "省")
    private String receiveProvince;

    @Schema(description = "市")
    private String receiveCity;

    @Schema(description = "区/县")
    private String receiveDistrict;

    @Schema(description = "详细地址")
    private String receiveDetail;

    @Schema(description = "邮编")
    private String receiveZipCode;

    @Schema(description = "订单备注")
    private String remark;

    @Schema(description = "购物车项ID列表(逗号分隔)")
    private String cartItemIds;

    @Schema(description = "支付时间")
    private LocalDateTime payTime;

    @Schema(description = "完成时间")
    private LocalDateTime finishTime;

    @Schema(description = "取消时间")
    private LocalDateTime cancelTime;

    @Schema(description = "取消原因")
    private String cancelReason;

    @Schema(description = "退款原因")
    private String refundReason;

    @Schema(description = "退款金额")
    private BigDecimal refundAmount;

    @TableField(fill = FieldFill.INSERT)
    @Schema(description = "创建时间")
    private LocalDateTime createdAt;

    @TableField(fill = FieldFill.INSERT_UPDATE)
    @Schema(description = "更新时间")
    private LocalDateTime updatedAt;

    @TableLogic
    @Schema(description = "逻辑删除")
    private Integer deleted;

    /** 订单商品列表(非数据库字段) */
    @TableField(exist = false)
    private List<OmsOrderItem> items;
}
