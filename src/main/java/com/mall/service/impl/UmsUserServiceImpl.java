package com.mall.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.mall.common.exception.BusinessException;
import com.mall.common.result.ResultCode;
import com.mall.common.utils.JwtUtils;
import com.mall.dao.UmsUserDao;
import com.mall.dto.LoginDTO;
import com.mall.dto.RegisterDTO;
import com.mall.entity.UmsUser;
import com.mall.service.UmsUserService;
import com.mall.vo.LoginVO;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.BeanUtils;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.util.StringUtils;

/**
 * 用户服务实现
 */
@Slf4j
@Service
public class UmsUserServiceImpl extends ServiceImpl<UmsUserDao, UmsUser> implements UmsUserService {

    private final UmsUserDao userDao;
    private final PasswordEncoder passwordEncoder;
    private final JwtUtils jwtUtils;

    public UmsUserServiceImpl(UmsUserDao userDao, PasswordEncoder passwordEncoder, JwtUtils jwtUtils) {
        this.userDao = userDao;
        this.passwordEncoder = passwordEncoder;
        this.jwtUtils = jwtUtils;
    }

    @Override
    public void register(RegisterDTO dto) {
        // 检查用户名是否已存在
        if (userDao.selectByUsername(dto.getUsername()) != null) {
            throw new BusinessException(ResultCode.USER_ALREADY_EXIST);
        }
        // 检查手机号是否已存在
        if (StringUtils.hasText(dto.getPhone()) && userDao.selectByPhone(dto.getPhone()) != null) {
            throw new BusinessException(ResultCode.USER_PHONE_EXIST);
        }
        // 检查邮箱是否已存在
        if (StringUtils.hasText(dto.getEmail()) && userDao.selectByEmail(dto.getEmail()) != null) {
            throw new BusinessException(ResultCode.USER_EMAIL_EXIST);
        }

        UmsUser user = new UmsUser();
        BeanUtils.copyProperties(dto, user);
        user.setPassword(passwordEncoder.encode(dto.getPassword()));
        user.setNickname(dto.getUsername());
        user.setStatus(1);
        user.setSourceType("NORMAL");
        user.setBalance(new java.math.BigDecimal("10000.00")); // 新用户赠送余额

        if (save(user)) {
            log.info("用户注册成功: username={}", dto.getUsername());
        } else {
            throw new BusinessException("注册失败，请稍后重试");
        }
    }

    @Override
    public LoginVO login(LoginDTO dto) {
        UmsUser user = userDao.selectByUsername(dto.getUsername());
        if (user == null) {
            throw new BusinessException(ResultCode.USER_NOT_EXIST);
        }
        if (user.getStatus() == 0) {
            throw new BusinessException(ResultCode.USER_ACCOUNT_DISABLED);
        }
        if (!passwordEncoder.matches(dto.getPassword(), user.getPassword())) {
            throw new BusinessException(ResultCode.USER_PASSWORD_ERROR);
        }

        // 生成token
        String token = jwtUtils.generateUserToken(user.getId(), user.getUsername());
        userDao.updateLoginTime(user.getId());

        return LoginVO.builder()
                .userId(user.getId())
                .username(user.getUsername())
                .nickname(user.getNickname())
                .avatar(user.getAvatar())
                .token(token)
                .expireIn(86400L)
                .build();
    }

    @Override
    public UmsUser getCurrentUser(Long userId) {
        UmsUser user = getById(userId);
        if (user == null) {
            throw new BusinessException(ResultCode.USER_NOT_EXIST);
        }
        return user;
    }

    @Override
    public void updateUserInfo(UmsUser user) {
        UmsUser exist = getById(user.getId());
        if (exist == null) {
            throw new BusinessException(ResultCode.USER_NOT_EXIST);
        }
        exist.setNickname(user.getNickname());
        exist.setAvatar(user.getAvatar());
        exist.setGender(user.getGender());
        exist.setBirthday(user.getBirthday());
        exist.setPhone(user.getPhone());
        exist.setEmail(user.getEmail());
        updateById(exist);
    }

    @Override
    public void updatePassword(Long userId, String oldPassword, String newPassword) {
        UmsUser user = getById(userId);
        if (user == null) {
            throw new BusinessException(ResultCode.USER_NOT_EXIST);
        }
        if (!passwordEncoder.matches(oldPassword, user.getPassword())) {
            throw new BusinessException("原密码不正确");
        }
        user.setPassword(passwordEncoder.encode(newPassword));
        updateById(user);
    }

    @Override
    public Page<UmsUser> listUsers(Integer pageNum, Integer pageSize, String keyword) {
        Page<UmsUser> page = new Page<>(pageNum, pageSize);
        LambdaQueryWrapper<UmsUser> wrapper = new LambdaQueryWrapper<>();
        if (StringUtils.hasText(keyword)) {
            wrapper.like(UmsUser::getUsername, keyword)
                    .or().like(UmsUser::getNickname, keyword)
                    .or().like(UmsUser::getPhone, keyword);
        }
        wrapper.orderByDesc(UmsUser::getCreatedAt);
        return page(page, wrapper);
    }

    @Override
    public void toggleUserStatus(Long userId) {
        UmsUser user = getById(userId);
        if (user == null) {
            throw new BusinessException(ResultCode.USER_NOT_EXIST);
        }
        user.setStatus(user.getStatus() == 1 ? 0 : 1);
        updateById(user);
    }
}
