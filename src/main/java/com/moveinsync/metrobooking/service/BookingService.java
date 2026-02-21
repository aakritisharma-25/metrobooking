package com.moveinsync.metrobooking.service;

import com.moveinsync.metrobooking.dto.*;
import com.moveinsync.metrobooking.exception.NoPathException;
import com.moveinsync.metrobooking.exception.StopNotFoundException;
import com.moveinsync.metrobooking.graph.MetroGraph;
import com.moveinsync.metrobooking.model.Booking;
import com.moveinsync.metrobooking.model.Stop;
import com.moveinsync.metrobooking.model.User;
import com.moveinsync.metrobooking.repository.BookingRepository;
import com.moveinsync.metrobooking.repository.StopRepository;
import com.fasterxml.jackson.databind.ObjectMapper;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import java.nio.charset.StandardCharsets;
import java.security.MessageDigest;
import java.time.LocalDateTime;
import java.util.Base64;
import java.util.List;
import java.util.UUID;

@Service
@Slf4j
@RequiredArgsConstructor
public class BookingService {

    private final BookingRepository bookingRepository;
    private final StopRepository stopRepository;
    private final GraphService graphService;
    private final PathFinderService pathFinderService;
    private final ObjectMapper objectMapper;

    public BookingResponse createBooking(BookingRequest request, User user) {
        log.info("Creating booking for user: {} from stop {} to stop {}",
                user.getEmail(), request.getSourceStopId(), request.getDestinationStopId());

        // Validate stops exist
        Stop sourceStop = stopRepository.findById(request.getSourceStopId())
                .orElseThrow(() -> new StopNotFoundException(
                        "Source stop not found with ID: " + request.getSourceStopId()));

        Stop destinationStop = stopRepository.findById(request.getDestinationStopId())
                .orElseThrow(() -> new StopNotFoundException(
                        "Destination stop not found with ID: " + request.getDestinationStopId()));

        // Same stop check
        if (sourceStop.getId().equals(destinationStop.getId())) {
            throw new NoPathException("Source and destination cannot be the same stop!");
        }

        // Build graph and find path
        MetroGraph graph = graphService.buildGraph();
        PathResult pathResult = pathFinderService.findOptimalPath(
                graph, sourceStop.getId(), destinationStop.getId());

        // No path found
        if (!pathResult.isPathFound()) {
            throw new NoPathException("No metro path found from "
                    + sourceStop.getName() + " to " + destinationStop.getName());
        }

        // Generate unique booking reference
        String bookingReference = generateBookingReference();

        // Generate QR string
        String qrString = generateQRString(bookingReference, sourceStop, destinationStop, user);

        // Convert path to JSON string
        String routePath = convertPathToJson(pathResult.getSegments());

        // Save booking
        Booking booking = Booking.builder()
                .bookingReference(bookingReference)
                .user(user)
                .sourceStop(sourceStop)
                .destinationStop(destinationStop)
                .routePath(routePath)
                .totalStops(pathResult.getTotalStops())
                .totalInterchanges(pathResult.getTotalInterchanges())
                .estimatedTime(pathResult.getTotalTravelTime())
                .qrString(qrString)
                .status(Booking.BookingStatus.CONFIRMED)
                .build();

        booking = bookingRepository.save(booking);
        log.info("Booking created successfully: {}", bookingReference);

        return BookingResponse.builder()
                .bookingId(booking.getId())
                .bookingReference(bookingReference)
                .sourceStop(sourceStop.getName())
                .destinationStop(destinationStop.getName())
                .path(pathResult.getSegments())
                .totalStops(pathResult.getTotalStops())
                .totalInterchanges(pathResult.getTotalInterchanges())
                .estimatedTime(pathResult.getTotalTravelTime())
                .qrString(qrString)
                .status(booking.getStatus().name())
                .createdAt(booking.getCreatedAt())
                .build();
    }

    public List<Booking> getUserBookings(Long userId) {
        return bookingRepository.findByUserId(userId);
    }

    public BookingResponse getBookingByReference(String reference) {
        Booking booking = bookingRepository.findByBookingReference(reference)
                .orElseThrow(() -> new RuntimeException("Booking not found: " + reference));

        return BookingResponse.builder()
                .bookingId(booking.getId())
                .bookingReference(booking.getBookingReference())
                .sourceStop(booking.getSourceStop().getName())
                .destinationStop(booking.getDestinationStop().getName())
                .totalStops(booking.getTotalStops())
                .totalInterchanges(booking.getTotalInterchanges())
                .estimatedTime(booking.getEstimatedTime())
                .qrString(booking.getQrString())
                .status(booking.getStatus().name())
                .createdAt(booking.getCreatedAt())
                .build();
    }

    // Generate unique booking reference e.g. MIS-20240221-ABC123
    private String generateBookingReference() {
        String uuid = UUID.randomUUID().toString().substring(0, 6).toUpperCase();
        String date = LocalDateTime.now().toString().substring(0, 10).replace("-", "");
        return "MIS-" + date + "-" + uuid;
    }

    // Generate tamper-resistant QR string using SHA-256
    private String generateQRString(String bookingRef, Stop source,
                                    Stop destination, User user) {
        try {
            String raw = bookingRef + "|" + source.getId() + "|"
                    + destination.getId() + "|" + user.getId()
                    + "|" + System.currentTimeMillis();

            MessageDigest digest = MessageDigest.getInstance("SHA-256");
            byte[] hash = digest.digest(raw.getBytes(StandardCharsets.UTF_8));
            String encoded = Base64.getUrlEncoder().encodeToString(hash);

            return bookingRef + "." + encoded.substring(0, 16);
        } catch (Exception e) {
            return bookingRef + "." + UUID.randomUUID().toString().substring(0, 16);
        }
    }

    private String convertPathToJson(List<PathSegment> segments) {
        try {
            return objectMapper.writeValueAsString(segments);
        } catch (Exception e) {
            return "[]";
        }
    }
}