-- =============================================
-- Mall电商系统 - v2.1 数据库迁移脚本
-- 订单表增加购物车项ID字段（用于付款后清除购物车）
-- =============================================

USE mall;

ALTER TABLE `oms_order`
    ADD COLUMN `cart_item_ids` VARCHAR(500) DEFAULT NULL COMMENT '购物车项ID列表(逗号分隔)' AFTER `remark`;
