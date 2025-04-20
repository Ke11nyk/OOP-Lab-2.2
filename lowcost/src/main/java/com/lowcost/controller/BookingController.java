package com.lowcost.controller;

import com.lowcost.dto.BookingDTO;
import com.lowcost.dto.BookingWithFlightDTO;
import com.lowcost.entity.Booking;
import com.lowcost.mapper.BookingMapper;
import com.lowcost.service.BookingService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("/api/bookings")
public class BookingController {

    private final BookingService bookingService;
    private final BookingMapper bookingMapper;

    @Autowired
    public BookingController(BookingService bookingService, BookingMapper bookingMapper) {
        this.bookingService = bookingService;
        this.bookingMapper = bookingMapper;
    }

    @GetMapping
    public ResponseEntity<List<BookingDTO>> getAllBookings() {
        List<Booking> bookings = bookingService.getAllBookings();
        return ResponseEntity.ok(bookingMapper.toDTOList(bookings));
    }

    @GetMapping("/{id}")
    public ResponseEntity<BookingDTO> getBookingById(@PathVariable int id) {
        return bookingService.getBookingById(id)
                .map(bookingMapper::toDTO)
                .map(ResponseEntity::ok)
                .orElse(ResponseEntity.notFound().build());
    }

    @GetMapping("/reference/{reference}")
    public ResponseEntity<BookingDTO> getBookingByReference(@PathVariable String reference) {
        return bookingService.getBookingByReference(reference)
                .map(bookingMapper::toDTO)
                .map(ResponseEntity::ok)
                .orElse(ResponseEntity.notFound().build());
    }

    @GetMapping("/user/{userId}")
    public ResponseEntity<List<BookingWithFlightDTO>> getBookingsByUserId(@PathVariable int userId) {
        List<Booking> bookings = bookingService.getBookingsByUserId(userId);
        return ResponseEntity.ok(bookingMapper.toDTOWithFlightList(bookings));
    }

    @GetMapping("/user/{userId}/status/{status}")
    public ResponseEntity<List<BookingDTO>> getBookingsByUserIdAndStatus(
            @PathVariable int userId,
            @PathVariable Booking.BookingStatus status) {
        List<Booking> bookings = bookingService.getBookingsByUserIdAndStatus(userId, status);
        return ResponseEntity.ok(bookingMapper.toDTOList(bookings));
    }

    @GetMapping("/flight/{flightId}")
    public ResponseEntity<List<BookingDTO>> getBookingsByFlightId(@PathVariable int flightId) {
        List<Booking> bookings = bookingService.getBookingsByFlightId(flightId);
        return ResponseEntity.ok(bookingMapper.toDTOList(bookings));
    }

    @PostMapping
    public ResponseEntity<BookingDTO> createBooking(@RequestBody Map<String, Object> request) {
        try {
            int userId = (int) request.get("userId");
            int flightId = (int) request.get("flightId");
            boolean priorityBoarding = (boolean) request.getOrDefault("priorityBoarding", false);
            boolean checkedBaggage = (boolean) request.getOrDefault("checkedBaggage", false);
            int baggageCount = (int) request.getOrDefault("baggageCount", 0);

            Booking booking = bookingService.createBooking(userId, flightId, priorityBoarding,
                    checkedBaggage, baggageCount);
            return ResponseEntity.status(HttpStatus.CREATED).body(bookingMapper.toDTO(booking));
        } catch (IllegalStateException e) {
            return ResponseEntity.badRequest().build();
        }
    }

    @PatchMapping("/{id}/confirm")
    public ResponseEntity<Void> confirmBooking(@PathVariable int id) {
        return bookingService.getBookingById(id)
                .map(booking -> {
                    bookingService.confirmBooking(id);
                    return ResponseEntity.noContent().<Void>build();
                })
                .orElse(ResponseEntity.notFound().build());
    }

    @PatchMapping("/{id}/pay")
    public ResponseEntity<Void> payForBooking(@PathVariable int id) {
        return bookingService.getBookingById(id)
                .map(booking -> {
                    bookingService.payForBooking(id);
                    return ResponseEntity.noContent().<Void>build();
                })
                .orElse(ResponseEntity.notFound().build());
    }

    @PatchMapping("/{id}/cancel")
    public ResponseEntity<Void> cancelBooking(@PathVariable int id) {
        return bookingService.getBookingById(id)
                .map(booking -> {
                    bookingService.cancelBooking(id);
                    return ResponseEntity.noContent().<Void>build();
                })
                .orElse(ResponseEntity.notFound().build());
    }

    @PatchMapping("/{id}/refund")
    public ResponseEntity<Void> refundBooking(@PathVariable int id) {
        return bookingService.getBookingById(id)
                .map(booking -> {
                    bookingService.refundBooking(id);
                    return ResponseEntity.noContent().<Void>build();
                })
                .orElse(ResponseEntity.notFound().build());
    }

    @PutMapping("/{id}")
    public ResponseEntity<BookingDTO> updateBooking(@PathVariable int id, @RequestBody BookingDTO bookingDTO) {
        return bookingService.getBookingById(id)
                .map(existingBooking -> {
                    bookingMapper.updateEntityFromDTO(bookingDTO, existingBooking);
                    Booking updatedBooking = bookingService.updateBooking(existingBooking);
                    return ResponseEntity.ok(bookingMapper.toDTO(updatedBooking));
                })
                .orElse(ResponseEntity.notFound().build());
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Void> deleteBooking(@PathVariable int id) {
        return bookingService.getBookingById(id)
                .map(booking -> {
                    bookingService.deleteBooking(id);
                    return ResponseEntity.noContent().<Void>build();
                })
                .orElse(ResponseEntity.notFound().build());
    }

    @GetMapping("/{id}/with-flight")
    public ResponseEntity<BookingWithFlightDTO> getBookingWithFlight(@PathVariable int id) {
        return ResponseEntity.ok(bookingService.getBookingWithFlightDetails(id));
    }
}