package org.finalactivitydg2627.repository;

import org.finalactivitydg2627.AbstractTest;
import org.finalactivitydg2627.database.DBConnection;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.sql.SQLException;
import java.time.LocalDate;

import static org.junit.jupiter.api.Assertions.*;

public class TestFlightsRepo extends AbstractTest {

    private FlightsRepo flightsRepo;

    @BeforeAll
    static void verifyTestDatabase() throws SQLException {
        try (var conn = DBConnection.getInstance().connect()) {
            assertEquals("flights2627_test", conn.getCatalog());
        }
    }

    @BeforeEach
    void setUp() {
        var connection = DBConnection.getInstance();
        flightsRepo = new FlightsRepo(connection, logger);

    }


    private static final LocalDate date = LocalDate.now();
    private static final String ORIGIN = "BOM";
    private static final String DESTINATION = "DFW";
    private static final String INVALID = "INVALID";

    @Test
    void getFlights_success() throws SQLException {
        var result = flightsRepo.getFlights(
                ORIGIN,
                DESTINATION, date);
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
                INVALID,
                INVALID, date);
        assertNotNull(result);
        assertNull(result.value());
        assertEquals(FlightsRepo.AirportResult.NO_ORIGIN_OR_DESTINATION_FOUND, result.codeEnum());
    }

    @Test
    void getFlights_failure_noOriginFound() throws SQLException {

        var result = flightsRepo.getFlights(
                INVALID,
                ORIGIN, date);
        assertNotNull(result);
        assertNull(result.value());
        assertEquals(FlightsRepo.AirportResult.NO_ORIGIN_AIRPORT_FOUND, result.codeEnum());
    }

    @Test
    void getFlights_failure_noDestinationFound() throws SQLException {

        var result = flightsRepo.getFlights(
                ORIGIN,
                INVALID, date);
        assertNotNull(result);
        assertNull(result.value());
        assertEquals(FlightsRepo.AirportResult.NO_DESTINATION_AIRPORT_FOUND, result.codeEnum());
    }

}
