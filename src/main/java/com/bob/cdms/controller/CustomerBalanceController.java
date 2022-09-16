package com.bob.cdms.controller;


import com.bob.cdms.entity.CustBalance;
import com.bob.cdms.service.ICustBalanceService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import java.time.LocalDateTime;

/**
 * <p>
 * 余额明细 前端控制器
 * </p>
 *
 * @author zengxuan
 * @since 2022-09-10
 */
@RestController
@RequestMapping("/cust-balance")
public class CustomerBalanceController {
    @Autowired
    private ICustBalanceService iCustBalanceService;

    /**
     * 查询余额
     */
    @RequestMapping("/get")
    public CustBalance get(@RequestParam(required = false) String customerId) {
        return iCustBalanceService.getById(customerId);
    }

    /**
     * 更新余额
     */
    @RequestMapping("/update")
    public boolean update(@RequestBody CustBalance custBalance) {
        if (custBalance.getBalance() == null && custBalance.getCurrency() == null) {
            return false;
        }
        custBalance.setLastChangeDate(LocalDateTime.now());
        return iCustBalanceService.saveOrUpdateByMultiId(custBalance);
    }

}
