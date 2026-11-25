package org.finalactivitydg2627.repository;

import org.finalactivitydg2627.AbstractTest;
import org.finalactivitydg2627.database.DBConnection;
import org.finalactivitydg2627.utils.StoredProceduresSQL;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.sql.SQLException;
import java.time.LocalDate;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;

public class TestTicketRepo extends AbstractTest {


    private TicketRepo ticketRepo;

    private static TestData testData;


    static class TestData {
        private static final String SQL_ADD_PASSENGER = """
                INSERT INTO passengers(passportno,firstname,lastname,address,phone) values(?,?,?,?,?);
                """;
        private static final String SQL_FIND_PASSENGERS = """
                       SELECT 1
                       FROM passengers
                       WHERE passportno IN (?, ?);
        """;
        public final String passnportno = "TST10001";
        public final String firstname = "John";
        public final String lastname = "Test";
        public final String address = "Spain";
        public final String phone = "Test phone";
        public final String flightCode = "9W2334";

        public final String passnportno2 = "TST10002";
        public final String firstname2 = "John";
        public final String lastname2 = "Example";
        public final String address2 = "America";
        public final String phone2 = "Le phone";
        public final String flightCode2 = "QR2305";

        public final String invalidFlightCode = "INVALID";
        public final String invalidPassportNo = "INVALID";
        public final LocalDate testDate = LocalDate.now();

        public TestData() throws SQLException {
            try (var conn = DBConnection.getInstance().connect()) {

                try(var stmt = conn.prepareStatement(SQL_FIND_PASSENGERS)) {
                    stmt.setString(1, passnportno);
                    stmt.setString(2, passnportno2);
                    try (var rs = stmt.executeQuery()) {
                        if (rs.next()) {
                            return;
                        }
                    }
                }

                try (var stmt = conn.prepareStatement(SQL_ADD_PASSENGER)) {
                    stmt.setString(1, passnportno);
                    stmt.setString(2, firstname);
                    stmt.setString(3, lastname);
                    stmt.setString(4, address);
                    stmt.setString(5, phone);
                    stmt.addBatch();
                    stmt.setString(1, passnportno2);
                    stmt.setString(2, firstname2);
                    stmt.setString(3, lastname2);
                    stmt.setString(4, address2);
                    stmt.setString(5, phone2);
                    stmt.addBatch();
                    stmt.executeBatch();
                }
            }
        }
    }


    @BeforeAll
    static void verifyTestDatabase() throws SQLException {
        try (var conn = DBConnection.getInstance().connect()) {
            assertEquals("flights2627_test", conn.getCatalog());
            testData = new TestData();
        }

        new StoredProceduresSQL().initialize();
    }

    @BeforeEach
    void setUp() {
        var connection = DBConnection.getInstance();
        ticketRepo = new TicketRepo(connection, logger);
    }

    @AfterEach
    void tearDown() throws SQLException {
        cleanTestTickets();
    }

    private void cleanTestTickets() throws SQLException {
        try (var conn = DBConnection.getInstance().connect();
             var stmt = conn.prepareStatement(
                     "DELETE FROM tickets WHERE passportno IN (?, ?)")) {

            stmt.setString(1, testData.passnportno);
            stmt.setString(2, testData.passnportno2);

            stmt.executeUpdate();
        }
    }

    @Test
        //-b Y1234567 QR2305 2026-12-16
    void buyTicket_success() throws SQLException {
        var result = ticketRepo.buyTicket(testData.passnportno, testData.flightCode2, testData.testDate);
        assertNotNull(result);
        assertEquals(TicketRepo.TicketPurchaseResult.SUCCESS, result.ticketPurchaseResult);
    }

    @Test
    void buyTicket_failure_flightNotFound() throws SQLException {
        var flightCode = testData.invalidFlightCode;
        var result = ticketRepo.buyTicket(testData.passnportno, flightCode, testData.testDate);
        assertNotNull(result);
        assertEquals(TicketRepo.TicketPurchaseResult.FLIGHT_NOT_FOUND, result.ticketPurchaseResult);
    }

