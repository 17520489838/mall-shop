package com.mall.common.utils;

import com.mall.common.security.UserPrincipal;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;

/**
 * 获取当前登录用户工具类
 */
public class CurrentUserUtils {

    private CurrentUserUtils() {}

    /**
     * 获取当前用户ID
     */
    public static Long getUserId() {
        UserPrincipal principal = getUserPrincipal();
        return principal != null ? principal.getUserId() : null;
    }

    /**
     * 获取当前用户名
     */
    public static String getUsername() {
        UserPrincipal principal = getUserPrincipal();
        return principal != null ? principal.getUsername() : null;
    }

    /**
     * 获取当前用户类型
     */
    public static String getUserType() {
        UserPrincipal principal = getUserPrincipal();
        return principal != null ? principal.getType() : null;
    }

    /**
     * 获取当前用户主体
     */
    public static UserPrincipal getUserPrincipal() {
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        if (authentication != null && authentication.getPrincipal() instanceof UserPrincipal) {
            return (UserPrincipal) authentication.getPrincipal();
        }
        return null;
    }
}
