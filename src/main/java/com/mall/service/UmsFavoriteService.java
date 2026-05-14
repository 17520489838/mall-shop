package com.mall.service;

import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.mall.entity.PmsProduct;

/**
 * 收藏服务接口
 */
public interface UmsFavoriteService {

    void toggleFavorite(Long userId, Long productId);

    Page<PmsProduct> listFavorites(Long userId, Integer pageNum, Integer pageSize);

    boolean isFavorited(Long userId, Long productId);
}
