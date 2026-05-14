package com.mall.common.security;

import lombok.AllArgsConstructor;
import lombok.Data;

import java.security.Principal;

/**
 * 当前登录用户主体
 */
@Data
@AllArgsConstructor
public class UserPrincipal implements Principal {

    private Long userId;
    private String username;
    private String type;

    @Override
    public String getName() {
        return username;
    }
}
