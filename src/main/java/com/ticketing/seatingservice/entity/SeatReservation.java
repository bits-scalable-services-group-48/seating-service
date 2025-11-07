package com.ticketing.seatingservice.entity;

import jakarta.persistence.*;
import lombok.*;

import java.time.LocalDateTime;

@Entity
@Table(
        name = "seat_reservations",
        indexes = {
                @Index(name = "idx_event_seat", columnList = "event_id, seat_id"),
                @Index(name = "idx_order_ref", columnList = "order_reference")
        }
)
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class SeatReservation {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(name = "event_id", nullable = false)
    private Long eventId;

    @Column(name = "seat_id", nullable = false)
    private Long seatId;

    @Enumerated(EnumType.STRING)
    @Column(name = "status", nullable = false)
    private ReservationStatus status;

    /**
     * Order reference from Order Service (e.g., "ORDER-1001").
     */
    @Column(name = "order_reference", nullable = false)
    private String orderReference;

    @Column(name = "created_at", nullable = false)
    private LocalDateTime createdAt;

    /**
     * For HELD reservations; when now() > expiresAt => EXPIRED.
     */
    @Column(name = "expires_at")
    private LocalDateTime expiresAt;
}
