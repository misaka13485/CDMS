package com.bob.cdms.mapper;

import com.bob.cdms.entity.CertificateOfDepositInfo;
import com.baomidou.mybatisplus.core.mapper.BaseMapper;

/**
 * <p>
 *  Mapper 接口
 * </p>
 *
 * @author zengxuan
 * @since 2022-09-10
 */
public interface CertificateOfDepositInfoMapper extends BaseMapper<CertificateOfDepositInfo> {

    public CertificateOfDepositInfo getToLock(String id);


}
