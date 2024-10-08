package com.crud.demo.repository;

import com.crud.demo.model.StatisticsFinal;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import java.time.LocalDateTime;
import java.util.List;

@Repository
public interface StatisticsFinalRepository extends JpaRepository<StatisticsFinal, Long> {
    
    List<StatisticsFinal> findAllByUserId(Long userId);
    List<StatisticsFinal> findAllByUserIdAndTimestampBetween(Long userId, LocalDateTime fromDate, LocalDateTime toDate);

    StatisticsFinal findTopByUserIdOrderByIdDesc(Long userId);
}
