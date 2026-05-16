package com.mall.service.impl;

import com.mall.common.constant.CommonConstant;
import com.mall.common.exception.BusinessException;
import com.mall.common.result.ResultCode;
import com.mall.common.utils.JwtUtils;
import com.mall.common.utils.RedisCache;
import com.mall.dao.*;
import com.mall.dto.LoginDTO;
import com.mall.entity.*;
import com.mall.service.UmsAdminService;
import com.mall.vo.DashboardVO;
import com.mall.vo.LoginVO;
import lombok.extern.slf4j.Slf4j;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.concurrent.TimeUnit;
import java.util.stream.Collectors;

/**
 * 管理员服务实现
 */
@Slf4j
@Service
public class UmsAdminServiceImpl implements UmsAdminService {

    private final UmsAdminDao adminDao;
    private final UmsRoleDao roleDao;
    private final UmsMenuDao menuDao;
    private final UmsRoleMenuDao roleMenuDao;
    private final UmsAdminRoleDao adminRoleDao;
    private final OmsOrderDao orderDao;
    private final UmsUserDao userDao;
    private final PmsProductDao productDao;
    private final PasswordEncoder passwordEncoder;
    private final JwtUtils jwtUtils;
    private final RedisCache redisCache;

    public UmsAdminServiceImpl(UmsAdminDao adminDao, UmsRoleDao roleDao, UmsMenuDao menuDao,
                                UmsRoleMenuDao roleMenuDao, UmsAdminRoleDao adminRoleDao,
                                OmsOrderDao orderDao, UmsUserDao userDao, PmsProductDao productDao,
                                PasswordEncoder passwordEncoder, JwtUtils jwtUtils, RedisCache redisCache) {
        this.adminDao = adminDao;
        this.roleDao = roleDao;
        this.menuDao = menuDao;
        this.roleMenuDao = roleMenuDao;
        this.adminRoleDao = adminRoleDao;
        this.orderDao = orderDao;
        this.userDao = userDao;
        this.productDao = productDao;
        this.passwordEncoder = passwordEncoder;
        this.jwtUtils = jwtUtils;
        this.redisCache = redisCache;
    }

    @Override
    public LoginVO login(LoginDTO dto) {
        UmsAdmin admin = adminDao.selectByUsername(dto.getUsername());
        if (admin == null) {
            throw new BusinessException(ResultCode.ADMIN_NOT_EXIST);
        }
        if (admin.getStatus() == 0) {
            throw new BusinessException(ResultCode.ADMIN_ACCOUNT_DISABLED);
        }
        if (!passwordEncoder.matches(dto.getPassword(), admin.getPassword())) {
            throw new BusinessException(ResultCode.ADMIN_PASSWORD_ERROR);
        }

        String token = jwtUtils.generateAdminToken(admin.getId(), admin.getUsername());
        adminDao.updateLoginTime(admin.getId());

        // 缓存管理员权限
        List<String> permissions = menuDao.selectPermissionsByAdminId(admin.getId());
        String cacheKey = CommonConstant.CACHE_ADMIN_TOKEN + CommonConstant.CACHE_SEPARATOR + admin.getId();
        redisCache.hSet(cacheKey, "permissions", permissions);
        redisCache.expire(cacheKey, 2, TimeUnit.HOURS);

        return LoginVO.builder()
                .userId(admin.getId())
                .username(admin.getUsername())
                .nickname(admin.getNickname())
                .avatar(admin.getAvatar())
                .token(token)
                .expireIn(7200L)
                .build();
    }

    @Override
    public UmsAdmin getCurrentAdmin(Long adminId) {
        UmsAdmin admin = adminDao.selectById(adminId);
        if (admin == null) {
            throw new BusinessException(ResultCode.ADMIN_NOT_EXIST);
        }
        return admin;
    }

    @Override
    public List<UmsMenu> getAdminMenus(Long adminId) {
        String cacheKey = "mall:admin:menus:" + adminId;
        List<UmsMenu> cached = redisCache.get(cacheKey);
        if (cached != null) {
            return cached;
        }

        List<UmsMenu> allMenus = menuDao.selectByAdminId(adminId);
        // 过滤掉按钮级权限(type=3)，侧边栏只显示目录和菜单
        allMenus = allMenus.stream()
                .filter(m -> m.getType() != null && m.getType() != 3)
                .collect(Collectors.toList());
        List<UmsMenu> tree = buildMenuTree(allMenus, 0L);

        redisCache.set(cacheKey, tree, 5, TimeUnit.MINUTES);
        return tree;
    }

    @Override
    public List<String> getAdminPermissions(Long adminId) {
        return menuDao.selectPermissionsByAdminId(adminId);
    }

