package com.ticketing.seatingservice.dto;

import lombok.*;

import java.time.LocalDateTime;
import java.util.List;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class ReserveSeatsResponse {

    private Long eventId;
    private String orderReference;
    private List<Long> reservedSeatIds;
    private LocalDateTime expiresAt;
}

