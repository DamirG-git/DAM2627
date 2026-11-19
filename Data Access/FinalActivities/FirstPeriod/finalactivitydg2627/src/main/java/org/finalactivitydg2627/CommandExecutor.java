package org.finalactivitydg2627;

import org.finalactivitydg2627.controller.AirportController;
import org.finalactivitydg2627.controller.FlightsController;
import org.finalactivitydg2627.controller.PassengerController;
import org.finalactivitydg2627.controller.TicketController;
import org.finalactivitydg2627.utils.ApplicationLogger;
import org.finalactivitydg2627.utils.StoredProceduresSQL;

import java.sql.SQLException;
import java.util.Arrays;

public class CommandExecutor {
    private final ApplicationLogger logger;

    private final AirportController airportController;
    private final FlightsController flightsController;
    private final PassengerController passengerController;
    private final TicketController ticketController;

    public CommandExecutor(ApplicationLogger logger) {
        this.logger = logger;
        airportController = new AirportController(logger);
        flightsController = new FlightsController(logger);
        passengerController = new PassengerController(logger);
        ticketController = new TicketController(logger);
    }

    public CommandExecutor(ApplicationLogger logger, AirportController airportController, FlightsController flightsController, PassengerController passengerController, TicketController ticketController) {
        this.airportController = airportController;
        this.flightsController = flightsController;
        this.passengerController = passengerController;
        this.ticketController = ticketController;
        this.logger = logger;
    }

    public static final String HELP_MESSAGE = """
            Usage: flights [options]
            
            Options:
                -h, --help: show this help
                -a, --add <filename.xml>: add the passengers in the XML file to the database.
                -f, --flights <origin> <destination> <date>: show the flights available from origin to destination
                -b, --buy <customerId> <flightId> <date>: buy a ticket for a flight in a specific date
                -o, --origins: show the origins of the flights
                -d, --destinations <origin>: show the destinations of the flights from origin
            """;


    public static final String WARN_NO_INPUT_PROVIDED = "No input was provided.";


    public void executeCommand(String[] args) {
        if (args.length == 0) {
            logger.log(WARN_NO_INPUT_PROVIDED, ApplicationLogger.LogLevel.WARNING);
            return;
        }
        switch (args[0].trim().toLowerCase()) {
            case "-h", "--help":
                displayHelp();
                break;
            case "-o", "--origins":
                displayOriginAirports();
                break;
            case "-a", "--add":
                handleAddCommand(args);
                break;
            case "-d", "--destinations":
                handleDestinationsCommand(args);
                break;
            case "-b", "--buy":
                handleBuyCommand(args);
                break;
            case "-f", "--flights":
                handleFlightsCommand(args);
                break;
            default:
                displayUnknownCommand(args[0]);

        }
    }

    //-h, --help: show this help
    public void displayHelp() {
        logger.log(HELP_MESSAGE,
                ApplicationLogger.LogLevel.INFO);
    }

    //-o, --origins: show the origins of the flights
    public void displayOriginAirports() {
        airportController.displayOriginAirports();
    }


    //-d, --destinations <origin>: show the destinations of the flights from origin
    public static final String DEST_INSTR =
            "-d, --destinations <origin>: show the destinations of the flights from origin";
    public static final int DEST_MAX_ARGS = 2;

    public void handleDestinationsCommand(String[] args) {
        if (args.length != DEST_MAX_ARGS) {
            logger.log(formatInvalidArgumentCountMessage(args, DEST_MAX_ARGS),
                    ApplicationLogger.LogLevel.WARNING);
            logger.log(DEST_INSTR,
                    ApplicationLogger.LogLevel.WARNING);
            return;
        }
        airportController.displayDestinationAirports(args[1]);
    }

    //-a, --add <filename.xml>: add the passengers in the XML file to the database.
    public static final String ADD_INSTR =
            "-a, --add <filename.xml>: add the passengers in the XML file to the database.";
    public static final int ADD_MAX_ARGS = 2;

    public void handleAddCommand(String[] args) {
        if (args.length != 2) {
            logger.log(formatInvalidArgumentCountMessage(args, ADD_MAX_ARGS),
                    ApplicationLogger.LogLevel.WARNING);
            logger.log(ADD_INSTR,
                    ApplicationLogger.LogLevel.WARNING);
            return;
        }
        passengerController.importPassengersFromXml(args[1]);
    }


    public static final String BUY_INSTR =
            "-b, --buy <customerId> <flightId> <date>: buy a ticket for a flight in a specific date";
    public static final int BUY_MAX_ARGS = 4;

    public void handleBuyCommand(String[] args) {
        if (args.length != BUY_MAX_ARGS) {
            logger.log(formatInvalidArgumentCountMessage(args, BUY_MAX_ARGS),
                    ApplicationLogger.LogLevel.WARNING);
            logger.log(BUY_INSTR,
                    ApplicationLogger.LogLevel.WARNING);
            return;
        }
        ticketController.purchaseTicket(args);
    }

    public static final String DEBUG_STORED_PROCEDURES_INITIALIZED =
            "Created or replaced stored procedures!";
    public static final String ERROR_STORED_PROCEDURE_INITIALIZATION =
            "Custom SQL functions failed to create: ";


    public static final String FLIGHT_INSTR =
            "-f, --flights <origin> <destination> <date>: show the flights available from origin to destination";
    public static final int FLIGHTS_MAX_ARGS = 4;

    public void handleFlightsCommand(String[] args) {
        if (args.length != FLIGHTS_MAX_ARGS) {
            logger.log(FLIGHT_INSTR,
                    ApplicationLogger.LogLevel.WARNING);
            logger.log(formatInvalidArgumentCountMessage(args, FLIGHTS_MAX_ARGS),
                    ApplicationLogger.LogLevel.WARNING);
            return;
        }
        try {
            new StoredProceduresSQL().initialize();
            logger.log(DEBUG_STORED_PROCEDURES_INITIALIZED,
                    ApplicationLogger.LogLevel.DEBUG);// Create procedures
        } catch (SQLException e) {
            logger.log(ERROR_STORED_PROCEDURE_INITIALIZATION + e.getMessage(),
                    ApplicationLogger.LogLevel.ERROR);
            return;
        }
        flightsController.displayFlights(args);

    }

    public static final String WARN_UNKNOWN_COMMAND =
            "Unknown command: ";
    public static final String WARN_INVALID_ARGUMENT_COUNT_FORMAT_COUNT_ARGS =
            "Invalid amount of arguments passed: %d/%d max, %s";

    public static String formatInvalidArgumentCountMessage(String[] args, int max) {
        return String.format(
                WARN_INVALID_ARGUMENT_COUNT_FORMAT_COUNT_ARGS, args.length, max, Arrays.toString(args));

    }

    private void displayUnknownCommand(String command) {
        logger.log(WARN_UNKNOWN_COMMAND + command, ApplicationLogger.LogLevel.WARNING);
        logger.log(HELP_MESSAGE, ApplicationLogger.LogLevel.INFO);
    }

}
