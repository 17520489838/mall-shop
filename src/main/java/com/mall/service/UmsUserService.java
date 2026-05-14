package com.mall.service;

import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.baomidou.mybatisplus.extension.service.IService;
import com.mall.dto.LoginDTO;
import com.mall.dto.RegisterDTO;
import com.mall.entity.UmsUser;
import com.mall.vo.LoginVO;

/**
 * 用户服务接口
 */
public interface UmsUserService extends IService<UmsUser> {

    /**
     * 用户注册
     */
    void register(RegisterDTO dto);

    /**
     * 用户登录
     */
    LoginVO login(LoginDTO dto);

    /**
     * 获取当前登录用户信息
     */
    UmsUser getCurrentUser(Long userId);

    /**
     * 更新用户信息
     */
    void updateUserInfo(UmsUser user);

    /**
     * 修改密码
     */
    void updatePassword(Long userId, String oldPassword, String newPassword);

    /**
     * 分页查询用户(管理后台)
     */
    Page<UmsUser> listUsers(Integer pageNum, Integer pageSize, String keyword);

    /**
     * 启用/禁用用户
     */
    void toggleUserStatus(Long userId);
}
