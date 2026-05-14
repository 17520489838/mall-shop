package com.mall.entity;

import com.baomidou.mybatisplus.annotation.*;
import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;

import java.time.LocalDateTime;

@Data
@TableName("pms_comment")
@Schema(description = "商品评价")
public class PmsComment {

    @TableId(type = IdType.AUTO)
    @Schema(description = "评价ID")
    private Long id;

    @Schema(description = "商品ID")
    private Long productId;

    @Schema(description = "用户ID")
    private Long userId;

    @Schema(description = "订单ID")
    private Long orderId;

    @Schema(description = "评价内容")
    private String content;

    @Schema(description = "评分: 1-5")
    private Integer rating;

    @Schema(description = "晒图")
    private String pics;

    @Schema(description = "是否匿名: 0-否, 1-是")
    private Integer isAnonymous;

    @Schema(description = "状态: 0-待审核, 1-已通过, 2-未通过")
    private Integer status;

    @Schema(description = "商家回复")
    private String replyContent;

    @Schema(description = "回复时间")
    private LocalDateTime replyTime;

    @Schema(description = "点赞数")
    private Integer likeCount;

    @TableField(fill = FieldFill.INSERT)
    @Schema(description = "创建时间")
    private LocalDateTime createdAt;

    @TableField(fill = FieldFill.INSERT_UPDATE)
    @Schema(description = "更新时间")
    private LocalDateTime updatedAt;

    @TableLogic
    @Schema(description = "逻辑删除")
    private Integer deleted;

    /** 用户名(非数据库字段) */
    @TableField(exist = false)
    private String nickname;

    /** 用户头像(非数据库字段) */
    @TableField(exist = false)
    private String avatar;
}
