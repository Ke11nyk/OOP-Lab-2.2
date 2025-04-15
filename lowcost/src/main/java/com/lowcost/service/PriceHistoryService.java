package com.lowcost.service;

import com.lowcost.entity.PriceHistory;
import com.lowcost.repository.PriceHistoryRepo;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

@Service
public class PriceHistoryService {

    private final PriceHistoryRepo priceHistoryRepository;

    @Autowired
    public PriceHistoryService(PriceHistoryRepo priceHistoryRepository) {
        this.priceHistoryRepository = priceHistoryRepository;
    }

    public List<PriceHistory> getAllPriceHistories() {
        return priceHistoryRepository.findAll();
    }

    public Optional<PriceHistory> getPriceHistoryById(int id) {
        return priceHistoryRepository.findById(id);
    }

    public List<PriceHistory> getPriceHistoryByFlightId(int flightId) {
        return priceHistoryRepository.findByFlightId(flightId);
    }

    public List<PriceHistory> getPriceHistoryByReason(PriceHistory.PriceChangeReason reason) {
        return priceHistoryRepository.findByReason(reason);
    }

    public List<PriceHistory> getPriceHistoryByDateRange(LocalDateTime start, LocalDateTime end) {
        return priceHistoryRepository.findByChangeTimeBetween(start, end);
    }

    public PriceHistory getLatestPriceChangeByFlight(int flightId) {
        return priceHistoryRepository.findLatestPriceChangeByFlight(flightId);
    }

    public int countDemandIncreasesForFlight(int flightId) {
        return priceHistoryRepository.countDemandIncreasesForFlight(flightId);
    }

    public List<PriceHistory> getRecentPriceIncreases(LocalDateTime since) {
        return priceHistoryRepository.findRecentPriceIncreases(since);
    }

    public PriceHistory recordPriceChange(int flightId, BigDecimal oldPrice, BigDecimal newPrice,
                                          PriceHistory.PriceChangeReason reason) {
        PriceHistory priceHistory = new PriceHistory();
        priceHistory.setFlightId(flightId);
        priceHistory.setOldPrice(oldPrice);
        priceHistory.setNewPrice(newPrice);
        priceHistory.setChangeTime(LocalDateTime.now());
        priceHistory.setReason(reason);
        return priceHistoryRepository.save(priceHistory);
    }

    public PriceHistory updatePriceHistory(PriceHistory priceHistory) {
        return priceHistoryRepository.save(priceHistory);
    }

    public void deletePriceHistory(int id) {
        priceHistoryRepository.deleteById(id);
    }
}