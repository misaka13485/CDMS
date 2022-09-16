package com.bob.cdms.entity;

import java.math.BigDecimal;



import java.time.LocalDateTime;
import java.io.Serializable;

import com.baomidou.mybatisplus.annotation.FieldFill;
import com.baomidou.mybatisplus.annotation.TableField;
import com.baomidou.mybatisplus.annotation.TableName;
import com.github.jeffreyning.mybatisplus.anno.InsertFill;
import com.github.jeffreyning.mybatisplus.anno.MppMultiId;
import com.github.jeffreyning.mybatisplus.anno.UpdateFill;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.experimental.Accessors;
import org.apache.ibatis.type.JdbcType;

/**
 * <p>
 * 余额明细
 * </p>
 *
 * @author zengxuan
 * @since 2022-09-10
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
    @TableField("CUST_NO")
    @MppMultiId
    private String customerNo;

    /**
     * 可用余额
     */
    @TableField("BALANCE")
    private BigDecimal balance;

    /**
     * 币种
     */
    @TableField("CURRENCY")
    @MppMultiId
    private String currency;

    /**
     * 上次修改时间
     */

    @TableField(value = "LAST_CHANGE_DATE",jdbcType = JdbcType.DATE)
    private LocalDateTime lastChangeDate;

}
