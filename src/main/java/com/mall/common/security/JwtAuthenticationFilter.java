package com.mall.common.security;

import com.mall.common.constant.CommonConstant;
import com.mall.common.utils.JwtUtils;
import com.mall.common.utils.RedisCache;
import io.jsonwebtoken.Claims;
import lombok.extern.slf4j.Slf4j;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Component;
import org.springframework.util.StringUtils;
import org.springframework.web.filter.OncePerRequestFilter;

import javax.servlet.FilterChain;
import javax.servlet.ServletException;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.util.Collections;
import java.util.List;
import java.util.stream.Collectors;

/**
 * JWT认证过滤器
 */
@Slf4j
@Component
public class JwtAuthenticationFilter extends OncePerRequestFilter {

    private final JwtUtils jwtUtils;
    private final RedisCache redisCache;

    public JwtAuthenticationFilter(JwtUtils jwtUtils, RedisCache redisCache) {
        this.jwtUtils = jwtUtils;
        this.redisCache = redisCache;
    }

    @Override
    protected void doFilterInternal(HttpServletRequest request, HttpServletResponse response,
                                    FilterChain filterChain) throws ServletException, IOException {
        String token = extractToken(request);

        if (StringUtils.hasText(token) && jwtUtils.validateToken(token)) {
            Claims claims = jwtUtils.parseToken(token);
            if (claims != null) {
                Long userId = claims.get("userId", Long.class);
                String username = claims.get("username", String.class);
                String type = claims.get("type", String.class);

                // 从缓存中获取权限
                List<String> permissions = Collections.emptyList();
                if ("admin".equals(type)) {
                    // 管理员权限从Redis获取
                    String cacheKey = CommonConstant.CACHE_ADMIN_TOKEN + CommonConstant.CACHE_SEPARATOR + userId;
                    permissions = redisCache.hGet(cacheKey, "permissions");
                    if (permissions == null) {
                        permissions = Collections.emptyList();
                    }
                }

                List<SimpleGrantedAuthority> authorities = permissions.stream()
                        .map(SimpleGrantedAuthority::new)
                        .collect(Collectors.toList());

                UsernamePasswordAuthenticationToken authentication =
                        new UsernamePasswordAuthenticationToken(
                                new UserPrincipal(userId, username, type),
                                null, authorities);
                authentication.setDetails(request);
                SecurityContextHolder.getContext().setAuthentication(authentication);
            }
        }

        filterChain.doFilter(request, response);
    }

    /**
     * 从请求头中提取token
     */
    private String extractToken(HttpServletRequest request) {
        String bearerToken = request.getHeader("Authorization");
        if (StringUtils.hasText(bearerToken) && bearerToken.startsWith(CommonConstant.USER_TOKEN_PREFIX)) {
            return bearerToken.substring(7);
        }
        return null;
    }
}
