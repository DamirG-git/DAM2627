package org.finalactivitydg2627;

import org.finalactivitydg2627.controller.AirportController;
import org.finalactivitydg2627.controller.FlightsController;
import org.finalactivitydg2627.controller.PassengerController;
import org.finalactivitydg2627.controller.TicketController;
import org.finalactivitydg2627.database.DBConnection;
import org.finalactivitydg2627.utils.ApplicationLogger;
import org.finalactivitydg2627.utils.TicketPrinter;
import org.junit.jupiter.api.AfterAll;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.Mock;

import java.sql.SQLException;
import java.time.LocalDate;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.mockito.ArgumentMatchers.contains;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.verify;

public class TestAppE2E extends AbstractTest {

    @Mock
    private TicketPrinter ticketPrinter;

    private CommandExecutor commandExecutor;
    private static TestInput testInput;

    static class TestInput {
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

        public String origin = "BOM";
        public String destination = "DFW";

        public final LocalDate testDate = LocalDate.now();
        public final String testDateRaw = testDate.toString();
        public final LocalDate testDateTomorrow = testDate.plusDays(1);
        public final String testDateTomorrowRaw = testDateTomorrow.toString();

        public final String[] flightsTestValues = {CommandExecutor.Commands.FLIGHTS, origin, destination, testDateRaw};
        public final String[] ticketBuyTestValues = {CommandExecutor.Commands.BUY, passnportno, flightCode, testDateRaw};
        public final String[] ticketBuyTestValuesPrint = {CommandExecutor.Commands.BUY, passnportno, flightCode, testDateRaw, CommandExecutor.Commands.BUY_PRINT};
        public final String[] ticketCancelTestValues = {CommandExecutor.Commands.CANCEL, passnportno, testDateTomorrowRaw};

        public TestInput() throws SQLException {
            try (var conn = DBConnection.getInstance().connect()) {

                final String SQL_FIND_PASSENGERS = """
                               SELECT 1
                               FROM passengers
                               WHERE passportno IN (?, ?);
                        """;

                try (var stmt = conn.prepareStatement(SQL_FIND_PASSENGERS)) {
                    stmt.setString(1, passnportno);
                    stmt.setString(2, passnportno2);
                    try (var rs = stmt.executeQuery()) {
                        if (!rs.next()) {
                            final String SQL_ADD_PASSENGER = """
                                    INSERT INTO passengers(passportno,firstname,lastname,address,phone) values(?,?,?,?,?);
                                    """;
                            try (var stmt1 = conn.prepareStatement(SQL_ADD_PASSENGER)) {
                                stmt1.setString(1, passnportno);
                                stmt1.setString(2, firstname);
                                stmt1.setString(3, lastname);
                                stmt1.setString(4, address);
                                stmt1.setString(5, phone);
                                stmt1.addBatch();
                                stmt1.setString(1, passnportno2);
                                stmt1.setString(2, firstname2);
                                stmt1.setString(3, lastname2);
                                stmt1.setString(4, address2);
                                stmt1.setString(5, phone2);
                                stmt1.addBatch();
                                stmt1.executeBatch();
                            }
                        }
                    }
                }


                final String SQL_ADD_TICKET = """
                        INSERT INTO tickets
                            (date_of_booking,
                             date_of_travel,
                             date_of_cancellation,
                             passportno,
                             flight_code,
                             price)
                        values(?, ?, null, ?, ?, null);
                        """;
                try (var stmt = conn.prepareStatement(SQL_ADD_TICKET)) {
                    stmt.setObject(1, testDate); // Booking
                    stmt.setObject(2, testDate.plusDays(1)); //Travel
                    stmt.setString(3, passnportno);
                    stmt.setString(4, flightCode);
                    stmt.addBatch();

                    stmt.setString(3, passnportno2);
                    stmt.addBatch();
                    stmt.executeBatch();
                }
            }
        }
    }

    @BeforeAll
    static void firstSetup() throws SQLException {
        try (var conn = DBConnection.getInstance().connect()) {
            assertEquals("flights2627_test", conn.getCatalog(),
                    "Tests must run against the test database!");
        }
        testInput = new TestInput();

    }

