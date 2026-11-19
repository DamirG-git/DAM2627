package org.finalactivitydg2627;

import org.finalactivitydg2627.controller.AirportController;
import org.finalactivitydg2627.controller.FlightsController;
import org.finalactivitydg2627.controller.PassengerController;
import org.finalactivitydg2627.controller.TicketController;
import org.finalactivitydg2627.database.DBConnection;
import org.finalactivitydg2627.utils.ApplicationLogger;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.sql.SQLException;
import java.time.LocalDate;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.mockito.ArgumentMatchers.contains;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.verify;

public class TestAppE2E {

    private CommandExecutor commandExecutor;
    private ApplicationLogger logger;
    private final TestInput testInputs = new TestInput();


    private final String HELP = """
            Usage: flights [options]
            
            Options:
                -h, --help: show this help
                -a, --add <filename.xml>: add the passengers in the XML file to the database.
                -f, --flights <origin> <destination> <date>: show the flights available from origin to destination
                -b, --buy <customerId> <flightId> <date>: buy a ticket for a flight in a specific date
                -o, --origins: show the origins of the flights
                -d, --destinations <origin>: show the destinations of the flights from origin
            """;

    private static class TestInput {
        public String[] helpCommand = {"--help"};
        public String[] originsCommand = {"--origins"};
        public String addCommand = "--add";
        public String destinationsCommand = "--destinations";
        public String buyCommand = "--buy";
        public String flightsCommand = "--flights";

        public String origin = "BOM";
        public String destination = "DFW";
        public String date = String.valueOf(LocalDate.now());
        public String flightCode = "QR2305";
        public String passport = "Y1234567";

        public final String destinationTestValue = "BOM";
        public final String[] buyTestValues = {"Y1234567", "QR2305", date};
        public final String[] flightsTestValues = {flightsCommand, origin, destination, date};
        public final String[] ticketTestValues = {buyCommand, passport, flightCode, date};
    }

    @BeforeAll
    static void verifyTestDatabase() throws SQLException {
        try (var conn = DBConnection.getInstance().connect()) {
            assertEquals("flights2627_test", conn.getCatalog(),
                    "Tests must run against the test database!");
        }
    }

    @BeforeEach
    void setUp() {
        logger = mock(ApplicationLogger.class);
        commandExecutor = new CommandExecutor(logger);
    }

    // Airport controller START
    @Test
    void getOrigins_success() {
        commandExecutor.executeCommand(testInputs.originsCommand);
        verify(logger).log(contains(AirportController.INFO_FETCHING_ORIGIN_AIRPORTS), eq(ApplicationLogger.LogLevel.INFO));
        verify(logger).log(contains(AirportController.INFO_ORIGIN_AIRPORTS_RETRIEVED), eq(ApplicationLogger.LogLevel.INFO));
    }

    @Test
    void getDestinations_success() {
        String[] input = {testInputs.destinationsCommand, "BOM"};
        commandExecutor.executeCommand(input);
        verify(logger).log(contains(AirportController.INFO_FETCHING_DESTINATION_AIRPORTS), eq(ApplicationLogger.LogLevel.INFO));
        verify(logger).log(contains(AirportController.INFO_RETRIEVED_DESTINATIONS), eq(ApplicationLogger.LogLevel.INFO));
    }

    @Test
    void getDestinations_failure_AirportNotFound() {
        String[] input = {testInputs.destinationsCommand, "XXX"};
        commandExecutor.executeCommand(input);
        verify(logger).log(contains(AirportController.INFO_FETCHING_DESTINATION_AIRPORTS), eq(ApplicationLogger.LogLevel.INFO));
        verify(logger).log(contains(AirportController.WARN_NO_AIRPORT_FOUND_IN_DB), eq(ApplicationLogger.LogLevel.WARNING));
    }

    @Test
    void getDestinations_failure_Verification() {
        String[] input = {testInputs.destinationsCommand, "000"};
        commandExecutor.executeCommand(input);
        verify(logger).log(contains(AirportController.WARN_INVALID_ORIGIN_AIRPORT_IATA), eq(ApplicationLogger.LogLevel.WARNING));
    }
    // Airport controller END

    // Flights controller START
    @Test
    void getFlights_success() {
        commandExecutor.executeCommand(testInputs.flightsTestValues);
        verify(logger).log(contains(FlightsController.INFO_FETCHING_FLIGHTS), eq(ApplicationLogger.LogLevel.INFO));
        verify(logger).log(contains(FlightsController.INFO_FLIGHTS_RETRIEVED), eq(ApplicationLogger.LogLevel.INFO));
    }


    @Test
    void getFlights_failure_Verification() {
        String[] input = {testInputs.flightsTestValues[0], "000", null, null};
        commandExecutor.executeCommand(input);
        verify(logger).log(contains(FlightsController.WARN_INPUT_VALIDATION_FAILED), eq(ApplicationLogger.LogLevel.WARNING));
    }

