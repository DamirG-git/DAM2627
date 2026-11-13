package org.finalactivitydg2627.controller;

import org.finalactivitydg2627.Utils.ApplicationLogger;
import org.finalactivitydg2627.Utils.Verification;
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
            logger.log("Origin airports found...", ApplicationLogger.LogLevel.INFO);
            var builder = new StringBuilder();
            for (var airport : result) {
                builder.append("Airport ")
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
            logger.log(builder.toString(), ApplicationLogger.LogLevel.INFO);
            System.out.println(builder);
        }

    }

    public void getDestinationAirports(String input) {
        var originResult = Verification.basicIntegerVerification("destination", input, null, null);
        if (originResult.result() == null) {
            logger.log(originResult.message(), ApplicationLogger.LogLevel.ERROR);
            System.out.println(originResult.message());
            return;
        }
        logger.log("Calling service for destinations...", ApplicationLogger.LogLevel.INFO);
        var results = airportService.getDestinationAirports(originResult.result());
        if (results.isEmpty()) {
            var message = "No destinations found for origin airport " + originResult.result() + ".";
            logger.log(message + " found!", ApplicationLogger.LogLevel.WARNING);
            return;
        }
        logger.log("Destination airports found...", ApplicationLogger.LogLevel.INFO);
        var builder = new StringBuilder();
        for (var airport : results) {
            builder.append("Airport ")
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
        logger.log(builder.toString(), ApplicationLogger.LogLevel.INFO);
        System.out.println(builder);


    }


}
