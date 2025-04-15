package com.lowcost.controller;

import com.lowcost.entity.PriceHistory;
import com.lowcost.service.PriceHistoryService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.time.LocalDateTime;
import java.util.List;

@RestController
@RequestMapping("/api/price-history")
public class PriceHistoryController {

    private final PriceHistoryService priceHistoryService;

    @Autowired
    public PriceHistoryController(PriceHistoryService priceHistoryService) {
        this.priceHistoryService = priceHistoryService;
    }

    @GetMapping
    public ResponseEntity<List<PriceHistory>> getAllPriceHistories() {
        return ResponseEntity.ok(priceHistoryService.getAllPriceHistories());
    }

    @GetMapping("/{id}")
    public ResponseEntity<PriceHistory> getPriceHistoryById(@PathVariable int id) {
        return priceHistoryService.getPriceHistoryById(id)
                .map(ResponseEntity::ok)
                .orElse(ResponseEntity.notFound().build());
    }

    @GetMapping("/flight/{flightId}")
    public ResponseEntity<List<PriceHistory>> getPriceHistoryByFlightId(@PathVariable int flightId) {
        return ResponseEntity.ok(priceHistoryService.getPriceHistoryByFlightId(flightId));
    }

    @GetMapping("/reason/{reason}")
    public ResponseEntity<List<PriceHistory>> getPriceHistoryByReason(
            @PathVariable PriceHistory.PriceChangeReason reason) {
        return ResponseEntity.ok(priceHistoryService.getPriceHistoryByReason(reason));
    }

    @GetMapping("/date-range")
    public ResponseEntity<List<PriceHistory>> getPriceHistoryByDateRange(
            @RequestParam @DateTimeFormat(iso = DateTimeFormat.ISO.DATE_TIME) LocalDateTime start,
            @RequestParam @DateTimeFormat(iso = DateTimeFormat.ISO.DATE_TIME) LocalDateTime end) {
        return ResponseEntity.ok(priceHistoryService.getPriceHistoryByDateRange(start, end));
    }

    @GetMapping("/flight/{flightId}/latest")
    public ResponseEntity<PriceHistory> getLatestPriceChangeByFlight(@PathVariable int flightId) {
        PriceHistory latestChange = priceHistoryService.getLatestPriceChangeByFlight(flightId);
        if (latestChange != null) {
            return ResponseEntity.ok(latestChange);
        } else {
            return ResponseEntity.notFound().build();
        }
    }

    @GetMapping("/flight/{flightId}/demand-increases")
    public ResponseEntity<Integer> countDemandIncreasesForFlight(@PathVariable int flightId) {
        return ResponseEntity.ok(priceHistoryService.countDemandIncreasesForFlight(flightId));
    }

    @GetMapping("/recent-increases")
    public ResponseEntity<List<PriceHistory>> getRecentPriceIncreases(
            @RequestParam @DateTimeFormat(iso = DateTimeFormat.ISO.DATE_TIME) LocalDateTime since) {
        return ResponseEntity.ok(priceHistoryService.getRecentPriceIncreases(since));
    }

    @PutMapping("/{id}")
    public ResponseEntity<PriceHistory> updatePriceHistory(@PathVariable int id, @RequestBody PriceHistory priceHistory) {
        return priceHistoryService.getPriceHistoryById(id)
                .map(existingPriceHistory -> {
                    priceHistory.setId(id);
                    return ResponseEntity.ok(priceHistoryService.updatePriceHistory(priceHistory));
                })
                .orElse(ResponseEntity.notFound().build());
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Void> deletePriceHistory(@PathVariable int id) {
        return priceHistoryService.getPriceHistoryById(id)
                .map(priceHistory -> {
                    priceHistoryService.deletePriceHistory(id);
                    return ResponseEntity.noContent().<Void>build();
                })
                .orElse(ResponseEntity.notFound().build());
    }

    @GetMapping("/flight/{flightId}/by-reason/{reason}")
    public ResponseEntity<List<PriceHistory>> getPriceHistoryByFlightIdAndReason(
            @PathVariable int flightId,
            @PathVariable PriceHistory.PriceChangeReason reason) {
        List<PriceHistory> priceHistories = priceHistoryService.getPriceHistoryByFlightId(flightId)
                .stream()
                .filter(ph -> ph.getReason() == reason)
                .toList();
        return ResponseEntity.ok(priceHistories);
    }
}