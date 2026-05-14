package com.mall.service;

import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.mall.dto.OrderDTO;
import com.mall.dto.QuickOrderDTO;
import com.mall.entity.OmsOrder;
import com.mall.vo.OrderVO;

/**
 * 订单服务接口
 */
public interface OmsOrderService {

    /**
     * 创建订单
     */
    OmsOrder createOrder(Long userId, OrderDTO orderDTO);

    /**
     * 立即购买(跳过购物车,直接下单)
     */
    OmsOrder quickOrder(Long userId, QuickOrderDTO dto);

    /**
     * 支付订单
     * @param payType 支付方式: 0-在线支付(TODO), 1-余额支付, 2-货到付款
     */
    void payOrder(Long userId, Long orderId, Integer payType);

    /**
     * 取消订单
     */
    void cancelOrder(Long userId, Long orderId, String reason);

    /**
     * 确认收货
     */
    void confirmReceive(Long userId, Long orderId);

    /**
     * 删除订单(用户)
     */
    void deleteOrder(Long userId, Long orderId);

    /**
     * 申请退款
     */
    void applyRefund(Long userId, Long orderId, String reason);

    /**
     * 订单详情
     */
    OrderVO getOrderDetail(Long userId, Long orderId);

    /**
     * 获取用户订单列表
     */
    Page<OmsOrder> listUserOrders(Long userId, Integer status, Integer pageNum, Integer pageSize);

    /**
     * 管理后台: 订单列表
     */
    Page<OmsOrder> adminListOrders(Integer status, String keyword, Integer pageNum, Integer pageSize);

    /**
     * 管理后台: 修改订单状态
     */
    void adminUpdateOrderStatus(Long orderId, Integer status, String note);

    /**
     * 管理后台: 发货
     */
    void deliverOrder(Long orderId, String deliveryCompany, String deliveryNo);
}
