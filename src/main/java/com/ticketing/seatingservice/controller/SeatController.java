package com.ticketing.seatingservice.controller;

import com.ticketing.seatingservice.dto.*;
import com.ticketing.seatingservice.service.SeatReservationService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/v1/seats")
@RequiredArgsConstructor
public class SeatController {

    private final SeatReservationService reservationService;

    /**
     * Seat availability for an event.
     * GET /v1/seats?eventId=123
     * Returns active HELD/RESERVED seats for this event.
     */
    @GetMapping
    public List<SeatAvailabilityDto> getSeatStatus(
            @RequestParam Long eventId
    ) {
        return reservationService.getSeatStatusForEvent(eventId);
    }

    /**
     * Reserve seats (temporary hold).
     * POST /v1/seats/reserve
     */
    @PostMapping("/reserve")
    public ReserveSeatsResponse reserveSeats(
            @RequestBody @Valid ReserveSeatsRequest request
    ) {
        return reservationService.reserveSeats(request);
    }

    /**
     * Release seats (on cancel or payment failure).
     * POST /v1/seats/release
     */
    @PostMapping("/release")
    public void releaseSeats(
            @RequestBody @Valid ReleaseSeatsRequest request
    ) {
        reservationService.releaseSeats(request);
    }

    /**
     * Allocate seats after successful payment.
     * POST /v1/seats/allocate
     */
    @PostMapping("/allocate")
    public void allocateSeats(
            @RequestBody @Valid AllocateSeatsRequest request
    ) {
        reservationService.allocateSeats(request);
    }
}

