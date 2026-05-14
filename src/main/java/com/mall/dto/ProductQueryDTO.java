package com.mall.dto;


import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;

import java.math.BigDecimal;

@Data
@Schema(description = "商品查询参数")
public class ProductQueryDTO {

    @Schema(description = "分类ID")
    private Long categoryId;

    @Schema(description = "品牌ID")
    private Long brandId;

    @Schema(description = "关键词")
    private String keyword;

    @Schema(description = "最低价格")
    private BigDecimal minPrice;

    @Schema(description = "最高价格")
    private BigDecimal maxPrice;

    @Schema(description = "是否新品")
    private Integer isNew;

    @Schema(description = "是否热销")
    private Integer isHot;

    @Schema(description = "是否推荐")
    private Integer isRecommend;

    @Schema(description = "排序字段: price/sales/created_at")
    private String sortField;

    @Schema(description = "排序方向: asc/desc")
    private String sortOrder = "desc";

    @Schema(description = "页码")
    private Integer pageNum = 1;

    @Schema(description = "每页条数")
    private Integer pageSize = 20;

    @Schema(description = "商品状态")
    private Integer status;


}
