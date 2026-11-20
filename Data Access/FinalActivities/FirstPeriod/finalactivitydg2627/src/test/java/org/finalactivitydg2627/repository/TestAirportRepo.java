package org.finalactivitydg2627.repository;

import org.finalactivitydg2627.AbstractTest;
import org.finalactivitydg2627.database.DBConnection;
import org.finalactivitydg2627.utils.ApplicationLogger;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.sql.SQLException;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.mock;

public class TestAirportRepo  extends AbstractTest {


    private AirportRepo airportRepo;

    @BeforeAll
    static void verifyTestDatabase() throws SQLException {
        try (var conn = DBConnection.getInstance().connect()) {
            assertEquals("flights2627_test", conn.getCatalog());
        }
    }

    @BeforeEach
    void setUp() {
        var connection = DBConnection.getInstance();
        airportRepo = new AirportRepo(connection,logger);
    }

    @Test
    void getOriginAirports_returns() throws SQLException {

        var results = airportRepo.getOriginAirports();
        assertNotNull(results);
        assertFalse(results.isEmpty());

        for(var airport : results) {
            assertNotNull(airport.getAirportIata());
            assertNotNull(airport.getAirportName());
            assertNotNull(airport.getCityName());
            assertNotNull(airport.getFlightCode());
        }
    }

    @Test
    void getDestinationAirports_success() throws SQLException {
        var results = airportRepo.getDestinationAirports("BOM");
        assertNotNull(results);
        assertSame(AirportRepo.AirportStatus.FOUND, results.codeEnum());
        assertFalse(results.value().isEmpty());
    }

    @Test
    void getDestinationAirports_originNotFoundInDB() throws SQLException {
        var results = airportRepo.getDestinationAirports("NON");
        assertNotNull(results);
        assertSame(AirportRepo.AirportStatus.NOT_FOUND, results.codeEnum());
        assertNull(results.value());
    }

    @Test
    void getDestinationAirports_noDestinationsFound() throws SQLException {
        var results = airportRepo.getDestinationAirports("DFW");
        assertNotNull(results);
        assertSame(AirportRepo.AirportStatus.FOUND, results.codeEnum());
        assertTrue(results.value().isEmpty());
    }
}
