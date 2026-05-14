package com.mall.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.mall.common.constant.CommonConstant;
import com.mall.common.exception.BusinessException;
import com.mall.common.result.ResultCode;
import com.mall.common.utils.RedisCache;
import com.mall.dao.*;
import com.mall.dto.ProductQueryDTO;
import com.mall.entity.*;
import com.mall.service.PmsProductService;
import com.mall.vo.ProductDetailVO;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.util.StringUtils;

import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.TimeUnit;
import java.util.stream.Collectors;

/**
 * 商品服务实现
 */
@Slf4j
@Service
public class PmsProductServiceImpl implements PmsProductService {

    private final PmsProductDao productDao;
    private final PmsProductSpecDao productSpecDao;
    private final PmsCategoryDao categoryDao;
    private final PmsBrandDao brandDao;
    private final PmsCommentDao commentDao;
    private final UmsFavoriteDao favoriteDao;
    private final RedisCache redisCache;

    public PmsProductServiceImpl(PmsProductDao productDao, PmsProductSpecDao productSpecDao,
                                  PmsCategoryDao categoryDao, PmsBrandDao brandDao,
                                  PmsCommentDao commentDao, UmsFavoriteDao favoriteDao,
                                  RedisCache redisCache) {
        this.productDao = productDao;
        this.productSpecDao = productSpecDao;
        this.categoryDao = categoryDao;
        this.brandDao = brandDao;
        this.commentDao = commentDao;
        this.favoriteDao = favoriteDao;
        this.redisCache = redisCache;
    }

    @Override
    public ProductDetailVO getProductDetail(Long productId, Long userId) {
        String cacheKey = CommonConstant.CACHE_PRODUCT + CommonConstant.CACHE_SEPARATOR + productId;

        // 尝试从缓存获取
        ProductDetailVO detailVO = redisCache.get(cacheKey);
        if (detailVO != null) {
            // 更新收藏状态
            if (userId != null) {
                UmsFavorite fav = favoriteDao.selectOne(
                        new LambdaQueryWrapper<UmsFavorite>()
                                .eq(UmsFavorite::getUserId, userId)
                                .eq(UmsFavorite::getProductId, productId));
                detailVO.setIsFavorited(fav != null);
            }
            return detailVO;
        }

        // 查询数据库
        PmsProduct product = productDao.selectById(productId);
        if (product == null || product.getDeleted() == 1) {
            throw new BusinessException(ResultCode.PRODUCT_NOT_EXIST);
        }

        // 查询分类和品牌名称
        PmsCategory category = categoryDao.selectById(product.getCategoryId());
        if (category != null) {
            product.setCategoryName(category.getName());
        }
        PmsBrand brand = brandDao.selectById(product.getBrandId());
        if (brand != null) {
            product.setBrandName(brand.getName());
        }

        // 查询规格
        List<PmsProductSpec> specs = productSpecDao.selectList(
                new LambdaQueryWrapper<PmsProductSpec>()
                        .eq(PmsProductSpec::getProductId, productId));

        // 查询评价
        List<PmsComment> comments = commentDao.selectList(
                new LambdaQueryWrapper<PmsComment>()
                        .eq(PmsComment::getProductId, productId)
                        .eq(PmsComment::getStatus, 1)
                        .orderByDesc(PmsComment::getCreatedAt)
                        .last("LIMIT 10"));

        // 统计评价
        Long commentCount = commentDao.selectCountByProductId(productId);
        Double avgRating = commentDao.selectAvgRatingByProductId(productId);

        // 收藏状态
        boolean isFavorited = false;
        if (userId != null) {
            UmsFavorite fav = favoriteDao.selectOne(
                    new LambdaQueryWrapper<UmsFavorite>()
                            .eq(UmsFavorite::getUserId, userId)
                            .eq(UmsFavorite::getProductId, productId));
            isFavorited = fav != null;
        }

        detailVO = new ProductDetailVO();
        detailVO.setProduct(product);
        detailVO.setSpecs(specs);
        detailVO.setComments(comments);
        detailVO.setCommentCount(commentCount != null ? commentCount : 0L);
        detailVO.setAvgRating(avgRating != null ? avgRating : 0.0);
        detailVO.setIsFavorited(isFavorited);

        // 缓存商品详情(不含评论)
        redisCache.set(cacheKey, detailVO, 1, TimeUnit.HOURS);

        return detailVO;
    }

