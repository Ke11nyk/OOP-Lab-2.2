package com.lowcost.dto;

import com.lowcost.entity.PriceHistory;
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
public class PriceHistoryDTO {
    private int id;
    private int flightId;
    private BigDecimal oldPrice;
    private BigDecimal newPrice;
    private LocalDateTime changeTime;
    private String reason;

    // Додаткові поля для відображення більш детальної інформації
    private String flightNumber;
    private BigDecimal percentageChange;
}
