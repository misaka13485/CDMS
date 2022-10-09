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
 * 余额明细
 * </p>
 *
 * @author ZengXuan
 * @since 2022-09-18
 */
@Data
@EqualsAndHashCode(callSuper = false)
@Accessors(chain = true)
@TableName("CUST_BALANCE")
public class CustBalance implements Serializable {

    private static final long serialVersionUID = 1L;

    /**
     * 用户ID
     */
    @TableId(value = "CUST_NO", type = IdType.NONE)
    private String custNo;

    /**
     * 币种
     */
    @TableField("CURRENCY")
    private String currency;

    /**
     * 可用余额
     */
    @TableField("BALANCE")
    private BigDecimal balance;

    @TableField("BALANCE_AVL")
    private BigDecimal balanceAvailble;

    /**
     * 上次修改时间
     */
    @TableField("LAST_CHANGE_DATE")
    private LocalDateTime lastChangeDate;

    /**
     * 昨日余额
     */
    @TableField("YD_BALANCE")
    private BigDecimal ydBalance;


}
