package com.bob.cdms.controller;

import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.bob.cdms.VO.BuyDepositVO;
import com.bob.cdms.VO.NewUser;
import com.bob.cdms.VO.ResultVO;
import com.bob.cdms.entity.CertificateOfDepositInfo;
import com.bob.cdms.exception.BusinessException;
import com.bob.cdms.service.ICertificateOfDepositInfoService;
import com.bob.cdms.service.ICertificateOfDepositOrderService;
import com.bob.cdms.service.ICustBalanceService;
import com.bob.cdms.service.ICustomerInfoService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.cache.annotation.CachePut;
import org.springframework.cache.annotation.Cacheable;
import org.springframework.web.bind.annotation.*;

import java.math.BigDecimal;
import java.sql.SQLException;

import static com.bob.cdms.constant.ErrorCode.*;

/**
 * <p>
 * 用户访问 前端控制器
 * </p>
 *
 * @author zengxuan
 * @since 2022-09-10
 */
@RestController
@RequestMapping("/user")
@Slf4j
public class UserController {
    @Autowired
    private ICustomerInfoService customerInfoService;

    @Autowired
    private ICertificateOfDepositOrderService iCertificateOfDepositOrderService;
    @Autowired
    private ICustBalanceService custBalanceService;

    @Autowired
    private ICertificateOfDepositInfoService certificateOfDepositInfoService;

    /**
     * 用户注册
     *
     * @param newUser
     * @return ResultVO
     * @throws BusinessException
     */
    @RequestMapping("/register")
    public ResultVO register(@RequestBody NewUser newUser) {
        try {
            if (customerInfoService.register(newUser)) {
                return new ResultVO(SUCCESS_CODE, SUCCESS_MSG);
            } else {
                return new ResultVO(FAIL_CODE, FAIL_MSG);
            }
        } catch (BusinessException e) {
            return new ResultVO(e.getCode(), e.getMessage());
        } catch (Exception e) {
            return new ResultVO(SEVER_ERROR_CODE, SEVER_ERROR_MSG);
        }

    }

    /**
     * 用户登录
     */
    @RequestMapping("/login")
    public ResultVO login(@RequestParam String userName, @RequestParam String password) {
        try {
            return new ResultVO(customerInfoService.login(userName, password));
        } catch (BusinessException e) {
            return new ResultVO(e.getCode(), e.getMsg());
        } catch (Exception e) {
            return new ResultVO(SEVER_ERROR_CODE, SEVER_ERROR_MSG);
        }
    }
    /**
     * 查看用户存款
     *
     */
    @RequestMapping("/balance")
    public ResultVO getBalance(@RequestHeader String token){
        //登录检查
        String userId = "";
        if (token != null || !token.equals("")) {
            try {
                userId = customerInfoService.checkLogin(token);
                log.info("用户id为：{}", userId);
            } catch (BusinessException e) {
                return new ResultVO(NO_LOGIN_CODE, NO_LOGIN_MSG);
            }
        }else{
            return new ResultVO(NO_LOGIN_CODE, NO_LOGIN_MSG);
        }
        try {
            return new ResultVO(custBalanceService.getBalance(userId));
        } catch (BusinessException e) {
            return new ResultVO(e.getCode(), e.getMsg());
        } catch (Exception e) {
            return new ResultVO(SEVER_ERROR_CODE, SEVER_ERROR_MSG);
        }
    }

    /**
     * 存取款,负数为取款,正数为存款
     */
    @RequestMapping("/saveOrWithdraw")
    public ResultVO saveOrWithdraw(@RequestParam BigDecimal money, @RequestParam String type,
                                   @RequestHeader String token) {
        //登录检查
        String userId = "";
        if (token != null || token.equals("")) {
            try {
                userId = customerInfoService.checkLogin(token);
            } catch (BusinessException e) {
                return new ResultVO(NO_LOGIN_CODE, NO_LOGIN_MSG);
            }
        }else{
            return new ResultVO(NO_LOGIN_CODE, NO_LOGIN_MSG);
        }
        try {
            if (custBalanceService.changeBalance(userId, money, type)) {
                return new ResultVO(SUCCESS_CODE, SUCCESS_MSG);
            } else {
                return new ResultVO(FAIL_CODE, FAIL_MSG);
            }
        } catch (BusinessException e) {
            return new ResultVO(e.getCode(), e.getMsg());
        } catch (Exception e) {
            return new ResultVO(SEVER_ERROR_CODE, SEVER_ERROR_MSG);
        }

    }

    /**
     * 购买存单产品
     */
    @RequestMapping("/buyDeposit")
    public ResultVO buyDeposit(@RequestBody BuyDepositVO buyDepositVO,
                               @RequestHeader String token) {
        //登录检查
        String userId = "";
        if (token != null || !"".equals(token)) {
            try {
                userId = customerInfoService.checkLogin(token);
            } catch (BusinessException e) {
                return new ResultVO(NO_LOGIN_CODE, NO_LOGIN_MSG);
            }
        }else {
            return new ResultVO(NO_LOGIN_CODE, NO_LOGIN_MSG);
        }
        try {
            boolean result = false;
            //循环执行购买操作
            for(int i=0;i<5;i++){
                try {
                    result = iCertificateOfDepositOrderService.buyProduct( buyDepositVO.getCertificateOfDepositId(), userId,buyDepositVO.getAmount());
                } catch (BusinessException e) {
                    throw new BusinessException(e.getCode(),e.getMsg());
                } catch (Exception e) {
                    log.warn("购买存单失败,重试中,第{}次",i+1);
                    //加入随机等待时间
                    Thread.sleep((long) (Math.random()*100));
                    continue;
                }
                break;
            }
            if (result) {
                return new ResultVO(SUCCESS_CODE, SUCCESS_MSG);
            } else {
                return new ResultVO(FAIL_CODE, FAIL_MSG);
            }
        } catch (BusinessException e) {
            return new ResultVO(e.getCode(), e.getMsg());
        } catch (Exception e) {
            log.error("购买存单失败",e);
            return new ResultVO(SEVER_ERROR_CODE, SEVER_ERROR_MSG);
        }
    }
    /**
     * 查询存单产品
     */
    @RequestMapping("/query")

    public ResultVO query(@RequestParam(required = false) String name,
                          @RequestHeader String token) {
        //登录检查
        String userId = "";
        if (token != null || !"".equals(token)) {
            try {
                userId = customerInfoService.checkLogin(token);
            } catch (BusinessException e) {
                return new ResultVO(NO_LOGIN_CODE, NO_LOGIN_MSG);
            }
        }else {
            return new ResultVO(NO_LOGIN_CODE, NO_LOGIN_MSG);
        }
        try {
                return new ResultVO(certificateOfDepositInfoService.selectAll(name));
        } catch (BusinessException e) {
            return new ResultVO(e.getCode(), e.getMsg());
        } catch (Exception e) {
            e.printStackTrace();
            return new ResultVO(SEVER_ERROR_CODE, SEVER_ERROR_MSG);
        }
    }
}
