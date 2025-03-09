package com.gis.repository;

import com.gis.model.Booking;
import com.gis.model.Customer;
import com.gis.model.Status;
import com.gis.model.User;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.List;
import java.util.Optional;

public interface BookingRepository extends JpaRepository<Booking, String> {
    @Query("SELECT b FROM Booking b JOIN FETCH b.statuses s WHERE b.id = :bookingId ORDER BY s.time DESC LIMIT 1")
    Optional<Booking> findBookingWithLatestStatus(@Param("bookingId") String bookingId);
    List<Booking> findBookingsByCustomerId(String customer_id);
    List<Booking> findBookingsByUserId(String user);
}
