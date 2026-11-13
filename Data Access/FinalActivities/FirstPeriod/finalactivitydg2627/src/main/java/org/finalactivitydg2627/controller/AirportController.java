package org.finalactivitydg2627.controller;

import org.finalactivitydg2627.Utils.ApplicationLogger;
import org.finalactivitydg2627.service.AirportService;

import java.sql.SQLException;

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
            logger.log("Origin airports not found...", ApplicationLogger.LogLevel.ERROR);
        } else {
            logger.log("Origin airports found...", ApplicationLogger.LogLevel.INFO);
            var builder = new StringBuilder();
            for (var airport : result) {
                builder.append("Airport with code:")
                        .append(airport.airportCode())
                        .append(" called ")
                        .append(airport.airportName())
                        .append(" is origin for flight with code: ")
                        .append(airport.flightCode())
                        .append("\n");
            }
            logger.log(builder.toString(), ApplicationLogger.LogLevel.INFO);
            System.out.println(builder);
        }

    }


}
