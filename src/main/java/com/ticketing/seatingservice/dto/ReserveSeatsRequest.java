package com.ticketing.seatingservice.dto;


import jakarta.validation.constraints.NotEmpty;
import jakarta.validation.constraints.NotNull;
import lombok.*;

import java.util.List;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class ReserveSeatsRequest {

    @NotNull
    private Long eventId;

    @NotEmpty
    private List<Long> seatIds;

    /**
     * Provided by Order Service (e.g. "ORDER-123").
     */
    @NotNull
    private String orderReference;

    /**
     * Optional TTL override; default 15 minutes if null or <= 0.
     */
    private Integer ttlMinutes;
}

