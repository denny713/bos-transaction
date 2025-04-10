package com.bos.model.dto;

import lombok.Getter;
import lombok.Setter;

import java.math.BigDecimal;

@Getter
@Setter
public class TransReqDto {

    private String accountId;
    private String accountDest;
    private String currencyId;
    private BigDecimal amount;
}
