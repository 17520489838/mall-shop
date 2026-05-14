package com.mall.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.mall.common.exception.BusinessException;
import com.mall.common.result.ResultCode;
import com.mall.dao.UmsAddressDao;
import com.mall.dto.AddressDTO;
import com.mall.entity.UmsAddress;
import com.mall.service.UmsAddressService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.BeanUtils;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Slf4j
@Service
public class UmsAddressServiceImpl implements UmsAddressService {

    private final UmsAddressDao addressDao;

    public UmsAddressServiceImpl(UmsAddressDao addressDao) {
        this.addressDao = addressDao;
    }

    @Override
    public List<UmsAddress> listAddresses(Long userId) {
        return addressDao.selectList(
                new LambdaQueryWrapper<UmsAddress>()
                        .eq(UmsAddress::getUserId, userId)
                        .orderByDesc(UmsAddress::getIsDefault)
                        .orderByDesc(UmsAddress::getCreatedAt));
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public void addAddress(Long userId, AddressDTO addressDTO) {
        if (addressDTO.getIsDefault() == 1) {
            addressDao.clearDefault(userId);
        }
        UmsAddress address = new UmsAddress();
        BeanUtils.copyProperties(addressDTO, address);
        address.setUserId(userId);
        addressDao.insert(address);
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public void updateAddress(Long userId, Long addressId, AddressDTO addressDTO) {
        UmsAddress address = addressDao.selectById(addressId);
        if (address == null || !address.getUserId().equals(userId)) {
            throw new BusinessException(ResultCode.NOT_FOUND);
        }
        if (addressDTO.getIsDefault() == 1) {
            addressDao.clearDefault(userId);
        }
        BeanUtils.copyProperties(addressDTO, address);
        address.setId(addressId);
        address.setUserId(userId);
        addressDao.updateById(address);
    }

    @Override
    public void deleteAddress(Long userId, Long addressId) {
        UmsAddress address = addressDao.selectById(addressId);
        if (address == null || !address.getUserId().equals(userId)) {
            throw new BusinessException(ResultCode.NOT_FOUND);
        }
        addressDao.deleteById(addressId);
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public void setDefault(Long userId, Long addressId) {
        addressDao.clearDefault(userId);
        UmsAddress address = addressDao.selectById(addressId);
        if (address != null && address.getUserId().equals(userId)) {
            address.setIsDefault(1);
            addressDao.updateById(address);
        }
    }
}
