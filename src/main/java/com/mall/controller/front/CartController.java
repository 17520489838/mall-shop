package com.mall.controller.front;

import com.mall.common.result.Result;
import com.mall.common.utils.CurrentUserUtils;
import com.mall.service.OmsCartItemService;
import com.mall.vo.CartVO;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("/v1/cart")
@Tag(name = "购物车接口")
public class CartController {

    private final OmsCartItemService cartService;

    public CartController(OmsCartItemService cartService) {
        this.cartService = cartService;
    }

    @GetMapping
    @Operation(summary = "获取购物车")
    public Result<CartVO> getCart() {
        return Result.success(cartService.getCart(CurrentUserUtils.getUserId()));
    }

    @PostMapping
    @Operation(summary = "添加商品到购物车")
    public Result<String> addItem(@RequestBody Map<String, Object> params) {
        Long productId = Long.valueOf(params.get("productId").toString());
        Long specId = params.get("specId") != null ? Long.valueOf(params.get("specId").toString()) : null;
        Integer quantity = Integer.valueOf(params.get("quantity").toString());
        cartService.addItem(CurrentUserUtils.getUserId(), productId, specId, quantity);
        return Result.success("已加入购物车");
    }

    @PutMapping("/{id}")
    @Operation(summary = "更新购物车商品数量")
    public Result<Void> updateQuantity(@PathVariable Long id, @RequestBody Map<String, Integer> params) {
        cartService.updateQuantity(CurrentUserUtils.getUserId(), id, params.get("quantity"));
        return Result.success();
    }

    @PutMapping("/{id}/check")
    @Operation(summary = "切换选中状态")
    public Result<Void> toggleCheck(@PathVariable Long id) {
        cartService.toggleCheck(CurrentUserUtils.getUserId(), id);
        return Result.success();
    }

    @PutMapping("/check-all")
    @Operation(summary = "全选/取消全选")
    public Result<Void> checkAll(@RequestBody Map<String, Boolean> params) {
        cartService.checkAll(CurrentUserUtils.getUserId(), params.getOrDefault("checked", true));
        return Result.success();
    }

    @DeleteMapping
    @Operation(summary = "删除购物车商品")
    public Result<Void> removeItems(@RequestBody List<Long> ids) {
        cartService.removeItem(CurrentUserUtils.getUserId(), ids);
        return Result.success();
    }

    @GetMapping("/count")
    @Operation(summary = "获取购物车商品数量")
    public Result<?> getCartCount() {
        CartVO cart = cartService.getCart(CurrentUserUtils.getUserId());
        return Result.success(new java.util.HashMap<String, Object>() {{
            put("count", cart.getTotalCount());
        }});
    }
}
