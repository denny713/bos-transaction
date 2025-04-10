package com.bos.repository;

import com.bos.model.entity.BosBalance;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;

@Repository
public interface BosBalanceRepository extends JpaRepository<BosBalance, String> {

    @Query(value = "SELECT szAccountId, szCurrencyId, decAmount from BOS_Balance where szAccountId = :accountId AND szCurrencyId = :currencyId", nativeQuery = true)
    BosBalance getBalance(String accountId, String currencyId);

    @Query(value = "SELECT COALESCE((SELECT decAmount FROM BOS_Balance WHERE szAccountId = :accountId AND szCurrencyId = :currencyId), 0);", nativeQuery = true)
    BigDecimal getBalanceAmount(String accountId, String currencyId);

    @Modifying
    @Transactional
    @Query(value = "INSERT INTO BOS_Balance (szAccountId, szCurrencyId, decAmount) VALUES (:accountId, :currencyId, :amount)", nativeQuery = true)
    int insertBalance(String accountId, String currencyId, BigDecimal amount);

    @Modifying
    @Transactional
    @Query(value = "UPDATE BOS_Balance SET decAmount = :amount WHERE szAccountId = :accountId AND szCurrencyId = :currencyId", nativeQuery = true)
    int updateBalance(String accountId, String currencyId, BigDecimal amount);
}
