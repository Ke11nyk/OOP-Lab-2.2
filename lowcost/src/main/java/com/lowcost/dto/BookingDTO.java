package com.lowcost.dto;

import com.lowcost.entity.Booking;
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
public class BookingDTO {
    private int id;
    private int userId;
    private int flightId;
    private String bookingReference;
    private LocalDateTime bookingDate;
    private BigDecimal totalPrice;
    private String status;
    private boolean priorityBoarding;
    private boolean checkedBaggage;
    private int baggageCount;

    // Додаткові поля для відображення більш детальної інформації
    private String flightNumber;
    private String departureAirport;
    private String arrivalAirport;
    private LocalDateTime departureTime;
    private LocalDateTime arrivalTime;
    private String userFullName;
}
