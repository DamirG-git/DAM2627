package org.finalactivitydg2627;

import org.finalactivitydg2627.Utils.ApplicationLogger;
import org.finalactivitydg2627.controller.AirportController;
import org.finalactivitydg2627.controller.FlightsController;
import org.finalactivitydg2627.controller.PassengerController;
import org.finalactivitydg2627.controller.TicketController;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import static org.mockito.Mockito.*;

public class TestCommandExecutor {

    private ApplicationLogger logger;
    private CommandExecutor commandExecutor;
    private final TestInput testInput = new TestInput();

    private AirportController airportController;
    private FlightsController flightsController;
    private PassengerController passengerController;
    private TicketController ticketController;

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

        public final String destinationTestValue = "DEST";
        public final String addTestValue = "ADD";
        public final String[] buyTestValues = {"TEST PASSPORT", "TESTFLIGHT", "TEST DATE"};
        public final String[] flightsTestValue = {"TEST ORIGIN", "DESTINATION", "TEST DATE"};
    }

    @BeforeEach
    public void setUp() {
        logger = mock(ApplicationLogger.class);
        ticketController = mock(TicketController.class);
        airportController = mock(AirportController.class);
        flightsController = mock(FlightsController.class);
        passengerController = mock(PassengerController.class);

        commandExecutor = new CommandExecutor(
                logger, airportController, flightsController, passengerController, ticketController);
    }

    @Test
    public void inputLogic_help_success() {
        commandExecutor.inputLogic(testInput.helpCommand);
        verify(logger).log(contains(CommandExecutor.HELP), eq(ApplicationLogger.LogLevel.INFO));
    }

    @Test
    public void inputLogic_origins_success() {
        commandExecutor.inputLogic(testInput.originsCommand);
        verify(airportController).findOriginAirports();
    }

    @Test
    public void inputLogic_destinations_success() {
        String[] input = {testInput.destinationsCommand, testInput.destinationTestValue};
        commandExecutor.inputLogic(input);
        verify(airportController).findDestinationAirports(input[1]);
    }

    @Test
    public void inputLogic_destination_failure() {
        String[] input = {testInput.destinationsCommand};
        commandExecutor.inputLogic(input);
        verify(airportController, never()).findDestinationAirports(anyString());
        verify(logger).log(contains(CommandExecutor.DEST_INSTR), eq(ApplicationLogger.LogLevel.WARNING));
    }

    @Test
    public void inputLogic_flights_success() {
        String[] input = {testInput.flightsCommand,
                testInput.flightsTestValue[0], //origin
                testInput.flightsTestValue[1], //destination
                testInput.flightsTestValue[2]}; //date
        commandExecutor.inputLogic(input);
        verify(flightsController).findFlights(input);
    }

    @Test
    public void inputLogic_flights_failure() {
        String[] input = {testInput.flightsCommand};
        commandExecutor.inputLogic(input);
        verify(flightsController, never()).findFlights(any());
        verify(logger).log(contains(CommandExecutor.FLIGHT_INSTR), eq(ApplicationLogger.LogLevel.WARNING));
    }

    @Test
    public void inputLogic_add_success() {
        String[] input = {testInput.addCommand, testInput.addTestValue};
        commandExecutor.inputLogic(input);
        verify(passengerController).importPassengerFromXml(testInput.addTestValue);
    }

    @Test
    public void inputLogic_add_failure() {
        String[] input = {testInput.addCommand};
        commandExecutor.inputLogic(input);
        verify(passengerController, never()).importPassengerFromXml(anyString());
        verify(logger).log(contains(CommandExecutor.ADD_INSTR), eq(ApplicationLogger.LogLevel.WARNING));
    }

    @Test
    public void inputLogic_buy_success() {
        String[] input = {testInput.buyCommand,
                testInput.buyTestValues[0], //Passport
                testInput.buyTestValues[1], //Flight
                testInput.buyTestValues[2]}; //Date
        commandExecutor.inputLogic(input);
        verify(ticketController).insertTicket(input);
    }
    @Test
    public void inputLogic_buy_failure() {
        String[] input = {testInput.buyCommand};
        commandExecutor.inputLogic(input);
        verify(ticketController, never()).insertTicket(any());
        verify(logger).log(contains(CommandExecutor.BUY_INSTR), eq(ApplicationLogger.LogLevel.WARNING));
    }


}


