package com.mall.common.annotation;

import java.lang.annotation.*;

/**
 * 接口限流注解
 */
@Target(ElementType.METHOD)
@Retention(RetentionPolicy.RUNTIME)
@Documented
public @interface RateLimit {

    /** 限制时间间隔(秒) */
    int time() default 60;

    /** 限制请求次数 */
    int count() default 10;
}
