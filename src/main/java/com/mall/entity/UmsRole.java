package com.mall.entity;

import com.baomidou.mybatisplus.annotation.*;
import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;

import java.time.LocalDateTime;

@Data
@TableName("ums_role")
@Schema(description = "角色")
public class UmsRole {

    @TableId(type = IdType.AUTO)
    private Long id;

    private String name;
    private String code;
    private String description;

    @Schema(description = "状态: 0-禁用, 1-启用")
    private Integer status;

    @TableField(fill = FieldFill.INSERT)
    private LocalDateTime createdAt;

    @TableField(fill = FieldFill.INSERT_UPDATE)
    private LocalDateTime updatedAt;
}
