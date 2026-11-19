package org.finalactivitydg2627.repository;

import org.finalactivitydg2627.database.DBConnection;
import org.finalactivitydg2627.utils.ApplicationLogger;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.sql.SQLException;
import java.time.LocalDate;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.mock;

public class TestFlightsRepo {

    private FlightsRepo flightsRepo;

    @BeforeAll
    static void verifyTestDatabase() throws SQLException {
        try (var conn = DBConnection.getInstance().connect()) {
            assertEquals("flights2627_test", conn.getCatalog());
        }
    }

    @BeforeEach
    void setUp() {
        var logger = mock(ApplicationLogger.class);
        var connection = DBConnection.getInstance();
        flightsRepo = new FlightsRepo(connection, logger);

    }


    private static final LocalDate date = LocalDate.now();
    private static final String origin = "BOM";
    private static final String destination = "DFW";
    private static final String invalid = "INVALID";

    @Test
    void getFlights_success() throws SQLException {
        var result = flightsRepo.getFlights(
                origin,
                destination, date);
        assertNotNull(result);
        assertNotNull(result.value());
        assertEquals(FlightsRepo.AirportResult.SUCCESS, result.codeEnum());
        for (var airport : result.value()) {
            assertNotNull(airport);
            assertNotNull(airport.getFlightCode());
            assertNotNull(airport.getIataCode());
            assertNotNull(airport.getCityName());

            assertTrue(airport.getTotalSeats() > 0);
            assertTrue(airport.getFreeSeats() >= 0);
            assertTrue(airport.getFreeSeats() <= airport.getTotalSeats());
        }
    }

    @Test
    void getFlights_failure_noOriginOrDestinationFound() throws SQLException {

        var result = flightsRepo.getFlights(
                invalid,
                invalid, date);
        assertNotNull(result);
        assertNull(result.value());
        assertEquals(FlightsRepo.AirportResult.NO_ORIGIN_OR_DESTINATION_FOUND, result.codeEnum());
    }

    @Test
    void getFlights_failure_noOriginFound() throws SQLException {

        var result = flightsRepo.getFlights(
                invalid,
                origin, date);
        assertNotNull(result);
        assertNull(result.value());
        assertEquals(FlightsRepo.AirportResult.NO_ORIGIN_AIRPORT_FOUND, result.codeEnum());
    }

    @Test
    void getFlights_failure_noDestinationFound() throws SQLException {

        var result = flightsRepo.getFlights(
                origin,
                invalid, date);
        assertNotNull(result);
        assertNull(result.value());
        assertEquals(FlightsRepo.AirportResult.NO_DESTINATION_AIRPORT_FOUND, result.codeEnum());
    }

}
