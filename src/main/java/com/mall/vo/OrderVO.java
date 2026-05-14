package com.mall.vo;

import com.mall.entity.OmsOrder;
import com.mall.entity.OmsOrderItem;
import com.mall.entity.OmsOrderLog;
import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;

import java.util.List;

@Data
@Schema(description = "订单详情")
public class OrderVO {

    @Schema(description = "订单信息")
    private OmsOrder order;

    @Schema(description = "订单商品列表")
    private List<OmsOrderItem> items;

    @Schema(description = "订单操作日志")
    private List<OmsOrderLog> logs;
}
