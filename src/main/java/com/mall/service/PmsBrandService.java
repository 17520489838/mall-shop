package com.mall.service;

import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.mall.entity.PmsBrand;

import java.util.List;

public interface PmsBrandService {

    List<PmsBrand> listAll();

    Page<PmsBrand> listBrands(Integer pageNum, Integer pageSize, String keyword);

    PmsBrand getBrand(Long id);

    void createBrand(PmsBrand brand);

    void updateBrand(PmsBrand brand);

    void deleteBrand(Long id);
}
