package com.mall.controller.front;

import com.mall.common.result.Result;
import com.mall.common.utils.CurrentUserUtils;
import com.mall.dto.ProductQueryDTO;
import com.mall.entity.PmsProduct;
import com.mall.service.PmsProductService;
import com.mall.vo.ProductDetailVO;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/v1/products")
@Tag(name = "商品接口")
public class ProductController {

    private final PmsProductService productService;

    public ProductController(PmsProductService productService) {
        this.productService = productService;
    }

    @GetMapping("/{id}")
    @Operation(summary = "商品详情")
    public Result<ProductDetailVO> getProductDetail(@PathVariable Long id) {
        Long userId = CurrentUserUtils.getUserId();
        return Result.success(productService.getProductDetail(id, userId));
    }

    @GetMapping
    @Operation(summary = "商品列表/搜索")
    public Result<?> listProducts(ProductQueryDTO queryDTO) {
        return Result.success(productService.listProducts(queryDTO));
    }

    @GetMapping("/hot")
    @Operation(summary = "热销商品")
    public Result<List<PmsProduct>> getHotProducts(@RequestParam(defaultValue = "8") int limit) {
        return Result.success(productService.getHotProducts(limit));
    }

    @GetMapping("/new")
    @Operation(summary = "新品推荐")
    public Result<List<PmsProduct>> getNewProducts(@RequestParam(defaultValue = "8") int limit) {
        return Result.success(productService.getNewProducts(limit));
    }

    @GetMapping("/recommend")
    @Operation(summary = "推荐商品")
    public Result<List<PmsProduct>> getRecommendProducts(@RequestParam(defaultValue = "8") int limit) {
        return Result.success(productService.getRecommendProducts(limit));
    }
}
