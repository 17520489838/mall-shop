package com.mall.common.result;

import lombok.AllArgsConstructor;
import lombok.Getter;

/**
 * 统一返回状态码枚举
 */
@Getter
@AllArgsConstructor
public enum ResultCode {

    SUCCESS(200, "操作成功"),
    FAILED(500, "操作失败"),
    VALIDATE_FAILED(400, "参数校验失败"),
    UNAUTHORIZED(401, "未登录或token已过期"),
    FORBIDDEN(403, "没有相关权限"),
    NOT_FOUND(404, "资源不存在"),

    // 用户模块 1001-1099
    USER_NOT_EXIST(1001, "用户不存在"),
    USER_ALREADY_EXIST(1002, "用户已存在"),
    USER_PASSWORD_ERROR(1003, "密码错误"),
    USER_ACCOUNT_DISABLED(1004, "账号已被禁用"),
    USER_PHONE_EXIST(1005, "手机号已注册"),
    USER_EMAIL_EXIST(1006, "邮箱已注册"),
    USER_CAPTCHA_ERROR(1007, "验证码错误"),
    USER_CAPTCHA_EXPIRED(1008, "验证码已过期"),
    USER_BALANCE_INSUFFICIENT(1009, "余额不足"),

    // 商品模块 2001-2099
    PRODUCT_NOT_EXIST(2001, "商品不存在"),
    PRODUCT_OUT_OF_STOCK(2002, "商品库存不足"),
    PRODUCT_OFF_SHELF(2003, "商品已下架"),
    CATEGORY_NOT_EXIST(2010, "分类不存在"),
    CATEGORY_HAS_CHILDREN(2011, "分类下有子分类，无法删除"),

    // 购物车模块 3001-3099
    CART_ITEM_NOT_EXIST(3001, "购物车商品不存在"),
    CART_IS_EMPTY(3002, "购物车为空"),

    // 订单模块 4001-4099
    ORDER_NOT_EXIST(4001, "订单不存在"),
    ORDER_STATUS_ERROR(4002, "订单状态异常"),
    ORDER_CANNOT_CANCEL(4003, "当前状态不允许取消"),
    ORDER_CANNOT_DELETE(4004, "当前状态不允许删除"),
    ORDER_ADDRESS_REQUIRED(4005, "请选择收货地址"),

    // 评价模块 5001-5099
    COMMENT_NOT_EXIST(5001, "评价不存在"),
    COMMENT_ALREADY_EXIST(5002, "已评价过该商品"),

    // 管理员模块 6001-6099
    ADMIN_NOT_EXIST(6001, "管理员不存在"),
    ADMIN_PASSWORD_ERROR(6002, "密码错误"),
    ADMIN_ACCOUNT_DISABLED(6003, "账号已被禁用"),
    ADMIN_NO_PERMISSION(6004, "没有权限"),
    ADMIN_USERNAME_EXIST(6005, "用户名已存在"),

    // 系统模块 9001-9099
    SYSTEM_ERROR(9001, "系统异常，请联系管理员"),
    FILE_UPLOAD_ERROR(9002, "文件上传失败"),
    FILE_SIZE_EXCEED(9003, "文件大小超出限制"),
    REQUEST_METHOD_ERROR(9004, "请求方法不支持"),
    FREQUENT_REQUEST(9005, "请求过于频繁");

    private final int code;
    private final String message;
}
