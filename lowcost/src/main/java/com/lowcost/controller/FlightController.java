package com.lowcost.controller;

import com.lowcost.entity.Flight;
import com.lowcost.service.FlightService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.List;

@RestController
@RequestMapping("/api/flights")
public class FlightController {

    private final FlightService flightService;

    @Autowired
    public FlightController(FlightService flightService) {
        this.flightService = flightService;
    }

    @GetMapping
    public ResponseEntity<List<Flight>> getAllFlights() {
        return ResponseEntity.ok(flightService.getAllFlights());
    }

    @GetMapping("/{id}")
    public ResponseEntity<Flight> getFlightById(@PathVariable int id) {
        return flightService.getFlightById(id)
                .map(ResponseEntity::ok)
                .orElse(ResponseEntity.notFound().build());
    }

    @GetMapping("/search")
    public ResponseEntity<List<Flight>> searchFlights(
            @RequestParam String departure,
            @RequestParam String arrival,
            @RequestParam @DateTimeFormat(iso = DateTimeFormat.ISO.DATE_TIME) LocalDateTime startDate,
            @RequestParam @DateTimeFormat(iso = DateTimeFormat.ISO.DATE_TIME) LocalDateTime endDate) {
        return ResponseEntity.ok(flightService.searchFlights(departure, arrival, startDate, endDate));
    }

    @PostMapping
    public ResponseEntity<Flight> createFlight(@RequestBody Flight flight) {
        return ResponseEntity.status(HttpStatus.CREATED).body(flightService.saveFlight(flight));
    }

    @PutMapping("/{id}")
    public ResponseEntity<Flight> updateFlight(@PathVariable int id, @RequestBody Flight flight) {
        return flightService.getFlightById(id)
                .map(existingFlight -> {
                    flight.setId(id);
                    return ResponseEntity.ok(flightService.saveFlight(flight));
                })
                .orElse(ResponseEntity.notFound().build());
    }

    @PatchMapping("/{id}/price")
    public ResponseEntity<Void> adjustPrice(@PathVariable int id, @RequestParam BigDecimal newPrice) {
        return flightService.getFlightById(id)
                .map(flight -> {
                    flightService.manualPriceAdjustment(id, newPrice);
                    return ResponseEntity.noContent().<Void>build();
                })
                .orElse(ResponseEntity.notFound().build());
    }

    @PatchMapping("/{id}/status")
    public ResponseEntity<Void> updateStatus(@PathVariable int id, @RequestParam boolean active) {
        return flightService.getFlightById(id)
                .map(flight -> {
                    flightService.updateFlightStatus(id, active);
                    return ResponseEntity.noContent().<Void>build();
                })
                .orElse(ResponseEntity.notFound().build());
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Void> deleteFlight(@PathVariable int id) {
        return flightService.getFlightById(id)
                .map(flight -> {
                    flightService.deleteFlight(id);
                    return ResponseEntity.noContent().<Void>build();
                })
                .orElse(ResponseEntity.notFound().build());
    }

    // Admin endpoint to run price adjustment based on approaching departure dates
    @PostMapping("/adjust-prices-by-departure")
    public ResponseEntity<Void> adjustPricesByDeparture() {
        flightService.checkAndAdjustPriceBasedOnDeparture();
        return ResponseEntity.noContent().build();
    }

    // Admin endpoint to adjust price based on demand
    @PatchMapping("/{id}/adjust-price-by-demand")
    public ResponseEntity<Void> adjustPriceByDemand(@PathVariable int id, @RequestParam BigDecimal newPrice) {
        return flightService.getFlightById(id)
                .map(flight -> {
                    flightService.adjustPriceBasedOnDemand(id, newPrice);
                    return ResponseEntity.noContent().<Void>build();
                })
                .orElse(ResponseEntity.notFound().build());
    }
}