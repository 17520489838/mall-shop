package com.mall.common.security;

import com.mall.common.result.Result;
import com.mall.common.utils.ResultSerializer;
import org.springframework.security.access.AccessDeniedException;
import org.springframework.security.web.access.AccessDeniedHandler;
import org.springframework.stereotype.Component;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;

/**
 * JWT权限不足处理 - 已登录但无权限
 */
@Component
public class JwtAccessDeniedHandler implements AccessDeniedHandler {

    @Override
    public void handle(HttpServletRequest request, HttpServletResponse response,
                       AccessDeniedException accessDeniedException) throws IOException {
        response.setContentType("application/json;charset=UTF-8");
        response.setStatus(HttpServletResponse.SC_FORBIDDEN);
        Result<Void> result = Result.forbidden(accessDeniedException.getMessage());
        response.getWriter().write(ResultSerializer.toJson(result));
    }
}
