package com.bos.model.entity;

import jakarta.persistence.*;
import lombok.*;

import java.math.BigDecimal;
import java.time.LocalDateTime;

@Getter
@Setter
@Entity
@Builder
@AllArgsConstructor
@NoArgsConstructor
@Table(name = "BOS_History")
public class BosHistory {

    @Id
    @Column(name = "szTransactionId")
    private String szTransactionId;

    @Column(name = "szAccountId")
    private String szAccountId;

    @Column(name = "szCurrencyId")
    private String szCurrencyId;

    @Column(name = "dtmTransaction")
    private LocalDateTime dtmTransaction;

    @Column(name = "decAmount")
    private BigDecimal decAmount;

    @Column(name = "szNote")
    private String szNote;
}
