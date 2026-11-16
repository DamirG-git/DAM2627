package org.finalactivitydg2627.controller;

import org.finalactivitydg2627.Utils.ApplicationLogger;
import org.finalactivitydg2627.Utils.MessageFormatter;
import org.finalactivitydg2627.Utils.Verification;
import org.finalactivitydg2627.dto.FlightDTO;
import org.finalactivitydg2627.service.FlightsService;

import java.sql.SQLException;
import java.util.Arrays;

public class FlightsController {

    private final ApplicationLogger logger;
    private final FlightsService flightsService;

    public FlightsController() {
        logger = ApplicationLogger.getInstance();
        flightsService = new FlightsService();
    }

    public FlightsController(ApplicationLogger logger, FlightsService flightsService) {
        this.logger = logger;
        this.flightsService = flightsService;
    }

    public static final String INFO_VALIDATING_INPUT = "Validating input values: ";
    public static final String WARN_VALIDATION_FAILED = "Input validation failed: \n";
    public static final String INFO_ATTEMPT_TO_GET_FLIGHTS = "Attempting to get flights!";
    public static final String WARN_NO_FLIGHTS_FOUND = "No flights found with these parameters: origin (%s), destination (%s), date (%s).";
    public static final String INFO_SUCCESS_GET_FLIGHTS = "Successfully retrieved flights!\n";
    public static final String ERROR_GET_FLIGHTS = "Error on fetching data: ";

    public void findFlights(String[] args) {
        logger.log(INFO_VALIDATING_INPUT + Arrays.toString(args), ApplicationLogger.LogLevel.INFO);
        var origin = Verification.basicStringVerification("source location", args[1], 3, null, null);
        var destination = Verification.basicStringVerification("destination location", args[2], 3, null, null);
        var date = Verification.basicDateVerification("date", args[3], null, null);


        var errors = MessageFormatter.multipleErrorsFormatter(origin, destination, date);
        if (!errors.isEmpty()) {
            logger.log(WARN_VALIDATION_FAILED + errors, ApplicationLogger.LogLevel.WARNING);
            return;
        }
        logger.log(INFO_ATTEMPT_TO_GET_FLIGHTS, ApplicationLogger.LogLevel.INFO);
        try {
            var flightsOptional = flightsService.getFlights(origin.result(),
                    destination.result(),
                    date.result());

            if (flightsOptional.isEmpty()) {
                logger.log(
                        String.format(
                                WARN_NO_FLIGHTS_FOUND, origin.result(), destination.result(), date.result()),
                        ApplicationLogger.LogLevel.WARNING);
                return;
            }
            var flights = flightsOptional.get();
            var stringBuilder = new StringBuilder();
            stringBuilder.append(INFO_SUCCESS_GET_FLIGHTS);
            for (var flight : flights) {
                getFlightsMessage(flight, stringBuilder);
            }

            logger.log(stringBuilder.toString(), ApplicationLogger.LogLevel.INFO);
        } catch (SQLException e) {
            logger.log(ERROR_GET_FLIGHTS + e.getMessage(), ApplicationLogger.LogLevel.ERROR);
        }


    }

    private void getFlightsMessage(FlightDTO flight, StringBuilder stringBuilder) {
        stringBuilder.append("  - ").append("Flight: ")
                .append(flight.flightCode()).append(" from airport ")
                .append(flight.iataCode()).append(" in ")
                .append("(").append(flight.cityName()).append(")")
                .append(" has ").append(flight.availableSeats()).append(" available seats out of ").append(flight.totalSeats()).append(" seats.\n");
    }
}
