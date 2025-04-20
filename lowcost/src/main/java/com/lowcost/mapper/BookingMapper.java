package com.lowcost.mapper;

import com.lowcost.controller.FlightController;
import com.lowcost.dto.BookingDTO;
import com.lowcost.dto.BookingWithFlightDTO;
import com.lowcost.entity.Booking;
import com.lowcost.entity.Flight;
import com.lowcost.entity.User;
import com.lowcost.repository.FlightRepo;
import com.lowcost.repository.UserRepo;
import com.lowcost.service.FlightService;
import jakarta.persistence.EntityNotFoundException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.util.Collections;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

@Component
public class BookingMapper {

    private final FlightRepo flightRepository;
    private final UserRepo userRepository;
    private final FlightService flightService;
    private final FlightMapper flightMapper;

    @Autowired
    public BookingMapper(FlightRepo flightRepository, UserRepo userRepository, FlightService flightService, FlightMapper flightMapper) {
        this.flightRepository = flightRepository;
        this.userRepository = userRepository;
        this.flightService = flightService;
        this.flightMapper = flightMapper;
    }

    public BookingDTO toDTO(Booking booking) {
        if (booking == null) {
            return null;
        }

        BookingDTO dto = BookingDTO.builder()
                .id(booking.getId())
                .userId(booking.getUserId())
                .flightId(booking.getFlightId())
                .bookingReference(booking.getBookingReference())
                .bookingDate(booking.getBookingDate())
                .totalPrice(booking.getTotalPrice())
                .status(String.valueOf(Booking.BookingStatus.valueOf(booking.getStatus().name())))
                .priorityBoarding(booking.isPriorityBoarding())
                .checkedBaggage(booking.isCheckedBaggage())
                .baggageCount(booking.getBaggageCount())
                .build();

        // Додаємо додаткову інформацію з пов'язаних сутностей, якщо вони доступні
        Optional<Flight> flightOpt = flightRepository.findById(booking.getFlightId());
        flightOpt.ifPresent(flight -> {
            dto.setFlightNumber(flight.getFlightNumber());
            dto.setDepartureAirport(flight.getDepartureAirport());
            dto.setArrivalAirport(flight.getArrivalAirport());
            dto.setDepartureTime(flight.getDepartureTime());
            dto.setArrivalTime(flight.getArrivalTime());
        });

        Optional<User> userOpt = userRepository.findById(String.valueOf(booking.getUserId()));
        userOpt.ifPresent(user -> {
            dto.setUserFullName(user.getLogin()); // Припускаючи, що у вас немає поля fullName у User
        });

        return dto;
    }

    public List<BookingDTO> toDTOList(List<Booking> bookings) {
        if (bookings == null) {
            return null;
        }

        return bookings.stream()
                .map(this::toDTO)
                .collect(Collectors.toList());
    }

    public List<BookingWithFlightDTO> toDTOWithFlightList(List<Booking> bookings) {
        if (bookings == null) {
            return Collections.emptyList();  // Краще повертати пустий список замість null
        }

        return bookings.stream()
                .map(this::toDTOWithFlight)
                .collect(Collectors.toList());
    }

    private BookingWithFlightDTO toDTOWithFlight(Booking booking) {
        if (booking == null) {
            return null;
        }

        // Отримуємо пов'язаний рейс через сервіс
        Flight flight = flightService.getFlightById(booking.getFlightId())
                .orElseThrow(() -> new EntityNotFoundException("Flight not found for booking id: " + booking.getId()));

        return BookingWithFlightDTO.builder()
                .id(booking.getId())
                .userId(booking.getUserId())
                .bookingReference(booking.getBookingReference())
                .bookingDate(booking.getBookingDate())
                .totalPrice(booking.getTotalPrice())
                .status(booking.getStatus().toString())
                .priorityBoarding(booking.isPriorityBoarding())
                .checkedBaggage(booking.isCheckedBaggage())
                .baggageCount(booking.getBaggageCount())
                .flight(flightMapper.toDTO(flight))  // Мапимо повний об'єкт рейсу
                .build();
    }

    public Booking toEntity(BookingDTO bookingDTO) {
        if (bookingDTO == null) {
            return null;
        }

        Booking booking = new Booking();
        booking.setId(bookingDTO.getId());
        booking.setUserId(bookingDTO.getUserId());
        booking.setFlightId(bookingDTO.getFlightId());
        booking.setBookingReference(bookingDTO.getBookingReference());
        booking.setBookingDate(bookingDTO.getBookingDate());
        booking.setTotalPrice(bookingDTO.getTotalPrice());
        booking.setStatus(Booking.BookingStatus.valueOf(String.valueOf(bookingDTO.getStatus())));
        booking.setPriorityBoarding(bookingDTO.isPriorityBoarding());
        booking.setCheckedBaggage(bookingDTO.isCheckedBaggage());
        booking.setBaggageCount(bookingDTO.getBaggageCount());

        return booking;
    }

    public void updateEntityFromDTO(BookingDTO bookingDTO, Booking booking) {
        if (bookingDTO == null || booking == null) {
            return;
        }

        // Зазвичай ми не оновлюємо userId, flightId та bookingReference після створення
        booking.setTotalPrice(bookingDTO.getTotalPrice());
        booking.setStatus(Booking.BookingStatus.valueOf(String.valueOf(bookingDTO.getStatus())));
        booking.setPriorityBoarding(bookingDTO.isPriorityBoarding());
        booking.setCheckedBaggage(bookingDTO.isCheckedBaggage());
        booking.setBaggageCount(bookingDTO.getBaggageCount());
    }
}