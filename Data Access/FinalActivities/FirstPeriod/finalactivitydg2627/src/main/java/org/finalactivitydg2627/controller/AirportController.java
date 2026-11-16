package org.finalactivitydg2627.controller;

import org.finalactivitydg2627.Utils.ApplicationLogger;
import org.finalactivitydg2627.Utils.Verification;
import org.finalactivitydg2627.dto.AirportDTO;
import org.finalactivitydg2627.service.AirportService;

import java.sql.SQLException;
import java.util.List;

public class AirportController {


    private final ApplicationLogger logger;
    private final AirportService airportService;

    public AirportController(ApplicationLogger logger, AirportService airportService) {
        this.logger = logger;
        this.airportService = airportService;
    }

    public AirportController() {
        this.airportService = new AirportService();
        logger = ApplicationLogger.getInstance();
    }

    public static final String INFO_CALLING_SERVICE = "Calling service for ";
    public static final String EXCEPTION_SERVICE_AIRPORTS = "Error obtaining airports: ";
    public static final String WARN_ORIGIN_AIRPORTS_NOT_FOUND = "Origin airports not found.";
    public static final String INFO_RETRIEVED_ORIGINS = "Successfully retrieved origin airports!\n";

    public void findOriginAirports() {
        logger.log(INFO_CALLING_SERVICE + "airports...", ApplicationLogger.LogLevel.INFO);

        List<AirportDTO> result;
        try {
            result = airportService.getOriginAirports();
        } catch (SQLException e) {
            logger.log(EXCEPTION_SERVICE_AIRPORTS + e.getMessage(), ApplicationLogger.LogLevel.ERROR);
            return;
        }

        if (result.isEmpty()) {
            logger.log(WARN_ORIGIN_AIRPORTS_NOT_FOUND, ApplicationLogger.LogLevel.WARNING);
        } else {
            var builder = new StringBuilder();
            builder.append(INFO_RETRIEVED_ORIGINS);
            for (var airport : result) {
                findOriginAirportMessage(airport, builder);
            }
            logger.log(builder.toString(), ApplicationLogger.LogLevel.INFO);
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
                .append(" is the origin of flight ")
                .append(airport.flightCode())
                .append(".")
                .append("\n");
    }

    public static final String WARN_NO_DESTINATIONS_FOUND = "No destinations found for origin airport ";
    public static final String INFO_RETRIEVED_DESTINATIONS = "Successfully retrieved destinations!\n";

    public void findDestinationAirports(String input) {
        var originResult = Verification.basicStringVerification("origin airport", input, null, 3, null);
        if (originResult.result() == null) {
            logger.log(originResult.message(), ApplicationLogger.LogLevel.ERROR);
            return;
        }

        logger.log(INFO_CALLING_SERVICE + "destinations...", ApplicationLogger.LogLevel.INFO);

        List<AirportDTO> results;
        try {
            results = airportService.getDestinationAirports(originResult.result());
        } catch (SQLException e) {
            logger.log(EXCEPTION_SERVICE_AIRPORTS + e.getMessage(), ApplicationLogger.LogLevel.ERROR);
            return;
        }

        if (results.isEmpty()) {
            var message = WARN_NO_DESTINATIONS_FOUND + originResult.result() + ".";
            logger.log(message, ApplicationLogger.LogLevel.WARNING);
            return;
        }
        var builder = new StringBuilder();
        builder.append(INFO_RETRIEVED_DESTINATIONS);
        for (var airport : results) {
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