    @Test
    void buyTicket_failure_passengerNotFound() throws SQLException {
        var passport = testData.invalidPassportNo;
        var result = ticketRepo.buyTicket(passport, testData.flightCode, testData.testDate);
        assertNotNull(result);
        assertEquals(TicketRepo.TicketPurchaseResult.PASSENGER_DOES_NOT_EXIST, result.ticketPurchaseResult);
    }

    @Test
    void buyTicket_failure_ticketOnSameDate() throws SQLException {
        var passport = testData.passnportno;
        var flightCode = testData.flightCode;
        var date = testData.testDate;

        var result = ticketRepo.buyTicket(passport, flightCode, date);
        assertNotNull(result);
        assertEquals(TicketRepo.TicketPurchaseResult.SUCCESS, result.ticketPurchaseResult);
        var result2 = ticketRepo.buyTicket(passport, flightCode, date);
        assertNotNull(result2);
        assertEquals(TicketRepo.TicketPurchaseResult.PASSENGER_ALREADY_HAS_TICKET_ON_SAME_DATE, result2.ticketPurchaseResult);
    }

    @Test
    void buyTicket_failure_noFreeSeats() throws SQLException {
        try (var conn = DBConnection.getInstance().connect()) {


            var passport = testData.passnportno;
            var passport2 = testData.passnportno2;
            var flightCode = "EY1234"; //Id 1
            var date = testData.testDate;

            try (var stmt = conn.prepareStatement("UPDATE planes p SET total_seats = ? where p.plane_id = ?; ")) {
                stmt.setInt(1, 1);
                stmt.setInt(2, 1);
                stmt.executeUpdate();

                var result = ticketRepo.buyTicket(passport, flightCode, date);
                assertNotNull(result);
                assertEquals(TicketRepo.TicketPurchaseResult.SUCCESS, result.ticketPurchaseResult);

                var result2 = ticketRepo.buyTicket(passport2, flightCode, date);
                assertNotNull(result2);
                assertEquals(TicketRepo.TicketPurchaseResult.NO_SEATS_AVAILABLE, result2.ticketPurchaseResult);

                stmt.setInt(1, 90);
                stmt.executeUpdate();
            }
        }
    }

    @Test
    void cancelTicket_success() throws SQLException {
        var result = ticketRepo.buyTicket(testData.passnportno, testData.flightCode2, testData.testDate);
        assertNotNull(result);
        assertEquals(TicketRepo.TicketPurchaseResult.SUCCESS, result.ticketPurchaseResult);

        var cancelResult = ticketRepo.cancelTicket(testData.passnportno,testData.testDate);
        assertNotNull(cancelResult);
        assertEquals(TicketRepo.TicketCancellationResult.SUCCESS, cancelResult);
    }

    @Test
    void cancelTicket_failure_noPassengerFound() throws SQLException {
        var cancelResult = ticketRepo.cancelTicket(testData.invalidPassportNo,testData.testDate);
        assertNotNull(cancelResult);
        assertEquals(TicketRepo.TicketCancellationResult.PASSENGER_NOT_FOUND, cancelResult);
    }

    @Test
    void cancelTicket_failure_noTicketFound() throws SQLException {
        var cancelResult = ticketRepo.cancelTicket(testData.passnportno,testData.testDate);
        assertNotNull(cancelResult);
        assertEquals(TicketRepo.TicketCancellationResult.TICKET_NOT_FOUND, cancelResult);
    }

    @Test
    void cancelTicket_failure_ticketAlreadyCancelled() throws SQLException {
        var result = ticketRepo.buyTicket(testData.passnportno, testData.flightCode2, testData.testDate);
        assertNotNull(result);
        assertEquals(TicketRepo.TicketPurchaseResult.SUCCESS, result.ticketPurchaseResult);

        var cancelResult = ticketRepo.cancelTicket(testData.passnportno,testData.testDate);
        assertNotNull(cancelResult);
        assertEquals(TicketRepo.TicketCancellationResult.SUCCESS, cancelResult);

        var cancelFail = ticketRepo.cancelTicket(testData.passnportno,testData.testDate);
        assertNotNull(cancelFail);
        assertEquals(TicketRepo.TicketCancellationResult.TICKET_ALREADY_CANCELLED, cancelFail);
    }


}
