package com.moveinsync.metrobooking.dto;

import lombok.*;
import java.util.List;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class PathResult {
    private List<PathSegment> segments;
    private int totalStops;
    private int totalInterchanges;
    private double totalTravelTime;
    private boolean pathFound;
}