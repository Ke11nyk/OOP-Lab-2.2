package com.lowcost.controller;

import com.lowcost.dto.FlightDTO;
import com.lowcost.entity.Flight;
import com.lowcost.mapper.FlightMapper;
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
    private final FlightMapper flightMapper;

    @Autowired
    public FlightController(FlightService flightService, FlightMapper flightMapper) {
        this.flightService = flightService;
        this.flightMapper = flightMapper;
    }

    @GetMapping
    public ResponseEntity<List<FlightDTO>> getAllFlights() {
        List<Flight> flights = flightService.getAllFlights();
        return ResponseEntity.ok(flightMapper.toDTOList(flights));
    }

    @GetMapping("/{id}")
    public ResponseEntity<FlightDTO> getFlightById(@PathVariable int id) {
        return flightService.getFlightById(id)
                .map(flightMapper::toDTO)
                .map(ResponseEntity::ok)
                .orElse(ResponseEntity.notFound().build());
    }

    @GetMapping("/search")
    public ResponseEntity<List<FlightDTO>> searchFlights(
            @RequestParam String departure,
            @RequestParam String arrival,
            @RequestParam @DateTimeFormat(iso = DateTimeFormat.ISO.DATE_TIME) LocalDateTime startDate,
            @RequestParam @DateTimeFormat(iso = DateTimeFormat.ISO.DATE_TIME) LocalDateTime endDate) {
        List<Flight> flights = flightService.searchFlights(departure, arrival, startDate, endDate);
        return ResponseEntity.ok(flightMapper.toDTOList(flights));
    }

    @PostMapping
    public ResponseEntity<FlightDTO> createFlight(@RequestBody FlightDTO flightDTO) {
        Flight flight = flightMapper.toEntity(flightDTO);
        Flight savedFlight = flightService.saveFlight(flight);
        return ResponseEntity.status(HttpStatus.CREATED).body(flightMapper.toDTO(savedFlight));
    }

    @PutMapping("/{id}")
    public ResponseEntity<FlightDTO> updateFlight(@PathVariable int id, @RequestBody FlightDTO flightDTO) {
        return flightService.getFlightById(id)
                .map(existingFlight -> {
                    flightMapper.updateEntityFromDTO(flightDTO, existingFlight);
                    Flight updatedFlight = flightService.saveFlight(existingFlight);
                    return ResponseEntity.ok(flightMapper.toDTO(updatedFlight));
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

    @PostMapping("/adjust-prices-by-departure")
    public ResponseEntity<Void> adjustPricesByDeparture() {
        flightService.checkAndAdjustPriceBasedOnDeparture();
        return ResponseEntity.noContent().build();
    }

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