    @BeforeEach
    void setUp() {
        commandExecutor = new CommandExecutor(logger, ticketPrinter);
    }

    @AfterAll
    static void cleanupTickets() throws SQLException {
        try (var conn = DBConnection.getInstance().connect()) {
            try (var stmt = conn.prepareStatement("DELETE FROM tickets WHERE passportno in (?,?)")) {
                stmt.setString(1, testInput.passnportno);
                stmt.setString(2, testInput.passnportno2);
                stmt.executeUpdate();
            }

            try (var stmt = conn.prepareStatement("""
                    DELETE FROM passengers
                    WHERE passportno IN (?, ?)
                    """)) {
                stmt.setString(1, testInput.passnportno);
                stmt.setString(2, testInput.passnportno2);
                stmt.executeUpdate();
            }
        }


    }

    // Airport controller START
    @Test
    void getOrigins_success() {
        commandExecutor.executeCommand(new String[]{CommandExecutor.Commands.ORIGINS});
        verify(logger).log(contains(AirportController.INFO_FETCHING_ORIGIN_AIRPORTS), eq(ApplicationLogger.LogLevel.INFO));
        verify(logger).log(contains(AirportController.INFO_ORIGIN_AIRPORTS_RETRIEVED), eq(ApplicationLogger.LogLevel.INFO));
    }

    @Test
    void getDestinations_success() {
        String[] input = {CommandExecutor.Commands.DESTINATIONS, testInput.origin};
        commandExecutor.executeCommand(input);
        verify(logger).log(contains(AirportController.INFO_FETCHING_DESTINATION_AIRPORTS), eq(ApplicationLogger.LogLevel.INFO));
        verify(logger).log(contains(AirportController.INFO_RETRIEVED_DESTINATIONS), eq(ApplicationLogger.LogLevel.INFO));
    }

    @Test
    void getDestinations_failure_AirportNotFound() {
        var invalidOrigin = "XXX";
        String[] input = {CommandExecutor.Commands.DESTINATIONS, invalidOrigin};
        commandExecutor.executeCommand(input);
        verify(logger).log(contains(AirportController.INFO_FETCHING_DESTINATION_AIRPORTS), eq(ApplicationLogger.LogLevel.INFO));
        verify(logger).log(contains(AirportController.WARN_NO_AIRPORT_FOUND_IN_DB), eq(ApplicationLogger.LogLevel.WARNING));
    }

    @Test
    void getDestinations_failure_Verification() {
        var invalidOrigin = "000";
        String[] input = {CommandExecutor.Commands.DESTINATIONS, invalidOrigin};
        commandExecutor.executeCommand(input);
        verify(logger).log(contains(AirportController.WARN_INVALID_ORIGIN_AIRPORT_IATA), eq(ApplicationLogger.LogLevel.WARNING));
    }
// Airport controller END

    // Flights controller START
    @Test
    void getFlights_success() {
        commandExecutor.executeCommand(testInput.flightsTestValues);
        verify(logger).log(contains(FlightsController.INFO_FETCHING_FLIGHTS), eq(ApplicationLogger.LogLevel.INFO));
        verify(logger).log(contains(FlightsController.INFO_FLIGHTS_RETRIEVED), eq(ApplicationLogger.LogLevel.INFO));
    }


    @Test
    void getFlights_failure_Verification() {
        var invalidOrigin = "000";
        String[] input = {testInput.flightsTestValues[0], invalidOrigin, null, null};
        commandExecutor.executeCommand(input);
        verify(logger).log(contains(FlightsController.WARN_INPUT_VALIDATION_FAILED), eq(ApplicationLogger.LogLevel.WARNING));
    }

    @Test
    void getFlights_failure_AirportOriginAndDestinationNotFound() {
        var invalidOrigin = "ORG";
        var invalidDestination = "DES";
        String[] input = {CommandExecutor.Commands.FLIGHTS, invalidOrigin, invalidDestination, testInput.testDateRaw};
        commandExecutor.executeCommand(input);
        verify(logger).log(contains(String.format(FlightsController.WARN_ORIGIN_OR_DESTINATION_NOT_FOUND_FORMAT_ORIGIN_DEST, invalidOrigin, invalidDestination)), eq(ApplicationLogger.LogLevel.WARNING));
    }

// Flight controller END

