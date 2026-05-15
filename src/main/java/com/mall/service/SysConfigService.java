package com.mall.service;

import com.mall.entity.SysConfig;

import java.util.List;

/**
 * 系统配置服务接口
 */
public interface SysConfigService {

    /** 获取所有配置 */
    List<SysConfig> getAllConfigs();

    /** 根据键获取配置值 */
    String getConfigValue(String key);

    /** 更新配置 */
    void updateConfig(List<SysConfig> configs);
}
