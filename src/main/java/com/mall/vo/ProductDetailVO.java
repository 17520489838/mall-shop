package com.mall.vo;

import com.mall.entity.PmsComment;
import com.mall.entity.PmsProduct;
import com.mall.entity.PmsProductSpec;
import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;

import java.util.List;

@Data
@Schema(description = "商品详情")
public class ProductDetailVO {

    @Schema(description = "商品信息")
    private PmsProduct product;

    @Schema(description = "商品规格列表")
    private List<PmsProductSpec> specs;

    @Schema(description = "商品评价列表")
    private List<PmsComment> comments;

    @Schema(description = "评价总数")
    private Long commentCount;

    @Schema(description = "平均评分")
    private Double avgRating;

    @Schema(description = "是否已收藏")
    private Boolean isFavorited;
}
