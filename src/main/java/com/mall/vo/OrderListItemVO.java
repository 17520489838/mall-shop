package com.mall.vo;

import com.mall.entity.OmsOrder;
import com.mall.entity.OmsOrderItem;
import io.swagger.v3.oas.annotations.media.Schema;
import lombok.AllArgsConstructor;
import lombok.Data;

import java.util.List;

@Data
@AllArgsConstructor
@Schema(description = "订单列表项")
public class OrderListItemVO {

    @Schema(description = "订单信息")
    private OmsOrder order;

    @Schema(description = "订单商品列表")
    private List<OmsOrderItem> items;
}
