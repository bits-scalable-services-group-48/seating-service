package com.ticketing.seatingservice.dto;


import com.ticketing.seatingservice.entity.ReservationStatus;
import lombok.*;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class SeatAvailabilityDto {

    private Long eventId;
    private Long seatId;
    private ReservationStatus status;
    private String orderReference;
}

