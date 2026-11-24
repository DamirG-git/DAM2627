package org.finalactivitydg2627;

import org.finalactivitydg2627.controller.AirportController;
import org.finalactivitydg2627.controller.FlightsController;
import org.finalactivitydg2627.controller.PassengerController;
import org.finalactivitydg2627.controller.TicketController;
import org.finalactivitydg2627.utils.ApplicationLogger;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import static org.mockito.Mockito.*;

public class TestCommandExecutor {

     ApplicationLogger logger;
     CommandExecutor commandExecutor;
     final TestInput testInput = new TestInput();

     AirportController airportController;
     FlightsController flightsController;
     PassengerController passengerController;
     TicketController ticketController;

            /*
            Usage: flights [options]

            Options:
                -h, --help: show this help
                -a, --add <filename.xml>: add the passengers in the XML file to the database.
                -f, --flights <origin> <destination> <date>: show the flights available from origin to destination
                -b, --buy <customerId> <flightId> <date>: buy a ticket for a flight in a specific date
                -c, --cancel <customerId> <date>: cancel ticket on specific date
                -o, --origins: show the origins of the flights
                -d, --destinations <origin>: show the destinations of the flights from origin
            */

    static class TestInput {
        public final String destinationTestValue = "DEST";
        public final String addTestValue = "ADD";
        public final String[] buyTestValues = {"TEST PASSPORT", "TESTFLIGHT", "TEST DATE"};
        public final String[] flightsTestValue = {"TEST ORIGIN", "DESTINATION", "TEST DATE"};
    }

    @BeforeEach
    void setUp() {
        logger = mock(ApplicationLogger.class);
        ticketController = mock(TicketController.class);
        airportController = mock(AirportController.class);
        flightsController = mock(FlightsController.class);
        passengerController = mock(PassengerController.class);

        commandExecutor = new CommandExecutor(
                logger, airportController, flightsController, passengerController, ticketController);
    }

    @Test
    void executeCommand_help_success() {
        commandExecutor.executeCommand(new String[]{CommandExecutor.Commands.HELP});
        verify(logger).log(contains(CommandExecutor.HELP_MESSAGE), eq(ApplicationLogger.LogLevel.INFO));
    }

    @Test
    void executeCommand_origins_success() {
        commandExecutor.executeCommand(new String[]{CommandExecutor.Commands.ORIGINS});
        verify(airportController).displayOriginAirports();
    }

    @Test
    void executeCommand_destinations_success() {
        String[] input = {CommandExecutor.Commands.DESTINATIONS, testInput.destinationTestValue};
        commandExecutor.executeCommand(input);
        verify(airportController).displayDestinationAirports(input[1]);
    }

    @Test
    void executeCommand_destination_failure() {
        String[] input = {CommandExecutor.Commands.DESTINATIONS};
        commandExecutor.executeCommand(input);
        verify(airportController, never()).displayDestinationAirports(anyString());
        verify(logger).log(contains(CommandExecutor.DEST_INSTR), eq(ApplicationLogger.LogLevel.WARNING));
    }

    @Test
    void executeCommand_flights_success() {
        String[] input = {CommandExecutor.Commands.FLIGHTS,
                testInput.flightsTestValue[0], //origin
                testInput.flightsTestValue[1], //destination
                testInput.flightsTestValue[2]}; //date
        commandExecutor.executeCommand(input);
        verify(flightsController).displayFlights(input);
    }

    @Test
    void executeCommand_flights_failure() {
        String[] input = {CommandExecutor.Commands.FLIGHTS};
        commandExecutor.executeCommand(input);
        verify(flightsController, never()).displayFlights(any());
        verify(logger).log(contains(CommandExecutor.FLIGHT_INSTR), eq(ApplicationLogger.LogLevel.WARNING));
    }

    @Test
    void executeCommand_add_success() {
        String[] input = {CommandExecutor.Commands.ADD, testInput.addTestValue};
        commandExecutor.executeCommand(input);
        verify(passengerController).importPassengersFromXml(testInput.addTestValue);
    }

    @Test
    void executeCommand_add_failure() {
        String[] input = {CommandExecutor.Commands.ADD};
        commandExecutor.executeCommand(input);
        verify(passengerController, never()).importPassengersFromXml(anyString());
        verify(logger).log(contains(CommandExecutor.ADD_INSTR), eq(ApplicationLogger.LogLevel.WARNING));
    }

    @Test
    void executeCommand_buy_success() {
        String[] input = {CommandExecutor.Commands.BUY,
                testInput.buyTestValues[0], //Passport
                testInput.buyTestValues[1], //Flight
                testInput.buyTestValues[2]}; //Date
        commandExecutor.executeCommand(input);
        verify(ticketController).purchaseTicket(input);
    }

    @Test
    void executeCommand_buy_successPrint() {
        String[] input = {CommandExecutor.Commands.BUY,
                testInput.buyTestValues[0], //Passport
                testInput.buyTestValues[1], //Flight
                testInput.buyTestValues[2], //Date
                CommandExecutor.Commands.BUY_PRINT};
        commandExecutor.executeCommand(input);
        verify(ticketController).purchaseTicket(input);
    }

    @Test
    void executeCommand_buy_failureNotEnoughArguments() {
        String[] input = {CommandExecutor.Commands.BUY};
        commandExecutor.executeCommand(input);
        verify(ticketController, never()).purchaseTicket(any());
        verify(logger).log(contains(CommandExecutor.BUY_INSTR), eq(ApplicationLogger.LogLevel.WARNING));
    }

    @Test
    void executeCommand_failurePrintMissWritten() {
        String[] input = {CommandExecutor.Commands.BUY,
                testInput.buyTestValues[0], //Passport
                testInput.buyTestValues[1], //Flight
                testInput.buyTestValues[2], //Date
                "[--prina]"};
        commandExecutor.executeCommand(input);
        verify(ticketController, never()).purchaseTicket(any());
        verify(logger).log(contains(CommandExecutor.WARN_WRONG_PRINT_ARG), eq(ApplicationLogger.LogLevel.WARNING));
        verify(logger).log(contains(CommandExecutor.BUY_INSTR), eq(ApplicationLogger.LogLevel.WARNING));
    }


}


