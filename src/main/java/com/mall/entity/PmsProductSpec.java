package com.mall.entity;

import com.baomidou.mybatisplus.annotation.*;
import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;

import java.math.BigDecimal;
import java.time.LocalDateTime;

@Data
@TableName("pms_product_spec")
@Schema(description = "商品规格")
public class PmsProductSpec {

    @TableId(type = IdType.AUTO)
    @Schema(description = "规格ID")
    private Long id;

    @Schema(description = "商品ID")
    private Long productId;

    @Schema(description = "规格名称")
    private String name;

    @Schema(description = "规格值")
    private String value;

    @Schema(description = "额外价格")
    private BigDecimal price;

    @Schema(description = "规格库存")
    private Integer stock;

    @Schema(description = "规格图片")
    private String pic;

    @TableField(fill = FieldFill.INSERT)
    @Schema(description = "创建时间")
    private LocalDateTime createdAt;
}