    @Test
    void addPassengers_success() throws SQLException {
        // Passenger controller START
        String testXml = "src/test/resources/passengers_test_db_input.xml";
        String[] input = {CommandExecutor.Commands.ADD, testXml};
        commandExecutor.executeCommand(input);
        verify(logger).log(contains(PassengerController.INFO_VALIDATING_XML_FILE), eq(ApplicationLogger.LogLevel.INFO));
        verify(logger).log(contains(PassengerController.INFO_IMPORTING_PASSENGERS_FROM_XML), eq(ApplicationLogger.LogLevel.INFO));
        verify(logger).log(contains(PassengerController.INFO_PASSENGERS_IMPORTED + 3), eq(ApplicationLogger.LogLevel.INFO));

        try (var conn = DBConnection.getInstance().connect();
             var stmt = conn.prepareStatement("DELETE from passengers p where p.passportno IN ('TEST9001','TEST9002','TEST9003')")) {
            stmt.executeUpdate();
        }
    }

    @Test
    void addPassengers_invalidXmlFile() {
        String[] input = {CommandExecutor.Commands.ADD, "invalid/location/file.txt"};
        commandExecutor.executeCommand(input);
        verify(logger).log(contains(PassengerController.INFO_VALIDATING_XML_FILE), eq(ApplicationLogger.LogLevel.INFO));
        verify(logger).log(contains(PassengerController.WARN_INVALID_XML_FILE_PATH), eq(ApplicationLogger.LogLevel.WARNING));
    }

    @Test
    void addPassengers_failure_failedVerification() {
        String testXmlFail = "src/test/resources/passengers_fail.xml";
        String[] input = {CommandExecutor.Commands.ADD, testXmlFail};
        commandExecutor.executeCommand(input);
        verify(logger).log(contains(PassengerController.INFO_IMPORTING_PASSENGERS_FROM_XML + testXmlFail), eq(ApplicationLogger.LogLevel.INFO));
        verify(logger).log(contains(PassengerController.WARN_XML_PASSENGER_VALIDATION_FAILED), eq(ApplicationLogger.LogLevel.WARNING));
    }

    @Test
    void addPassengers_failure_sqlErrorDuplicateKey() {
        String testXmlFailDuplicate = "src/test/resources/passengers_fail_duplicate.xml";
        String[] input = {CommandExecutor.Commands.ADD, testXmlFailDuplicate};
        commandExecutor.executeCommand(input);
        verify(logger).log(contains(PassengerController.INFO_IMPORTING_PASSENGERS_FROM_XML + testXmlFailDuplicate), eq(ApplicationLogger.LogLevel.INFO));
        verify(logger).log(contains(PassengerController.ERROR_INSERTING_PASSENGER), eq(ApplicationLogger.LogLevel.ERROR));
        verify(logger).log(contains(String.format(PassengerController.WARN_DUPLICATE_PASSENGER_PASSPORT_FORMAT_PASSPORT, "TEST9001")), eq(ApplicationLogger.LogLevel.WARNING));
    }
//Passenger controller END

//Tickets controller START

    void cleanUpTicket() throws SQLException {
        try (var conn = DBConnection.getInstance().connect();
             var stsm = conn.prepareStatement("DELETE from tickets t where t.passportno = ? and t.date_of_travel = ?")) {
            stsm.setString(1, testInput.passnportno);
            stsm.setObject(2, testInput.testDate);
            stsm.executeUpdate();
        }
    }
    @Test
    void buyTickets_success() throws SQLException {
        commandExecutor.executeCommand(testInput.ticketBuyTestValues);
        verify(logger).log(contains(TicketController.INFO_TICKET_PURCHASED), eq(ApplicationLogger.LogLevel.INFO));
        cleanUpTicket();

    }

    @Test
    void buyTickets_success_print() throws SQLException {
        commandExecutor.executeCommand(testInput.ticketBuyTestValuesPrint);
        verify(logger).log(contains(TicketController.INFO_TICKET_PURCHASED), eq(ApplicationLogger.LogLevel.INFO));
        verify(logger).log(contains(TicketController.INFO_PRINTING_TICKET), eq(ApplicationLogger.LogLevel.INFO));
        cleanUpTicket();
    }

