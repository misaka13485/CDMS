package com.bob.cdms.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.bob.cdms.VO.ResultVO;
import com.bob.cdms.entity.CertificateOfDepositInfo;
import com.bob.cdms.entity.CertificateOfDepositOrder;
import com.bob.cdms.entity.CustBalance;
import com.bob.cdms.exception.BusinessException;
import com.bob.cdms.mapper.CertificateOfDepositInfoMapper;
import com.bob.cdms.mapper.CertificateOfDepositOrderMapper;
import com.bob.cdms.service.ICertificateOfDepositInfoService;
import com.bob.cdms.service.ICertificateOfDepositOrderService;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.bob.cdms.service.ICustBalanceService;
import org.apache.ibatis.annotations.Param;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.cache.annotation.CacheEvict;
import org.springframework.data.redis.core.StringRedisTemplate;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.sql.SQLException;
import java.time.LocalDateTime;
import java.util.HashMap;
import java.util.Map;
import java.util.Random;

import static com.bob.cdms.constant.ErrorCode.*;

/**
 * <p>
 * 存单销售流水表 服务实现类
 * </p>
 *
 * @author zengxuan
 * @since 2022-09-10
 */
@Service
public class CertificateOfDepositOrderServiceImpl extends ServiceImpl<CertificateOfDepositOrderMapper, CertificateOfDepositOrder> implements ICertificateOfDepositOrderService {

    @Autowired
    private ICertificateOfDepositInfoService iCertificateOfDepositInfoService;

    @Autowired
    private ICustBalanceService iCustBalanceService;

    @Autowired
    private CertificateOfDepositInfoMapper certificateOfDepositInfoMapper;

    @Autowired
    private CertificateOfDepositOrderMapper certificateOfDepositOrderMapper;

    @Autowired
    ICertificateOfDepositOrderService iCertificateOfDepositOrderService;

    @Autowired
    private StringRedisTemplate stringRedisTemplate;
    /**
     * 购买产品
     *
     */
    @Override
    @Transactional(rollbackFor = Exception.class, timeout = 15)
    @CacheEvict(value = "CDinfo", allEntries = true)
    public boolean buyProduct(String certificateId, String customerId, BigDecimal amount) throws BusinessException {
        //参数校验
        if (certificateId == null || customerId == null || amount == null) {
            throw new BusinessException(PARAM_ERROR_CODE, PARAM_ERROR_MSG);
        }
        CertificateOfDepositInfo certificateOfDepositInfo = null;

        certificateOfDepositInfo = certificateOfDepositInfoMapper.getToLock(certificateId);

        if(certificateOfDepositInfo == null){
            throw new BusinessException(PARAM_ERROR_CODE, PARAM_ERROR_MSG);
        }
        //判断产品是否已经售罄
        if (certificateOfDepositInfo.getResidualAmount().compareTo(amount) < 0) {
            throw new BusinessException(SOLD_OUT_CODE, SOLD_OUT_MSG);
        }
        //判断购买量是否处于每人允许购买区间内
        Map<String, Object> params = new HashMap<>();
        params.put("certificateId", certificateId);
        params.put("customerNo", customerId);
        BigDecimal custTotalAmount = certificateOfDepositOrderMapper.getCustCertificateOfDepositOrderAmount(params);
        if (custTotalAmount == null) {
            custTotalAmount = new BigDecimal(0);
        }
        if (custTotalAmount.add(amount).compareTo(certificateOfDepositInfo.getMaxAmountPer()) > 0 || certificateOfDepositInfo.getMinAmount().compareTo(amount) > 0) {
            throw new BusinessException(AMOUNT_OUT_OF_RANGE_CODE, AMOUNT_OUT_OF_RANGE_MSG);
        }
        //判断用户余额是否足够
        QueryWrapper<CustBalance> custBalanceQueryWrapper = new QueryWrapper<>();
        custBalanceQueryWrapper.eq("CUST_NO", customerId);
        custBalanceQueryWrapper.eq("CURRENCY", certificateOfDepositInfo.getCurrency());
        CustBalance custBalance = iCustBalanceService.getOne(custBalanceQueryWrapper);
        if (custBalance == null || custBalance.getBalance().compareTo(amount) < 0) {
            throw new BusinessException(BALANCE_NOT_ENOUGH_CODE, BALANCE_NOT_ENOUGH_MSG);
        }
        //判断当前日期是否在产品的购买日期范围内
        LocalDateTime now = LocalDateTime.now();
        if (now.isBefore(certificateOfDepositInfo.getStartDate()) || now.isAfter(certificateOfDepositInfo.getEndDate())) {
            throw new BusinessException(NOT_IN_DATE_RANGE_CODE, NOT_IN_DATE_RANGE_MSG);
        }
        //检查通过,开始购买
        certificateOfDepositInfo.setResidualAmount(certificateOfDepositInfo.getResidualAmount().subtract(amount));
        iCertificateOfDepositInfoService.updateById(certificateOfDepositInfo);
        //更新用户余额
        custBalance.setBalance(custBalance.getBalance().subtract(amount));
        iCustBalanceService.updateByMultiId(custBalance);
        //生成交易订单
        CertificateOfDepositOrder certificateOfDepositOrder = new CertificateOfDepositOrder();
        certificateOfDepositOrder.setOrderNo(certificateOfDepositOrderMapper.getSEQ());
        certificateOfDepositOrder.setAmount(amount);
        certificateOfDepositOrder.setCertificatesOfDepositId(certificateId);
        certificateOfDepositOrder.setCustomerNo(customerId);
        iCertificateOfDepositOrderService.save(certificateOfDepositOrder);
        return true;
    }

    @Override
    public ResultVO buyProductByProc(String certificateId, String customerId, BigDecimal amount) {
        Map<String, Object> params = new HashMap<>();
        params.put("I_Product_Id", certificateId);
        params.put("I_Cust_No", customerId);
        params.put("I_Buy_Amount", amount);
        params.put("O_Cwdm", "");
        params.put("O_Cwxx", "");
        certificateOfDepositOrderMapper.buyProductByProc(params);
        return new ResultVO(params.get("O_Cwdm").toString(), params.get("O_Cwxx").toString());
    }

    


}
