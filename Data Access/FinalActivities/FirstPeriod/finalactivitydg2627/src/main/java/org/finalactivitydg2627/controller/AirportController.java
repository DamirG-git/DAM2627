package org.finalactivitydg2627.controller;

import org.finalactivitydg2627.Utils.ApplicationLogger;
import org.finalactivitydg2627.Utils.Verification;
import org.finalactivitydg2627.dto.AirportDTO;
import org.finalactivitydg2627.service.AirportService;

public class AirportController {


    private final ApplicationLogger logger;
    private final AirportService airportService;

    public AirportController() {
        this.airportService = new AirportService();
        logger = ApplicationLogger.getInstance();
    }

    public void getOriginAirports() {
        logger.log("Calling service for airports...", ApplicationLogger.LogLevel.INFO);
        var result = airportService.getOriginAirports();
        if (result.isEmpty()) {

            logger.log("Flight origin airports not found.", ApplicationLogger.LogLevel.ERROR);
        } else {
            var builder = new StringBuilder();
            builder.append("Successfully retrieved origin airports!\n");
            for (var airport : result) {
                getOriginAirportMessage(airport, builder);
            }
            logger.log(builder.toString(), ApplicationLogger.LogLevel.INFO);
        }

    }

    private static void getOriginAirportMessage(AirportDTO airport, StringBuilder builder) {
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

    public void getDestinationAirports(String input) {
        var originResult = Verification.basicStringVerification("destination", input,null ,3,null);
        if (originResult.result() == null) {
            logger.log(originResult.message(), ApplicationLogger.LogLevel.ERROR);
            return;
        }
        logger.log("Calling service for destinations...", ApplicationLogger.LogLevel.INFO);
        var results = airportService.getDestinationAirports(originResult.result());
        if (results.isEmpty()) {
            var message = "No destinations found for origin airport " + originResult.result() + ".";
            logger.log(message + " found!", ApplicationLogger.LogLevel.WARNING);
            return;
        }
        var builder = new StringBuilder();
        builder.append("Successfully retrieved destinations!\n");
        for (var airport : results) {
            getDestinationAirportMessage(airport, builder, originResult);
        }
        logger.log(builder.toString(), ApplicationLogger.LogLevel.INFO);


    }

    private static void getDestinationAirportMessage(AirportDTO airport, StringBuilder builder, Verification.ResultValue<String> originResult) {
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
