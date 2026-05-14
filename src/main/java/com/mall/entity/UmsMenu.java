package com.mall.entity;

import com.baomidou.mybatisplus.annotation.*;
import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;

import java.time.LocalDateTime;
import java.util.List;

@Data
@TableName("ums_menu")
@Schema(description = "菜单")
public class UmsMenu {

    @TableId(type = IdType.AUTO)
    private Long id;

    private Long parentId;
    private String name;
    private String url;
    private String icon;
    private String permission;

    @Schema(description = "类型: 1-目录, 2-菜单, 3-按钮")
    private Integer type;

    private Integer sortOrder;

    @Schema(description = "状态: 0-隐藏, 1-显示")
    private Integer status;

    @TableField(fill = FieldFill.INSERT)
    private LocalDateTime createdAt;

    @TableField(fill = FieldFill.INSERT_UPDATE)
    private LocalDateTime updatedAt;

    @TableField(exist = false)
    private List<UmsMenu> children;
}
