package com.mall.dao;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.mall.entity.OmsOrder;
import org.apache.ibatis.annotations.Param;
import org.apache.ibatis.annotations.Select;

import java.math.BigDecimal;
import java.util.List;
import java.util.Map;

/**
 * 订单DAO
 */
public interface OmsOrderDao extends BaseMapper<OmsOrder> {

    @Select("SELECT COUNT(*) FROM oms_order WHERE DATE(created_at) = CURDATE() AND deleted = 0")
    Long selectTodayOrderCount();

    @Select("SELECT COALESCE(SUM(pay_amount), 0) FROM oms_order WHERE DATE(pay_time) = CURDATE() AND status IN (1,2,3)")
    BigDecimal selectTodaySalesAmount();

    @Select("SELECT COUNT(*) FROM oms_order WHERE deleted = 0")
    Long selectTotalOrderCount();

    @Select("SELECT DATE_FORMAT(created_at, '%Y-%m-%d') as date, COUNT(*) as count, COALESCE(SUM(pay_amount), 0) as amount " +
            "FROM oms_order WHERE created_at >= DATE_SUB(CURDATE(), INTERVAL 6 DAY) AND deleted = 0 " +
            "GROUP BY DATE_FORMAT(created_at, '%Y-%m-%d') ORDER BY date")
    List<Map<String, Object>> selectOrderTrend();

    @Select("SELECT status, COUNT(*) as count FROM oms_order WHERE deleted = 0 GROUP BY status")
    List<Map<String, Object>> selectOrderStatusDistribution();
}
