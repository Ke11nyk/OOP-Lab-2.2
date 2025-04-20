package com.lowcost.entity;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.math.BigDecimal;
import java.time.LocalDateTime;


@Data
@NoArgsConstructor
@AllArgsConstructor
@Entity
@Table(name="flights")
public class Flight {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "id")
    private int id;
    @Column(name = "flight_number", nullable = false)
    private String flightNumber;
    @Column(name = "departure_airport")
    private String departureAirport;
    @Column(name = "arrival_airport")
    private String arrivalAirport;
    @Column(name = "departure_time")
    private LocalDateTime departureTime;
    @Column(name = "arrival_time")
    private LocalDateTime arrivalTime;
    @Column(name = "base_price")
    private BigDecimal basePrice;
    @Column(name = "total_seats")
    private int totalSeats;
    @Column(name = "available_seats")
    private int availableSeats;
    @Column(name = "current_price")
    private BigDecimal currentPrice;
    @Column(name = "is_active")
    private boolean isActive;

    public boolean isPriceIncreased() {
        if (currentPrice == null) {
            return false;
        }
        return currentPrice.compareTo(basePrice) > 0;
    }
}
