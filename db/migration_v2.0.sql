-- =============================================
-- Mall电商系统 - v2.0 数据库迁移脚本
-- 1. 用户表增加余额字段
-- 2. 为已有用户设置初始余额
-- =============================================

USE mall;

-- 用户表增加余额字段
ALTER TABLE `ums_user`
    ADD COLUMN `balance` DECIMAL(10, 2) NOT NULL DEFAULT 10000.00 COMMENT '账户余额' AFTER `last_login_time`;

-- 为已有用户设置初始余额
UPDATE `ums_user` SET `balance` = 10000.00 WHERE `balance` IS NULL OR `balance` = 0;
