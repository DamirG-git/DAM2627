package org.finalactivitydg2627.repository;

import org.finalactivitydg2627.Utils.ApplicationLogger;
import org.finalactivitydg2627.database.DBConnection;
import org.finalactivitydg2627.model.Passengers;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.sql.SQLException;
import java.sql.Time;
import java.time.LocalDate;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

public class TestTicketRepo {


    private TicketRepo ticketRepo;
    private static final Passengers test1 = new Passengers("TEST001", "John", "Smith",
            "Test Street 1", "111111111");
    private static final Passengers test2 = new Passengers("TEST002", "Jane", "Doe",
            "Test Street 2", "222222222");
    private static final LocalDate testDate = LocalDate.now();

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
        ticketRepo = new TicketRepo(connection, logger);
    }

    @Test
        //-b Y1234567 QR2305 2026-12-16
    void buyTicket_success() throws SQLException {
        var result = ticketRepo.buyTicket("Y1234567", "QR2305", testDate);
        assertNotNull(result);
        assertEquals(TicketRepo.TicketPurchaseResult.SUCCESS, result);
        try (var conn = DBConnection.getInstance().connect(); var stmt = conn.prepareStatement("DELETE FROM tickets t where t.passportno = ? and t.date_of_travel = ?")) {
            stmt.setString(1, "Y1234567");
            stmt.setObject(2, testDate);
            stmt.executeUpdate();
        }
    }

    @Test
    void buyTicket_failure_flightNotFound() throws SQLException {

        var result = ticketRepo.buyTicket("Y1234567", "INVALID", testDate);
        assertNotNull(result);
        assertEquals(TicketRepo.TicketPurchaseResult.FLIGHT_NOT_FOUND, result);
    }

    @Test
    void buyTicket_failure_passengerNotFound() throws SQLException {
        var result = ticketRepo.buyTicket("INVALID", "QR2305", testDate);
        assertNotNull(result);
        assertEquals(TicketRepo.TicketPurchaseResult.PASSENGER_DOES_NOT_EXIST, result);
    }

    @Test
    void buyTicket_failure_noFlightCodeFound() throws SQLException {
        var result = ticketRepo.buyTicket("Y1234567", "QR2305", testDate);
        var result2 = ticketRepo.buyTicket("Y1234567", "9W2334", testDate);
        assertNotNull(result);
        assertEquals(TicketRepo.TicketPurchaseResult.SUCCESS, result);
        assertEquals(TicketRepo.TicketPurchaseResult.PASSENGER_ALREADY_HAS_TICKET_ON_SAME_DATE, result2);
        try (var conn = DBConnection.getInstance().connect(); var pstm = conn.prepareStatement("DELETE FROM tickets t where t.passportno = ? and t.date_of_travel = ?")) {
            pstm.setString(1, "Y1234567");
            pstm.setObject(2, testDate);
            pstm.executeUpdate();
        }
    }

    @Test
    void buyTicket_failure_noFreeSeats() throws SQLException {
        try (var conn = DBConnection.getInstance().connect()) {
            try (var stmt = conn.prepareStatement("UPDATE planes p SET total_seats = ? where p.plane_id = ?; ")) {
                stmt.setInt(1, 1);
                stmt.setInt(2, 1);
                stmt.executeUpdate();

                var result = ticketRepo.buyTicket("Y1234567", "EY1234", testDate);
                assertNotNull(result);
                assertEquals(TicketRepo.TicketPurchaseResult.SUCCESS, result);

                var result2 = ticketRepo.buyTicket("W7543336", "EY1234", testDate);
                assertNotNull(result2);
                assertEquals(TicketRepo.TicketPurchaseResult.NO_SEATS_AVAILABLE, result2);

                stmt.setInt(2, 90);
            }
            try(var stmt = conn.prepareStatement("DELETE FROM tickets t where t.passportno = ? and  t.date_of_travel = ?; ")) {
                stmt.setString(1, "Y1234567");
                stmt.setObject(2, testDate);
                stmt.executeUpdate();
            }
        }
    }
}
