package com.mall.controller.front;

import com.mall.common.result.Result;
import com.mall.common.utils.CurrentUserUtils;
import com.mall.dto.OrderDTO;
import com.mall.dto.QuickOrderDTO;
import com.mall.entity.OmsOrder;
import com.mall.service.OmsOrderService;
import com.mall.vo.OrderListItemVO;
import com.mall.vo.OrderVO;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.extern.slf4j.Slf4j;
import org.springframework.web.bind.annotation.*;

import javax.validation.Valid;
import java.util.Map;

@Slf4j
@RestController
@RequestMapping("/v1/orders")
@Tag(name = "订单接口")
public class OrderController {

    private final OmsOrderService orderService;

    public OrderController(OmsOrderService orderService) {
        this.orderService = orderService;
    }

    @PostMapping
    @Operation(summary = "创建订单(购物车结算)")
    public Result<OmsOrder> createOrder(@Valid @RequestBody OrderDTO orderDTO) {
        OmsOrder order = orderService.createOrder(CurrentUserUtils.getUserId(), orderDTO);
        return Result.success("订单创建成功", order);
    }

    @PostMapping("/quick")
    @Operation(summary = "立即购买(跳过购物车)")
    public Result<OmsOrder> quickOrder(@Valid @RequestBody QuickOrderDTO dto) {
        OmsOrder order = orderService.quickOrder(CurrentUserUtils.getUserId(), dto);
        return Result.success("订单创建成功", order);
    }

    @GetMapping
    @Operation(summary = "订单列表")
    public Result<?> listOrders(@RequestParam(required = false) Integer status,
                                @RequestParam(defaultValue = "1") Integer pageNum,
                                @RequestParam(defaultValue = "10") Integer pageSize) {
        return Result.success(orderService.listUserOrdersWithItems(CurrentUserUtils.getUserId(), status, pageNum, pageSize));
    }

    @GetMapping("/{id}")
    @Operation(summary = "订单详情")
    public Result<OrderVO> getOrderDetail(@PathVariable Long id) {
        return Result.success(orderService.getOrderDetail(CurrentUserUtils.getUserId(), id));
    }

    @PostMapping("/{id}/pay")
    @Operation(summary = "支付订单")
    public Result<String> payOrder(@PathVariable Long id, @RequestBody(required = false) Map<String, Object> params) {
        Integer payType = params != null && params.get("payType") != null
                ? Integer.valueOf(params.get("payType").toString()) : null;
        orderService.payOrder(CurrentUserUtils.getUserId(), id, payType);
        return Result.success("支付成功");
    }

    @PostMapping("/{id}/cancel")
    @Operation(summary = "取消订单")
    public Result<String> cancelOrder(@PathVariable Long id, @RequestBody(required = false) Map<String, String> params) {
        String reason = params != null ? params.getOrDefault("reason", "用户主动取消") : "用户主动取消";
        orderService.cancelOrder(CurrentUserUtils.getUserId(), id, reason);
        return Result.success("订单已取消");
    }

    @PostMapping("/{id}/receive")
    @Operation(summary = "确认收货")
    public Result<String> confirmReceive(@PathVariable Long id) {
        orderService.confirmReceive(CurrentUserUtils.getUserId(), id);
        return Result.success("已确认收货");
    }

    @PostMapping("/{id}/refund")
    @Operation(summary = "申请退款")
    public Result<String> applyRefund(@PathVariable Long id, @RequestBody Map<String, String> params) {
        orderService.applyRefund(CurrentUserUtils.getUserId(), id, params.getOrDefault("reason", "申请退款"));
        return Result.success("退款申请已提交");
    }

    @DeleteMapping("/{id}")
    @Operation(summary = "删除订单")
    public Result<String> deleteOrder(@PathVariable Long id) {
        orderService.deleteOrder(CurrentUserUtils.getUserId(), id);
        return Result.success("订单已删除");
    }
}
