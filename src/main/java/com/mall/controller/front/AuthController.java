package com.mall.controller.front;

import com.mall.common.result.Result;
import com.mall.dto.LoginDTO;
import com.mall.dto.RegisterDTO;
import com.mall.service.UmsUserService;
import com.mall.vo.LoginVO;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.extern.slf4j.Slf4j;
import org.springframework.web.bind.annotation.*;

import javax.validation.Valid;

@Slf4j
@RestController
@RequestMapping("/v1/auth")
@Tag(name = "用户认证接口")
public class AuthController {

    private final UmsUserService userService;

    public AuthController(UmsUserService userService) {
        this.userService = userService;
    }

    @PostMapping("/register")
    @Operation(summary = "用户注册")
    public Result<String> register(@Valid @RequestBody RegisterDTO dto) {
        userService.register(dto);
        return Result.success("注册成功");
    }

    @PostMapping("/login")
    @Operation(summary = "用户登录")
    public Result<LoginVO> login(@Valid @RequestBody LoginDTO dto) {
        LoginVO loginVO = userService.login(dto);
        return Result.success(loginVO);
    }

    @PostMapping("/logout")
    @Operation(summary = "用户登出")
    public Result<Void> logout() {
        // JWT无状态，客户端删除token即可
        return Result.success();
    }
}
