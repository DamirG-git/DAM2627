package org.finalactivitydg2627;

import org.finalactivitydg2627.Utils.ApplicationLogger;
import org.finalactivitydg2627.Utils.StoredProceduresSQL;
import org.finalactivitydg2627.controller.AirportController;
import org.finalactivitydg2627.controller.FlightsController;
import org.finalactivitydg2627.controller.PassengerController;
import org.finalactivitydg2627.controller.TicketController;

import java.sql.SQLException;
import java.util.Arrays;

public class CommandExecutor {
    private final ApplicationLogger logger;

    private final AirportController airportController;
    private final FlightsController flightsController;
    private final PassengerController passengerController;
    private final TicketController ticketController;


    public CommandExecutor() {
        logger = ApplicationLogger.getInstance();
        airportController = new AirportController();
        flightsController = new FlightsController();
        passengerController = new PassengerController();
        ticketController = new TicketController();
    }

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

    public static final String HELP = """
            Usage: flights [options]
            
            Options:
                -h, --help: show this help
                -a, --add <filename.xml>: add the passengers in the XML file to the database.
                -f, --flights <origin> <destination> <date>: show the flights available from origin to destination
                -b, --buy <customerId> <flightId> <date>: buy a ticket for a flight in a specific date
                -o, --origins: show the origins of the flights
                -d, --destinations <origin>: show the destinations of the flights from origin
            """;


    public static final String WARN_NO_INPUT_GIVEN = "No input was provided.";


    public void inputLogic(String[] args) {
        if (args.length == 0) {
            logger.log(WARN_NO_INPUT_GIVEN, ApplicationLogger.LogLevel.WARNING);
            return;
        }
        switch (args[0].trim().toLowerCase()) {
            case "-h", "--help":
                helpMessage();
                break;
            case "-o", "--origins":
                origins();
                break;
            case "-a", "--add":
                add(args);
                break;
            case "-d", "--destinations":
                destinations(args);
                break;
            case "-b", "--buy":
                buy(args);
                break;
            case "-f", "--flights":
                flights(args);
                break;
            default:
                printUnknownCommand(args[0]);

        }
    }

    //-h, --help: show this help
    public void helpMessage() {
        logger.log(HELP,
                ApplicationLogger.LogLevel.INFO);
    }

    //-o, --origins: show the origins of the flights
    public void origins() {
        airportController.findOriginAirports();
    }


    //-d, --destinations <origin>: show the destinations of the flights from origin
    public static final String DEST_INSTR =
            "-d, --destinations <origin>: show the destinations of the flights from origin";
    public static final int DEST_MAX_ARGS = 2;

    public void destinations(String[] args) {
        if (args.length != DEST_MAX_ARGS) {
            logger.log(messageInvalidAmountOfArguments(args, DEST_MAX_ARGS),
                    ApplicationLogger.LogLevel.WARNING);
            logger.log(DEST_INSTR,
                    ApplicationLogger.LogLevel.WARNING);
            return;
        }
        airportController.findDestinationAirports(args[1]);
    }

    //-a, --add <filename.xml>: add the passengers in the XML file to the database.
    public static final String ADD_INSTR =
            "-a, --add <filename.xml>: add the passengers in the XML file to the database.";
    public static final int ADD_MAX_ARGS = 2;

    public void add(String[] args) {
        if (args.length != 2) {
            logger.log(messageInvalidAmountOfArguments(args, ADD_MAX_ARGS),
                    ApplicationLogger.LogLevel.WARNING);
            logger.log(ADD_INSTR,
                    ApplicationLogger.LogLevel.WARNING);
            return;
        }
        passengerController.importPassengerFromXml(args[1]);
    }


    public static final String BUY_INSTR =
            "-b, --buy <customerId> <flightId> <date>: buy a ticket for a flight in a specific date";
    public static final int BUY_MAX_ARGS = 4;

    public void buy(String[] args) {
        if (args.length != BUY_MAX_ARGS) {
            logger.log(messageInvalidAmountOfArguments(args, BUY_MAX_ARGS),
                    ApplicationLogger.LogLevel.WARNING);
            logger.log(BUY_INSTR,
                    ApplicationLogger.LogLevel.WARNING);
            return;
        }
        ticketController.insertTicket(args);
    }

    public static final String DEBUG_CREATING_PROCEDURE =
            "Created or replaced stored procedures!";
    public static final String ERROR_SQL_FUNCTION_CREATION_FAILURE =
            "Custom SQL functions failed to create: ";


    public static final String FLIGHT_INSTR =
            "-f, --flights <origin> <destination> <date>: show the flights available from origin to destination";
    public static final int FLIGHTS_MAX_ARGS = 4;

    public void flights(String[] args) {
        if (args.length != FLIGHTS_MAX_ARGS) {
            logger.log(FLIGHT_INSTR,
                    ApplicationLogger.LogLevel.WARNING);
            logger.log(messageInvalidAmountOfArguments(args, FLIGHTS_MAX_ARGS),
                    ApplicationLogger.LogLevel.WARNING);
            return;
        }
        try {
            new StoredProceduresSQL().initialize();
            logger.log(DEBUG_CREATING_PROCEDURE,
                    ApplicationLogger.LogLevel.DEBUG);// Create procedures
        } catch (SQLException e) {
            logger.log(ERROR_SQL_FUNCTION_CREATION_FAILURE + e.getMessage(),
                    ApplicationLogger.LogLevel.ERROR);
            return;
        }
        flightsController.findFlights(args);

    }

    public static final String ERROR_UNKNOWN_COMMAND =
            "Unknown command: ";
    public static final String WARN_INVALID_AMOUNT_OF_ARGUMENTS_FORMATTED =
            "Invalid amount of arguments passed: %d/%d max, %s";

    public static String messageInvalidAmountOfArguments(String[] args, int max) {
        return String.format(
                WARN_INVALID_AMOUNT_OF_ARGUMENTS_FORMATTED, args.length, max, Arrays.toString(args));

    }

    private void printUnknownCommand(String command) {
        logger.log(ERROR_UNKNOWN_COMMAND + command, ApplicationLogger.LogLevel.WARNING);
        logger.log(HELP, ApplicationLogger.LogLevel.INFO);
    }

}
