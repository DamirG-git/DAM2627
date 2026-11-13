package org.finalactivitydg2627.dto;

public record FlightDTO(
        String flightCode,
        String iataCode,
        String cityName,
        int totalSeats,
        long availableSeats
) {}
