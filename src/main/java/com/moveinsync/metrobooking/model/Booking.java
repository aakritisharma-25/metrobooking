package com.moveinsync.metrobooking.model;

import jakarta.persistence.*;
import lombok.*;
import java.time.LocalDateTime;

@Entity
@Table(name = "bookings")
@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class Booking {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(nullable = false, unique = true)
    private String bookingReference;

    @ManyToOne
    @JoinColumn(name = "user_id", nullable = false)
    private User user;

    @ManyToOne
    @JoinColumn(name = "source_stop_id", nullable = false)
    private Stop sourceStop;

    @ManyToOne
    @JoinColumn(name = "destination_stop_id", nullable = false)
    private Stop destinationStop;

    @Column(columnDefinition = "TEXT")
    private String routePath;

    private int totalStops;
    private int totalInterchanges;
    private double estimatedTime;

    @Column(nullable = false)
    private String qrString;

    @Enumerated(EnumType.STRING)
    private BookingStatus status;

    private LocalDateTime createdAt;

    @PrePersist
    public void prePersist() {
        this.createdAt = LocalDateTime.now();
        if (this.status == null) this.status = BookingStatus.CONFIRMED;
    }

    public enum BookingStatus {
        CONFIRMED, CANCELLED, USED
    }
}