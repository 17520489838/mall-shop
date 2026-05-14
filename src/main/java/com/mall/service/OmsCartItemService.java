package com.mall.service;

import com.mall.entity.OmsCartItem;
import com.mall.vo.CartVO;

import java.util.List;

/**
 * 购物车服务接口
 */
public interface OmsCartItemService {

    /**
     * 获取用户购物车
     */
    CartVO getCart(Long userId);

    /**
     * 添加商品到购物车
     */
    void addItem(Long userId, Long productId, Long specId, Integer quantity);

    /**
     * 更新购物车商品数量
     */
    void updateQuantity(Long userId, Long cartItemId, Integer quantity);

    /**
     * 切换商品选中状态
     */
    void toggleCheck(Long userId, Long cartItemId);

    /**
     * 批量选中/取消选中
     */
    void checkAll(Long userId, boolean checked);

    /**
     * 删除购物车商品
     */
    void removeItem(Long userId, List<Long> cartItemIds);

    /**
     * 获取选中的购物车商品
     */
    List<OmsCartItem> getCheckedItems(Long userId);
}
