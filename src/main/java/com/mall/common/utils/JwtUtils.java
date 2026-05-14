package com.mall.common.utils;

import io.jsonwebtoken.*;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

import java.util.Date;
import java.util.HashMap;
import java.util.Map;

/**
 * JWT工具类
 */
@Slf4j
@Component
public class JwtUtils {

    @Value("${jwt.secret}")
    private String secret;

    @Value("${jwt.expiration:86400000}")
    private long expiration;

    @Value("${jwt.admin-expiration:7200000}")
    private long adminExpiration;

    /**
     * 生成用户token
     */
    public String generateUserToken(Long userId, String username) {
        Map<String, Object> claims = new HashMap<>();
        claims.put("userId", userId);
        claims.put("username", username);
        claims.put("type", "user");
        return generateToken(claims, expiration);
    }

    /**
     * 生成管理员token
     */
    public String generateAdminToken(Long adminId, String username) {
        Map<String, Object> claims = new HashMap<>();
        claims.put("userId", adminId);
        claims.put("username", username);
        claims.put("type", "admin");
        return generateToken(claims, adminExpiration);
    }

    /**
     * 生成token
     */
    private String generateToken(Map<String, Object> claims, long expiration) {
        return Jwts.builder()
                .setClaims(claims)
                .setIssuedAt(new Date())
                .setExpiration(new Date(System.currentTimeMillis() + expiration))
                .signWith(SignatureAlgorithm.HS512, secret)
                .compact();
    }

    /**
     * 从token中解析claims
     */
    public Claims parseToken(String token) {
        try {
            return Jwts.parser()
                    .setSigningKey(secret)
                    .parseClaimsJws(token)
                    .getBody();
        } catch (ExpiredJwtException e) {
            log.warn("token已过期: {}", e.getMessage());
            return null;
        } catch (JwtException e) {
            log.warn("token解析失败: {}", e.getMessage());
            return null;
        }
    }

    /**
     * 从token中获取用户ID
     */
    public Long getUserId(String token) {
        Claims claims = parseToken(token);
        return claims != null ? claims.get("userId", Long.class) : null;
    }

    /**
     * 从token中获取用户名
     */
    public String getUsername(String token) {
        Claims claims = parseToken(token);
        return claims != null ? claims.get("username", String.class) : null;
    }

    /**
     * 从token中获取类型
     */
    public String getType(String token) {
        Claims claims = parseToken(token);
        return claims != null ? claims.get("type", String.class) : null;
    }

    /**
     * 验证token是否有效
     */
    public boolean validateToken(String token) {
        try {
            Jwts.parser().setSigningKey(secret).parseClaimsJws(token);
            return true;
        } catch (JwtException | IllegalArgumentException e) {
            return false;
        }
    }

    /**
     * 获取token剩余过期时间(毫秒)
     */
    public long getExpirationMs(String token) {
        Claims claims = parseToken(token);
        if (claims == null) return 0;
        return claims.getExpiration().getTime() - System.currentTimeMillis();
    }
}