    @Test
    void getFlights_failure_AirportOriginAndDestinationNotFound() {
        String[] input = {testInputs.flightsCommand, "ORG", "DES", testInputs.date};
        commandExecutor.executeCommand(input);
        verify(logger).log(contains(String.format(FlightsController.WARN_ORIGIN_OR_DESTINATION_NOT_FOUND_FORMAT_ORIGIN_DEST, "ORG", "DES")), eq(ApplicationLogger.LogLevel.WARNING));
    }

    // Flight controller END

    @Test
    void addPassengers_success() throws SQLException {
        // Passenger controller START
        String testXml = "src/test/resources/passengers_test_db_input.xml";
        String[] input = {testInputs.addCommand, testXml};
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
        String[] input = {testInputs.addCommand, "invalid/location/file.txt"};
        commandExecutor.executeCommand(input);
        verify(logger).log(contains(PassengerController.INFO_VALIDATING_XML_FILE), eq(ApplicationLogger.LogLevel.INFO));
        verify(logger).log(contains(PassengerController.WARN_INVALID_XML_FILE_PATH), eq(ApplicationLogger.LogLevel.WARNING));
    }

    @Test
    void addPassengers_failure_failedVerification() {
        String testXmlFail = "src/test/resources/passengers_fail.xml";
        String[] input = {testInputs.addCommand, testXmlFail};
        commandExecutor.executeCommand(input);
        verify(logger).log(contains(PassengerController.INFO_IMPORTING_PASSENGERS_FROM_XML + testXmlFail), eq(ApplicationLogger.LogLevel.INFO));
        verify(logger).log(contains(PassengerController.WARN_XML_PASSENGER_VALIDATION_FAILED), eq(ApplicationLogger.LogLevel.WARNING));
    }

    @Test
    void addPassengers_failure_sqlErrorDuplicateKey() {
        String testXmlFailDuplicate = "src/test/resources/passengers_fail_duplicate.xml";
        String[] input = {testInputs.addCommand, testXmlFailDuplicate};
        commandExecutor.executeCommand(input);
        verify(logger).log(contains(PassengerController.INFO_IMPORTING_PASSENGERS_FROM_XML + testXmlFailDuplicate), eq(ApplicationLogger.LogLevel.INFO));
        verify(logger).log(contains(PassengerController.ERROR_INSERTING_PASSENGER), eq(ApplicationLogger.LogLevel.ERROR));
        verify(logger).log(contains(String.format(PassengerController.WARN_DUPLICATE_PASSENGER_PASSPORT_FORMAT_PASSPORT, "TEST9001")), eq(ApplicationLogger.LogLevel.WARNING));
    }
    //Passenger controller END

    //Tickets controller START

    @Test
    void buyTickets_success() throws SQLException {
        commandExecutor.executeCommand(testInputs.ticketTestValues);
        verify(logger).log(contains(TicketController.INFO_TICKET_PURCHASED), eq(ApplicationLogger.LogLevel.INFO));

        try (var conn = DBConnection.getInstance().connect();
             var stsm = conn.prepareStatement("DELETE from tickets t where t.passportno = 'Y1234567'")) {
            stsm.executeUpdate();
        }
    }

    @Test
    void buyTickets_failure_flightNotExist() {
        String[] input = {testInputs.buyCommand, testInputs.passport, "XXX", testInputs.date};
        commandExecutor.executeCommand(input);
        verify(logger).log(contains(String.format(TicketController.WARN_FLIGHT_NOT_FOUND_FORMAT_IATA, "XXX")), eq(ApplicationLogger.LogLevel.WARNING));
    }

    @Test
    void buyTickets_failure_flightNotFound() {
        String[] input = {testInputs.buyCommand, "XXXTEST", testInputs.flightCode, testInputs.date};
        commandExecutor.executeCommand(input);
        verify(logger).log(contains(String.format(TicketController.WARN_PASSENGER_NOT_FOUND_FORMAT_PASSPORT, "XXXTEST")), eq(ApplicationLogger.LogLevel.WARNING));
    }

    @Test
    void buyTickets_failure_ticketOnSameDate() throws SQLException {
        commandExecutor.executeCommand(testInputs.ticketTestValues);
        verify(logger).log(contains(TicketController.INFO_TICKET_PURCHASED), eq(ApplicationLogger.LogLevel.INFO));
        commandExecutor.executeCommand(testInputs.ticketTestValues);
        verify(logger).log(contains(String.format(
                TicketController.WARN_DUPLICATE_TICKET_PURCHASE_FORMAT, testInputs.passport, testInputs.date)),
                eq(ApplicationLogger.LogLevel.WARNING));

        try (var conn = DBConnection.getInstance().connect();
             var stsm = conn.prepareStatement("DELETE from tickets t where t.passportno = 'Y1234567'")) {
            stsm.executeUpdate();
        }
    }

    //Tickets controller END


}
