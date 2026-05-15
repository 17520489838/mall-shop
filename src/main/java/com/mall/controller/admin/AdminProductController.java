package com.mall.controller.admin;

import com.mall.common.result.Result;
import com.mall.dto.ProductQueryDTO;
import com.mall.entity.PmsBrand;
import com.mall.entity.PmsCategory;
import com.mall.entity.PmsProduct;
import com.mall.service.*;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/v1/admin/products")
@Tag(name = "管理后台商品接口")
public class AdminProductController {

    private final PmsProductService productService;
    private final PmsCategoryService categoryService;
    private final PmsBrandService brandService;

    public AdminProductController(PmsProductService productService, PmsCategoryService categoryService,
                                   PmsBrandService brandService) {
        this.productService = productService;
        this.categoryService = categoryService;
        this.brandService = brandService;
    }

    // ========== 商品管理 ==========

    @GetMapping
    @Operation(summary = "商品列表")
    public Result<?> listProducts(ProductQueryDTO queryDTO) {
        return Result.success(productService.adminListProducts(queryDTO));
    }

    @GetMapping("/{id}")
    @Operation(summary = "商品详情")
    public Result<PmsProduct> getProduct(@PathVariable Long id) {
        return Result.success(productService.getProductDetail(id, null).getProduct());
    }

    @PostMapping
    @PreAuthorize("hasAuthority('product:create')")
    @Operation(summary = "新增商品")
    public Result<String> createProduct(@RequestBody PmsProduct product) {
        productService.createProduct(product);
        return Result.success("新增成功");
    }

    @PutMapping("/{id}")
    @PreAuthorize("hasAuthority('product:update')")
    @Operation(summary = "更新商品")
    public Result<String> updateProduct(@PathVariable Long id, @RequestBody PmsProduct product) {
        product.setId(id);
        productService.updateProduct(product);
        return Result.success("更新成功");
    }

    @DeleteMapping("/{id}")
    @PreAuthorize("hasAuthority('product:delete')")
    @Operation(summary = "删除商品")
    public Result<String> deleteProduct(@PathVariable Long id) {
        productService.deleteProduct(id);
        return Result.success("删除成功");
    }

    @PutMapping("/{id}/status")
    @Operation(summary = "上架/下架")
    public Result<String> toggleStatus(@PathVariable Long id) {
        productService.toggleProductStatus(id);
        return Result.success("操作成功");
    }

    // ========== 分类管理 ==========

    @GetMapping("/categories")
    @Operation(summary = "分类列表")
    public Result<?> listCategories(@RequestParam(defaultValue = "1") Integer pageNum,
                                    @RequestParam(defaultValue = "20") Integer pageSize) {
        return Result.success(categoryService.adminListCategories(pageNum, pageSize));
    }

    @GetMapping("/categories/tree")
    @Operation(summary = "分类树")
    public Result<?> listCategoryTree() {
        return Result.success(categoryService.treeCategories());
    }

    @PostMapping("/categories")
    @Operation(summary = "新增分类")
    public Result<String> createCategory(@RequestBody PmsCategory category) {
        categoryService.createCategory(category);
        return Result.success("新增成功");
    }

    @PutMapping("/categories/{id}")
    @Operation(summary = "更新分类")
    public Result<String> updateCategory(@PathVariable Long id, @RequestBody PmsCategory category) {
        category.setId(id);
        categoryService.updateCategory(category);
        return Result.success("更新成功");
    }

    @DeleteMapping("/categories/{id}")
    @Operation(summary = "删除分类")
    public Result<String> deleteCategory(@PathVariable Long id) {
        categoryService.deleteCategory(id);
        return Result.success("删除成功");
    }

    // ========== 品牌管理 ==========

    @GetMapping("/brands")
    @Operation(summary = "品牌列表")
    public Result<?> listBrands(@RequestParam(defaultValue = "1") Integer pageNum,
                                @RequestParam(defaultValue = "20") Integer pageSize,
                                @RequestParam(required = false) String keyword) {
        return Result.success(brandService.listBrands(pageNum, pageSize, keyword));
    }

    @GetMapping("/brands/all")
    @Operation(summary = "所有品牌")
    public Result<?> listAllBrands() {
        return Result.success(brandService.listAll());
    }

    @PostMapping("/brands")
    @Operation(summary = "新增品牌")
    public Result<String> createBrand(@RequestBody PmsBrand brand) {
        brandService.createBrand(brand);
        return Result.success("新增成功");
    }

    @PutMapping("/brands/{id}")
    @Operation(summary = "更新品牌")
    public Result<String> updateBrand(@PathVariable Long id, @RequestBody PmsBrand brand) {
        brand.setId(id);
        brandService.updateBrand(brand);
        return Result.success("更新成功");
    }

    @DeleteMapping("/brands/{id}")
    @Operation(summary = "删除品牌")
    public Result<String> deleteBrand(@PathVariable Long id) {
        brandService.deleteBrand(id);
        return Result.success("删除成功");
    }
}
