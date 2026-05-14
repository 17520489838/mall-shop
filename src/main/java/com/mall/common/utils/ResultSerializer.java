package com.mall.common.utils;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.datatype.jsr310.JavaTimeModule;
import com.mall.common.result.Result;

/**
 * Result序列化工具 - 用于JwtAuthenticationEntryPoint中的toString
 */
public class ResultSerializer {

    private static final ObjectMapper mapper = new ObjectMapper();

    static {
        mapper.registerModule(new JavaTimeModule());
    }

    public static String toJson(Result<?> result) {
        try {
            return mapper.writeValueAsString(result);
        } catch (JsonProcessingException e) {
            return "{\"code\":500,\"message\":\"系统内部错误\"}";
        }
    }
}
