package com.bob.cdms.VO;

import lombok.Data;

import java.math.BigDecimal;

@Data
public class BuyDepositVO {

    private String certificateOfDepositId;

    private BigDecimal amount;
}
