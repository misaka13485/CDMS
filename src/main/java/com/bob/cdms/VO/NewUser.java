package com.bob.cdms.VO;

import com.baomidou.mybatisplus.annotation.TableField;
import lombok.Data;

/**
 * @author zengxuan
 */
@Data
public class NewUser {


    /**
     * 用户名称
     */
    private String userName;

    /**
     * 密码
     */
    private String password;

    /**
     * 用户性别
     */
    private String sex;

    /**
     * 电话
     */
    private String phoneNumber;
}
