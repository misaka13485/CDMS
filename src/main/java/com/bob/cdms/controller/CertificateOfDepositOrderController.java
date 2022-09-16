package com.bob.cdms.controller;


import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.bob.cdms.entity.CertificateOfDepositOrder;
import com.bob.cdms.service.ICertificateOfDepositOrderService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

/**
 * <p>
 * 存单销售流水表 前端控制器
 * </p>
 *
 * @author zengxuan
 * @since 2022-09-10
 */
@RestController
@RequestMapping("/certificate-of-deposit-order")
public class CertificateOfDepositOrderController {
    @Autowired
    private ICertificateOfDepositOrderService iCertificateOfDepositOrderService;

    /**
     * 查询存单销售流水,如无参数则查询所有
     *
     * @return 存单销售流水
     */
    @RequestMapping("/list")
    public List<CertificateOfDepositOrder> list(@RequestParam(required = false) String customerId) {
        if (customerId == null) {
            return iCertificateOfDepositOrderService.list();
        } else {
            QueryWrapper<CertificateOfDepositOrder> queryWrapper = new QueryWrapper<>();
            queryWrapper.eq("CUST_NO", customerId);
            return iCertificateOfDepositOrderService.list(queryWrapper);
        }
    }

    /**
     * 根据订单号查询存单销售流水
     */
    @RequestMapping("/get")
    public CertificateOfDepositOrder get(@RequestParam String orderId) {
        return iCertificateOfDepositOrderService.getById(orderId);
    }
}
