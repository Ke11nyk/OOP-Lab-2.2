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
@Table(name="bookings")
public class Booking {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "id")
    private int id;
    @Column(name = "user_id")
    private int userId;
    @Column(name = "flight_id")
    private int flightId;
    @Column(name = "booking_reference")
    private String bookingReference;
    @Column(name = "booking_date")
    private LocalDateTime bookingDate;
    @Column(name = "total_price")
    private BigDecimal totalPrice;
    @Column(name = "status")
    private BookingStatus status; // Enum
    @Column(name = "has_priority_boarding")
    private boolean priorityBoarding;
    @Column(name = "has_checked_baggage")
    private boolean checkedBaggage;
    @Column(name = "baggage_count")
    private int baggageCount;

    public enum BookingStatus {
        PENDING, CONFIRMED, PAID, CANCELLED, REFUNDED
    }
}
