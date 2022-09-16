package com.bob.cdms.mapper;

import com.bob.cdms.entity.CertificateOfDepositOrder;
import com.baomidou.mybatisplus.core.mapper.BaseMapper;

import java.math.BigDecimal;
import java.util.Map;

/**
 * <p>
 * 存单销售流水表 Mapper 接口
 * </p>
 *
 * @author zengxuan
 * @since 2022-09-10
 */
public interface CertificateOfDepositOrderMapper extends BaseMapper<CertificateOfDepositOrder> {
    BigDecimal getCustCertificateOfDepositOrderAmount(Map<String, Object> params);

}
