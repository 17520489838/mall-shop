package com.mall.dao;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.mall.entity.UmsUser;
import org.apache.ibatis.annotations.Param;
import org.apache.ibatis.annotations.Select;
import org.apache.ibatis.annotations.Update;

/**
 * 用户DAO
 */
public interface UmsUserDao extends BaseMapper<UmsUser> {

    @Select("SELECT * FROM ums_user WHERE username = #{username} AND deleted = 0")
    UmsUser selectByUsername(@Param("username") String username);

    @Select("SELECT * FROM ums_user WHERE phone = #{phone} AND deleted = 0")
    UmsUser selectByPhone(@Param("phone") String phone);

    @Select("SELECT * FROM ums_user WHERE email = #{email} AND deleted = 0")
    UmsUser selectByEmail(@Param("email") String email);

    @Update("UPDATE ums_user SET last_login_time = NOW() WHERE id = #{userId}")
    int updateLoginTime(@Param("userId") Long userId);

    @Update("UPDATE ums_user SET balance = balance - #{amount} WHERE id = #{userId} AND balance >= #{amount}")
    int deductBalance(@Param("userId") Long userId, @Param("amount") java.math.BigDecimal amount);

    @Update("UPDATE ums_user SET balance = balance + #{amount} WHERE id = #{userId}")
    int addBalance(@Param("userId") Long userId, @Param("amount") java.math.BigDecimal amount);

    @Select("SELECT balance FROM ums_user WHERE id = #{userId}")
    java.math.BigDecimal selectBalance(@Param("userId") Long userId);
}
