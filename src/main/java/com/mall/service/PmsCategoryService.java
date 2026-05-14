package com.mall.service;

import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.mall.entity.PmsCategory;

import java.util.List;

/**
 * 分类服务接口
 */
public interface PmsCategoryService {

    List<PmsCategory> listCategories();

    List<PmsCategory> treeCategories();

    PmsCategory getCategory(Long id);

    void createCategory(PmsCategory category);

    void updateCategory(PmsCategory category);

    void deleteCategory(Long id);

    Page<PmsCategory> adminListCategories(Integer pageNum, Integer pageSize);
}
