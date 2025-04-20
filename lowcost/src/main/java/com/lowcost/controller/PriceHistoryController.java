package com.lowcost.controller;

import com.lowcost.dto.PriceHistoryDTO;
import com.lowcost.entity.PriceHistory;
import com.lowcost.mapper.PriceHistoryMapper;
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
    private final PriceHistoryMapper priceHistoryMapper;

    @Autowired
    public PriceHistoryController(PriceHistoryService priceHistoryService, PriceHistoryMapper priceHistoryMapper) {
        this.priceHistoryService = priceHistoryService;
        this.priceHistoryMapper = priceHistoryMapper;
    }

    @GetMapping
    public ResponseEntity<List<PriceHistoryDTO>> getAllPriceHistories() {
        List<PriceHistory> priceHistories = priceHistoryService.getAllPriceHistories();
        return ResponseEntity.ok(priceHistoryMapper.toDTOList(priceHistories));
    }

    @GetMapping("/{id}")
    public ResponseEntity<PriceHistoryDTO> getPriceHistoryById(@PathVariable int id) {
        return priceHistoryService.getPriceHistoryById(id)
                .map(priceHistoryMapper::toDTO)
                .map(ResponseEntity::ok)
                .orElse(ResponseEntity.notFound().build());
    }

    @GetMapping("/flight/{flightId}")
    public ResponseEntity<List<PriceHistoryDTO>> getPriceHistoryByFlightId(@PathVariable int flightId) {
        List<PriceHistory> priceHistories = priceHistoryService.getPriceHistoryByFlightId(flightId);
        return ResponseEntity.ok(priceHistoryMapper.toDTOList(priceHistories));
    }

    @GetMapping("/reason/{reason}")
    public ResponseEntity<List<PriceHistoryDTO>> getPriceHistoryByReason(
            @PathVariable PriceHistory.PriceChangeReason reason) {
        List<PriceHistory> priceHistories = priceHistoryService.getPriceHistoryByReason(reason);
        return ResponseEntity.ok(priceHistoryMapper.toDTOList(priceHistories));
    }

    @GetMapping("/date-range")
    public ResponseEntity<List<PriceHistoryDTO>> getPriceHistoryByDateRange(
            @RequestParam @DateTimeFormat(iso = DateTimeFormat.ISO.DATE_TIME) LocalDateTime start,
            @RequestParam @DateTimeFormat(iso = DateTimeFormat.ISO.DATE_TIME) LocalDateTime end) {
        List<PriceHistory> priceHistories = priceHistoryService.getPriceHistoryByDateRange(start, end);
        return ResponseEntity.ok(priceHistoryMapper.toDTOList(priceHistories));
    }

    @GetMapping("/flight/{flightId}/latest")
    public ResponseEntity<PriceHistoryDTO> getLatestPriceChangeByFlight(@PathVariable int flightId) {
        PriceHistory latestChange = priceHistoryService.getLatestPriceChangeByFlight(flightId);
        if (latestChange != null) {
            return ResponseEntity.ok(priceHistoryMapper.toDTO(latestChange));
        } else {
            return ResponseEntity.notFound().build();
        }
    }

    @GetMapping("/flight/{flightId}/demand-increases")
    public ResponseEntity<Integer> countDemandIncreasesForFlight(@PathVariable int flightId) {
        return ResponseEntity.ok(priceHistoryService.countDemandIncreasesForFlight(flightId));
    }

    @GetMapping("/recent-increases")
    public ResponseEntity<List<PriceHistoryDTO>> getRecentPriceIncreases(
            @RequestParam @DateTimeFormat(iso = DateTimeFormat.ISO.DATE_TIME) LocalDateTime since) {
        List<PriceHistory> priceHistories = priceHistoryService.getRecentPriceIncreases(since);
        return ResponseEntity.ok(priceHistoryMapper.toDTOList(priceHistories));
    }

    @PutMapping("/{id}")
    public ResponseEntity<PriceHistoryDTO> updatePriceHistory(@PathVariable int id, @RequestBody PriceHistoryDTO priceHistoryDTO) {
        return priceHistoryService.getPriceHistoryById(id)
                .map(existingPriceHistory -> {
                    priceHistoryMapper.updateEntityFromDTO(priceHistoryDTO, existingPriceHistory);
                    PriceHistory updatedPriceHistory = priceHistoryService.updatePriceHistory(existingPriceHistory);
                    return ResponseEntity.ok(priceHistoryMapper.toDTO(updatedPriceHistory));
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
    public ResponseEntity<List<PriceHistoryDTO>> getPriceHistoryByFlightIdAndReason(
            @PathVariable int flightId,
            @PathVariable PriceHistory.PriceChangeReason reason) {
        List<PriceHistory> priceHistories = priceHistoryService.getPriceHistoryByFlightId(flightId)
                .stream()
                .filter(ph -> ph.getReason() == reason)
                .toList();
        return ResponseEntity.ok(priceHistoryMapper.toDTOList(priceHistories));
    }
}