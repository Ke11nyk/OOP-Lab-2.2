package com.lowcost.service;

import com.lowcost.entity.Flight;
import com.lowcost.repository.FlightRepo;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

@Service
public class FlightService {

    private final FlightRepo flightRepository;
    private final PriceHistoryService priceHistoryService;

    @Autowired
    public FlightService(FlightRepo flightRepository, PriceHistoryService priceHistoryService) {
        this.flightRepository = flightRepository;
        this.priceHistoryService = priceHistoryService;
    }

    public List<Flight> getAllFlights() {
        return flightRepository.findAll();
    }

    public Optional<Flight> getFlightById(int id) {
        return flightRepository.findById(id);
    }

    public List<Flight> searchFlights(String departureAirport, String arrivalAirport,
                                      LocalDateTime startDate, LocalDateTime endDate) {
        return flightRepository.findAvailableFlights(departureAirport, arrivalAirport, startDate, endDate);
    }

    public Flight saveFlight(Flight flight) {
        if (flight.getCurrentPrice() == null) {
            flight.setCurrentPrice(flight.getBasePrice());
        }
        return flightRepository.save(flight);
    }

    @Transactional
    public boolean decreaseAvailableSeats(int flightId) {
        Optional<Flight> flightOpt = flightRepository.findById(flightId);
        if (flightOpt.isPresent()) {
            Flight flight = flightOpt.get();
            if (flight.getAvailableSeats() > 0) {
                flight.setAvailableSeats(flight.getAvailableSeats() - 1);
                flightRepository.save(flight);

                // Check if price adjustment is needed based on remaining seats
                checkAndAdjustPriceBasedOnSeats(flight);

                return true;
            }
        }
        return false;
    }

    @Transactional
    public boolean increaseAvailableSeats(int flightId) {
        Optional<Flight> flightOpt = flightRepository.findById(flightId);
        if (flightOpt.isPresent()) {
            Flight flight = flightOpt.get();
            if (flight.getAvailableSeats() < flight.getTotalSeats()) {
                flight.setAvailableSeats(flight.getAvailableSeats() + 1);
                flightRepository.save(flight);
                return true;
            }
        }
        return false;
    }

    @Transactional
    public void adjustPriceBasedOnDemand(int flightId, BigDecimal newPrice) {
        Optional<Flight> flightOpt = flightRepository.findById(flightId);
        if (flightOpt.isPresent()) {
            Flight flight = flightOpt.get();
            BigDecimal oldPrice = flight.getCurrentPrice();
            flight.setCurrentPrice(newPrice);
            flightRepository.save(flight);

            // Record price change history
            priceHistoryService.recordPriceChange(flightId, oldPrice, newPrice,
                    com.lowcost.entity.PriceHistory.PriceChangeReason.DEMAND_INCREASE);
        }
    }

    @Transactional
    public void checkAndAdjustPriceBasedOnDeparture() {
        // Find flights that are close to departure (e.g., within 7 days)
        LocalDateTime thresholdDate = LocalDateTime.now().plusDays(7);
        List<Flight> flights = flightRepository.findByDepartureTimeBefore(thresholdDate);

        for (Flight flight : flights) {
            if (flight.getDepartureTime().isAfter(LocalDateTime.now())) {
                // Increase price by 10% for last-minute bookings
                BigDecimal oldPrice = flight.getCurrentPrice();
                BigDecimal newPrice = oldPrice.multiply(new BigDecimal("1.10"));
                flight.setCurrentPrice(newPrice);
                flightRepository.save(flight);

                // Record price change history
                priceHistoryService.recordPriceChange(flight.getId(), oldPrice, newPrice,
                        com.lowcost.entity.PriceHistory.PriceChangeReason.LAST_MINUTE);
            }
        }
    }

    private void checkAndAdjustPriceBasedOnSeats(Flight flight) {
        // If seats are less than 20% of total capacity, increase price
        int threshold = (int) (flight.getTotalSeats() * 0.2);
        if (flight.getAvailableSeats() <= threshold) {
            BigDecimal oldPrice = flight.getCurrentPrice();
            BigDecimal newPrice = oldPrice.multiply(new BigDecimal("1.15")); // 15% increase
            flight.setCurrentPrice(newPrice);
            flightRepository.save(flight);

            // Record price change history
            priceHistoryService.recordPriceChange(flight.getId(), oldPrice, newPrice,
                    com.lowcost.entity.PriceHistory.PriceChangeReason.SEATS_LEFT);
        }
    }

    @Transactional
    public void manualPriceAdjustment(int flightId, BigDecimal newPrice) {
        Optional<Flight> flightOpt = flightRepository.findById(flightId);
        if (flightOpt.isPresent()) {
            Flight flight = flightOpt.get();
            BigDecimal oldPrice = flight.getCurrentPrice();
            flight.setCurrentPrice(newPrice);
            flightRepository.save(flight);

            // Record price change history
            priceHistoryService.recordPriceChange(flightId, oldPrice, newPrice,
                    com.lowcost.entity.PriceHistory.PriceChangeReason.MANUAL_ADJUSTMENT);
        }
    }

    public void deleteFlight(int id) {
        flightRepository.deleteById(id);
    }

    @Transactional
    public void updateFlightStatus(int flightId, boolean isActive) {
        Optional<Flight> flightOpt = flightRepository.findById(flightId);
        flightOpt.ifPresent(flight -> {
            flight.setActive(isActive);
            flightRepository.save(flight);
        });
    }
}