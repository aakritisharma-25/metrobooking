package com.moveinsync.metrobooking.dto;

import lombok.*;
import java.time.LocalDateTime;
import java.util.List;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class BookingResponse {
    private Long bookingId;
    private String bookingReference;
    private String sourceStop;
    private String destinationStop;
    private List<PathSegment> path;
    private int totalStops;
    private int totalInterchanges;
    private double estimatedTime;
    private String qrString;
    private String status;
    private LocalDateTime createdAt;
}