package com.lowcost.service;

import com.lowcost.dto.BookingWithFlightDTO;
import com.lowcost.entity.Booking;
import com.lowcost.entity.Flight;
import com.lowcost.mapper.FlightMapper;
import com.lowcost.repository.BookingRepo;
import jakarta.persistence.EntityNotFoundException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;
import java.util.Random;

@Service
public class BookingService {

    private final BookingRepo bookingRepository;
    private final FlightService flightService;
    private final FlightMapper flightMapper;

    @Autowired
    public BookingService(BookingRepo bookingRepository, FlightService flightService, FlightMapper flightMapper) {
        this.bookingRepository = bookingRepository;
        this.flightService = flightService;
        this.flightMapper = flightMapper;
    }

    public List<Booking> getAllBookings() {
        return bookingRepository.findAll();
    }

    public Optional<Booking> getBookingById(int id) {
        return bookingRepository.findById(id);
    }

    public Optional<Booking> getBookingByReference(String reference) {
        return bookingRepository.findByBookingReference(reference);
    }

    public List<Booking> getBookingsByUserId(int userId) {
        return bookingRepository.findByUserId(userId);
    }

    public List<Booking> getBookingsByUserIdAndStatus(int userId, Booking.BookingStatus status) {
        return bookingRepository.findByUserIdAndStatus(userId, Booking.BookingStatus.valueOf(status.name()));
    }

    public List<Booking> getBookingsByFlightId(int flightId) {
        return bookingRepository.findByFlightId(flightId);
    }

    @Transactional
    public Booking createBooking(int userId, int flightId, boolean priorityBoarding,
                                 boolean checkedBaggage, int baggageCount) {
        // Check if the flight exists and has available seats
        Optional<Flight> flightOpt = flightService.getFlightById(flightId);
        if (!flightOpt.isPresent() || flightOpt.get().getAvailableSeats() <= 0 || !flightOpt.get().isActive()) {
            throw new IllegalStateException("Flight is not available for booking");
        }

        Flight flight = flightOpt.get();

        // Calculate total price based on flight price and add-ons
        BigDecimal totalPrice = calculateTotalPrice(flight, priorityBoarding, checkedBaggage, baggageCount);

        // Generate booking reference
        String bookingReference = generateBookingReference();

        // Create booking
        Booking booking = new Booking();
        booking.setUserId(userId);
        booking.setFlightId(flightId);
        booking.setBookingReference(bookingReference);
        booking.setBookingDate(LocalDateTime.now());
        booking.setTotalPrice(totalPrice);
        booking.setStatus(Booking.BookingStatus.PENDING);
        booking.setPriorityBoarding(priorityBoarding);
        booking.setCheckedBaggage(checkedBaggage);
        booking.setBaggageCount(baggageCount);

        // Save booking
        Booking savedBooking = bookingRepository.save(booking);

        // Decrease available seats for the flight
        flightService.decreaseAvailableSeats(flightId);

        return savedBooking;
    }

    @Transactional
    public void confirmBooking(int bookingId) {
        Optional<Booking> bookingOpt = bookingRepository.findById(bookingId);
        bookingOpt.ifPresent(booking -> {
            if (booking.getStatus() == Booking.BookingStatus.PENDING) {
                booking.setStatus(Booking.BookingStatus.CONFIRMED);
                bookingRepository.save(booking);
            }
        });
    }

    @Transactional
    public void payForBooking(int bookingId) {
        Optional<Booking> bookingOpt = bookingRepository.findById(bookingId);
        bookingOpt.ifPresent(booking -> {
            if (booking.getStatus() == Booking.BookingStatus.CONFIRMED) {
                booking.setStatus(Booking.BookingStatus.PAID);
                bookingRepository.save(booking);
            }
        });
    }

    @Transactional
    public void cancelBooking(int bookingId) {
        Optional<Booking> bookingOpt = bookingRepository.findById(bookingId);
        bookingOpt.ifPresent(booking -> {
            if (booking.getStatus() != Booking.BookingStatus.CANCELLED &&
                    booking.getStatus() != Booking.BookingStatus.REFUNDED) {
                booking.setStatus(Booking.BookingStatus.CANCELLED);
                bookingRepository.save(booking);

                // Increase available seats for the flight
                flightService.increaseAvailableSeats(booking.getFlightId());
            }
        });
    }

    @Transactional
    public void refundBooking(int bookingId) {
        Optional<Booking> bookingOpt = bookingRepository.findById(bookingId);
        bookingOpt.ifPresent(booking -> {
            if (booking.getStatus() == Booking.BookingStatus.CANCELLED) {
                booking.setStatus(Booking.BookingStatus.REFUNDED);
                bookingRepository.save(booking);
            }
        });
    }

    private BigDecimal calculateTotalPrice(Flight flight, boolean priorityBoarding,
                                           boolean checkedBaggage, int baggageCount) {
        BigDecimal totalPrice = flight.getCurrentPrice();

        // Add priority boarding fee if selected
        if (priorityBoarding) {
            totalPrice = totalPrice.add(new BigDecimal("15.00")); // Example priority boarding fee
        }

        // Add baggage fee if selected
        if (checkedBaggage) {
            BigDecimal baggageFee = new BigDecimal("25.00"); // Example base baggage fee
            // Additional fee for each extra bag
            BigDecimal extraBaggageFee = baggageFee.multiply(new BigDecimal(baggageCount));
            totalPrice = totalPrice.add(extraBaggageFee);
        }

        return totalPrice;
    }

    private String generateBookingReference() {
        // Generate a random 6-character alphanumeric booking reference
        String chars = "ABCDEFGHIJKLMNOPQRSTUVWXYZ0123456789";
        StringBuilder sb = new StringBuilder();
        Random random = new Random();
        for (int i = 0; i < 6; i++) {
            sb.append(chars.charAt(random.nextInt(chars.length())));
        }
        return sb.toString();
    }

    public Booking updateBooking(Booking booking) {
        return bookingRepository.save(booking);
    }

    public void deleteBooking(int id) {
        bookingRepository.deleteById(id);
    }

    public BookingWithFlightDTO getBookingWithFlightDetails(int id) {


        Booking booking = bookingRepository.findById(id)
                .orElseThrow(() -> new EntityNotFoundException("Booking not found"));

        Flight flight = flightService.getFlightById(booking.getFlightId())
                .orElseThrow(() -> new EntityNotFoundException("Flight not found"));

        return BookingWithFlightDTO.builder()
                .id(booking.getId())
                .bookingReference(booking.getBookingReference())
                .bookingDate(booking.getBookingDate())
                .totalPrice(booking.getTotalPrice())
                .status(booking.getStatus().toString())
                .priorityBoarding(booking.isPriorityBoarding())
                .checkedBaggage(booking.isCheckedBaggage())
                .baggageCount(booking.getBaggageCount())
                .flight(flightMapper.toDTO(flight))
                .userId(booking.getUserId())
                .build();
    }
}