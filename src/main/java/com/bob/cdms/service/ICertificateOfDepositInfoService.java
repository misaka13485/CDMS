package com.bob.cdms.service;

import com.bob.cdms.entity.CertificateOfDepositInfo;
import com.baomidou.mybatisplus.extension.service.IService;

import java.util.List;

/**
 * <p>
 *  服务类
 * </p>
 *
 * @author zengxuan
 * @since 2022-09-10
 */
public interface ICertificateOfDepositInfoService extends IService<CertificateOfDepositInfo> {

    List<CertificateOfDepositInfo> selectAll(String name);
}
