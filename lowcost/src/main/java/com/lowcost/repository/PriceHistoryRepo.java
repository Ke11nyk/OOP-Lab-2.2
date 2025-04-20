package com.lowcost.repository;

import com.lowcost.entity.PriceHistory;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.time.LocalDateTime;
import java.util.List;

@Repository
public interface PriceHistoryRepo extends JpaRepository<PriceHistory, Integer> {
    List<PriceHistory> findByFlightId(int flightId);

    @Query("SELECT ph FROM PriceHistory ph WHERE ph.reason = :reason")
    List<PriceHistory> findByReason(@Param("reason") PriceHistory.PriceChangeReason reason);

    List<PriceHistory> findByChangeTimeBetween(LocalDateTime start, LocalDateTime end);

    @Query("SELECT ph FROM PriceHistory ph WHERE ph.flightId = :flightId " +
            "ORDER BY ph.changeTime DESC LIMIT 1")
    PriceHistory findLatestPriceChangeByFlight(@Param("flightId") int flightId);

    @Query("SELECT COUNT(ph) FROM PriceHistory ph " +
            "WHERE ph.reason = 'DEMAND_INCREASE' " +  // Зверніть увагу на лапки
            "AND ph.flightId = :flightId")
    int countDemandIncreasesForFlight(@Param("flightId") int flightId);

    List<PriceHistory> findByFlightIdAndReasonOrderByChangeTimeDesc(
            int flightId,
            PriceHistory.PriceChangeReason reason);

    @Query("SELECT ph FROM PriceHistory ph WHERE ph.changeTime > :date " +
            "AND ph.newPrice > ph.oldPrice")
    List<PriceHistory> findRecentPriceIncreases(@Param("date") LocalDateTime date);
}