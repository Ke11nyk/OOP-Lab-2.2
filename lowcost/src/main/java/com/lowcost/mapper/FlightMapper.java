package com.lowcost.mapper;

import com.lowcost.dto.FlightDTO;
import com.lowcost.entity.Flight;
import org.springframework.stereotype.Component;

import java.util.List;
import java.util.stream.Collectors;

@Component
public class FlightMapper {

    public FlightDTO toDTO(Flight flight) {
        if (flight == null) {
            return null;
        }

        return FlightDTO.builder()
                .id(flight.getId())
                .flightNumber(flight.getFlightNumber())
                .departureAirport(flight.getDepartureAirport())
                .arrivalAirport(flight.getArrivalAirport())
                .departureTime(flight.getDepartureTime())
                .arrivalTime(flight.getArrivalTime())
                .basePrice(flight.getBasePrice())
                .currentPrice(flight.getCurrentPrice())
                .totalSeats(flight.getTotalSeats())
                .availableSeats(flight.getAvailableSeats())
                .isActive(flight.isActive())
                .isPriceIncreased(flight.isPriceIncreased())
                .build();
    }

    public List<FlightDTO> toDTOList(List<Flight> flights) {
        if (flights == null) {
            return null;
        }

        return flights.stream()
                .map(this::toDTO)
                .collect(Collectors.toList());
    }

    public Flight toEntity(FlightDTO flightDTO) {
        if (flightDTO == null) {
            return null;
        }

        Flight flight = new Flight();
        flight.setId(flightDTO.getId());
        flight.setFlightNumber(flightDTO.getFlightNumber());
        flight.setDepartureAirport(flightDTO.getDepartureAirport());
        flight.setArrivalAirport(flightDTO.getArrivalAirport());
        flight.setDepartureTime(flightDTO.getDepartureTime());
        flight.setArrivalTime(flightDTO.getArrivalTime());
        flight.setBasePrice(flightDTO.getBasePrice());
        flight.setCurrentPrice(flightDTO.getCurrentPrice());
        flight.setTotalSeats(flightDTO.getTotalSeats());
        flight.setAvailableSeats(flightDTO.getAvailableSeats());
        flight.setActive(flightDTO.isActive());

        return flight;
    }

    public void updateEntityFromDTO(FlightDTO flightDTO, Flight flight) {
        if (flightDTO == null || flight == null) {
            return;
        }

        flight.setFlightNumber(flightDTO.getFlightNumber());
        flight.setDepartureAirport(flightDTO.getDepartureAirport());
        flight.setArrivalAirport(flightDTO.getArrivalAirport());
        flight.setDepartureTime(flightDTO.getDepartureTime());
        flight.setArrivalTime(flightDTO.getArrivalTime());
        flight.setBasePrice(flightDTO.getBasePrice());
        flight.setCurrentPrice(flightDTO.getCurrentPrice());
        flight.setTotalSeats(flightDTO.getTotalSeats());
        flight.setAvailableSeats(flightDTO.getAvailableSeats());
        flight.setActive(flightDTO.isActive());
    }
}