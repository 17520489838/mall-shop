package com.mall.entity;

import com.baomidou.mybatisplus.annotation.*;
import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;

import java.time.LocalDateTime;
import java.util.List;

@Data
@TableName("pms_category")
@Schema(description = "商品分类")
public class PmsCategory {

    @TableId(type = IdType.AUTO)
    @Schema(description = "分类ID")
    private Long id;

    @Schema(description = "父分类ID, 0表示顶级")
    private Long parentId;

    @Schema(description = "分类名称")
    private String name;

    @Schema(description = "图标")
    private String icon;

    @Schema(description = "图片")
    private String image;

    @Schema(description = "排序值")
    private Integer sortOrder;

    @Schema(description = "层级: 1-一级, 2-二级, 3-三级")
    private Integer level;

    @Schema(description = "状态: 0-隐藏, 1-显示")
    private Integer status;

    @Schema(description = "商品数量")
    private Integer productCount;

    @TableField(fill = FieldFill.INSERT)
    @Schema(description = "创建时间")
    private LocalDateTime createdAt;

    @TableField(fill = FieldFill.INSERT_UPDATE)
    @Schema(description = "更新时间")
    private LocalDateTime updatedAt;

    @TableLogic
    @Schema(description = "逻辑删除")
    private Integer deleted;

    /** 子分类列表(非数据库字段) */
    @TableField(exist = false)
    private List<PmsCategory> children;
}
