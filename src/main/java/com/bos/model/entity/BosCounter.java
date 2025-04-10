package com.bos.model.entity;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.Id;
import jakarta.persistence.Table;
import lombok.*;

@Getter
@Setter
@Entity
@Builder
@AllArgsConstructor
@NoArgsConstructor
@Table(name = "BOS_Counter")
public class BosCounter {

    @Id
    @Column(name = "szCounterId")
    private String szCounterId;

    @Column(name = "iLastNumber")
    private Integer iLastNumber;
}