    @Override
    public Page<PmsProduct> listProducts(ProductQueryDTO queryDTO) {
        Page<PmsProduct> page = new Page<>(queryDTO.getPageNum(), queryDTO.getPageSize());
        LambdaQueryWrapper<PmsProduct> wrapper = new LambdaQueryWrapper<>();

        wrapper.eq(PmsProduct::getStatus, 1); // 只查上架商品
        wrapper.eq(PmsProduct::getDeleted, 0);

        if (queryDTO.getCategoryId() != null) {
            List<Long> categoryIds = getCategoryAndDescendantIds(queryDTO.getCategoryId());
            wrapper.in(PmsProduct::getCategoryId, categoryIds);
        }
        if (queryDTO.getBrandId() != null) {
            wrapper.eq(PmsProduct::getBrandId, queryDTO.getBrandId());
        }
        if (StringUtils.hasText(queryDTO.getKeyword())) {
            wrapper.and(w -> w.like(PmsProduct::getName, queryDTO.getKeyword())
                    .or().like(PmsProduct::getKeywords, queryDTO.getKeyword())
                    .or().like(PmsProduct::getSubtitle, queryDTO.getKeyword()));
        }
        if (queryDTO.getMinPrice() != null) {
            wrapper.ge(PmsProduct::getPrice, queryDTO.getMinPrice());
        }
        if (queryDTO.getMaxPrice() != null) {
            wrapper.le(PmsProduct::getPrice, queryDTO.getMaxPrice());
        }
        if (queryDTO.getIsNew() != null) {
            wrapper.eq(PmsProduct::getIsNew, queryDTO.getIsNew());
        }
        if (queryDTO.getIsHot() != null) {
            wrapper.eq(PmsProduct::getIsHot, queryDTO.getIsHot());
        }
        if (queryDTO.getIsRecommend() != null) {
            wrapper.eq(PmsProduct::getIsRecommend, queryDTO.getIsRecommend());
        }

        // 排序
        if (StringUtils.hasText(queryDTO.getSortField())) {
            boolean asc = "asc".equalsIgnoreCase(queryDTO.getSortOrder());
            switch (queryDTO.getSortField()) {
                case "price":
                    wrapper.orderBy(true, asc, PmsProduct::getPrice);
                    break;
                case "sales":
                    wrapper.orderBy(true, asc, PmsProduct::getSales);
                    break;
                default:
                    wrapper.orderByDesc(PmsProduct::getCreatedAt);
            }
        } else {
            wrapper.orderByDesc(PmsProduct::getSortOrder)
                    .orderByDesc(PmsProduct::getCreatedAt);
        }

        Page<PmsProduct> result = productDao.selectPage(page, wrapper);

        // 填充分类和品牌名称
        result.getRecords().forEach(p -> {
            PmsCategory c = categoryDao.selectById(p.getCategoryId());
            if (c != null) p.setCategoryName(c.getName());
        });

        return result;
    }

    @Override
    public List<PmsProduct> getHotProducts(int limit) {
        String cacheKey = CommonConstant.CACHE_HOT_PRODUCT;
        List<PmsProduct> hotList = redisCache.get(cacheKey);
        if (hotList == null) {
            hotList = productDao.selectHotProducts(limit);
            redisCache.set(cacheKey, hotList, 30, TimeUnit.MINUTES);
        }
        return hotList;
    }

    @Override
    public List<PmsProduct> getNewProducts(int limit) {
        return productDao.selectNewProducts(limit);
    }