    @Override
    public DashboardVO getDashboardData() {
        // 尝试从缓存获取（缓存60秒）
        String cacheKey = "mall:dashboard";
        DashboardVO cached = redisCache.get(cacheKey);
        if (cached != null) {
            return cached;
        }

        DashboardVO vo = new DashboardVO();

        vo.setTodayOrderCount(orderDao.selectTodayOrderCount());
        vo.setTodaySalesAmount(orderDao.selectTodaySalesAmount());
        vo.setTotalOrderCount(orderDao.selectTotalOrderCount());

        Long userCount = userDao.selectCount(null);
        vo.setTotalUserCount(userCount);

        Long productCount = productDao.selectCount(null);
        vo.setTotalProductCount(productCount);

        vo.setPendingOrderCount(orderDao.selectPendingOrderCount());
        vo.setPendingShipCount(orderDao.selectPendingShipCount());

        // 库存不足商品(stock <= 10)
        vo.setLowStockProducts(productDao.selectLowStockProducts());

        vo.setOrderTrend(orderDao.selectOrderTrend());
        vo.setSalesTrend(orderDao.selectSalesTrend());
        vo.setOrderStatusDistribution(orderDao.selectOrderStatusDistribution());

        // 热销商品TOP10
        List<PmsProduct> hotProducts = productDao.selectHotProducts(10);
        vo.setHotProducts(hotProducts.stream().map(p -> {
            Map<String, Object> map = new HashMap<>();
            map.put("id", p.getId());
            map.put("name", p.getName());
            map.put("pic", p.getPic());
            map.put("sales", p.getSales());
            map.put("price", p.getPrice());
            return map;
        }).collect(Collectors.toList()));

        redisCache.set(cacheKey, vo, 60, TimeUnit.SECONDS);
        return vo;
    }

    @Override
    public List<UmsRole> listRoles() {
        return roleDao.selectList(null);
    }

    @Override
    public void createRole(UmsRole role) {
        roleDao.insert(role);
    }

    @Override
    public void updateRole(UmsRole role) {
        roleDao.updateById(role);
    }

    @Override
    public void deleteRole(Long id) {
        roleDao.deleteById(id);
    }

    @Override
    public List<UmsMenu> listMenus() {
        List<UmsMenu> allMenus = menuDao.selectList(
                new com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper<UmsMenu>()
                        .orderByAsc(UmsMenu::getSortOrder));
        return buildMenuTree(allMenus, 0L);
    }

    @Override
    public void createMenu(UmsMenu menu) {
        menuDao.insert(menu);
        clearAdminMenuCache();
    }

    @Override
    public void updateMenu(UmsMenu menu) {
        menuDao.updateById(menu);
        clearAdminMenuCache();
    }

    @Override
    public void deleteMenu(Long id) {
        menuDao.deleteById(id);
        clearAdminMenuCache();
    }

    @Override
    public List<Long> getRoleMenuIds(Long roleId) {
        return roleMenuDao.selectList(
                new com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper<UmsRoleMenu>()
                        .eq(UmsRoleMenu::getRoleId, roleId))
                .stream()
                .map(UmsRoleMenu::getMenuId)
                .collect(Collectors.toList());
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public void updateRoleMenus(Long roleId, List<Long> menuIds) {
        // 删除角色原有的菜单关联
        roleMenuDao.delete(new com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper<UmsRoleMenu>()
                .eq(UmsRoleMenu::getRoleId, roleId));
        // 插入新的菜单关联
        if (menuIds != null && !menuIds.isEmpty()) {
            for (Long menuId : menuIds) {
                UmsRoleMenu rm = new UmsRoleMenu();
                rm.setRoleId(roleId);
                rm.setMenuId(menuId);
                roleMenuDao.insert(rm);
            }
        }
        clearAdminMenuCache();
    }

    /**
     * 清除所有管理员的菜单缓存（菜单结构变化时调用）
     */
    private void clearAdminMenuCache() {
        try {
            Set<String> keys = redisCache.keys("mall:admin:menus:*");
            if (keys != null && !keys.isEmpty()) {
                redisCache.delete(keys);
                log.info("已清除 {} 个管理员菜单缓存", keys.size());
            }
        } catch (Exception e) {
            log.warn("清除管理员菜单缓存失败", e);
        }
    }

    private List<UmsMenu> buildMenuTree(List<UmsMenu> all, Long parentId) {
        return all.stream()
                .filter(m -> parentId.equals(m.getParentId()))
                .peek(m -> m.setChildren(buildMenuTree(all, m.getId())))
                .collect(Collectors.toList());
    }
}
