package com.ticketing.seatingservice.entity;


public enum ReservationStatus {
    HELD,       // temporary hold (up to 15 minutes)
    RESERVED,   // final allocation after payment success
    RELEASED,   // explicitly released (cancel/fail)
    EXPIRED     // auto-expired after TTL
}

