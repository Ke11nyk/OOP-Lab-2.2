package com.lowcost.controller;

import com.lowcost.entity.Booking;
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

    @Autowired
    public BookingController(BookingService bookingService) {
        this.bookingService = bookingService;
    }

    @GetMapping
    public ResponseEntity<List<Booking>> getAllBookings() {
        return ResponseEntity.ok(bookingService.getAllBookings());
    }

    @GetMapping("/{id}")
    public ResponseEntity<Booking> getBookingById(@PathVariable int id) {
        return bookingService.getBookingById(id)
                .map(ResponseEntity::ok)
                .orElse(ResponseEntity.notFound().build());
    }

    @GetMapping("/reference/{reference}")
    public ResponseEntity<Booking> getBookingByReference(@PathVariable String reference) {
        return bookingService.getBookingByReference(reference)
                .map(ResponseEntity::ok)
                .orElse(ResponseEntity.notFound().build());
    }

    @GetMapping("/user/{userId}")
    public ResponseEntity<List<Booking>> getBookingsByUserId(@PathVariable int userId) {
        return ResponseEntity.ok(bookingService.getBookingsByUserId(userId));
    }

    @GetMapping("/user/{userId}/status/{status}")
    public ResponseEntity<List<Booking>> getBookingsByUserIdAndStatus(
            @PathVariable int userId,
            @PathVariable Booking.BookingStatus status) {
        return ResponseEntity.ok(bookingService.getBookingsByUserIdAndStatus(userId, status));
    }

    @GetMapping("/flight/{flightId}")
    public ResponseEntity<List<Booking>> getBookingsByFlightId(@PathVariable int flightId) {
        return ResponseEntity.ok(bookingService.getBookingsByFlightId(flightId));
    }

    @PostMapping
    public ResponseEntity<Booking> createBooking(@RequestBody Map<String, Object> request) {
        try {
            int userId = (int) request.get("userId");
            int flightId = (int) request.get("flightId");
            boolean priorityBoarding = (boolean) request.getOrDefault("priorityBoarding", false);
            boolean checkedBaggage = (boolean) request.getOrDefault("checkedBaggage", false);
            int baggageCount = (int) request.getOrDefault("baggageCount", 0);

            Booking booking = bookingService.createBooking(userId, flightId, priorityBoarding,
                    checkedBaggage, baggageCount);
            return ResponseEntity.status(HttpStatus.CREATED).body(booking);
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
    public ResponseEntity<Booking> updateBooking(@PathVariable int id, @RequestBody Booking booking) {
        return bookingService.getBookingById(id)
                .map(existingBooking -> {
                    booking.setId(id);
                    return ResponseEntity.ok(bookingService.updateBooking(booking));
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
}