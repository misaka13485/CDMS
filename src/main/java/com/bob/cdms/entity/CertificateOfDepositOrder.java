package com.bob.cdms.entity;

import java.math.BigDecimal;

import com.baomidou.mybatisplus.annotation.TableField;
import com.baomidou.mybatisplus.annotation.TableName;
import com.baomidou.mybatisplus.annotation.IdType;
import java.time.LocalDateTime;
import com.baomidou.mybatisplus.annotation.TableId;
import java.io.Serializable;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.experimental.Accessors;

/**
 * <p>
 * 存单销售流水表
 * </p>
 *
 * @author zengxuan
 * @since 2022-09-12
 */
@Data
@EqualsAndHashCode(callSuper = false)
@Accessors(chain = true)
@TableName("CERTIFICATE_OF_DEPOSIT_ORDER")
public class CertificateOfDepositOrder implements Serializable {

    private static final long serialVersionUID = 1L;

    /**
     * 订单号
     */
    @TableId(value = "ORDER_NO", type = IdType.NONE)
    private String orderNo;

    /**
     * 订单人ID
     */
    @TableField("CUST_NO")
    private String customerNo;

    /**
     * 购买数量
     */
    @TableField("AMOUNT")
    private BigDecimal amount;

    /**
     * 购买日期
     */
    @TableField("ORDER_DATE")
    private LocalDateTime orderDate;

    /**
     * 产品ID
     */
    @TableField("CERTIFICATES_OF_DEPOSIT_ID")
    private String certificatesOfDepositId;


}
