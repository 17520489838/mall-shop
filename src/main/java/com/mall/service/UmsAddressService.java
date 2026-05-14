package com.mall.service;

import com.mall.dto.AddressDTO;
import com.mall.entity.UmsAddress;

import java.util.List;

/**
 * 地址服务接口
 */
public interface UmsAddressService {

    List<UmsAddress> listAddresses(Long userId);

    void addAddress(Long userId, AddressDTO addressDTO);

    void updateAddress(Long userId, Long addressId, AddressDTO addressDTO);

    void deleteAddress(Long userId, Long addressId);

    void setDefault(Long userId, Long addressId);
}
