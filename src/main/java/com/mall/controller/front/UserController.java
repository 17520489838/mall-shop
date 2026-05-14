package com.mall.controller.front;

import com.mall.common.result.Result;
import com.mall.common.utils.CurrentUserUtils;
import com.mall.dto.AddressDTO;
import com.mall.entity.UmsAddress;
import com.mall.entity.UmsUser;
import com.mall.service.*;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.extern.slf4j.Slf4j;
import org.springframework.web.bind.annotation.*;

import javax.validation.Valid;
import java.util.List;
import java.util.Map;

@Slf4j
@RestController
@RequestMapping("/v1/user")
@Tag(name = "用户中心接口")
public class UserController {

    private final UmsUserService userService;
    private final UmsAddressService addressService;
    private final UmsFavoriteService favoriteService;

    public UserController(UmsUserService userService, UmsAddressService addressService,
                          UmsFavoriteService favoriteService) {
        this.userService = userService;
        this.addressService = addressService;
        this.favoriteService = favoriteService;
    }

    // ========== 个人信息 ==========

    @GetMapping("/info")
    @Operation(summary = "获取用户信息")
    public Result<UmsUser> getUserInfo() {
        Long userId = CurrentUserUtils.getUserId();
        return Result.success(userService.getCurrentUser(userId));
    }

    @PutMapping("/info")
    @Operation(summary = "更新用户信息")
    public Result<Void> updateUserInfo(@RequestBody UmsUser user) {
        user.setId(CurrentUserUtils.getUserId());
        userService.updateUserInfo(user);
        return Result.success();
    }

    @PutMapping("/password")
    @Operation(summary = "修改密码")
    public Result<String> updatePassword(@RequestBody Map<String, String> params) {
        userService.updatePassword(
                CurrentUserUtils.getUserId(),
                params.get("oldPassword"),
                params.get("newPassword"));
        return Result.success("密码修改成功");
    }

    // ========== 收货地址 ==========

    @GetMapping("/addresses")
    @Operation(summary = "获取地址列表")
    public Result<List<UmsAddress>> listAddresses() {
        return Result.success(addressService.listAddresses(CurrentUserUtils.getUserId()));
    }

    @PostMapping("/addresses")
    @Operation(summary = "添加地址")
    public Result<String> addAddress(@Valid @RequestBody AddressDTO addressDTO) {
        addressService.addAddress(CurrentUserUtils.getUserId(), addressDTO);
        return Result.success("添加成功");
    }

    @PutMapping("/addresses/{id}")
    @Operation(summary = "更新地址")
    public Result<String> updateAddress(@PathVariable Long id, @Valid @RequestBody AddressDTO addressDTO) {
        addressService.updateAddress(CurrentUserUtils.getUserId(), id, addressDTO);
        return Result.success("更新成功");
    }

    @DeleteMapping("/addresses/{id}")
    @Operation(summary = "删除地址")
    public Result<String> deleteAddress(@PathVariable Long id) {
        addressService.deleteAddress(CurrentUserUtils.getUserId(), id);
        return Result.success("删除成功");
    }

    @PutMapping("/addresses/{id}/default")
    @Operation(summary = "设为默认地址")
    public Result<Void> setDefaultAddress(@PathVariable Long id) {
        addressService.setDefault(CurrentUserUtils.getUserId(), id);
        return Result.success();
    }

    // ========== 收藏 ==========

    @PostMapping("/favorites/{productId}")
    @Operation(summary = "切换收藏状态")
    public Result<Void> toggleFavorite(@PathVariable Long productId) {
        favoriteService.toggleFavorite(CurrentUserUtils.getUserId(), productId);
        return Result.success();
    }

    @GetMapping("/favorites")
    @Operation(summary = "收藏列表")
    public Result<?> listFavorites(@RequestParam(defaultValue = "1") Integer pageNum,
                                   @RequestParam(defaultValue = "10") Integer pageSize) {
        return Result.success(favoriteService.listFavorites(CurrentUserUtils.getUserId(), pageNum, pageSize));
    }
}
