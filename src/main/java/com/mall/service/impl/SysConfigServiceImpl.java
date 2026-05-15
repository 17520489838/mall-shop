package com.mall.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.mall.dao.SysConfigDao;
import com.mall.entity.SysConfig;
import com.mall.service.SysConfigService;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

/**
 * 系统配置服务实现
 */
@Service
public class SysConfigServiceImpl implements SysConfigService {

    private final SysConfigDao sysConfigDao;

    public SysConfigServiceImpl(SysConfigDao sysConfigDao) {
        this.sysConfigDao = sysConfigDao;
    }

    @Override
    public List<SysConfig> getAllConfigs() {
        return sysConfigDao.selectList(null);
    }

    @Override
    public String getConfigValue(String key) {
        SysConfig config = sysConfigDao.selectOne(
                new LambdaQueryWrapper<SysConfig>()
                        .eq(SysConfig::getConfigKey, key));
        return config != null ? config.getConfigValue() : null;
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public void updateConfig(List<SysConfig> configs) {
        for (SysConfig config : configs) {
            SysConfig exist = sysConfigDao.selectOne(
                    new LambdaQueryWrapper<SysConfig>()
                            .eq(SysConfig::getConfigKey, config.getConfigKey()));
            if (exist != null) {
                exist.setConfigValue(config.getConfigValue());
                sysConfigDao.updateById(exist);
            }
        }
    }
}
