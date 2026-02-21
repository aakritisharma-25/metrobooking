package com.moveinsync.metrobooking.model;

import jakarta.persistence.*;
import lombok.*;

@Entity
@Table(name = "stops")
@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class Stop {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(nullable = false, unique = true)
    private String name;

    @Column(nullable = false, unique = true)
    private String code;

    private Boolean isInterchange;

    private Double latitude;
    private Double longitude;
}