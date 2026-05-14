package com.mall.controller.admin;

import com.mall.common.result.Result;
import com.mall.service.OmsOrderService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import org.springframework.web.bind.annotation.*;

import java.util.Map;

@RestController
@RequestMapping("/v1/admin/orders")
@Tag(name = "管理后台订单接口")
public class AdminOrderController {

    private final OmsOrderService orderService;

    public AdminOrderController(OmsOrderService orderService) {
        this.orderService = orderService;
    }

    @GetMapping
    @Operation(summary = "订单列表")
    public Result<?> listOrders(@RequestParam(required = false) Integer status,
                                @RequestParam(required = false) String keyword,
                                @RequestParam(defaultValue = "1") Integer pageNum,
                                @RequestParam(defaultValue = "20") Integer pageSize) {
        return Result.success(orderService.adminListOrders(status, keyword, pageNum, pageSize));
    }

    @GetMapping("/{id}")
    @Operation(summary = "订单详情")
    public Result<?> getOrderDetail(@PathVariable Long id) {
        // 管理后台查看订单，需要传userId但不需要验证
        return Result.success(orderService.getOrderDetail(0L, id));
    }

    @PutMapping("/{id}/status")
    @Operation(summary = "修改订单状态")
    public Result<String> updateOrderStatus(@PathVariable Long id, @RequestBody Map<String, Object> params) {
        Integer status = Integer.valueOf(params.get("status").toString());
        String note = (String) params.getOrDefault("note", "");
        orderService.adminUpdateOrderStatus(id, status, note);
        return Result.success("操作成功");
    }

    @PutMapping("/{id}/deliver")
    @Operation(summary = "发货")
    public Result<String> deliverOrder(@PathVariable Long id, @RequestBody Map<String, String> params) {
        orderService.deliverOrder(id, params.get("company"), params.get("deliveryNo"));
        return Result.success("发货成功");
    }
}
