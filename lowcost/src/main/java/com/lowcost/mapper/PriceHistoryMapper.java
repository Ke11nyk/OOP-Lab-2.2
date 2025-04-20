package com.lowcost.mapper;

import com.lowcost.dto.PriceHistoryDTO;
import com.lowcost.entity.Flight;
import com.lowcost.entity.PriceHistory;
import com.lowcost.repository.FlightRepo;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

@Component
public class PriceHistoryMapper {

    private final FlightRepo flightRepository;

    @Autowired
    public PriceHistoryMapper(FlightRepo flightRepository) {
        this.flightRepository = flightRepository;
    }

    public PriceHistoryDTO toDTO(PriceHistory priceHistory) {
        if (priceHistory == null) {
            return null;
        }

        PriceHistoryDTO dto = PriceHistoryDTO.builder()
                .id(priceHistory.getId())
                .flightId(priceHistory.getFlightId())
                .oldPrice(priceHistory.getOldPrice())
                .newPrice(priceHistory.getNewPrice())
                .changeTime(priceHistory.getChangeTime())
                .reason(priceHistory.getReason().name())
                .build();

        // Додаємо додаткову інформацію
        Optional<Flight> flightOpt = flightRepository.findById(priceHistory.getFlightId());
        flightOpt.ifPresent(flight -> {
            dto.setFlightNumber(flight.getFlightNumber());
        });

        // Обчислюємо відсоткову зміну ціни
        if (priceHistory.getOldPrice() != null && priceHistory.getNewPrice() != null
                && priceHistory.getOldPrice().compareTo(BigDecimal.ZERO) > 0) {
            BigDecimal percentageChange = priceHistory.getNewPrice()
                    .subtract(priceHistory.getOldPrice())
                    .divide(priceHistory.getOldPrice(), 4, RoundingMode.HALF_UP)
                    .multiply(new BigDecimal("100"))
                    .setScale(2, RoundingMode.HALF_UP);
            dto.setPercentageChange(percentageChange);
        }

        return dto;
    }

    public List<PriceHistoryDTO> toDTOList(List<PriceHistory> priceHistories) {
        if (priceHistories == null) {
            return null;
        }

        return priceHistories.stream()
                .map(this::toDTO)
                .collect(Collectors.toList());
    }

    public PriceHistory toEntity(PriceHistoryDTO priceHistoryDTO) {
        if (priceHistoryDTO == null) {
            return null;
        }

        PriceHistory priceHistory = new PriceHistory();
        priceHistory.setId(priceHistoryDTO.getId());
        priceHistory.setFlightId(priceHistoryDTO.getFlightId());
        priceHistory.setOldPrice(priceHistoryDTO.getOldPrice());
        priceHistory.setNewPrice(priceHistoryDTO.getNewPrice());
        priceHistory.setChangeTime(priceHistoryDTO.getChangeTime());
        priceHistory.setReason(PriceHistory.PriceChangeReason.valueOf(priceHistoryDTO.getReason()));

        return priceHistory;
    }

    public void updateEntityFromDTO(PriceHistoryDTO priceHistoryDTO, PriceHistory priceHistory) {
        if (priceHistoryDTO == null || priceHistory == null) {
            return;
        }

        // Зазвичай записи історії не оновлюються після створення,
        // але при потребі можна оновити деякі поля
        priceHistory.setReason(PriceHistory.PriceChangeReason.valueOf(priceHistoryDTO.getReason()));
    }
}