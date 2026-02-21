package com.moveinsync.metrobooking.graph;

import lombok.*;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class GraphNode {
    private Long stopId;
    private String stopName;
    private String stopCode;
    private boolean isInterchange;
}