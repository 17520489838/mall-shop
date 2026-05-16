-- =============================================
-- Mall电商系统 - v3.0 数据库迁移脚本
-- 修复菜单数据 (parent_id 错误、删除无用菜单)
-- =============================================

USE mall;

-- ===== 1. 删除角色关联 =====
SET @super_admin_role_id = (SELECT `id` FROM `ums_role` WHERE `code` = 'ROLE_SUPER_ADMIN');
DELETE FROM `ums_role_menu` WHERE `menu_id` IN (SELECT `id` FROM `ums_menu` WHERE `name` IN ('品牌管理', '数据统计'));

-- ===== 2. 删除无用菜单 =====
DELETE FROM `ums_menu` WHERE `name` = '品牌管理';
DELETE FROM `ums_menu` WHERE `name` = '数据统计';

-- ===== 3. 修复顶级菜单 parent_id =====
UPDATE `ums_menu` SET `parent_id` = 0 WHERE `name` = '订单管理';
UPDATE `ums_menu` SET `parent_id` = 0 WHERE `name` = '用户管理';

-- ===== 4. 修复子菜单的 parent_id =====
SET @goods_id = (SELECT `id` FROM `ums_menu` WHERE `name` = '商品管理' LIMIT 1);
SET @order_id = (SELECT `id` FROM `ums_menu` WHERE `name` = '订单管理' LIMIT 1);
SET @user_id = (SELECT `id` FROM `ums_menu` WHERE `name` = '用户管理' LIMIT 1);
SET @system_id = (SELECT `id` FROM `ums_menu` WHERE `name` = '系统设置' LIMIT 1);

UPDATE `ums_menu` SET `parent_id` = @goods_id WHERE `name` IN ('分类管理', '商品列表');
UPDATE `ums_menu` SET `parent_id` = @order_id WHERE `name` = '订单列表';
UPDATE `ums_menu` SET `parent_id` = @user_id WHERE `name` = '用户列表';
UPDATE `ums_menu` SET `parent_id` = @system_id WHERE `name` = '基础配置';

-- ===== 5. 重新插入角色-菜单关联 =====
DELETE FROM `ums_role_menu` WHERE `role_id` = @super_admin_role_id;
INSERT INTO `ums_role_menu` (`role_id`, `menu_id`)
SELECT @super_admin_role_id, `id` FROM `ums_menu`;
