package com.mall.service;

import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.mall.dto.ProductQueryDTO;
import com.mall.entity.PmsProduct;
import com.mall.vo.ProductDetailVO;

import java.util.List;

/**
 * 商品服务接口
 */
public interface PmsProductService {

    /**
     * 商品详情
     */
    ProductDetailVO getProductDetail(Long productId, Long userId);

    /**
     * 商品搜索/列表查询
     */
    Page<PmsProduct> listProducts(ProductQueryDTO queryDTO);

    /**
     * 热销商品
     */
    List<PmsProduct> getHotProducts(int limit);

    /**
     * 新品推荐
     */
    List<PmsProduct> getNewProducts(int limit);

    /**
     * 推荐商品
     */
    List<PmsProduct> getRecommendProducts(int limit);

    /**
     * 新增商品(管理后台)
     */
    void createProduct(PmsProduct product);

    /**
     * 更新商品(管理后台)
     */
    void updateProduct(PmsProduct product);

    /**
     * 删除商品(管理后台)
     */
    void deleteProduct(Long id);

    /**
     * 上架/下架商品(管理后台)
     */
    void toggleProductStatus(Long id);

    /**
     * 管理后台商品分页
     */
    Page<PmsProduct> adminListProducts(ProductQueryDTO queryDTO);
}
