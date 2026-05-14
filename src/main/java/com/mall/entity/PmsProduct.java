package com.mall.entity;

import com.baomidou.mybatisplus.annotation.*;
import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.List;

@Data
@TableName("pms_product")
@Schema(description = "商品")
public class PmsProduct {

    @TableId(type = IdType.AUTO)
    @Schema(description = "商品ID")
    private Long id;

    @Schema(description = "品牌ID")
    private Long brandId;

    @Schema(description = "分类ID")
    private Long categoryId;

    @Schema(description = "商品名称")
    private String name;

    @Schema(description = "副标题")
    private String subtitle;

    @Schema(description = "商品描述")
    private String description;

    @Schema(description = "关键词")
    private String keywords;

    @Schema(description = "价格")
    private BigDecimal price;

    @Schema(description = "原价")
    private BigDecimal originalPrice;

    @Schema(description = "库存")
    private Integer stock;

    @Schema(description = "销量")
    private Integer sales;

    @Schema(description = "主图")
    private String pic;

    @Schema(description = "商品相册")
    private String album;

    @Schema(description = "单位")
    private String unit;

    @Schema(description = "重量(kg)")
    private BigDecimal weight;

    @Schema(description = "是否新品: 0-否, 1-是")
    private Integer isNew;

    @Schema(description = "是否热销: 0-否, 1-是")
    private Integer isHot;

    @Schema(description = "是否推荐: 0-否, 1-是")
    private Integer isRecommend;

    @Schema(description = "状态: 0-下架, 1-上架")
    private Integer status;

    @Schema(description = "排序值")
    private Integer sortOrder;

    @TableField(fill = FieldFill.INSERT)
    @Schema(description = "创建时间")
    private LocalDateTime createdAt;

    @TableField(fill = FieldFill.INSERT_UPDATE)
    @Schema(description = "更新时间")
    private LocalDateTime updatedAt;

    @TableLogic
    @Schema(description = "逻辑删除")
    private Integer deleted;

    /** 分类名称(非数据库字段) */
    @TableField(exist = false)
    private String categoryName;

    /** 品牌名称(非数据库字段) */
    @TableField(exist = false)
    private String brandName;

    /** 商品规格列表(非数据库字段) */
    @TableField(exist = false)
    private List<PmsProductSpec> specs;
}
