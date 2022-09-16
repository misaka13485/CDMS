package com.bob.cdms.entity;

import java.math.BigDecimal;

import com.baomidou.mybatisplus.annotation.TableField;
import com.baomidou.mybatisplus.annotation.TableName;
import com.baomidou.mybatisplus.annotation.IdType;

import com.baomidou.mybatisplus.annotation.TableId;
import java.io.Serializable;
import java.time.LocalDateTime;


import com.fasterxml.jackson.annotation.JsonFormat;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.experimental.Accessors;


/**
 * <p>
 * 
 * </p>
 *
 * @author zengxuan
 * @since 2022-09-10
 */
@Data
@EqualsAndHashCode(callSuper = false)
@Accessors(chain = true)
@TableName("CERTIFICATE_OF_DEPOSIT_INFO")
public class CertificateOfDepositInfo implements Serializable {

    private static final long serialVersionUID = 1L;

    /**
     * 大额存单产品ID
     */
    @TableId(value = "CERTIFICATES_OF_DEPOSIT_ID", type = IdType.NONE)
    private String certificatesOfDepositId;

    /**
     * 产品名称
     */
    @TableField("CERTIFICATES_OF_DEPOSIT_NAME")
    private String certificatesOfDepositName;

    /**
     * 币种
     */
    @TableField("CURRENCY")
    private String currency;

    /**
     * 产品可售卖总金额
     */
    @TableField("TOTAL_AMOUNT")
    private BigDecimal totalAmount;

    /**
     * 产品剩余可售卖总金额
     */
    @TableField("RESIDUAL_AMOUNT")
    private BigDecimal residualAmount;

    /**
     * 每人可购买总金额上限
     */
    @TableField("MAX_AMOUNT_PER")
    private BigDecimal maxAmountPer;

    /**
     * 每次购买最低金额
     */
    @TableField("MIN_AMOUNT")
    private BigDecimal minAmount;

    /**
     * 起售日期
     */
    @TableField("START_DATE")
    @JsonFormat(pattern = "yyyy-MM-dd HH:mm:ss", timezone = "GMT+8")
    private LocalDateTime startDate;

    /**
     * 结束购买日期
     */
    @TableField("END_DATE")
    @JsonFormat(pattern = "yyyy-MM-dd HH:mm:ss", timezone = "GMT+8")
    private LocalDateTime endDate;

    /**
     * 年化利率
     */
    @TableField("ANNUAL_RATE")
    private String annualRate;


    public CertificateOfDepositInfo(CertificateOfDepositInfo certificateOfDepositInfo) {
        this.certificatesOfDepositId = certificateOfDepositInfo.getCertificatesOfDepositId();
        this.certificatesOfDepositName = certificateOfDepositInfo.getCertificatesOfDepositName();
        this.currency = certificateOfDepositInfo.getCurrency();
        this.totalAmount = certificateOfDepositInfo.getTotalAmount();
        this.residualAmount = certificateOfDepositInfo.getResidualAmount();
        this.maxAmountPer = certificateOfDepositInfo.getMaxAmountPer();
        this.minAmount = certificateOfDepositInfo.getMinAmount();
        this.startDate = certificateOfDepositInfo.getStartDate();
        this.endDate = certificateOfDepositInfo.getEndDate();
        this.annualRate = certificateOfDepositInfo.getAnnualRate();
    }

    public CertificateOfDepositInfo() {

    }
}
