package com.moveinsync.metrobooking.controller;

import com.moveinsync.metrobooking.dto.BookingRequest;
import com.moveinsync.metrobooking.dto.BookingResponse;
import com.moveinsync.metrobooking.model.Booking;
import com.moveinsync.metrobooking.model.User;
import com.moveinsync.metrobooking.repository.UserRepository;
import com.moveinsync.metrobooking.service.BookingService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.*;
import java.util.List;

@RestController
@RequestMapping("/api/bookings")
@RequiredArgsConstructor
@Slf4j
public class BookingController {

    private final BookingService bookingService;
    private final UserRepository userRepository;

    @PostMapping
    public ResponseEntity<BookingResponse> createBooking(
            @Valid @RequestBody BookingRequest request,
            Authentication authentication) {

        User user = userRepository.findByEmail(authentication.getName())
                .orElseThrow(() -> new RuntimeException("User not found"));

        BookingResponse response = bookingService.createBooking(request, user);
        return ResponseEntity.ok(response);
    }

    @GetMapping("/my-bookings")
    public ResponseEntity<List<Booking>> getMyBookings(Authentication authentication) {
        User user = userRepository.findByEmail(authentication.getName())
                .orElseThrow(() -> new RuntimeException("User not found"));
        return ResponseEntity.ok(bookingService.getUserBookings(user.getId()));
    }

    @GetMapping("/{reference}")
    public ResponseEntity<BookingResponse> getBookingByReference(
            @PathVariable String reference) {
        return ResponseEntity.ok(bookingService.getBookingByReference(reference));
    }
}