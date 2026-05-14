package com.mall.entity;

import com.baomidou.mybatisplus.annotation.*;
import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;

import java.time.LocalDateTime;

@Data
@TableName("pms_brand")
@Schema(description = "商品品牌")
public class PmsBrand {

    @TableId(type = IdType.AUTO)
    @Schema(description = "品牌ID")
    private Long id;

    @Schema(description = "品牌名称")
    private String name;

    @Schema(description = "品牌logo")
    private String logo;

    @Schema(description = "品牌描述")
    private String description;

    @Schema(description = "排序值")
    private Integer sortOrder;

    @Schema(description = "状态: 0-隐藏, 1-显示")
    private Integer status;

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
