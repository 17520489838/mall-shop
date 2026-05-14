-- =============================================
-- Mall电商系统 - 数据库初始化脚本
-- 数据库版本: MySQL 8.0
-- =============================================

CREATE DATABASE IF NOT EXISTS mall DEFAULT CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci;

USE mall;

-- =============================================
-- 1. 用户相关表
-- =============================================

-- 1.1 用户表
CREATE TABLE `ums_user` (
    `id` BIGINT NOT NULL AUTO_INCREMENT COMMENT '用户ID',
    `username` VARCHAR(64) NOT NULL COMMENT '用户名',
    `password` VARCHAR(256) NOT NULL COMMENT '密码(加密)',
    `phone` VARCHAR(20) DEFAULT NULL COMMENT '手机号',
    `email` VARCHAR(100) DEFAULT NULL COMMENT '邮箱',
    `nickname` VARCHAR(64) DEFAULT NULL COMMENT '昵称',
    `avatar` VARCHAR(500) DEFAULT NULL COMMENT '头像URL',
    `gender` TINYINT DEFAULT 0 COMMENT '性别: 0-未知, 1-男, 2-女',
    `birthday` DATE DEFAULT NULL COMMENT '生日',
    `status` TINYINT DEFAULT 1 COMMENT '状态: 0-禁用, 1-启用',
    `source_type` VARCHAR(32) DEFAULT 'NORMAL' COMMENT '注册来源: NORMAL-普通, WX-微信, ALIPAY-支付宝',
    `last_login_time` DATETIME DEFAULT NULL COMMENT '最后登录时间',
    `created_at` DATETIME NOT NULL DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
    `updated_at` DATETIME NOT NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP COMMENT '更新时间',
    `deleted` TINYINT DEFAULT 0 COMMENT '逻辑删除: 0-未删除, 1-已删除',
    PRIMARY KEY (`id`),
    UNIQUE KEY `uk_username` (`username`),
    UNIQUE KEY `uk_phone` (`phone`),
    UNIQUE KEY `uk_email` (`email`),
    KEY `idx_status` (`status`),
    KEY `idx_created_at` (`created_at`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COMMENT='用户表';

-- 1.2 用户收货地址表
CREATE TABLE `ums_address` (
    `id` BIGINT NOT NULL AUTO_INCREMENT COMMENT '地址ID',
    `user_id` BIGINT NOT NULL COMMENT '用户ID',
    `name` VARCHAR(64) NOT NULL COMMENT '收货人姓名',
    `phone` VARCHAR(20) NOT NULL COMMENT '收货人电话',
    `province` VARCHAR(50) NOT NULL COMMENT '省',
    `city` VARCHAR(50) NOT NULL COMMENT '市',
    `district` VARCHAR(50) NOT NULL COMMENT '区/县',
    `detail` VARCHAR(200) NOT NULL COMMENT '详细地址',
    `zip_code` VARCHAR(10) DEFAULT NULL COMMENT '邮编',
    `is_default` TINYINT DEFAULT 0 COMMENT '是否默认: 0-否, 1-是',
    `created_at` DATETIME NOT NULL DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
    `updated_at` DATETIME NOT NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP COMMENT '更新时间',
    `deleted` TINYINT DEFAULT 0 COMMENT '逻辑删除',
    PRIMARY KEY (`id`),
    KEY `idx_user_id` (`user_id`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COMMENT='用户收货地址表';

-- 1.3 用户收藏表
CREATE TABLE `ums_favorite` (
    `id` BIGINT NOT NULL AUTO_INCREMENT COMMENT '收藏ID',
    `user_id` BIGINT NOT NULL COMMENT '用户ID',
    `product_id` BIGINT NOT NULL COMMENT '商品ID',
    `created_at` DATETIME NOT NULL DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
    PRIMARY KEY (`id`),
    UNIQUE KEY `uk_user_product` (`user_id`, `product_id`),
    KEY `idx_user_id` (`user_id`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COMMENT='用户收藏表';

-- =============================================
-- 2. 商品相关表
-- =============================================

-- 2.1 商品分类表
CREATE TABLE `pms_category` (
    `id` BIGINT NOT NULL AUTO_INCREMENT COMMENT '分类ID',
    `parent_id` BIGINT DEFAULT 0 COMMENT '父分类ID, 0表示顶级',
    `name` VARCHAR(100) NOT NULL COMMENT '分类名称',
    `icon` VARCHAR(500) DEFAULT NULL COMMENT '图标URL',
    `image` VARCHAR(500) DEFAULT NULL COMMENT '图片URL',
    `sort_order` INT DEFAULT 0 COMMENT '排序值, 越小越靠前',
    `level` TINYINT DEFAULT 1 COMMENT '层级: 1-一级, 2-二级, 3-三级',
    `status` TINYINT DEFAULT 1 COMMENT '状态: 0-隐藏, 1-显示',
    `product_count` INT DEFAULT 0 COMMENT '商品数量',
    `created_at` DATETIME NOT NULL DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
    `updated_at` DATETIME NOT NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP COMMENT '更新时间',
    `deleted` TINYINT DEFAULT 0 COMMENT '逻辑删除',
    PRIMARY KEY (`id`),
    KEY `idx_parent_id` (`parent_id`),
    KEY `idx_sort_order` (`sort_order`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COMMENT='商品分类表';

-- 2.2 商品品牌表
CREATE TABLE `pms_brand` (
    `id` BIGINT NOT NULL AUTO_INCREMENT COMMENT '品牌ID',
    `name` VARCHAR(100) NOT NULL COMMENT '品牌名称',
    `logo` VARCHAR(500) DEFAULT NULL COMMENT '品牌logo',
    `description` VARCHAR(500) DEFAULT NULL COMMENT '品牌描述',
    `sort_order` INT DEFAULT 0 COMMENT '排序值',
    `status` TINYINT DEFAULT 1 COMMENT '状态: 0-隐藏, 1-显示',
    `created_at` DATETIME NOT NULL DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
    `updated_at` DATETIME NOT NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP COMMENT '更新时间',
    `deleted` TINYINT DEFAULT 0 COMMENT '逻辑删除',
    PRIMARY KEY (`id`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COMMENT='商品品牌表';

-- 2.3 商品表
CREATE TABLE `pms_product` (
    `id` BIGINT NOT NULL AUTO_INCREMENT COMMENT '商品ID',
    `brand_id` BIGINT DEFAULT NULL COMMENT '品牌ID',
    `category_id` BIGINT NOT NULL COMMENT '分类ID',
    `name` VARCHAR(200) NOT NULL COMMENT '商品名称',
    `subtitle` VARCHAR(500) DEFAULT NULL COMMENT '副标题',
    `description` TEXT COMMENT '商品描述(富文本)',
    `keywords` VARCHAR(200) DEFAULT NULL COMMENT '关键词(用于搜索)',
    `price` DECIMAL(10,2) NOT NULL COMMENT '价格',
    `original_price` DECIMAL(10,2) DEFAULT NULL COMMENT '原价',
    `stock` INT NOT NULL DEFAULT 0 COMMENT '库存',
    `sales` INT DEFAULT 0 COMMENT '销量',
    `pic` VARCHAR(500) DEFAULT NULL COMMENT '主图URL',
    `album` TEXT COMMENT '商品相册(JSON数组)',
    `unit` VARCHAR(20) DEFAULT '' COMMENT '单位',
    `weight` DECIMAL(10,2) DEFAULT 0 COMMENT '重量(kg)',
    `is_new` TINYINT DEFAULT 0 COMMENT '是否新品: 0-否, 1-是',
    `is_hot` TINYINT DEFAULT 0 COMMENT '是否热销: 0-否, 1-是',
    `is_recommend` TINYINT DEFAULT 0 COMMENT '是否推荐: 0-否, 1-是',
    `status` TINYINT DEFAULT 1 COMMENT '状态: 0-下架, 1-上架',
    `sort_order` INT DEFAULT 0 COMMENT '排序值',
    `created_at` DATETIME NOT NULL DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
    `updated_at` DATETIME NOT NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP COMMENT '更新时间',
    `deleted` TINYINT DEFAULT 0 COMMENT '逻辑删除',
    PRIMARY KEY (`id`),
    KEY `idx_category_id` (`category_id`),
    KEY `idx_brand_id` (`brand_id`),
    KEY `idx_price` (`price`),
    KEY `idx_status` (`status`),
    KEY `idx_is_hot` (`is_hot`),
    KEY `idx_is_new` (`is_new`),
    KEY `idx_is_recommend` (`is_recommend`),
    FULLTEXT KEY `ft_search` (`name`, `keywords`, `subtitle`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COMMENT='商品表';

-- 2.4 商品规格表
CREATE TABLE `pms_product_spec` (
    `id` BIGINT NOT NULL AUTO_INCREMENT COMMENT '规格ID',
    `product_id` BIGINT NOT NULL COMMENT '商品ID',
    `name` VARCHAR(100) NOT NULL COMMENT '规格名称(如颜色、尺寸)',
    `value` VARCHAR(200) NOT NULL COMMENT '规格值(如红色、XL)',
    `price` DECIMAL(10,2) DEFAULT NULL COMMENT '额外价格',
    `stock` INT DEFAULT 0 COMMENT '规格库存',
    `pic` VARCHAR(500) DEFAULT NULL COMMENT '规格图片',
    `created_at` DATETIME NOT NULL DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
    PRIMARY KEY (`id`),
    KEY `idx_product_id` (`product_id`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COMMENT='商品规格表';

-- 2.5 商品评价表
CREATE TABLE `pms_comment` (
    `id` BIGINT NOT NULL AUTO_INCREMENT COMMENT '评价ID',
    `product_id` BIGINT NOT NULL COMMENT '商品ID',
    `user_id` BIGINT NOT NULL COMMENT '用户ID',
    `order_id` BIGINT NOT NULL COMMENT '订单ID',
    `content` TEXT COMMENT '评价内容',
    `rating` TINYINT NOT NULL DEFAULT 5 COMMENT '评分: 1-5',
    `pics` TEXT COMMENT '晒图(JSON数组)',
    `is_anonymous` TINYINT DEFAULT 0 COMMENT '是否匿名: 0-否, 1-是',
    `status` TINYINT DEFAULT 1 COMMENT '状态: 0-待审核, 1-已通过, 2-未通过',
    `reply_content` TEXT COMMENT '商家回复',
    `reply_time` DATETIME DEFAULT NULL COMMENT '回复时间',
    `like_count` INT DEFAULT 0 COMMENT '点赞数',
    `created_at` DATETIME NOT NULL DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
    `updated_at` DATETIME NOT NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP COMMENT '更新时间',
    `deleted` TINYINT DEFAULT 0 COMMENT '逻辑删除',
    PRIMARY KEY (`id`),
    KEY `idx_product_id` (`product_id`),
    KEY `idx_user_id` (`user_id`),
    KEY `idx_order_id` (`order_id`),
    KEY `idx_rating` (`rating`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COMMENT='商品评价表';

-- =============================================
-- 3. 购物车表
-- =============================================
CREATE TABLE `oms_cart_item` (
    `id` BIGINT NOT NULL AUTO_INCREMENT COMMENT '购物车项ID',
    `user_id` BIGINT NOT NULL COMMENT '用户ID',
    `product_id` BIGINT NOT NULL COMMENT '商品ID',
    `product_spec_id` BIGINT DEFAULT NULL COMMENT '商品规格ID',
    `product_name` VARCHAR(200) NOT NULL COMMENT '商品名称',
    `product_pic` VARCHAR(500) DEFAULT NULL COMMENT '商品图片',
    `product_price` DECIMAL(10,2) NOT NULL COMMENT '加入时价格',
    `product_sku` VARCHAR(200) DEFAULT NULL COMMENT '商品规格描述',
    `quantity` INT NOT NULL DEFAULT 1 COMMENT '数量',
    `is_checked` TINYINT DEFAULT 1 COMMENT '是否选中: 0-未选中, 1-已选中',
    `created_at` DATETIME NOT NULL DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
    `updated_at` DATETIME NOT NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP COMMENT '更新时间',
    `deleted` TINYINT DEFAULT 0 COMMENT '逻辑删除',
    PRIMARY KEY (`id`),
    UNIQUE KEY `uk_user_product` (`user_id`, `product_id`, `product_spec_id`),
    KEY `idx_user_id` (`user_id`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COMMENT='购物车表';

-- =============================================
-- 4. 订单相关表
-- =============================================

-- 4.1 订单表
CREATE TABLE `oms_order` (
    `id` BIGINT NOT NULL AUTO_INCREMENT COMMENT '订单ID',
    `order_no` VARCHAR(64) NOT NULL COMMENT '订单编号',
    `user_id` BIGINT NOT NULL COMMENT '用户ID',
    `total_amount` DECIMAL(10,2) NOT NULL COMMENT '订单总金额',
    `pay_amount` DECIMAL(10,2) NOT NULL COMMENT '应付金额(含运费)',
    `freight_amount` DECIMAL(10,2) DEFAULT 0 COMMENT '运费金额',
    `discount_amount` DECIMAL(10,2) DEFAULT 0 COMMENT '优惠金额',
    `pay_type` TINYINT DEFAULT 1 COMMENT '支付方式: 1-在线支付, 2-货到付款',
    `status` TINYINT DEFAULT 0 COMMENT '订单状态: 0-待付款, 1-已付款, 2-已发货, 3-已完成, 4-已取消, 5-已退款',
    `delivery_company` VARCHAR(64) DEFAULT NULL COMMENT '物流公司',
    `delivery_no` VARCHAR(64) DEFAULT NULL COMMENT '物流单号',
    `delivery_time` DATETIME DEFAULT NULL COMMENT '发货时间',
    `receive_name` VARCHAR(64) NOT NULL COMMENT '收货人姓名',
    `receive_phone` VARCHAR(20) NOT NULL COMMENT '收货人电话',
    `receive_province` VARCHAR(50) NOT NULL COMMENT '省',
    `receive_city` VARCHAR(50) NOT NULL COMMENT '市',
    `receive_district` VARCHAR(50) NOT NULL COMMENT '区/县',
    `receive_detail` VARCHAR(200) NOT NULL COMMENT '详细地址',
    `receive_zip_code` VARCHAR(10) DEFAULT NULL COMMENT '邮编',
    `remark` VARCHAR(500) DEFAULT NULL COMMENT '订单备注',
    `pay_time` DATETIME DEFAULT NULL COMMENT '支付时间',
    `finish_time` DATETIME DEFAULT NULL COMMENT '完成时间',
    `cancel_time` DATETIME DEFAULT NULL COMMENT '取消时间',
    `cancel_reason` VARCHAR(200) DEFAULT NULL COMMENT '取消原因',
    `refund_reason` VARCHAR(200) DEFAULT NULL COMMENT '退款原因',
    `refund_amount` DECIMAL(10,2) DEFAULT NULL COMMENT '退款金额',
    `created_at` DATETIME NOT NULL DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
    `updated_at` DATETIME NOT NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP COMMENT '更新时间',
    `deleted` TINYINT DEFAULT 0 COMMENT '逻辑删除',
    PRIMARY KEY (`id`),
    UNIQUE KEY `uk_order_no` (`order_no`),
    KEY `idx_user_id` (`user_id`),
    KEY `idx_status` (`status`),
    KEY `idx_created_at` (`created_at`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COMMENT='订单表';

-- 4.2 订单商品表
CREATE TABLE `oms_order_item` (
    `id` BIGINT NOT NULL AUTO_INCREMENT COMMENT '订单商品ID',
    `order_id` BIGINT NOT NULL COMMENT '订单ID',
    `order_no` VARCHAR(64) NOT NULL COMMENT '订单编号',
    `product_id` BIGINT NOT NULL COMMENT '商品ID',
    `product_name` VARCHAR(200) NOT NULL COMMENT '商品名称',
    `product_pic` VARCHAR(500) DEFAULT NULL COMMENT '商品图片',
    `product_price` DECIMAL(10,2) NOT NULL COMMENT '商品单价',
    `product_quantity` INT NOT NULL COMMENT '购买数量',
    `product_sku` VARCHAR(200) DEFAULT NULL COMMENT '商品规格',
    `subtotal` DECIMAL(10,2) NOT NULL COMMENT '小计金额',
    `created_at` DATETIME NOT NULL DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
    PRIMARY KEY (`id`),
    KEY `idx_order_id` (`order_id`),
    KEY `idx_product_id` (`product_id`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COMMENT='订单商品表';

-- 4.3 订单操作日志表
CREATE TABLE `oms_order_log` (
    `id` BIGINT NOT NULL AUTO_INCREMENT COMMENT '日志ID',
    `order_id` BIGINT NOT NULL COMMENT '订单ID',
    `operator` VARCHAR(64) NOT NULL COMMENT '操作人',
    `action` VARCHAR(100) NOT NULL COMMENT '操作动作',
    `note` VARCHAR(500) DEFAULT NULL COMMENT '备注',
    `created_at` DATETIME NOT NULL DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
    PRIMARY KEY (`id`),
    KEY `idx_order_id` (`order_id`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COMMENT='订单操作日志表';

-- =============================================
-- 5. 管理员相关表
-- =============================================

-- 5.1 管理员用户表
CREATE TABLE `ums_admin` (
    `id` BIGINT NOT NULL AUTO_INCREMENT COMMENT '管理员ID',
    `username` VARCHAR(64) NOT NULL COMMENT '用户名',
    `password` VARCHAR(256) NOT NULL COMMENT '密码(加密)',
    `nickname` VARCHAR(64) DEFAULT NULL COMMENT '昵称',
    `avatar` VARCHAR(500) DEFAULT NULL COMMENT '头像',
    `email` VARCHAR(100) DEFAULT NULL COMMENT '邮箱',
    `phone` VARCHAR(20) DEFAULT NULL COMMENT '手机号',
    `status` TINYINT DEFAULT 1 COMMENT '状态: 0-禁用, 1-启用',
    `last_login_time` DATETIME DEFAULT NULL COMMENT '最后登录时间',
    `created_at` DATETIME NOT NULL DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
    `updated_at` DATETIME NOT NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP COMMENT '更新时间',
    `deleted` TINYINT DEFAULT 0 COMMENT '逻辑删除',
    PRIMARY KEY (`id`),
    UNIQUE KEY `uk_username` (`username`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COMMENT='管理员用户表';

-- 5.2 角色表
CREATE TABLE `ums_role` (
    `id` BIGINT NOT NULL AUTO_INCREMENT COMMENT '角色ID',
    `name` VARCHAR(64) NOT NULL COMMENT '角色名称',
    `code` VARCHAR(64) NOT NULL COMMENT '角色编码',
    `description` VARCHAR(200) DEFAULT NULL COMMENT '角色描述',
    `status` TINYINT DEFAULT 1 COMMENT '状态: 0-禁用, 1-启用',
    `created_at` DATETIME NOT NULL DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
    `updated_at` DATETIME NOT NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP COMMENT '更新时间',
    PRIMARY KEY (`id`),
    UNIQUE KEY `uk_code` (`code`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COMMENT='角色表';

-- 5.3 菜单表
CREATE TABLE `ums_menu` (
    `id` BIGINT NOT NULL AUTO_INCREMENT COMMENT '菜单ID',
    `parent_id` BIGINT DEFAULT 0 COMMENT '父菜单ID',
    `name` VARCHAR(64) NOT NULL COMMENT '菜单名称',
    `url` VARCHAR(200) DEFAULT NULL COMMENT '菜单URL',
    `icon` VARCHAR(64) DEFAULT NULL COMMENT '图标',
    `permission` VARCHAR(200) DEFAULT NULL COMMENT '权限标识',
    `type` TINYINT DEFAULT 1 COMMENT '类型: 1-目录, 2-菜单, 3-按钮',
    `sort_order` INT DEFAULT 0 COMMENT '排序值',
    `status` TINYINT DEFAULT 1 COMMENT '状态: 0-隐藏, 1-显示',
    `created_at` DATETIME NOT NULL DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
    `updated_at` DATETIME NOT NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP COMMENT '更新时间',
    PRIMARY KEY (`id`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COMMENT='菜单表';

-- 5.4 管理员角色关联表
CREATE TABLE `ums_admin_role` (
    `id` BIGINT NOT NULL AUTO_INCREMENT COMMENT '关联ID',
    `admin_id` BIGINT NOT NULL COMMENT '管理员ID',
    `role_id` BIGINT NOT NULL COMMENT '角色ID',
    PRIMARY KEY (`id`),
    UNIQUE KEY `uk_admin_role` (`admin_id`, `role_id`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COMMENT='管理员角色关联表';

-- 5.5 角色菜单关联表
CREATE TABLE `ums_role_menu` (
    `id` BIGINT NOT NULL AUTO_INCREMENT COMMENT '关联ID',
    `role_id` BIGINT NOT NULL COMMENT '角色ID',
    `menu_id` BIGINT NOT NULL COMMENT '菜单ID',
    PRIMARY KEY (`id`),
    UNIQUE KEY `uk_role_menu` (`role_id`, `menu_id`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COMMENT='角色菜单关联表';

-- =============================================
-- 6. 系统配置表
-- =============================================
CREATE TABLE `sys_config` (
    `id` BIGINT NOT NULL AUTO_INCREMENT COMMENT '配置ID',
    `config_key` VARCHAR(100) NOT NULL COMMENT '配置键',
    `config_value` VARCHAR(500) NOT NULL COMMENT '配置值',
    `description` VARCHAR(200) DEFAULT NULL COMMENT '配置描述',
    `created_at` DATETIME NOT NULL DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
    `updated_at` DATETIME NOT NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP COMMENT '更新时间',
    PRIMARY KEY (`id`),
    UNIQUE KEY `uk_config_key` (`config_key`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COMMENT='系统配置表';

-- =============================================
-- 初始化数据
-- =============================================

-- 管理员账号: admin / admin123 (BCrypt加密)
INSERT INTO `ums_admin` (`username`, `password`, `nickname`, `status`) VALUES
('admin', '$2a$10$N.zmdr9k7uOCQb376NoUnuTJ8iAt6Z5EHsM8lE9lBOsl7iAt6Z5EH', '系统管理员', 1);

-- 初始角色
INSERT INTO `ums_role` (`name`, `code`, `description`) VALUES
('超级管理员', 'ROLE_SUPER_ADMIN', '拥有所有权限'),
('运营管理员', 'ROLE_OPERATION', '商品管理、订单管理权限'),
('客服管理员', 'ROLE_CUSTOMER_SERVICE', '订单查询、用户管理权限');

-- 关联管理员和角色
INSERT INTO `ums_admin_role` (`admin_id`, `role_id`) VALUES (1, 1);

-- 初始化菜单数据
INSERT INTO `ums_menu` (`parent_id`, `name`, `url`, `icon`, `permission`, `type`, `sort_order`) VALUES
(0, '仪表盘', '/admin/dashboard', 'Dashboard', 'dashboard', 1, 1),
(0, '商品管理', '/admin/product', 'Goods', 'product:manage', 1, 2),
(1, '分类管理', '/admin/category', 'Collection', 'product:category:list', 2, 1),
(1, '品牌管理', '/admin/brand', 'Tag', 'product:brand:list', 2, 2),
(1, '商品列表', '/admin/product/list', 'List', 'product:list', 2, 3),
(0, '订单管理', '/admin/order', 'Tickets', 'order:manage', 1, 3),
(5, '订单列表', '/admin/order/list', 'List', 'order:list', 2, 1),
(0, '用户管理', '/admin/user', 'User', 'user:manage', 1, 4),
(7, '用户列表', '/admin/user/list', 'List', 'user:list', 2, 1),
(0, '数据统计', '/admin/statistics', 'DataAnalysis', 'statistics:manage', 1, 5),
(0, '系统设置', '/admin/system', 'Setting', 'system:manage', 1, 6),
(10, '基础配置', '/admin/system/config', 'Setting', 'system:config:list', 2, 1);

-- 角色菜单关联 (超级管理员拥有所有菜单)
INSERT INTO `ums_role_menu` (`role_id`, `menu_id`) SELECT 1, `id` FROM `ums_menu`;

-- 初始商品分类
INSERT INTO `pms_category` (`parent_id`, `name`, `level`, `sort_order`, `status`) VALUES
(0, '手机数码', 1, 1, 1),
(0, '电脑办公', 1, 2, 1),
(0, '家用电器', 1, 3, 1),
(0, '服装鞋帽', 1, 4, 1),
(1, '手机通讯', 2, 1, 1),
(1, '摄影摄像', 2, 2, 1),
(2, '笔记本', 2, 1, 1),
(2, '台式机', 2, 2, 1),
(3, '大家电', 2, 1, 1),
(3, '小家电', 2, 2, 1),
(4, '男装', 2, 1, 1),
(4, '女装', 2, 2, 1),
(5, '智能手机', 3, 1, 1),
(5, '功能手机', 3, 2, 1),
(6, '数码相机', 3, 1, 1),
(6, '镜头', 3, 2, 1),
(7, '轻薄本', 3, 1, 1),
(7, '游戏本', 3, 2, 1),
(9, '冰箱', 3, 1, 1),
(9, '洗衣机', 3, 2, 1);

-- 系统配置
INSERT INTO `sys_config` (`config_key`, `config_value`, `description`) VALUES
('shop_name', 'Mall电商系统', '店铺名称'),
('shop_logo', '', '店铺LOGO'),
('shop_description', '优质购物体验', '店铺描述'),
('freight_default', '10.00', '默认运费'),
('free_freight_threshold', '99.00', '免运费门槛'),
('order_cancel_timeout', '30', '订单未支付自动取消时间(分钟)'),
('order_receive_timeout', '15', '收货后自动确认时间(天)');
