-- =============================================
-- Mall电商系统 - v4.0 数据库迁移脚本
-- 添加按钮级权限标识 (type=3)，完善RBAC权限体系
-- =============================================

USE mall;

-- ===== 1. 获取父菜单ID =====
SET @goods_id = (SELECT `id` FROM `ums_menu` WHERE `name` = '商品管理' LIMIT 1);
SET @order_id = (SELECT `id` FROM `ums_menu` WHERE `name` = '订单管理' LIMIT 1);
SET @user_id = (SELECT `id` FROM `ums_menu` WHERE `name` = '用户管理' LIMIT 1);
SET @super_admin_role_id = (SELECT `id` FROM `ums_role` WHERE `code` = 'ROLE_SUPER_ADMIN' LIMIT 1);

-- ===== 2. 添加按钮级权限 (type=3) =====
-- 商品管理 - 按钮权限
INSERT INTO `ums_menu` (`parent_id`, `name`, `url`, `icon`, `permission`, `type`, `sort_order`) VALUES
(@goods_id, '新增商品', NULL, NULL, 'product:create', 3, 1),
(@goods_id, '编辑商品', NULL, NULL, 'product:update', 3, 2),
(@goods_id, '删除商品', NULL, NULL, 'product:delete', 3, 3);

-- 订单管理 - 按钮权限
INSERT INTO `ums_menu` (`parent_id`, `name`, `url`, `icon`, `permission`, `type`, `sort_order`) VALUES
(@order_id, '订单发货', NULL, NULL, 'order:ship', 3, 1);

-- 用户管理 - 按钮权限
INSERT INTO `ums_menu` (`parent_id`, `name`, `url`, `icon`, `permission`, `type`, `sort_order`) VALUES
(@user_id, '启用禁用', NULL, NULL, 'user:toggle-status', 3, 1);

-- ===== 3. 重新关联超级管理员所有菜单 =====
DELETE FROM `ums_role_menu` WHERE `role_id` = @super_admin_role_id;
INSERT INTO `ums_role_menu` (`role_id`, `menu_id`)
SELECT @super_admin_role_id, `id` FROM `ums_menu`;
