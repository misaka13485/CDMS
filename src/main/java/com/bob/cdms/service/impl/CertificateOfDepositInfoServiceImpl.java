package com.bob.cdms.service.impl;

import com.alibaba.fastjson.JSON;
import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.bob.cdms.constant.System;
import com.bob.cdms.entity.CertificateOfDepositInfo;
import com.bob.cdms.mapper.CertificateOfDepositInfoMapper;
import com.bob.cdms.service.ICertificateOfDepositInfoService;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.cache.annotation.Cacheable;
import org.springframework.data.redis.core.StringRedisTemplate;
import org.springframework.stereotype.Service;

import java.util.List;

/**
 * <p>
 *  服务实现类
 * </p>
 *
 * @author zengxuan
 * @since 2022-09-10
 */
@Service
public class CertificateOfDepositInfoServiceImpl extends ServiceImpl<CertificateOfDepositInfoMapper, CertificateOfDepositInfo> implements ICertificateOfDepositInfoService {

    @Autowired
    private CertificateOfDepositInfoMapper certificateOfDepositInfoMapper;

    @Autowired
    private StringRedisTemplate stringRedisTemplate;
    /**
     * 根据名称查询存单信息
     * @param name
     * @return List
     */
    @Override
    public List<CertificateOfDepositInfo> selectAll(String name){
        List<CertificateOfDepositInfo> result = null;
        //查询缓存
        String key = "cdInfo:" + name;
        String json = stringRedisTemplate.opsForValue().get(key);
        if (json != null){
            result = JSON.parseArray(json, CertificateOfDepositInfo.class);
            return result;
        }
        if (name != null && !"".equals(name)) {
            QueryWrapper<CertificateOfDepositInfo> queryWrapper = new QueryWrapper<>();
            queryWrapper.like("CERTIFICATES_OF_DEPOSIT_NAME", name);
            result = certificateOfDepositInfoMapper.selectList(queryWrapper);
        } else {
            QueryWrapper<CertificateOfDepositInfo> queryWrapper = new QueryWrapper<>();
            result =  certificateOfDepositInfoMapper.selectList(queryWrapper);
        }
        //写入redis
        stringRedisTemplate.opsForValue().set(key, JSON.toJSONString(result), System.CACHE_TIME, System.REDIS_EXPIRE_TIME_UNIT);
        return result;
    }
}
