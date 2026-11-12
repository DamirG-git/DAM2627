package org.finalactivitydg2627.dto;

public record FlightDTO(
        String flightCode,
        int totalSeats,
        long availableSeats
) {}
