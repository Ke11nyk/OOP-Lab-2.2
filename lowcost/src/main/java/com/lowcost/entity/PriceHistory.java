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
@Table(name="price_adjustments")
public class PriceHistory {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "id")
    private int id;
    @Column(name = "flight_id")
    private int flightId;
    @Column(name = "old_price")
    private BigDecimal oldPrice;
    @Column(name = "new_price")
    private BigDecimal newPrice;
    @Column(name = "applied_at")
    private LocalDateTime changeTime;
    @Column(name = "reason")
    @Enumerated(EnumType.STRING)  // Зберігатиме значення як текст
    private PriceChangeReason reason;

    public enum PriceChangeReason {
        DEMAND_INCREASE,
        LAST_MINUTE,
        SEATS_LEFT,
        MANUAL_ADJUSTMENT
    }
}
