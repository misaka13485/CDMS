package com.bob.cdms.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.bob.cdms.entity.CustBalance;
import com.bob.cdms.exception.BusinessException;
import com.bob.cdms.mapper.CustBalanceMapper;
import com.bob.cdms.service.ICustBalanceService;
import com.github.jeffreyning.mybatisplus.service.MppServiceImpl;
import org.springframework.stereotype.Service;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.List;

import static com.bob.cdms.constant.ErrorCode.*;

/**
 * <p>
 * 余额明细 服务实现类
 * </p>
 *
 * @author zengxuan
 * @since 2022-09-10
 */
@Service
public class CustBalanceServiceImpl extends MppServiceImpl<CustBalanceMapper, CustBalance> implements ICustBalanceService {

    @Override
    public boolean changeBalance(String userId, BigDecimal money, String type) throws BusinessException {
        //参数校验
        if (userId == null || money == null || type == null) {
            throw new BusinessException(PARAM_ERROR_CODE, PARAM_ERROR_MSG);
        }
        QueryWrapper<CustBalance> queryWrapper = new QueryWrapper<>();
        queryWrapper.eq("CUST_NO", userId);
        queryWrapper.eq("CURRENCY", type);
        CustBalance custBalance = this.getOne(queryWrapper);
        if (custBalance == null) {
            // 余额表中没有该用户的余额信息
            custBalance = new CustBalance();
            custBalance.setCustomerNo(userId);
            custBalance.setCurrency(type);
            custBalance.setBalance(money);
            custBalance.setLastChangeDate(LocalDateTime.now());
            return this.save(custBalance);
        } else {
            // 余额表中有该用户的余额信息
            custBalance.setBalance(custBalance.getBalance().add(money));
            custBalance.setLastChangeDate(LocalDateTime.now());
            return this.updateByMultiId(custBalance);
        }

    }

    @Override
    public List<CustBalance> getBalance(String userId) {
        QueryWrapper<CustBalance> queryWrapper = new QueryWrapper<>();
        queryWrapper.eq("CUST_NO", userId);
        return this.list(queryWrapper);
    }
}
