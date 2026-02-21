package com.moveinsync.metrobooking.dto;

import jakarta.validation.constraints.NotNull;
import lombok.*;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class BookingRequest {

    @NotNull(message = "Source stop ID is required")
    private Long sourceStopId;

    @NotNull(message = "Destination stop ID is required")
    private Long destinationStopId;
}