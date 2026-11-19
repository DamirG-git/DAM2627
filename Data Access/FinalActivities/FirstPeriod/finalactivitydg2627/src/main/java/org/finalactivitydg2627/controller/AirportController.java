package org.finalactivitydg2627.controller;

import org.finalactivitydg2627.Utils.ApplicationLogger;
import org.finalactivitydg2627.Utils.Verification;
import org.finalactivitydg2627.dto.AirportDTO;
import org.finalactivitydg2627.model.Airports;
import org.finalactivitydg2627.repository.AbstractRepo;
import org.finalactivitydg2627.repository.AirportRepo;
import org.finalactivitydg2627.service.AirportService;

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
        this.airportService = new AirportService();
    }


    public AirportController() {
        this.airportService = new AirportService();
    }

    public static final String INFO_CALLING_SERVICE_ORIGIN_AIRPORTS = "Calling service for origin airports...";
    public static final String INFO_CALLING_SERVICE_DESTINATION_AIRPORTS = "Calling service for destination airports...";
    public static final String INFO_RETRIEVED_ORIGINS = "Successfully retrieved origin airports!\n";

    public static final String WARN_ORIGIN_AIRPORTS_NOT_FOUND = "Origin airports not found.";

    public static final String ERROR_ON_OBTAINING_AIRPORTS = "Error obtaining airports: ";

    public void findOriginAirports() {
        logger.log(INFO_CALLING_SERVICE_ORIGIN_AIRPORTS, ApplicationLogger.LogLevel.INFO);
        List<AirportDTO> result;
        try {
            result = airportService.getOriginAirports();
        } catch (SQLException e) {
            logger.log(ERROR_ON_OBTAINING_AIRPORTS + e.getMessage(), ApplicationLogger.LogLevel.ERROR);
            return;
        }

        if (result.isEmpty()) {
            logger.log(WARN_ORIGIN_AIRPORTS_NOT_FOUND, ApplicationLogger.LogLevel.WARNING);
        } else {

            var messageBuilder = new StringBuilder();
            var flightCodes = new StringBuilder();
            messageBuilder.append(INFO_RETRIEVED_ORIGINS);

            processAirports(result, messageBuilder, flightCodes);
            logger.log(messageBuilder.toString(), ApplicationLogger.LogLevel.INFO);
        }

    }

    private void processAirports(
            List<AirportDTO> result, StringBuilder messageBuilder, StringBuilder flightCodes) {
        var lastAirport = "";
        var totalFlights = 0;
        for (var airport : result) {

            if (!lastAirport.equals(airport.airportIata())) {
                // Get new airport. Add all flight codes if there were any.
                if (!flightCodes.isEmpty()) {
                    appendFlightCodes(flightCodes, messageBuilder, totalFlights);
                    totalFlights = 0;
                }

                // Create new message for airport.
                findOriginAirportMessage(airport, messageBuilder);
                flightCodes.append(airport.flightCode());
                lastAirport = airport.airportIata();

            } else {
                // Save airport codes if its same airport.
                flightCodes.append(", ").append(airport.flightCode());
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

    private static void findOriginAirportMessage(AirportDTO airport, StringBuilder builder) {
        builder.append("    - ")
                .append("Airport ")
                .append(airport.airportIata())
                .append(" (")
                .append(airport.airportName())
                .append(") in ")
                .append(airport.cityName())
                .append(" is the origin of flight");
    }

    public static final String INFO_RETRIEVED_DESTINATIONS = "Successfully retrieved destinations!\n";

    public static final String WARN_INVALID_AIRPORT_CODE = "Airport IATA is invalid!\n";
    public static final String WARN_NO_SUCH_AIRPORT_EXISTS = "No such airport exists: ";
    public static final String WARN_NO_DESTINATIONS_FOUND = "No destinations found from airport: ";


    public void findDestinationAirports(String input) {
        input = input.toUpperCase();
        var originResult = Airports.validateIata(input);
        if (originResult.result() == null) {
            logger.log(WARN_INVALID_AIRPORT_CODE + originResult.message(), ApplicationLogger.LogLevel.WARNING);
            return;
        }

        logger.log(INFO_CALLING_SERVICE_DESTINATION_AIRPORTS, ApplicationLogger.LogLevel.INFO);

        AbstractRepo.RepoResultValue<List<AirportDTO>, AirportRepo.AirportStatus> results;
        try {
            results = airportService.getDestinationAirports(originResult.result());
        } catch (SQLException e) {
            logger.log(ERROR_ON_OBTAINING_AIRPORTS + e.getMessage(), ApplicationLogger.LogLevel.ERROR);
            return;
        }

        if (results.codeEnum() == AirportRepo.AirportStatus.NOT_FOUND) {
            var message = WARN_NO_SUCH_AIRPORT_EXISTS + originResult.result();
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
            findDestinationAirportMessage(airport, builder, originResult);
        }
        logger.log(builder.toString(), ApplicationLogger.LogLevel.INFO);


    }

    private static void findDestinationAirportMessage(AirportDTO airport, StringBuilder builder, Verification.ResultValue<String> originResult) {
        builder.append("    - ")
                .append("Airport ")
                .append(airport.airportIata())
                .append(" (")
                .append(airport.airportName())
                .append(") located in ")
                .append(airport.cityName())
                .append(" is reachable from origin airport ")
                .append(originResult.result())
                .append(" via flight ")
                .append(airport.flightCode())
                .append(".")
                .append("\n");
    }


}
