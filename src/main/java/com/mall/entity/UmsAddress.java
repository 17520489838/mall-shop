package com.mall.entity;

import com.baomidou.mybatisplus.annotation.*;
import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;

import java.time.LocalDateTime;

@Data
@TableName("ums_address")
@Schema(description = "用户地址")
public class UmsAddress {

    @TableId(type = IdType.AUTO)
    @Schema(description = "地址ID")
    private Long id;

    @Schema(description = "用户ID")
    private Long userId;

    @Schema(description = "收货人姓名")
    private String name;

    @Schema(description = "收货人电话")
    private String phone;

    @Schema(description = "省")
    private String province;

    @Schema(description = "市")
    private String city;

    @Schema(description = "区/县")
    private String district;

    @Schema(description = "详细地址")
    private String detail;

    @Schema(description = "邮编")
    private String zipCode;

    @Schema(description = "是否默认: 0-否, 1-是")
    private Integer isDefault;

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
