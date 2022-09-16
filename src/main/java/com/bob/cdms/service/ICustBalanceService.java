package com.bob.cdms.service;

import com.bob.cdms.entity.CustBalance;
import com.github.jeffreyning.mybatisplus.service.IMppService;

import java.math.BigDecimal;
import java.util.List;

/**
 * <p>
 * 余额明细 服务类
 * </p>
 *
 * @author zengxuan
 * @since 2022-09-10
 */
public interface ICustBalanceService extends IMppService<CustBalance> {


    boolean changeBalance(String userId, BigDecimal money, String type);

    List<CustBalance> getBalance(String userId);
}
