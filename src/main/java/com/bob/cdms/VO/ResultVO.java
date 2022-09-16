package com.bob.cdms.VO;

import lombok.Data;

import static com.bob.cdms.constant.ErrorCode.*;

@Data
public class ResultVO {
    private String code;
    private String msg;
    private Object data;

    public ResultVO(String code, String msg) {
        this.code = code;
        this.msg = msg;
    }

    public ResultVO(Object data) {
        this.code = SUCCESS_CODE;
        this.msg = SUCCESS_MSG;
        this.data = data;
    }
}
