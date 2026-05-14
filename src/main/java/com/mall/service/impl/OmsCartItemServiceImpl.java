package com.mall.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.mall.common.exception.BusinessException;
import com.mall.common.result.ResultCode;
import com.mall.dao.OmsCartItemDao;
import com.mall.dao.PmsProductDao;
import com.mall.dao.PmsProductSpecDao;
import com.mall.entity.OmsCartItem;
import com.mall.entity.PmsProduct;
import com.mall.entity.PmsProductSpec;
import com.mall.service.OmsCartItemService;
import com.mall.vo.CartVO;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.util.List;
import java.util.Objects;
import java.util.stream.Collectors;

/**
 * 购物车服务实现
 */
@Slf4j
@Service
public class OmsCartItemServiceImpl implements OmsCartItemService {

    private final OmsCartItemDao cartItemDao;
    private final PmsProductDao productDao;
    private final PmsProductSpecDao productSpecDao;

    public OmsCartItemServiceImpl(OmsCartItemDao cartItemDao, PmsProductDao productDao,
                                   PmsProductSpecDao productSpecDao) {
        this.cartItemDao = cartItemDao;
        this.productDao = productDao;
        this.productSpecDao = productSpecDao;
    }

    @Override
    public CartVO getCart(Long userId) {
        List<OmsCartItem> items = cartItemDao.selectList(
                new LambdaQueryWrapper<OmsCartItem>()
                        .eq(OmsCartItem::getUserId, userId)
                        .orderByDesc(OmsCartItem::getCreatedAt));

        CartVO cartVO = new CartVO();
        cartVO.setItems(items);
        cartVO.setTotalCount(items.size());

        // 计算选中商品总价和总数
        BigDecimal checkedTotal = BigDecimal.ZERO;
        int checkedCount = 0;
        for (OmsCartItem item : items) {
            if (item.getIsChecked() == 1) {
                checkedTotal = checkedTotal.add(item.getProductPrice().multiply(BigDecimal.valueOf(item.getQuantity())));
                checkedCount += item.getQuantity();
            }
        }
        cartVO.setCheckedTotal(checkedTotal);
        cartVO.setCheckedCount(checkedCount);

        return cartVO;
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public void addItem(Long userId, Long productId, Long specId, Integer quantity) {
        PmsProduct product = productDao.selectById(productId);
        if (product == null) {
            throw new BusinessException(ResultCode.PRODUCT_NOT_EXIST);
        }
        if (product.getStatus() == 0) {
            throw new BusinessException(ResultCode.PRODUCT_OFF_SHELF);
        }
        if (product.getStock() < quantity) {
            throw new BusinessException(ResultCode.PRODUCT_OUT_OF_STOCK);
        }

        BigDecimal price = product.getPrice();
        String sku = null;
        if (specId != null) {
            PmsProductSpec spec = productSpecDao.selectById(specId);
            if (spec != null) {
                price = price.add(spec.getPrice() != null ? spec.getPrice() : BigDecimal.ZERO);
                sku = spec.getName() + ":" + spec.getValue();
            }
        }

        // 检查是否已存在
        OmsCartItem exist = cartItemDao.selectOne(
                new LambdaQueryWrapper<OmsCartItem>()
                        .eq(OmsCartItem::getUserId, userId)
                        .eq(OmsCartItem::getProductId, productId)
                        .eq(specId != null, OmsCartItem::getProductSpecId, specId));

        if (exist != null) {
            // 更新数量
            exist.setQuantity(exist.getQuantity() + quantity);
            cartItemDao.updateById(exist);
        } else {
            OmsCartItem item = new OmsCartItem();
            item.setUserId(userId);
            item.setProductId(productId);
            item.setProductSpecId(specId);
            item.setProductName(product.getName());
            item.setProductPic(product.getPic());
            item.setProductPrice(price);
            item.setProductSku(sku);
            item.setQuantity(quantity);
            item.setIsChecked(1);
            cartItemDao.insert(item);
        }
    }

    @Override
    public void updateQuantity(Long userId, Long cartItemId, Integer quantity) {
        OmsCartItem item = cartItemDao.selectById(cartItemId);
        validateCartItemOwner(item, userId);

        if (quantity <= 0) {
            cartItemDao.deleteById(cartItemId);
        } else {
            item.setQuantity(quantity);
            cartItemDao.updateById(item);
        }
    }

    @Override
    public void toggleCheck(Long userId, Long cartItemId) {
        OmsCartItem item = cartItemDao.selectById(cartItemId);
        validateCartItemOwner(item, userId);
        item.setIsChecked(item.getIsChecked() == 1 ? 0 : 1);
        cartItemDao.updateById(item);
    }

    @Override
    public void checkAll(Long userId, boolean checked) {
        List<OmsCartItem> items = cartItemDao.selectList(
                new LambdaQueryWrapper<OmsCartItem>()
                        .eq(OmsCartItem::getUserId, userId));
        for (OmsCartItem item : items) {
            item.setIsChecked(checked ? 1 : 0);
            cartItemDao.updateById(item);
        }
    }

    @Override
    public void removeItem(Long userId, List<Long> cartItemIds) {
        cartItemDao.delete(
                new LambdaQueryWrapper<OmsCartItem>()
                        .eq(OmsCartItem::getUserId, userId)
                        .in(OmsCartItem::getId, cartItemIds));
    }

    @Override
    public List<OmsCartItem> getCheckedItems(Long userId) {
        return cartItemDao.selectList(
                new LambdaQueryWrapper<OmsCartItem>()
                        .eq(OmsCartItem::getUserId, userId)
                        .eq(OmsCartItem::getIsChecked, 1));
    }

    private void validateCartItemOwner(OmsCartItem item, Long userId) {
        if (item == null || !Objects.equals(item.getUserId(), userId)) {
            throw new BusinessException(ResultCode.CART_ITEM_NOT_EXIST);
        }
    }
}
