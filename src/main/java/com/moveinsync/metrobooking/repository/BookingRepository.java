package com.moveinsync.metrobooking.repository;

import com.moveinsync.metrobooking.model.Booking;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import java.util.List;
import java.util.Optional;

@Repository
public interface BookingRepository extends JpaRepository<Booking, Long> {
    Optional<Booking> findByBookingReference(String bookingReference);
    List<Booking> findByUserId(Long userId);
    boolean existsByBookingReference(String bookingReference);
}