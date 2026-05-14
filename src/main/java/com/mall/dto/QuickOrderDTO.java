package com.mall.dto;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;

import javax.validation.constraints.Min;
import javax.validation.constraints.NotNull;

@Data
@Schema(description = "立即购买参数")
public class QuickOrderDTO {

    @NotNull(message = "商品ID不能为空")
    @Schema(description = "商品ID", required = true)
    private Long productId;

    @Min(value = 1, message = "数量至少为1")
    @Schema(description = "购买数量")
    private Integer quantity = 1;

    @Schema(description = "商品规格ID")
    private Long specId;
}
