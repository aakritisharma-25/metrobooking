package com.moveinsync.metrobooking.dto;

import lombok.*;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class PathSegment {
    private String stopName;
    private String stopCode;
    private String routeName;
    private String routeColor;
    private boolean isInterchange;
}