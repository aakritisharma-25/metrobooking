package com.moveinsync.metrobooking.graph;

import lombok.*;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class GraphEdge {
    private Long fromStopId;
    private Long toStopId;
    private Long routeId;
    private String routeName;
    private String routeColor;
    private double travelTime;
}