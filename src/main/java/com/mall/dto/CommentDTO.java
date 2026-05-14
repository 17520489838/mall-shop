package com.mall.dto;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;

import javax.validation.constraints.Max;
import javax.validation.constraints.Min;
import javax.validation.constraints.NotNull;

@Data
@Schema(description = "评价参数")
public class CommentDTO {

    @NotNull(message = "订单ID不能为空")
    @Schema(description = "订单ID", required = true)
    private Long orderId;

    @NotNull(message = "商品ID不能为空")
    @Schema(description = "商品ID", required = true)
    private Long productId;

    @Schema(description = "评价内容")
    private String content;

    @NotNull(message = "评分不能为空")
    @Min(value = 1, message = "评分最低1分")
    @Max(value = 5, message = "评分最高5分")
    @Schema(description = "评分", required = true)
    private Integer rating;

    @Schema(description = "晒图(JSON数组)")
    private String pics;

    @Schema(description = "是否匿名")
    private Integer isAnonymous;
}
