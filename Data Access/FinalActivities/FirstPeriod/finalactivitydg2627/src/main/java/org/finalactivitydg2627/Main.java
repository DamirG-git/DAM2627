package org.finalactivitydg2627;

import org.finalactivitydg2627.Utils.ApplicationLogger;
import org.finalactivitydg2627.Utils.StoredProceduresSQL;
import org.finalactivitydg2627.controller.AirportController;
import org.finalactivitydg2627.controller.FlightsController;
import org.finalactivitydg2627.controller.PassengerController;

import java.sql.SQLException;
import java.util.Arrays;

//TIP To <b>Run</b> code, press <shortcut actionId="Run"/> or
// click the <icon src="AllIcons.Actions.Execute"/> icon in the gutter.
public class Main {

    private static final ApplicationLogger logger = ApplicationLogger.getInstance();

    public static void main(String[] args) {
        try {
            new StoredProceduresSQL().initialize(); // Create procedures
        } catch (SQLException e) {
            ApplicationLogger.getInstance().log("Custom SQL functions failed to create: " + e.getMessage(), ApplicationLogger.LogLevel.ERROR);
            throw new RuntimeException(e);
        }
        inputLogic(args);
    }


    private static final String HELP = """
            Usage: flights [options]
            
            Options:
                -h, --help: show this help
                -a, --add <filename.xml>: add the passengers in the XML file to the database.
                -f, --flights <origin> <destination> <date>: show the flights available from origin to destination
                -b, --buy <customerId> <flightId> <date>: buy a ticket for a flight in a specific date
                -o, --origins: show the origins of the flights
                -d, --destinations <origin>: show the destinations of the flights from origin
            """;

    private static void inputLogic(String[] args) {
        if (args.length == 0) {
            logger.log("No input given", ApplicationLogger.LogLevel.ERROR);
            logger.log(HELP, ApplicationLogger.LogLevel.INFO);
            System.out.println(HELP);
            return;
        }
        switch (args.length) {
            case 1:
                //-h, --help: show this help
                //-o, --origins: show the origins of the flights
                var command = args[0].trim().toLowerCase();
                switch (command) {
                    case "-h", "--help":
                        logger.log(HELP, ApplicationLogger.LogLevel.INFO);
                        System.out.println(HELP);
                        break;
                    case "-o", "--origins":
                        var aController = new AirportController();
                        aController.getOriginAirports();
                        break;
                    default:
                        printUnknownCommand(command);
                        break;
                }
                break;
            case 2:
                //-a, --add <filename.xml>: add the passengers in the XML file to the database.
                //-d, --destinations <origin>: show the destinations of the flights from origin
                var command2 = args[0].trim().toLowerCase();

                switch (command2) {
                    case "-a", "--add":
                        var pController = new PassengerController();
                        pController.AddPassengerFromXML(args[1]);
                        break;
                    case "-d", "--destinations":
                        var aController = new AirportController();
                        aController.getDestinationAirports(args[1]);
                        break;
                    default:
                        printUnknownCommand(command2);
                        break;

                }
                break;
            case 4:
                //-f, --flights <origin> <destination> <date>: show the flights available from origin to destination
                //-b, --buy <customerId> <flightId> <date>: buy a ticket for a flight in a specific date
                var command4 = args[0].trim().toLowerCase();
                switch (command4) {
                    case "-b", "--buy":
                        //TODO
                        break;
                    case "-f", "--flights":
                        var flightsController = new FlightsController();
                        flightsController.getFlights(args);
                        break;
                    default:
                        printUnknownCommand(command4);
                        break;
                }
                break;
            default:
                logger.log("Invalid number of arguments passed: " + args.length + "/4 max, " + Arrays.toString(args), ApplicationLogger.LogLevel.WARNING);
                printUnknownCommand("");

        }


    }

    private static void printUnknownCommand(String command) {
        logger.log("Unknown command: "+command, ApplicationLogger.LogLevel.WARNING);
        System.out.println("Unknown command: "+command);
        logger.log(HELP, ApplicationLogger.LogLevel.INFO);
        System.out.println(HELP);
    }


}