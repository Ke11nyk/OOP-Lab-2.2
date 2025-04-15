package com.lowcost.repository;

import com.lowcost.entity.Flight;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.time.LocalDateTime;
import java.util.List;

@Repository
public interface FlightRepo extends JpaRepository<Flight, Integer> {
    List<Flight> findByDepartureAirportAndArrivalAirport(String departureAirport, String arrivalAirport);

    List<Flight> findByDepartureTimeBetweenAndIsActiveTrue(LocalDateTime start, LocalDateTime end);

    List<Flight> findByFlightNumber(String flightNumber);

    @Query("SELECT f FROM Flight f WHERE f.departureAirport = :departure AND f.arrivalAirport = :arrival " +
            "AND f.departureTime >= :startDate AND f.departureTime <= :endDate AND f.availableSeats > 0 AND f.isActive = true")
    List<Flight> findAvailableFlights(
            @Param("departure") String departureAirport,
            @Param("arrival") String arrivalAirport,
            @Param("startDate") LocalDateTime startDate,
            @Param("endDate") LocalDateTime endDate);

    List<Flight> findByAvailableSeatsLessThanAndIsActiveTrue(int seatThreshold);

    List<Flight> findByDepartureTimeBefore(LocalDateTime thresholdDate);
}