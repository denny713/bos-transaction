package com.bos.repository;

import com.bos.model.entity.BosHistory;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.util.List;

@Repository
public interface BosHistoryRepository extends JpaRepository<BosHistory, String> {

    @Modifying
    @Transactional
    @Query(value = "INSERT INTO BOS_History (szTransactionId, szAccountId, szCurrencyId, dtmTransaction, decAmount, szNote) " +
            "VALUES (:transactionId, :accountId, :currencyId, GETDATE(), :amount, :note)", nativeQuery = true)
    void insertHistory(String transactionId, String accountId, String currencyId, BigDecimal amount, String note);

    @Query(value = """
            SELECT szTransactionId, szAccountId, szCurrencyId, dtmTransaction, decAmount, szNote
            FROM BOS_History WHERE szAccountId LIKE :szAccountId
            AND (
                (:startDate IS NULL AND :endDate IS NULL) OR
                (:startDate IS NULL AND :endDate IS NOT NULL AND dtmTransaction <= :endDate) OR
                (:startDate IS NOT NULL AND :endDate IS NULL AND dtmTransaction >= :startDate) OR
                (:startDate IS NOT NULL AND :endDate IS NOT NULL AND dtmTransaction BETWEEN :startDate AND :endDate)
            )
            GROUP BY szTransactionId, szAccountId, szCurrencyId, dtmTransaction, decAmount, szNote
            """, nativeQuery = true)
    List<BosHistory> searchHistory(
            @Param("szAccountId") String szAccountId,
            @Param("startDate") String startDate,
            @Param("endDate") String endDate
    );
}
