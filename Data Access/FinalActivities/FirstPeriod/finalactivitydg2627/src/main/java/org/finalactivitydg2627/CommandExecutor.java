package org.finalactivitydg2627;

import org.finalactivitydg2627.controller.AirportController;
import org.finalactivitydg2627.controller.FlightsController;
import org.finalactivitydg2627.controller.PassengerController;
import org.finalactivitydg2627.controller.TicketController;
import org.finalactivitydg2627.utils.ApplicationLogger;
import org.finalactivitydg2627.utils.ApplicationValidator;
import org.finalactivitydg2627.utils.MessageFormatter;
import org.finalactivitydg2627.utils.StoredProceduresSQL;

import java.sql.SQLException;
import java.util.Arrays;
import java.util.regex.Pattern;

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
                -b, --buy <customerId> <flightId> <date> [--print]: buy a ticket for a flight in a specific date, add [--print] at end to print it.
                -c, --cancel <customerId> <date>: cancel ticket on specific date
                -o, --origins: show the origins of the flights
                -d, --destinations <origin>: show the destinations of the flights from origin
            """;


    public static final String WARN_NO_INPUT_PROVIDED = "No input was provided.";

    public static class Commands {

        private Commands() {
        }

        public static final String HELP = "-h";
        public static final String HELP_LONG = "--help";

        public static final String ORIGINS = "-o";
        public static final String ORIGINS_LONG = "--origins";

        public static final String ADD = "-a";
        public static final String ADD_LONG = "--add";

        public static final String DESTINATIONS = "-d";
        public static final String DESTINATIONS_LONG = "--destinations";

        public static final String BUY = "-b";
        public static final String BUY_LONG = "--buy";
        public static final String BUY_PRINT = "[--print]";

        public static final String FLIGHTS = "-f";
        public static final String FLIGHTS_LONG = "--flights";

        public static final String CANCEL = "-c";
        public static final String CANCEL_LONG = "--cancel";
    }


    public void executeCommand(String[] args) {
        if (args.length == 0) {
            logger.log(WARN_NO_INPUT_PROVIDED, ApplicationLogger.LogLevel.WARNING);
            return;
        }
        switch (args[0].trim().toLowerCase()) {
            case Commands.HELP, Commands.HELP_LONG -> displayHelp();
            case Commands.ORIGINS, Commands.ORIGINS_LONG -> displayOriginAirports();
            case Commands.ADD, Commands.ADD_LONG -> handleAddCommand(args);
            case Commands.DESTINATIONS, Commands.DESTINATIONS_LONG -> handleDestinationsCommand(args);
            case Commands.BUY, Commands.BUY_LONG -> handleBuyCommand(args);
            case Commands.FLIGHTS, Commands.FLIGHTS_LONG -> handleFlightsCommand(args);
            case Commands.CANCEL, Commands.CANCEL_LONG -> handleCancelCommand(args);
            default -> displayUnknownCommand(args[0]);
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
            "-b, --buy <customerId> <flightId> <date> [--print]: buy a ticket for a flight in a specific date, add [--print] at end to print it.";
    public static final int BUY_MAX_ARGS = 5;
    public static final String WARN_WRONG_PRINT_ARG = "Print command had errors: \n";

    public void handleBuyCommand(String[] args) {
        switch (args.length) {
            case 4 -> ticketController.purchaseTicket(args);
            case 5 -> {
                var command = ApplicationValidator.basicStringVerification("print command", args[4], 9, 9, Pattern.compile("^\\[--print]$"));
                if (command.result() == null) {
                    logger.log(WARN_WRONG_PRINT_ARG+ MessageFormatter.multipleErrorsFormatter(command) + command.message(),
                            ApplicationLogger.LogLevel.WARNING);
                    logger.log(BUY_INSTR,
                            ApplicationLogger.LogLevel.WARNING);
                    return;
                }
                ticketController.purchaseTicket(args);
            }
            default -> {
                logger.log(formatInvalidArgumentCountMessage(args, BUY_MAX_ARGS),
                        ApplicationLogger.LogLevel.WARNING);
                logger.log(BUY_INSTR,
                        ApplicationLogger.LogLevel.WARNING);
            }
        }
    }

    public static final int CANCEL_MAX_ARGS = 3;
    public static final String CANCEL_INSTR =
            "-c, --cancel <customerId> <date>: cancel ticket on specific date";

    public void handleCancelCommand(String[] args) {
        if (args.length != CANCEL_MAX_ARGS) {
            logger.log(formatInvalidArgumentCountMessage(args, CANCEL_MAX_ARGS),
                    ApplicationLogger.LogLevel.WARNING);
            logger.log(CANCEL_INSTR,
                    ApplicationLogger.LogLevel.WARNING);
            return;
        }
        ticketController.cancelTicket(args);

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
