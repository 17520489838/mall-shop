package com.mall.vo;

import com.mall.entity.OmsCartItem;
import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;

import java.math.BigDecimal;
import java.util.List;

@Data
@Schema(description = "购物车")
public class CartVO {

    @Schema(description = "购物车商品列表")
    private List<OmsCartItem> items;

    @Schema(description = "选中商品总金额")
    private BigDecimal checkedTotal;

    @Schema(description = "选中商品总数")
    private Integer checkedCount;

    @Schema(description = "购物车商品总数")
    private Integer totalCount;
}
