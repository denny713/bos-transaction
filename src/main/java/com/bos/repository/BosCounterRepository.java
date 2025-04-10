package com.bos.repository;

import com.bos.model.entity.BosCounter;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;
import org.springframework.transaction.annotation.Transactional;

@Repository
public interface BosCounterRepository extends JpaRepository<BosCounter, String> {

    @Query(value = "SELECT iLastNumber FROM BOS_Counter WHERE szCounterId = '001-COU'", nativeQuery = true)
    Long getLastCounterNumber();

    @Modifying
    @Transactional
    @Query(value = "UPDATE BOS_Counter SET iLastNumber = :newCounter WHERE szCounterId = '001-COU'", nativeQuery = true)
    int updateLastCounter(Long newCounter);
}
