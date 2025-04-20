package com.lowcost.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.math.BigDecimal;
import java.time.LocalDateTime;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class BookingWithFlightDTO {
    private int id;
    private String bookingReference;
    private LocalDateTime bookingDate;
    private BigDecimal totalPrice;
    private String status;
    private boolean priorityBoarding;
    private boolean checkedBaggage;
    private int baggageCount;
    private FlightDTO flight; // Повний об'єкт рейсу
    private int userId;
}