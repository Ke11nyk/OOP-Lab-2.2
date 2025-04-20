package com.lowcost.repository;

import com.lowcost.entity.Booking;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

@Repository
public interface BookingRepo extends JpaRepository<Booking, Integer> {
    List<Booking> findByUserId(int userId);

    Optional<Booking> findByBookingReference(String bookingReference);

    List<Booking> findByFlightId(int flightId);

    List<Booking> findByStatus(Booking.BookingStatus status);

    @Query("SELECT b FROM Booking b JOIN Flight f ON b.flightId = f.id " +
            "WHERE f.departureTime BETWEEN :start AND :end")
    List<Booking> findByFlightDepartureTimeBetween(
            @Param("start") LocalDateTime start,
            @Param("end") LocalDateTime end);

    @Query("SELECT COUNT(b) FROM Booking b WHERE b.flightId = :flightId AND b.status IN (com.lowcost.entity.Booking.BookingStatus.CONFIRMED, com.lowcost.entity.Booking.BookingStatus.PAID)")
    int countActiveBookingsByFlight(@Param("flightId") int flightId);

    List<Booking> findByBookingDateAfterAndStatus(
            LocalDateTime date,
            Booking.BookingStatus status);

    @Query("SELECT b FROM Booking b WHERE b.userId = :userId AND b.status = :status")
    List<Booking> findByUserIdAndStatus(
            @Param("userId") int userId,
            @Param("status") Booking.BookingStatus status);

    List<Booking> findByPriorityBoardingTrue();

    List<Booking> findByCheckedBaggageTrue();
}