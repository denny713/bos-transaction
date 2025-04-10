package com.bos.model.entity;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.Id;
import jakarta.persistence.Table;
import lombok.*;

import java.math.BigDecimal;

@Getter
@Setter
@Entity
@Builder
@AllArgsConstructor
@NoArgsConstructor
@Table(name = "BOS_Balance")
public class BosBalance {

    @Id
    @Column(name = "szAccountId")
    private String szAccountId;

    @Column(name = "szCurrencyId")
    private String szCurrencyId;

    @Column(name = "decAmount")
    private BigDecimal decAmount;
}