    @Override
    public List<PmsProduct> getRecommendProducts(int limit) {
        String cacheKey = CommonConstant.CACHE_RECOMMEND_PRODUCT;
        List<PmsProduct> recommendList = redisCache.get(cacheKey);
        if (recommendList == null) {
            recommendList = productDao.selectRecommendProducts(limit);
            redisCache.set(cacheKey, recommendList, 30, TimeUnit.MINUTES);
        }
        return recommendList;
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public void createProduct(PmsProduct product) {
        productDao.insert(product);
        // 保存规格
        if (product.getSpecs() != null && !product.getSpecs().isEmpty()) {
            for (PmsProductSpec spec : product.getSpecs()) {
                spec.setProductId(product.getId());
                productSpecDao.insert(spec);
            }
        }
        redisCache.delete(CommonConstant.CACHE_HOT_PRODUCT);
        redisCache.delete(CommonConstant.CACHE_RECOMMEND_PRODUCT);
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public void updateProduct(PmsProduct product) {
        PmsProduct exist = productDao.selectById(product.getId());
        if (exist == null) {
            throw new BusinessException(ResultCode.PRODUCT_NOT_EXIST);
        }
        productDao.updateById(product);

        // 更新规格: 先删除再插入
        productSpecDao.delete(new LambdaQueryWrapper<PmsProductSpec>()
                .eq(PmsProductSpec::getProductId, product.getId()));
        if (product.getSpecs() != null) {
            for (PmsProductSpec spec : product.getSpecs()) {
                spec.setId(null);
                spec.setProductId(product.getId());
                productSpecDao.insert(spec);
            }
        }
        // 清除缓存
        redisCache.delete(CommonConstant.CACHE_PRODUCT + CommonConstant.CACHE_SEPARATOR + product.getId());
    }

    @Override
    public void deleteProduct(Long id) {
        productDao.deleteById(id);
        redisCache.delete(CommonConstant.CACHE_PRODUCT + CommonConstant.CACHE_SEPARATOR + id);
    }

    @Override
    public void toggleProductStatus(Long id) {
        PmsProduct product = productDao.selectById(id);
        if (product == null) {
            throw new BusinessException(ResultCode.PRODUCT_NOT_EXIST);
        }
        product.setStatus(product.getStatus() == 1 ? 0 : 1);
        productDao.updateById(product);
        redisCache.delete(CommonConstant.CACHE_PRODUCT + CommonConstant.CACHE_SEPARATOR + id);
    }

    @Override
    public Page<PmsProduct> adminListProducts(ProductQueryDTO queryDTO) {
        Page<PmsProduct> page = new Page<>(queryDTO.getPageNum(), queryDTO.getPageSize());
        LambdaQueryWrapper<PmsProduct> wrapper = new LambdaQueryWrapper<>();

        if (queryDTO.getCategoryId() != null) {
            wrapper.eq(PmsProduct::getCategoryId, queryDTO.getCategoryId());
        }
        if (queryDTO.getBrandId() != null) {
            wrapper.eq(PmsProduct::getBrandId, queryDTO.getBrandId());
        }
        if (StringUtils.hasText(queryDTO.getKeyword())) {
            wrapper.like(PmsProduct::getName, queryDTO.getKeyword());
        }
        if (queryDTO.getStatus() != null) {
            wrapper.eq(PmsProduct::getStatus, queryDTO.getStatus());
        }
        wrapper.orderByDesc(PmsProduct::getCreatedAt);

        Page<PmsProduct> result = productDao.selectPage(page, wrapper);
        result.getRecords().forEach(p -> {
            PmsCategory c = categoryDao.selectById(p.getCategoryId());
            if (c != null) p.setCategoryName(c.getName());
            PmsBrand b = brandDao.selectById(p.getBrandId());
            if (b != null) p.setBrandName(b.getName());
        });

        return result;
    }

    /**
     * 递归获取分类及其所有子分类ID（支持三级分类层级）
     */
    private List<Long> getCategoryAndDescendantIds(Long categoryId) {
        List<Long> ids = new ArrayList<>();
        ids.add(categoryId);
        List<PmsCategory> children = categoryDao.selectList(
                new LambdaQueryWrapper<PmsCategory>()
                        .eq(PmsCategory::getParentId, categoryId)
                        .eq(PmsCategory::getStatus, 1));
        for (PmsCategory child : children) {
            ids.add(child.getId());
            List<PmsCategory> grandchildren = categoryDao.selectList(
                    new LambdaQueryWrapper<PmsCategory>()
                            .eq(PmsCategory::getParentId, child.getId())
                            .eq(PmsCategory::getStatus, 1));
            for (PmsCategory grandchild : grandchildren) {
                ids.add(grandchild.getId());
            }
        }
        return ids;
    }
}
