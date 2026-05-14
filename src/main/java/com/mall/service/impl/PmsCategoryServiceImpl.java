package com.mall.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.mall.common.constant.CommonConstant;
import com.mall.common.exception.BusinessException;
import com.mall.common.result.ResultCode;
import com.mall.common.utils.RedisCache;
import com.mall.dao.PmsCategoryDao;
import com.mall.entity.PmsCategory;
import com.mall.service.PmsCategoryService;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.concurrent.TimeUnit;
import java.util.stream.Collectors;

@Service
public class PmsCategoryServiceImpl implements PmsCategoryService {

    private final PmsCategoryDao categoryDao;
    private final RedisCache redisCache;

    public PmsCategoryServiceImpl(PmsCategoryDao categoryDao, RedisCache redisCache) {
        this.categoryDao = categoryDao;
        this.redisCache = redisCache;
    }

    @Override
    public List<PmsCategory> listCategories() {
        String cacheKey = CommonConstant.CACHE_CATEGORY;
        List<PmsCategory> categories = redisCache.get(cacheKey);
        if (categories == null) {
            categories = categoryDao.selectList(
                    new LambdaQueryWrapper<PmsCategory>()
                            .eq(PmsCategory::getStatus, 1)
                            .orderByAsc(PmsCategory::getSortOrder));
            redisCache.set(cacheKey, categories, 2, TimeUnit.HOURS);
        }
        return categories;
    }

    @Override
    public List<PmsCategory> treeCategories() {
        List<PmsCategory> all = listCategories();
        return buildTree(all, 0L);
    }

    @Override
    public PmsCategory getCategory(Long id) {
        PmsCategory category = categoryDao.selectById(id);
        if (category == null) {
            throw new BusinessException(ResultCode.CATEGORY_NOT_EXIST);
        }
        return category;
    }

    @Override
    public void createCategory(PmsCategory category) {
        categoryDao.insert(category);
        redisCache.delete(CommonConstant.CACHE_CATEGORY);
    }

    @Override
    public void updateCategory(PmsCategory category) {
        categoryDao.updateById(category);
        redisCache.delete(CommonConstant.CACHE_CATEGORY);
    }

    @Override
    public void deleteCategory(Long id) {
        // 检查是否有子分类
        Long count = categoryDao.selectCount(
                new LambdaQueryWrapper<PmsCategory>().eq(PmsCategory::getParentId, id));
        if (count > 0) {
            throw new BusinessException(ResultCode.CATEGORY_HAS_CHILDREN);
        }
        categoryDao.deleteById(id);
        redisCache.delete(CommonConstant.CACHE_CATEGORY);
    }

    @Override
    public Page<PmsCategory> adminListCategories(Integer pageNum, Integer pageSize) {
        Page<PmsCategory> page = new Page<>(pageNum, pageSize);
        return categoryDao.selectPage(page,
                new LambdaQueryWrapper<PmsCategory>()
                        .orderByAsc(PmsCategory::getSortOrder));
    }

    private List<PmsCategory> buildTree(List<PmsCategory> all, Long parentId) {
        return all.stream()
                .filter(c -> parentId.equals(c.getParentId()))
                .peek(c -> c.setChildren(buildTree(all, c.getId())))
                .collect(Collectors.toList());
    }
}
