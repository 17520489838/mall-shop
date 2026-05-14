package com.mall.entity;

import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;
import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;

@Data
@TableName("ums_admin_role")
@Schema(description = "管理员角色关联")
public class UmsAdminRole {

    @TableId(type = IdType.AUTO)
    private Long id;

    private Long adminId;

    private Long roleId;
}
