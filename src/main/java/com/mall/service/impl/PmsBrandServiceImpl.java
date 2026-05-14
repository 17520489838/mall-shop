package com.mall.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.mall.dao.PmsBrandDao;
import com.mall.entity.PmsBrand;
import com.mall.service.PmsBrandService;
import org.springframework.stereotype.Service;
import org.springframework.util.StringUtils;

import java.util.List;

@Service
public class PmsBrandServiceImpl implements PmsBrandService {

    private final PmsBrandDao brandDao;

    public PmsBrandServiceImpl(PmsBrandDao brandDao) {
        this.brandDao = brandDao;
    }

    @Override
    public List<PmsBrand> listAll() {
        return brandDao.selectList(
                new LambdaQueryWrapper<PmsBrand>().eq(PmsBrand::getStatus, 1)
                        .orderByAsc(PmsBrand::getSortOrder));
    }

    @Override
    public Page<PmsBrand> listBrands(Integer pageNum, Integer pageSize, String keyword) {
        Page<PmsBrand> page = new Page<>(pageNum, pageSize);
        LambdaQueryWrapper<PmsBrand> wrapper = new LambdaQueryWrapper<>();
        if (StringUtils.hasText(keyword)) {
            wrapper.like(PmsBrand::getName, keyword);
        }
        wrapper.orderByAsc(PmsBrand::getSortOrder);
        return brandDao.selectPage(page, wrapper);
    }

    @Override
    public PmsBrand getBrand(Long id) {
        return brandDao.selectById(id);
    }

    @Override
    public void createBrand(PmsBrand brand) {
        brandDao.insert(brand);
    }

    @Override
    public void updateBrand(PmsBrand brand) {
        brandDao.updateById(brand);
    }

    @Override
    public void deleteBrand(Long id) {
        brandDao.deleteById(id);
    }
}
