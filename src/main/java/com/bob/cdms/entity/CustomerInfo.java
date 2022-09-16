package com.bob.cdms.entity;

import com.baomidou.mybatisplus.annotation.TableField;
import com.baomidou.mybatisplus.annotation.TableName;
import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableId;
import java.io.Serializable;
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
@TableName("CUST_INFO")
public class CustomerInfo implements Serializable {

    private static final long serialVersionUID = 1L;

    /**
     * 用户ID
     */
    @TableId(value = "CUST_NO", type = IdType.NONE)
    private String customerNo;

    /**
     * 用户名称
     */
    @TableField("CUST_NAME")
    private String customerName;

    /**
     * 密码
     */
    @TableField("CUST_PASSWORD")
    private String customerPassword;

    /**
     * 用户性别
     */
    @TableField("CUST_SEX")
    private String customerSex;

    /**
     * 电话
     */
    @TableField("PHONE_NUMBER")
    private String phoneNumber;

    /**
     * 状态
     */
    @TableField("STATUS")
    private String status;


}
