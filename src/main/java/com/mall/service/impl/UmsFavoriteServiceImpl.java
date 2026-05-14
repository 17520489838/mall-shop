package com.mall.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.mall.dao.PmsProductDao;
import com.mall.dao.UmsFavoriteDao;
import com.mall.entity.PmsProduct;
import com.mall.entity.UmsFavorite;
import com.mall.service.UmsFavoriteService;
import org.springframework.stereotype.Service;

@Service
public class UmsFavoriteServiceImpl implements UmsFavoriteService {

    private final UmsFavoriteDao favoriteDao;
    private final PmsProductDao productDao;

    public UmsFavoriteServiceImpl(UmsFavoriteDao favoriteDao, PmsProductDao productDao) {
        this.favoriteDao = favoriteDao;
        this.productDao = productDao;
    }

    @Override
    public void toggleFavorite(Long userId, Long productId) {
        UmsFavorite exist = favoriteDao.selectOne(
                new LambdaQueryWrapper<UmsFavorite>()
                        .eq(UmsFavorite::getUserId, userId)
                        .eq(UmsFavorite::getProductId, productId));
        if (exist != null) {
            favoriteDao.deleteById(exist.getId());
        } else {
            UmsFavorite fav = new UmsFavorite();
            fav.setUserId(userId);
            fav.setProductId(productId);
            favoriteDao.insert(fav);
        }
    }

    @Override
    public Page<PmsProduct> listFavorites(Long userId, Integer pageNum, Integer pageSize) {
        Page<PmsProduct> page = new Page<>(pageNum, pageSize);
        return productDao.selectPage(page,
                new LambdaQueryWrapper<PmsProduct>()
                        .inSql(PmsProduct::getId,
                                "SELECT product_id FROM ums_favorite WHERE user_id = " + userId)
                        .orderByDesc(PmsProduct::getCreatedAt));
    }

    @Override
    public boolean isFavorited(Long userId, Long productId) {
        UmsFavorite exist = favoriteDao.selectOne(
                new LambdaQueryWrapper<UmsFavorite>()
                        .eq(UmsFavorite::getUserId, userId)
                        .eq(UmsFavorite::getProductId, productId));
        return exist != null;
    }
}
