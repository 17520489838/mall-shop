package com.mall.service.impl;

import cn.hutool.core.util.IdUtil;
import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.mall.common.constant.CommonConstant;
import com.mall.common.exception.BusinessException;
import com.mall.common.result.ResultCode;
import com.mall.common.utils.RedisCache;
import com.mall.dao.*;
import com.mall.dto.OrderDTO;
import com.mall.dto.QuickOrderDTO;
import com.mall.entity.*;
import com.mall.service.OmsOrderService;
import com.mall.vo.OrderListItemVO;
import com.mall.vo.OrderVO;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.util.StringUtils;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.*;
import java.util.stream.Collectors;

/**
 * 订单服务实现
 */
@Slf4j
@Service
public class OmsOrderServiceImpl implements OmsOrderService {

    private final OmsOrderDao orderDao;
    private final OmsOrderItemDao orderItemDao;
    private final OmsOrderLogDao orderLogDao;
    private final OmsCartItemDao cartItemDao;
    private final PmsProductDao productDao;
    private final UmsAddressDao addressDao;
    private final UmsUserDao userDao;
    private final RedisCache redisCache;

    public OmsOrderServiceImpl(OmsOrderDao orderDao, OmsOrderItemDao orderItemDao,
                                OmsOrderLogDao orderLogDao, OmsCartItemDao cartItemDao,
                                PmsProductDao productDao, UmsAddressDao addressDao,
                                UmsUserDao userDao, RedisCache redisCache) {
        this.orderDao = orderDao;
        this.orderItemDao = orderItemDao;
        this.orderLogDao = orderLogDao;
        this.cartItemDao = cartItemDao;
        this.productDao = productDao;
        this.addressDao = addressDao;
        this.userDao = userDao;
        this.redisCache = redisCache;
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public OmsOrder createOrder(Long userId, OrderDTO orderDTO) {
        // 获取地址
        UmsAddress address = addressDao.selectById(orderDTO.getAddressId());
        if (address == null) {
            throw new BusinessException(ResultCode.ORDER_ADDRESS_REQUIRED);
        }

        // 获取商品(直接购买) 或 购物车选中商品
        List<OmsCartItem> cartItems;
        boolean isDirectBuy = orderDTO.getProductId() != null;

        if (isDirectBuy) {
            // 直接购买: 根据商品ID构建虚拟购物车项
            PmsProduct product = productDao.selectById(orderDTO.getProductId());
            if (product == null || product.getStatus() == 0) {
                throw new BusinessException(ResultCode.PRODUCT_NOT_EXIST, "商品已下架或不存在");
            }
            int quantity = orderDTO.getQuantity() != null ? orderDTO.getQuantity() : 1;
            if (product.getStock() < quantity) {
                throw new BusinessException(ResultCode.PRODUCT_OUT_OF_STOCK);
            }
            OmsCartItem fakeItem = new OmsCartItem();
            fakeItem.setProductId(product.getId());
            fakeItem.setProductName(product.getName());
            fakeItem.setProductPic(product.getPic());
            fakeItem.setProductPrice(product.getPrice());
            fakeItem.setQuantity(quantity);
            fakeItem.setProductSku(orderDTO.getSpecId() != null ? String.valueOf(orderDTO.getSpecId()) : null);
            cartItems = java.util.Collections.singletonList(fakeItem);
        } else if (orderDTO.getCartItemIds() != null && !orderDTO.getCartItemIds().isEmpty()) {
            cartItems = cartItemDao.selectList(
                    new LambdaQueryWrapper<OmsCartItem>()
                            .eq(OmsCartItem::getUserId, userId)
                            .in(OmsCartItem::getId, orderDTO.getCartItemIds())
                            .eq(OmsCartItem::getIsChecked, 1));
        } else {
            cartItems = cartItemDao.selectList(
                    new LambdaQueryWrapper<OmsCartItem>()
                            .eq(OmsCartItem::getUserId, userId)
                            .eq(OmsCartItem::getIsChecked, 1));
        }

        if (!isDirectBuy && (cartItems == null || cartItems.isEmpty())) {
            throw new BusinessException(ResultCode.CART_IS_EMPTY);
        }

        // 计算金额
        BigDecimal totalAmount = BigDecimal.ZERO;
        int totalQuantity = 0;
        for (OmsCartItem cartItem : cartItems) {
            // 检查库存(直接购买时已检查, 但仍需计算商品价格)
            if (!isDirectBuy) {
                PmsProduct product = productDao.selectById(cartItem.getProductId());
                if (product == null || product.getStatus() == 0) {
                    throw new BusinessException(ResultCode.PRODUCT_NOT_EXIST, "商品【" + cartItem.getProductName() + "】已下架");
                }
                if (product.getStock() < cartItem.getQuantity()) {
                    throw new BusinessException(ResultCode.PRODUCT_OUT_OF_STOCK, "商品【" + cartItem.getProductName() + "】库存不足");
                }
            }
            totalAmount = totalAmount.add(cartItem.getProductPrice().multiply(BigDecimal.valueOf(cartItem.getQuantity())));
            totalQuantity += cartItem.getQuantity();
        }

        // 运费计算(满99免运费)
        BigDecimal freightAmount = BigDecimal.TEN;
        if (totalAmount.compareTo(new BigDecimal("99")) >= 0) {
            freightAmount = BigDecimal.ZERO;
        }

        BigDecimal payAmount = totalAmount.add(freightAmount);
        String orderNo = generateOrderNo();

        // 创建订单
        OmsOrder order = new OmsOrder();
        order.setOrderNo(orderNo);
        order.setUserId(userId);
        order.setTotalAmount(totalAmount);
        order.setPayAmount(payAmount);
        order.setFreightAmount(freightAmount);
        order.setDiscountAmount(BigDecimal.ZERO);
        order.setPayType(orderDTO.getPayType());
        order.setStatus(CommonConstant.ORDER_STATUS_PENDING);
        order.setReceiveName(address.getName());
        order.setReceivePhone(address.getPhone());
        order.setReceiveProvince(address.getProvince());
        order.setReceiveCity(address.getCity());
        order.setReceiveDistrict(address.getDistrict());
        order.setReceiveDetail(address.getDetail());
        order.setReceiveZipCode(address.getZipCode());
        order.setRemark(orderDTO.getRemark());
        // 保存购物车项ID(直接购买无需清除购物车)
        if (!isDirectBuy) {
            String cartItemIdsStr = cartItems.stream()
                    .map(c -> String.valueOf(c.getId()))
                    .collect(Collectors.joining(","));
            order.setCartItemIds(cartItemIdsStr);
        }
        orderDao.insert(order);

        // 创建订单商品
        for (OmsCartItem cartItem : cartItems) {
            OmsOrderItem orderItem = new OmsOrderItem();
            orderItem.setOrderId(order.getId());
            orderItem.setOrderNo(orderNo);
            orderItem.setProductId(cartItem.getProductId());
            orderItem.setProductName(cartItem.getProductName());
            orderItem.setProductPic(cartItem.getProductPic());
            orderItem.setProductPrice(cartItem.getProductPrice());
            orderItem.setProductQuantity(cartItem.getQuantity());
            orderItem.setProductSku(cartItem.getProductSku());
            orderItem.setSubtotal(cartItem.getProductPrice().multiply(BigDecimal.valueOf(cartItem.getQuantity())));
            orderItemDao.insert(orderItem);

            // 扣减库存
            productDao.reduceStock(cartItem.getProductId(), cartItem.getQuantity());
            // 清除该商品缓存(库存已变)
            redisCache.delete(CommonConstant.CACHE_PRODUCT + CommonConstant.CACHE_SEPARATOR + cartItem.getProductId());
        }

        // 记录订单日志
        addOrderLog(order.getId(), "用户", "创建订单", "订单创建成功");

        log.info("订单创建成功: orderNo={}, userId={}, payAmount={}", orderNo, userId, payAmount);
        return order;
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public void payOrder(Long userId, Long orderId, Integer payType) {
        OmsOrder order = validateOrderOwner(orderId, userId);

        if (order.getStatus() != CommonConstant.ORDER_STATUS_PENDING) {
            throw new BusinessException(ResultCode.ORDER_STATUS_ERROR, "当前订单状态不允许支付");
        }

        // 余额支付
        if (payType != null && payType == 1) {
            int rows = userDao.deductBalance(userId, order.getPayAmount());
            if (rows == 0) {
                throw new BusinessException(ResultCode.USER_BALANCE_INSUFFICIENT);
            }
            order.setPayType(1);
        } else {
            // 在线支付/货到付款 — TODO: 接入微信/支付宝第三方支付
            order.setPayType(payType != null ? payType : 0);
        }

        order.setStatus(CommonConstant.ORDER_STATUS_PAID);
        order.setPayTime(LocalDateTime.now());
        orderDao.updateById(order);

        // 增加销量
        List<OmsOrderItem> items = orderItemDao.selectList(
                new LambdaQueryWrapper<OmsOrderItem>().eq(OmsOrderItem::getOrderId, orderId));
        for (OmsOrderItem item : items) {
            productDao.addSales(item.getProductId(), item.getProductQuantity());
        }

        // 付款成功 → 清除购物车中已购买的商品
        if (StringUtils.hasText(order.getCartItemIds())) {
            List<Long> ids = java.util.Arrays.stream(order.getCartItemIds().split(","))
                    .map(Long::valueOf).collect(Collectors.toList());
            cartItemDao.delete(
                    new LambdaQueryWrapper<OmsCartItem>()
                            .eq(OmsCartItem::getUserId, userId)
                            .in(OmsCartItem::getId, ids));
        }

        // 清除相关缓存(库存/销量已变)
        clearOrderProductCache(items);

        String payTypeStr = payType != null && payType == 1 ? "余额支付" : "在线支付";
        addOrderLog(orderId, "用户", "支付订单", payTypeStr + "成功");
        log.info("订单支付成功: orderNo={}, payType={}", order.getOrderNo(), payType);
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public void cancelOrder(Long userId, Long orderId, String reason) {
        OmsOrder order = validateOrderOwner(orderId, userId);

        if (order.getStatus() != CommonConstant.ORDER_STATUS_PENDING) {
            throw new BusinessException(ResultCode.ORDER_CANNOT_CANCEL);
        }

        order.setStatus(CommonConstant.ORDER_STATUS_CANCELED);
        order.setCancelTime(LocalDateTime.now());
        order.setCancelReason(reason);
        orderDao.updateById(order);

        // 恢复库存并清除缓存
        restoreStock(orderId);

        addOrderLog(orderId, "用户", "取消订单", reason);
        log.info("订单已取消: orderNo={}, reason={}", order.getOrderNo(), reason);
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public void confirmReceive(Long userId, Long orderId) {
        OmsOrder order = validateOrderOwner(orderId, userId);

        if (order.getStatus() != CommonConstant.ORDER_STATUS_SHIPPED) {
            throw new BusinessException(ResultCode.ORDER_STATUS_ERROR, "当前订单状态不允许确认收货");
        }

        order.setStatus(CommonConstant.ORDER_STATUS_COMPLETED);
        order.setFinishTime(LocalDateTime.now());
        orderDao.updateById(order);

        addOrderLog(orderId, "用户", "确认收货", "订单已完成");
        log.info("订单确认收货: orderNo={}", order.getOrderNo());
    }

    @Override
    public void deleteOrder(Long userId, Long orderId) {
        OmsOrder order = validateOrderOwner(orderId, userId);

        if (order.getStatus() != CommonConstant.ORDER_STATUS_CANCELED
                && order.getStatus() != CommonConstant.ORDER_STATUS_COMPLETED) {
            throw new BusinessException(ResultCode.ORDER_CANNOT_DELETE);
        }

        orderDao.deleteById(orderId);
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public void applyRefund(Long userId, Long orderId, String reason) {
        OmsOrder order = validateOrderOwner(orderId, userId);

        if (order.getStatus() != CommonConstant.ORDER_STATUS_PAID
                && order.getStatus() != CommonConstant.ORDER_STATUS_SHIPPED) {
            throw new BusinessException(ResultCode.ORDER_STATUS_ERROR, "当前订单状态不允许退款");
        }

        order.setStatus(CommonConstant.ORDER_STATUS_REFUNDED);
        order.setRefundReason(reason);
        order.setRefundAmount(order.getPayAmount());
        orderDao.updateById(order);

        restoreStock(orderId);

        // 余额支付的订单退还余额
        if (order.getPayType() == 1) {
            userDao.addBalance(userId, order.getPayAmount());
            addOrderLog(orderId, "用户", "退款", "余额已退还，原因: " + reason);
        } else {
            addOrderLog(orderId, "用户", "申请退款", reason);
        }

        log.info("订单退款申请: orderNo={}", order.getOrderNo());
    }

    @Override
    public OrderVO getOrderDetail(Long userId, Long orderId) {
        OmsOrder order = orderDao.selectById(orderId);
        if (order == null || order.getDeleted() == 1) {
            throw new BusinessException(ResultCode.ORDER_NOT_EXIST);
        }

        List<OmsOrderItem> items = orderItemDao.selectList(
                new LambdaQueryWrapper<OmsOrderItem>().eq(OmsOrderItem::getOrderId, orderId));
        List<OmsOrderLog> logs = orderLogDao.selectList(
                new LambdaQueryWrapper<OmsOrderLog>().eq(OmsOrderLog::getOrderId, orderId)
                        .orderByDesc(OmsOrderLog::getCreatedAt));

        OrderVO orderVO = new OrderVO();
        orderVO.setOrder(order);
        orderVO.setItems(items);
        orderVO.setLogs(logs);
        return orderVO;
    }

    @Override
    public Page<OmsOrder> listUserOrders(Long userId, Integer status, Integer pageNum, Integer pageSize) {
        Page<OmsOrder> page = new Page<>(pageNum, pageSize);
        LambdaQueryWrapper<OmsOrder> wrapper = new LambdaQueryWrapper<>();
        wrapper.eq(OmsOrder::getUserId, userId);
        if (status != null) {
            wrapper.eq(OmsOrder::getStatus, status);
        }
        wrapper.orderByDesc(OmsOrder::getCreatedAt);
        return orderDao.selectPage(page, wrapper);
    }

    @Override
    public Page<OrderListItemVO> listUserOrdersWithItems(Long userId, Integer status, Integer pageNum, Integer pageSize) {
        Page<OmsOrder> page = new Page<>(pageNum, pageSize);
        LambdaQueryWrapper<OmsOrder> wrapper = new LambdaQueryWrapper<>();
        wrapper.eq(OmsOrder::getUserId, userId);
        if (status != null) {
            wrapper.eq(OmsOrder::getStatus, status);
        }
        wrapper.orderByDesc(OmsOrder::getCreatedAt);
        Page<OmsOrder> orderPage = orderDao.selectPage(page, wrapper);

        // 批量查询每个订单的商品
        List<OmsOrder> orders = orderPage.getRecords();
        if (orders.isEmpty()) {
            Page<OrderListItemVO> emptyPage = new Page<>(pageNum, pageSize);
            emptyPage.setRecords(Collections.emptyList());
            return emptyPage;
        }
        List<Long> orderIds = orders.stream().map(OmsOrder::getId).collect(Collectors.toList());
        List<OmsOrderItem> allItems = orderItemDao.selectList(
                new LambdaQueryWrapper<OmsOrderItem>().in(OmsOrderItem::getOrderId, orderIds));
        Map<Long, List<OmsOrderItem>> itemsMap = allItems.stream()
                .collect(Collectors.groupingBy(OmsOrderItem::getOrderId));

        List<OrderListItemVO> voList = orders.stream()
                .map(o -> new OrderListItemVO(o, itemsMap.getOrDefault(o.getId(), Collections.emptyList())))
                .collect(Collectors.toList());

        Page<OrderListItemVO> result = new Page<>(orderPage.getCurrent(), orderPage.getSize(), orderPage.getTotal());
        result.setRecords(voList);
        return result;
    }

    @Override
    public Page<OmsOrder> adminListOrders(Integer status, String keyword, Integer pageNum, Integer pageSize) {
        Page<OmsOrder> page = new Page<>(pageNum, pageSize);
        LambdaQueryWrapper<OmsOrder> wrapper = new LambdaQueryWrapper<>();
        if (status != null) {
            wrapper.eq(OmsOrder::getStatus, status);
        }
        if (StringUtils.hasText(keyword)) {
            wrapper.like(OmsOrder::getOrderNo, keyword)
                    .or().like(OmsOrder::getReceiveName, keyword)
                    .or().like(OmsOrder::getReceivePhone, keyword);
        }
        wrapper.orderByDesc(OmsOrder::getCreatedAt);
        return orderDao.selectPage(page, wrapper);
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public void adminUpdateOrderStatus(Long orderId, Integer status, String note) {
        OmsOrder order = orderDao.selectById(orderId);
        if (order == null) {
            throw new BusinessException(ResultCode.ORDER_NOT_EXIST);
        }
        order.setStatus(status);
        orderDao.updateById(order);
        addOrderLog(orderId, "管理员", "修改订单状态", note);
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public void deliverOrder(Long orderId, String deliveryCompany, String deliveryNo) {
        OmsOrder order = orderDao.selectById(orderId);
        if (order == null) {
            throw new BusinessException(ResultCode.ORDER_NOT_EXIST);
        }
        if (order.getStatus() != CommonConstant.ORDER_STATUS_PAID) {
            throw new BusinessException(ResultCode.ORDER_STATUS_ERROR, "当前订单状态不允许发货");
        }

        order.setStatus(CommonConstant.ORDER_STATUS_SHIPPED);
        order.setDeliveryCompany(deliveryCompany);
        order.setDeliveryNo(deliveryNo);
        order.setDeliveryTime(LocalDateTime.now());
        orderDao.updateById(order);

        addOrderLog(orderId, "管理员", "发货",
                "物流公司: " + deliveryCompany + ", 物流单号: " + deliveryNo);
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public OmsOrder quickOrder(Long userId, QuickOrderDTO dto) {
        // 获取用户地址(默认优先)
        List<UmsAddress> addresses = addressDao.selectList(
                new LambdaQueryWrapper<UmsAddress>()
                        .eq(UmsAddress::getUserId, userId)
                        .orderByDesc(UmsAddress::getIsDefault)
                        .last("LIMIT 1"));
        if (addresses == null || addresses.isEmpty()) {
            throw new BusinessException(ResultCode.ORDER_ADDRESS_REQUIRED, "请先添加收货地址");
        }
        UmsAddress address = addresses.get(0);

        // 获取商品并检查库存
        PmsProduct product = productDao.selectById(dto.getProductId());
        if (product == null || product.getStatus() == 0) {
            throw new BusinessException(ResultCode.PRODUCT_NOT_EXIST, "商品已下架或不存在");
        }
        if (product.getStock() < dto.getQuantity()) {
            throw new BusinessException(ResultCode.PRODUCT_OUT_OF_STOCK);
        }

        // 计算金额
        BigDecimal totalAmount = product.getPrice().multiply(BigDecimal.valueOf(dto.getQuantity()));
        BigDecimal freightAmount = totalAmount.compareTo(new BigDecimal("99")) >= 0 ? BigDecimal.ZERO : BigDecimal.TEN;
        BigDecimal payAmount = totalAmount.add(freightAmount);
        String orderNo = generateOrderNo();

        // 创建订单
        OmsOrder order = new OmsOrder();
        order.setOrderNo(orderNo);
        order.setUserId(userId);
        order.setTotalAmount(totalAmount);
        order.setPayAmount(payAmount);
        order.setFreightAmount(freightAmount);
        order.setDiscountAmount(BigDecimal.ZERO);
        order.setPayType(0);
        order.setStatus(CommonConstant.ORDER_STATUS_PENDING);
        order.setReceiveName(address.getName());
        order.setReceivePhone(address.getPhone());
        order.setReceiveProvince(address.getProvince());
        order.setReceiveCity(address.getCity());
        order.setReceiveDistrict(address.getDistrict());
        order.setReceiveDetail(address.getDetail());
        order.setReceiveZipCode(address.getZipCode());
        orderDao.insert(order);

        // 创建订单商品(无cartItem关联)
        OmsOrderItem orderItem = new OmsOrderItem();
        orderItem.setOrderId(order.getId());
        orderItem.setOrderNo(orderNo);
        orderItem.setProductId(product.getId());
        orderItem.setProductName(product.getName());
        orderItem.setProductPic(product.getPic());
        orderItem.setProductPrice(product.getPrice());
        orderItem.setProductQuantity(dto.getQuantity());
        orderItem.setProductSku(dto.getSpecId() != null ? String.valueOf(dto.getSpecId()) : null);
        orderItem.setSubtotal(totalAmount);
        orderItemDao.insert(orderItem);

        // 扣减库存并清除缓存
        productDao.reduceStock(product.getId(), dto.getQuantity());
        redisCache.delete(CommonConstant.CACHE_PRODUCT + CommonConstant.CACHE_SEPARATOR + product.getId());

        // 记录订单日志
        addOrderLog(order.getId(), "用户", "创建订单", "立即购买-订单创建成功");

        log.info("快速下单成功: orderNo={}, userId={}, payAmount={}", orderNo, userId, payAmount);
        return order;
    }

    // ========== 私有方法 ==========

    private OmsOrder validateOrderOwner(Long orderId, Long userId) {
        OmsOrder order = orderDao.selectById(orderId);
        if (order == null || order.getDeleted() == 1) {
            throw new BusinessException(ResultCode.ORDER_NOT_EXIST);
        }
        if (!order.getUserId().equals(userId)) {
            throw new BusinessException(ResultCode.ORDER_NOT_EXIST);
        }
        return order;
    }

    private void addOrderLog(Long orderId, String operator, String action, String note) {
        OmsOrderLog log = new OmsOrderLog();
        log.setOrderId(orderId);
        log.setOperator(operator);
        log.setAction(action);
        log.setNote(note);
        orderLogDao.insert(log);
    }

    private void restoreStock(Long orderId) {
        List<OmsOrderItem> items = orderItemDao.selectList(
                new LambdaQueryWrapper<OmsOrderItem>().eq(OmsOrderItem::getOrderId, orderId));
        for (OmsOrderItem item : items) {
            productDao.addSales(item.getProductId(), -item.getProductQuantity());
            redisCache.delete(CommonConstant.CACHE_PRODUCT + CommonConstant.CACHE_SEPARATOR + item.getProductId());
        }
        redisCache.delete(CommonConstant.CACHE_HOT_PRODUCT);
        redisCache.delete(CommonConstant.CACHE_RECOMMEND_PRODUCT);
    }

    private String generateOrderNo() {
        String date = LocalDateTime.now().format(java.time.format.DateTimeFormatter.ofPattern("yyyyMMdd"));
        String uuid = IdUtil.fastSimpleUUID().substring(0, 8).toUpperCase();
        return "MALL" + date + uuid;
    }

    private void clearOrderProductCache(List<OmsOrderItem> items) {
        for (OmsOrderItem item : items) {
            redisCache.delete(CommonConstant.CACHE_PRODUCT + CommonConstant.CACHE_SEPARATOR + item.getProductId());
        }
        redisCache.delete(CommonConstant.CACHE_HOT_PRODUCT);
        redisCache.delete(CommonConstant.CACHE_RECOMMEND_PRODUCT);
    }
}