    @Test
    void buyTickets_failure_flightNotExist() {
        var invalidFlight = "XXX";
        String[] input = {CommandExecutor.Commands.BUY, testInput.passnportno, invalidFlight, testInput.testDateRaw};
        commandExecutor.executeCommand(input);
        verify(logger).log(contains(String.format(TicketController.WARN_FLIGHT_NOT_FOUND_FORMAT_IATA, invalidFlight)), eq(ApplicationLogger.LogLevel.WARNING));
    }

    @Test
    void buyTickets_failure_passengerNotFound() {
        var invalidPassport = "XXXTEST";
        String[] input = {CommandExecutor.Commands.BUY, invalidPassport, testInput.flightCode, testInput.testDateRaw};
        commandExecutor.executeCommand(input);
        verify(logger).log(contains(String.format(TicketController.WARN_PASSENGER_NOT_FOUND_FORMAT_PASSPORT, invalidPassport)), eq(ApplicationLogger.LogLevel.WARNING));
    }


    @Test
    void buyTickets_failure_ticketOnSameDate() throws SQLException {
        commandExecutor.executeCommand(testInput.ticketBuyTestValues);
        verify(logger).log(contains(TicketController.INFO_TICKET_PURCHASED), eq(ApplicationLogger.LogLevel.INFO));
        commandExecutor.executeCommand(testInput.ticketBuyTestValues);
        verify(logger).log(contains(String.format(
                        TicketController.WARN_DUPLICATE_TICKET_PURCHASE_FORMAT, testInput.passnportno, testInput.testDateRaw)),
                eq(ApplicationLogger.LogLevel.WARNING));
        cleanUpTicket();
    }

    @Test
    void cancelTickets_success() throws SQLException {
        commandExecutor.executeCommand(testInput.ticketCancelTestValues);
        verify(logger).log(contains(TicketController.INFO_TICKET_CANCELLED), eq(ApplicationLogger.LogLevel.INFO));

        try (var conn = DBConnection.getInstance().connect();
             var stsm = conn.prepareStatement("UPDATE tickets SET date_of_cancellation = null where passportno = ?")) {
            stsm.setString(1, testInput.passnportno);
            stsm.executeUpdate();
        }
    }

    @Test
    void cancelTickets_failure_ticketAlreadyCancelled() throws SQLException {
        commandExecutor.executeCommand(testInput.ticketCancelTestValues);
        verify(logger).log(contains(TicketController.INFO_TICKET_CANCELLED), eq(ApplicationLogger.LogLevel.INFO));

        commandExecutor.executeCommand(testInput.ticketCancelTestValues);
        verify(logger).log(contains(TicketController.WARN_TICKET_ALREADY_CANCELLED), eq(ApplicationLogger.LogLevel.WARNING));

        try (var conn = DBConnection.getInstance().connect();
             var stsm = conn.prepareStatement("UPDATE tickets SET date_of_cancellation = null where passportno = ?")) {
            stsm.setString(1, testInput.passnportno);
            stsm.executeUpdate();
        }
    }

    @Test
    void cancelTickets_failure_passengerNotFound() {
        var passport = "XXXTEST";
        commandExecutor.executeCommand(new String[]{CommandExecutor.Commands.CANCEL, passport, testInput.testDateTomorrowRaw});
        verify(logger).log(contains(String.format(TicketController.WARN_PASSENGER_NOT_FOUND_FORMAT_PASSPORT, passport)), eq(ApplicationLogger.LogLevel.WARNING));
    }

    @Test
    void cancelTickets_failure_ticketNotFound() {
        commandExecutor.executeCommand(new String[]{CommandExecutor.Commands.CANCEL, testInput.passnportno, testInput.testDateRaw});
        verify(logger).log(contains(TicketController.WARN_TICKET_NOT_FOUND + testInput.testDateRaw), eq(ApplicationLogger.LogLevel.WARNING));
    }
//Tickets controller END


}
