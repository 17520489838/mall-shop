package com.mall.common.constant;

/**
 * 通用常量
 */
public class CommonConstant {

    private CommonConstant() {}

    /** 用户端token前缀 */
    public static final String USER_TOKEN_PREFIX = "Bearer ";
    /** 缓存key分隔符 */
    public static final String CACHE_SEPARATOR = ":";

    // ========== Redis缓存Key ==========
    /** 商品缓存 */
    public static final String CACHE_PRODUCT = "mall:product";
    /** 分类缓存 */
    public static final String CACHE_CATEGORY = "mall:category";
    /** 验证码缓存 */
    public static final String CACHE_CAPTCHA = "mall:captcha";
    /** 用户token缓存 */
    public static final String CACHE_USER_TOKEN = "mall:user:token";
    /** 管理员token缓存 */
    public static final String CACHE_ADMIN_TOKEN = "mall:admin:token";
    /** 热门商品缓存 */
    public static final String CACHE_HOT_PRODUCT = "mall:hot:product";
    /** 推荐商品缓存 */
    public static final String CACHE_RECOMMEND_PRODUCT = "mall:recommend:product";

    // ========== 订单状态 ==========
    /** 待付款 */
    public static final int ORDER_STATUS_PENDING = 0;
    /** 已付款 */
    public static final int ORDER_STATUS_PAID = 1;
    /** 已发货 */
    public static final int ORDER_STATUS_SHIPPED = 2;
    /** 已完成 */
    public static final int ORDER_STATUS_COMPLETED = 3;
    /** 已取消 */
    public static final int ORDER_STATUS_CANCELED = 4;
    /** 已退款 */
    public static final int ORDER_STATUS_REFUNDED = 5;

    // ========== 支付方式 ==========
    /** 在线支付 */
    public static final int PAY_TYPE_ONLINE = 1;
    /** 货到付款 */
    public static final int PAY_TYPE_COD = 2;
}
