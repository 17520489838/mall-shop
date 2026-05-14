package com.mall.entity;

import com.baomidou.mybatisplus.annotation.*;
import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;

import java.math.BigDecimal;
import java.time.LocalDateTime;

@Data
@TableName("oms_cart_item")
@Schema(description = "购物车项")
public class OmsCartItem {

    @TableId(type = IdType.AUTO)
    @Schema(description = "购物车项ID")
    private Long id;

    @Schema(description = "用户ID")
    private Long userId;

    @Schema(description = "商品ID")
    private Long productId;

    @Schema(description = "商品规格ID")
    private Long productSpecId;

    @Schema(description = "商品名称")
    private String productName;

    @Schema(description = "商品图片")
    private String productPic;

    @Schema(description = "商品单价(加入时)")
    private BigDecimal productPrice;

    @Schema(description = "商品规格描述")
    private String productSku;

    @Schema(description = "数量")
    private Integer quantity;

    @Schema(description = "是否选中: 0-未选中, 1-已选中")
    private Integer isChecked;

    @TableField(fill = FieldFill.INSERT)
    @Schema(description = "创建时间")
    private LocalDateTime createdAt;

    @TableField(fill = FieldFill.INSERT_UPDATE)
    @Schema(description = "更新时间")
    private LocalDateTime updatedAt;

    @TableLogic
    @Schema(description = "逻辑删除")
    private Integer deleted;
}
