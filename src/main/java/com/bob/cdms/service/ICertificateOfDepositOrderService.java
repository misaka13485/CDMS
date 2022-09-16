package com.bob.cdms.service;

import com.bob.cdms.entity.CertificateOfDepositOrder;
import com.baomidou.mybatisplus.extension.service.IService;
import com.bob.cdms.exception.BusinessException;

import java.math.BigDecimal;
import java.sql.SQLException;

/**
 * <p>
 * 存单销售流水表 服务类
 * </p>
 *
 * @author zengxuan
 * @since 2022-09-10
 */
public interface ICertificateOfDepositOrderService extends IService<CertificateOfDepositOrder> {

    boolean buyProduct(String certificateId, String customerId, BigDecimal amount) throws BusinessException, SQLException;
}
