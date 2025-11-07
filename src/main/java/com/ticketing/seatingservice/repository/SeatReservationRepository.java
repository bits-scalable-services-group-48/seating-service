package com.ticketing.seatingservice.repository;

import com.ticketing.seatingservice.entity.ReservationStatus;
import com.ticketing.seatingservice.entity.SeatReservation;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;

import java.time.LocalDateTime;
import java.util.List;

public interface SeatReservationRepository extends JpaRepository<SeatReservation, Long> {

    @Query("""
           SELECT r FROM SeatReservation r
           WHERE r.eventId = :eventId
             AND r.status IN ('HELD', 'RESERVED')
             AND (r.expiresAt IS NULL OR r.expiresAt > :now)
           """)
    List<SeatReservation> findActiveReservationsForEvent(Long eventId, LocalDateTime now);

    @Query("""
           SELECT r FROM SeatReservation r
           WHERE r.eventId = :eventId
             AND r.seatId IN :seatIds
             AND r.status IN ('HELD', 'RESERVED')
             AND (r.expiresAt IS NULL OR r.expiresAt > :now)
           """)
    List<SeatReservation> findActiveByEventAndSeats(Long eventId, List<Long> seatIds, LocalDateTime now);

    List<SeatReservation> findByEventIdAndOrderReferenceAndStatusIn(
            Long eventId,
            String orderReference,
            List<ReservationStatus> statuses
    );

    @Modifying
    @Query("""
           UPDATE SeatReservation r
           SET r.status = 'EXPIRED'
           WHERE r.status = 'HELD'
             AND r.expiresAt IS NOT NULL
             AND r.expiresAt <= :now
           """)
    int expireHolds(LocalDateTime now);
}

