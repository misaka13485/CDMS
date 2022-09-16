package com.bob.cdms.service;

import com.bob.cdms.VO.NewUser;
import com.bob.cdms.entity.CustomerInfo;
import com.baomidou.mybatisplus.extension.service.IService;
import com.bob.cdms.exception.BusinessException;

/**
 * <p>
 *  服务类
 * </p>
 *
 * @author zengxuan
 * @since 2022-09-10
 */
public interface ICustomerInfoService extends IService<CustomerInfo> {

    boolean register(NewUser newUser) throws BusinessException;

    String login(String userName, String password);

    String checkLogin(String token) throws BusinessException;
}
