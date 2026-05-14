package com.mall.entity;

import com.baomidou.mybatisplus.annotation.*;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;

import java.time.LocalDateTime;

@Data
@TableName("oms_order_log")
@Schema(defaultValue = "订单操作日志")
public class OmsOrderLog {

    @TableId(type = IdType.AUTO)
    private Long id;

    private Long orderId;
    private String operator;
    private String action;
    private String note;

    @TableField(fill = FieldFill.INSERT)
    private LocalDateTime createdAt;
}
