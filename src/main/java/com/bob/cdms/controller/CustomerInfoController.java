package com.bob.cdms.controller;


import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.bob.cdms.entity.CustomerInfo;
import com.bob.cdms.service.ICustomerInfoService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

/**
 * <p>
 * 前端控制器
 * </p>
 *
 * @author zengxuan
 * @since 2022-09-10
 */
@RestController
@RequestMapping("/cust-info")
@Slf4j
public class CustomerInfoController {
    @Autowired
    private ICustomerInfoService iCustomerInfoService;

    /**
     * 查询所有客户信息
     */
    @RequestMapping("/list")
    public List<CustomerInfo> list() {
        return iCustomerInfoService.list();
    }

    /**
     * 查询客户信息
     */
    @RequestMapping("/get")
    public CustomerInfo get(@RequestParam(required = false) String customerId) {
        return iCustomerInfoService.getById(customerId);
    }

    /**
     * 根据名字查询客户信息
     */
    @RequestMapping("/getByName")
    public List<CustomerInfo> getByName(@RequestParam String customerName) {
        QueryWrapper<CustomerInfo> queryWrapper = new QueryWrapper<>();
        queryWrapper.eq("customer_name", customerName);
        return iCustomerInfoService.list(queryWrapper);
    }

    /**
     * 更新客户信息
     */
    @RequestMapping("/update")
    public boolean update(@RequestBody CustomerInfo customerInfo) {
        return iCustomerInfoService.updateById(customerInfo);
    }

    /**
     * 新增客户信息
     */
    @RequestMapping("/add")
    public boolean add(@RequestBody CustomerInfo customerInfo) {
        try {
            return iCustomerInfoService.save(customerInfo);
        } catch (Exception e) {
            log.warn("新增失败，原因：{}", e.getMessage());
            return false;
        }
    }

    /**
     * 删除客户信息
     */
    @RequestMapping("/delete")
    public boolean delete(@RequestParam String customerId) {
        return iCustomerInfoService.removeById(customerId);
    }


}
