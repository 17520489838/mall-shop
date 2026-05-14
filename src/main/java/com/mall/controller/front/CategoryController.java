package com.mall.controller.front;

import com.mall.common.result.Result;
import com.mall.entity.PmsCategory;
import com.mall.service.PmsCategoryService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

@RestController
@RequestMapping("/v1/categories")
@Tag(name = "分类接口")
public class CategoryController {

    private final PmsCategoryService categoryService;

    public CategoryController(PmsCategoryService categoryService) {
        this.categoryService = categoryService;
    }

    @GetMapping
    @Operation(summary = "获取所有分类")
    public Result<List<PmsCategory>> listCategories() {
        return Result.success(categoryService.listCategories());
    }

    @GetMapping("/tree")
    @Operation(summary = "获取分类树")
    public Result<List<PmsCategory>> treeCategories() {
        return Result.success(categoryService.treeCategories());
    }
}
