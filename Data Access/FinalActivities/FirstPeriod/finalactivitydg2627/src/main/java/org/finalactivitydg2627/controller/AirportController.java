package org.finalactivitydg2627.controller;

import org.finalactivitydg2627.dto.AirportDTO;
import org.finalactivitydg2627.model.Airports;
import org.finalactivitydg2627.repository.AbstractRepo;
import org.finalactivitydg2627.repository.AirportRepo;
import org.finalactivitydg2627.service.AirportService;
import org.finalactivitydg2627.utils.ApplicationLogger;
import org.finalactivitydg2627.utils.ApplicationValidator;

import java.sql.SQLException;
import java.util.List;

public class AirportController extends AbstractController {

    private final AirportService airportService;

    public AirportController(ApplicationLogger logger, AirportService airportService) {
        super(logger);
        this.airportService = airportService;
    }

    public AirportController(ApplicationLogger logger) {
        super(logger);
        this.airportService = new AirportService(logger);
    }

    public static final String INFO_FETCHING_ORIGIN_AIRPORTS = "Calling service for origin airports...";
    public static final String INFO_FETCHING_DESTINATION_AIRPORTS = "Calling service for destination airports...";
    public static final String INFO_ORIGIN_AIRPORTS_RETRIEVED = "Successfully retrieved origin airports!\n";

    public static final String WARN_NO_ORIGIN_AIRPORTS_FOUND = "Origin airports not found.";

    public static final String ERROR_FETCHING_AIRPORTS = "Error obtaining airports: ";

    public void displayOriginAirports() {
        logger.log(INFO_FETCHING_ORIGIN_AIRPORTS, ApplicationLogger.LogLevel.INFO);
        List<AirportDTO> result;
        try {
            result = airportService.getOriginAirports();
        } catch (SQLException e) {
            logger.log(ERROR_FETCHING_AIRPORTS + e.getMessage(), ApplicationLogger.LogLevel.ERROR);
            return;
        }

        if (result.isEmpty()) {
            logger.log(WARN_NO_ORIGIN_AIRPORTS_FOUND, ApplicationLogger.LogLevel.WARNING);
        } else {

            var messageBuilder = new StringBuilder();
            var flightCodes = new StringBuilder();
            messageBuilder.append(INFO_ORIGIN_AIRPORTS_RETRIEVED);

            processAirports(result, messageBuilder, flightCodes);
            logger.log(messageBuilder.toString(), ApplicationLogger.LogLevel.INFO);
        }

    }

    private void processAirports(
            List<AirportDTO> result, StringBuilder messageBuilder, StringBuilder flightCodes) {
        var lastAirport = "";
        var totalFlights = 0;
        for (var airport : result) {

            if (!lastAirport.equals(airport.getAirportIata())) {
                // Get new airport. Add all flight codes if there were any.
                if (!flightCodes.isEmpty()) {
                    appendFlightCodes(flightCodes, messageBuilder, totalFlights);
                    totalFlights = 0;
                }

                // Create new message for airport.
                appendOriginAirportMessage(airport, messageBuilder);
                flightCodes.append(airport.getAirportIata());
                lastAirport = airport.getAirportIata();

            } else {
                // Save airport codes if its same airport.
                flightCodes.append(", ").append(airport.getFlightCode());
                totalFlights++;
            }
        }
        // Dealing with last airport.
        if (!flightCodes.isEmpty()) {
            appendFlightCodes(flightCodes, messageBuilder, totalFlights);
        }
    }

    private void appendFlightCodes(StringBuilder flightCodes, StringBuilder messageBuilder, int totalFlights) {
        if (!flightCodes.isEmpty()) {
            if (totalFlights >= 1) {
                messageBuilder.append("s ").append(flightCodes).append(".\n");
            } else {
                messageBuilder.append(" ").append(flightCodes).append(".\n");
            }
            flightCodes.setLength(0);
        }
    }

    private static void appendOriginAirportMessage(AirportDTO airport, StringBuilder builder) {
        builder.append("    - ")
                .append("Airport ")
                .append(airport.getAirportIata())
                .append(" (")
                .append(airport.getAirportName())
                .append(") in ")
                .append(airport.getCityName())
                .append(" is the origin of flight");
    }

    public static final String INFO_RETRIEVED_DESTINATIONS = "Successfully retrieved destinations!\n";
    public static final String INFO_VALIDATING_INPUTS = "Validating input data: ";

    public static final String WARN_INVALID_ORIGIN_AIRPORT_IATA = "Airport IATA is invalid!\n";
    public static final String WARN_NO_AIRPORT_FOUND_IN_DB = "No airports with IATA: ";
    public static final String WARN_NO_DESTINATIONS_FOUND = "No destinations found from origin airport: ";


    public void displayDestinationAirports(String input) {
        input = input.toUpperCase();
        logger.log(INFO_VALIDATING_INPUTS+input, ApplicationLogger.LogLevel.INFO);
        var originResult = Airports.validateIata(input);
        if (originResult.result() == null) {
            logger.log(WARN_INVALID_ORIGIN_AIRPORT_IATA + originResult.message(), ApplicationLogger.LogLevel.WARNING);
            return;
        }

        logger.log(INFO_FETCHING_DESTINATION_AIRPORTS, ApplicationLogger.LogLevel.INFO);

        AbstractRepo.RepoResultValue<List<AirportDTO>, AirportRepo.AirportStatus> results;
        try {
            results = airportService.getDestinationAirports(originResult.result());
        } catch (SQLException e) {
            logger.log(ERROR_FETCHING_AIRPORTS + e.getMessage(), ApplicationLogger.LogLevel.ERROR);
            return;
        }

        if (results.codeEnum() == AirportRepo.AirportStatus.NOT_FOUND) {
            var message = WARN_NO_AIRPORT_FOUND_IN_DB + originResult.result();
            logger.log(message, ApplicationLogger.LogLevel.WARNING);
            return;
        }
        if (results.value().isEmpty()) {
            var message = WARN_NO_DESTINATIONS_FOUND + originResult.result();
            logger.log(message, ApplicationLogger.LogLevel.WARNING);
            return;
        }
        var builder = new StringBuilder();
        builder.append(INFO_RETRIEVED_DESTINATIONS);
        for (var airport : results.value()) {
            appendDestinationAirportMessage(airport, builder, originResult);
        }
        logger.log(builder.toString(), ApplicationLogger.LogLevel.INFO);


    }

    private static void appendDestinationAirportMessage(AirportDTO airport, StringBuilder builder, ApplicationValidator.ResultValue<String> originResult) {
        builder.append("    - ")
                .append("Airport ")
                .append(airport.getAirportIata())
                .append(" (")
                .append(airport.getAirportName())
                .append(") located in ")
                .append(airport.getCityName())
                .append(" is reachable from origin airport ")
                .append(originResult.result())
                .append(" via flight ")
                .append(airport.getFlightCode())
                .append(".")
                .append("\n");
    }


}
