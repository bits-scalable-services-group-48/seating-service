package com.ticketing.seatingservice.service;

import com.ticketing.seatingservice.dto.*;
import com.ticketing.seatingservice.entity.ReservationStatus;
import com.ticketing.seatingservice.entity.SeatReservation;
import com.ticketing.seatingservice.repository.SeatReservationRepository;
import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.*;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
@Transactional
public class SeatReservationService {

    private final SeatReservationRepository seatReservationRepository;

    private static final int DEFAULT_TTL_MINUTES = 15;

    private void expireOldHolds() {
        LocalDateTime now = LocalDateTime.now();
        seatReservationRepository.expireHolds(now);
    }

    /**
     * Returns active reservations (HELD/RESERVED) for an event.
     * Available seats = all canonical seats from Catalog - these ones.
     */
    public List<SeatAvailabilityDto> getSeatStatusForEvent(Long eventId) {
        expireOldHolds();

        LocalDateTime now = LocalDateTime.now();
        List<SeatReservation> reservations =
                seatReservationRepository.findActiveReservationsForEvent(eventId, now);

        return reservations.stream()
                .map(r -> SeatAvailabilityDto.builder()
                        .eventId(r.getEventId())
                        .seatId(r.getSeatId())
                        .status(r.getStatus())
                        .orderReference(r.getOrderReference())
                        .build())
                .toList();
    }

    public ReserveSeatsResponse reserveSeats(ReserveSeatsRequest request) {
        expireOldHolds();

        LocalDateTime now = LocalDateTime.now();
        int ttl = (request.getTtlMinutes() != null && request.getTtlMinutes() > 0)
                ? request.getTtlMinutes()
                : DEFAULT_TTL_MINUTES;
        LocalDateTime expiresAt = now.plusMinutes(ttl);

        // Check conflicts
        List<SeatReservation> existingActive =
                seatReservationRepository.findActiveByEventAndSeats(
                        request.getEventId(),
                        request.getSeatIds(),
                        now
                );

        if (!existingActive.isEmpty()) {
            String conflictSeats = existingActive.stream()
                    .map(r -> r.getSeatId().toString())
                    .distinct()
                    .collect(Collectors.joining(", "));
            throw new IllegalStateException("Some seats are not available: " + conflictSeats);
        }

        // Create HELD reservations
        List<SeatReservation> toSave = request.getSeatIds().stream()
                .map(seatId -> SeatReservation.builder()
                        .eventId(request.getEventId())
                        .seatId(seatId)
                        .status(ReservationStatus.HELD)
                        .orderReference(request.getOrderReference())
                        .createdAt(now)
                        .expiresAt(expiresAt)
                        .build())
                .toList();

        seatReservationRepository.saveAll(toSave);

        return ReserveSeatsResponse.builder()
                .eventId(request.getEventId())
                .orderReference(request.getOrderReference())
                .reservedSeatIds(request.getSeatIds())
                .expiresAt(expiresAt)
                .build();
    }

    public void releaseSeats(ReleaseSeatsRequest request) {
        expireOldHolds();

        List<ReservationStatus> activeStatuses = List.of(
                ReservationStatus.HELD,
                ReservationStatus.RESERVED
        );

        List<SeatReservation> reservations =
                seatReservationRepository.findByEventIdAndOrderReferenceAndStatusIn(
                        request.getEventId(),
                        request.getOrderReference(),
                        activeStatuses
                );

        if (request.getSeatIds() != null && !request.getSeatIds().isEmpty()) {
            Set<Long> ids = new HashSet<>(request.getSeatIds());
            reservations = reservations.stream()
                    .filter(r -> ids.contains(r.getSeatId()))
                    .toList();
        }

        reservations.forEach(r -> {
            r.setStatus(ReservationStatus.RELEASED);
            r.setExpiresAt(null);
        });

        seatReservationRepository.saveAll(reservations);
    }

    public void allocateSeats(AllocateSeatsRequest request) {
        expireOldHolds();

        List<ReservationStatus> allowedStatuses = List.of(ReservationStatus.HELD);

        List<SeatReservation> reservations =
                seatReservationRepository.findByEventIdAndOrderReferenceAndStatusIn(
                        request.getEventId(),
                        request.getOrderReference(),
                        allowedStatuses
                );

        if (request.getSeatIds() != null && !request.getSeatIds().isEmpty()) {
            Set<Long> ids = new HashSet<>(request.getSeatIds());
            reservations = reservations.stream()
                    .filter(r -> ids.contains(r.getSeatId()))
                    .toList();
        }

        if (reservations.isEmpty()) {
            throw new IllegalStateException("No HELD reservations found to allocate.");
        }

        reservations.forEach(r -> {
            r.setStatus(ReservationStatus.RESERVED);
            r.setExpiresAt(null);
        });

        seatReservationRepository.saveAll(reservations);
    }
}

