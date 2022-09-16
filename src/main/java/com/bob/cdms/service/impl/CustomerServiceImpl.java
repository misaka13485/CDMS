package com.bob.cdms.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.bob.cdms.VO.NewUser;
import com.bob.cdms.constant.System;
import com.bob.cdms.entity.CustomerInfo;
import com.bob.cdms.exception.BusinessException;
import com.bob.cdms.mapper.CustomerInfoMapper;
import com.bob.cdms.service.ICustomerInfoService;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.redis.core.StringRedisTemplate;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.Random;

import static com.bob.cdms.constant.ErrorCode.*;
import static com.bob.cdms.constant.System.LOGIN_KEY;

/**
 * <p>
 *  服务实现类
 * </p>
 *
 * @author zengxuan
 * @since 2022-09-10
 */
@Service
public class CustomerServiceImpl extends ServiceImpl<CustomerInfoMapper, CustomerInfo> implements ICustomerInfoService {

    @Autowired
    private StringRedisTemplate stringRedisTemplate;

    @Override
    public boolean register(NewUser newUser) throws BusinessException {
        //判断参数属性值是否为空
        if (newUser == null) {
            throw new BusinessException(PARAM_ERROR_CODE, PARAM_ERROR_MSG);
        }
        if (newUser.getUserName() == null || newUser.getUserName().equals("")) {
            throw new BusinessException(PARAM_ERROR_CODE, PARAM_ERROR_MSG);
        }
        if (newUser.getPassword() == null || newUser.getPassword().equals("")) {
            throw new BusinessException(PARAM_ERROR_CODE, PARAM_ERROR_MSG);
        }
        if (newUser.getSex() == null || newUser.getSex().equals("")) {
            throw new BusinessException(PARAM_ERROR_CODE, PARAM_ERROR_MSG);
        }
        if (newUser.getPhoneNumber() == null || newUser.getPhoneNumber().equals("")) {
            throw new BusinessException(PARAM_ERROR_CODE, PARAM_ERROR_MSG);
        }
        //非空验证通过，开始插入
        CustomerInfo customerInfo = new CustomerInfo();
        customerInfo.setCustomerName(newUser.getUserName());
        customerInfo.setCustomerPassword(newUser.getPassword());
        customerInfo.setCustomerSex(newUser.getSex());
        customerInfo.setPhoneNumber(newUser.getPhoneNumber());
        customerInfo.setStatus("启用");
        try {
            return this.save(customerInfo);
        }catch (Exception e) {
            throw new BusinessException(INSERT_ERROR_CODE, INSERT_ERROR_MSG);
        }

    }

    @Override
    public String login(String userName, String password) throws BusinessException {
        QueryWrapper<CustomerInfo> queryWrapper = new QueryWrapper<>();
        queryWrapper.eq("CUST_NAME", userName);
        queryWrapper.eq("CUST_PASSWORD", password);
        CustomerInfo customerInfo = this.getOne(queryWrapper);
        if (customerInfo == null) {
            throw new BusinessException(NO_LOGIN_CODE, NO_LOGIN_MSG);
        }else {
            //随机生成的token
            String token =  LOGIN_KEY + java.lang.System.currentTimeMillis() + userName.hashCode();
            //将token存入redis,设置过期时间
            stringRedisTemplate.opsForValue().set(token, customerInfo.getCustomerNo(), System.REDIS_EXPIRE_TIME, System.REDIS_EXPIRE_TIME_UNIT);
            return token;
        }
    }

    /**
     * 快速登录检查
     */
    @Override
    public String checkLogin(String token) throws BusinessException {
        String userId = stringRedisTemplate.opsForValue().get(token);
        if (userId == null) {
            throw new BusinessException(NO_LOGIN_CODE, NO_LOGIN_MSG);
        }else {
            return userId;
        }
    }

}
