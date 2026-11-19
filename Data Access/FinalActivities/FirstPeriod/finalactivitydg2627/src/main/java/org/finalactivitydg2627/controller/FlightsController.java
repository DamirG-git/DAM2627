package org.finalactivitydg2627.controller;

import org.finalactivitydg2627.Utils.ApplicationLogger;
import org.finalactivitydg2627.Utils.MessageFormatter;
import org.finalactivitydg2627.Utils.Verification;
import org.finalactivitydg2627.dto.FlightDTO;
import org.finalactivitydg2627.service.FlightsService;

import java.sql.SQLException;
import java.util.Arrays;

public class FlightsController extends AbstractController {

    private final FlightsService flightsService;

    public FlightsController() {
        flightsService = new FlightsService();
    }

    public FlightsController(ApplicationLogger logger) {
        super(logger);
        flightsService = new FlightsService();
    }

    public FlightsController(ApplicationLogger logger, FlightsService flightsService) {
        super(logger);
        this.flightsService = flightsService;
    }

    public static final String INFO_VALIDATING_INPUT = "Validating input values: ";
    public static final String INFO_ATTEMPT_TO_GET_FLIGHTS = "Attempting to get flights!";
    public static final String INFO_SUCCESS_GET_FLIGHTS = "Successfully retrieved flights!\n";

    public static final String WARN_VALIDATION_FAILED = "Input validation failed: \n";
    public static final String WARN_NO_ORIGIN_AIRPORT_FOUND = "No origin airport found with IATA: ";
    public static final String WARN_NO_DESTINATION_AIRPORT_FOUND = "No destination airport found with IATA: ";

    public static final String WARN_NO_ORIGIN_OR_DESTINATION_FOUND_FORMATTED_ORIGIN_DEST = "No origin IATA (%s) or destination IATA (%s) have been found in the database.";
    public static final String WARN_NO_FLIGHTS_FOUND_FORMATTED_ORIGIN_DEST_DATE = "No flights found with these parameters: origin (%s), destination (%s), date (%s).";

    public static final String ERROR_GET_FLIGHTS = "Error on fetching data: ";

    public void findFlights(String[] args) {
        logger.log(INFO_VALIDATING_INPUT + Arrays.toString(args), ApplicationLogger.LogLevel.INFO);
        var origin = Verification.basicStringVerification("source location", args[1] == null ? null : args[1].toUpperCase(), 3, null, null);
        var destination = Verification.basicStringVerification("destination location", args[2] == null ? null : args[2].toUpperCase(), 3, null, null);
        var date = Verification.basicDateVerification("date", args[3], null, null);


        var errors = MessageFormatter.multipleErrorsFormatter(origin, destination, date);
        if (!errors.isEmpty()) {
            logger.log(WARN_VALIDATION_FAILED + errors, ApplicationLogger.LogLevel.WARNING);
            return;
        }
        logger.log(INFO_ATTEMPT_TO_GET_FLIGHTS, ApplicationLogger.LogLevel.INFO);
        try {
            var flightsRepoResult = flightsService.getFlights(origin.result(),
                    destination.result(),
                    date.result());
            var code = flightsRepoResult.codeEnum();
            switch (code) {
                case NO_DESTINATION_AIRPORT_FOUND:
                    logger.log(WARN_NO_DESTINATION_AIRPORT_FOUND + destination.result(), ApplicationLogger.LogLevel.WARNING);
                    return;
                case NO_ORIGIN_AIRPORT_FOUND:
                    logger.log(WARN_NO_ORIGIN_AIRPORT_FOUND + origin.result(), ApplicationLogger.LogLevel.WARNING);
                    return;
                case NO_ORIGIN_OR_DESTINATION_FOUND:
                    logger.log(String.format(WARN_NO_ORIGIN_OR_DESTINATION_FOUND_FORMATTED_ORIGIN_DEST, origin.result(), destination.result()), ApplicationLogger.LogLevel.WARNING);
                    return;
                case SUCCESS:
                    if (flightsRepoResult.value().isEmpty()) {
                        logger.log(
                                String.format(
                                        WARN_NO_FLIGHTS_FOUND_FORMATTED_ORIGIN_DEST_DATE, origin.result(), destination.result(), date.result()),
                                ApplicationLogger.LogLevel.WARNING);
                        return;
                    }
                    var flights = flightsRepoResult.value();
                    var stringBuilder = new StringBuilder();
                    stringBuilder.append(INFO_SUCCESS_GET_FLIGHTS);
                    for (var flight : flights) {
                        getFlightsMessage(flight, stringBuilder);
                    }
                    logger.log(stringBuilder.toString(), ApplicationLogger.LogLevel.INFO);
                    break;
                default:
                    throw new IllegalStateException("Unexpected value: " + code);
            }
        } catch (SQLException e) {
            logger.log(ERROR_GET_FLIGHTS + e.getMessage(), ApplicationLogger.LogLevel.ERROR);
        }


    }

    private void getFlightsMessage(FlightDTO flight, StringBuilder stringBuilder) {
        stringBuilder.append("  - ").append("Flight: ")
                .append(flight.getFlightCode()).append(" from airport ")
                .append(flight.getIataCode()).append(" in ")
                .append("(").append(flight.getCityName()).append(")")
                .append(" has ").append(flight.getFreeSeats()).append(" available seats out of ").append(flight.getTotalSeats()).append(" seats.\n");
    }
}
