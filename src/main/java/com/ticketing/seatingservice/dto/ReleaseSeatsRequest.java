package com.ticketing.seatingservice.dto;

import jakarta.validation.constraints.NotNull;
import lombok.*;

import java.util.List;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class ReleaseSeatsRequest {

    @NotNull
    private Long eventId;

    @NotNull
    private String orderReference;

    /**
     * Optional: if null, release all seats for this order+event.
     */
    private List<Long> seatIds;
}

