package org.finalactivitydg2627.controller;

import org.finalactivitydg2627.Utils.ApplicationLogger;
import org.finalactivitydg2627.Utils.MessageFormatter;
import org.finalactivitydg2627.Utils.Verification;
import org.finalactivitydg2627.dto.FlightDTO;
import org.finalactivitydg2627.service.FlightsService;

import java.util.Arrays;

public class FlightsController {

    private final ApplicationLogger logger;
    private final FlightsService flightsService;

    public FlightsController() {
        logger = ApplicationLogger.getInstance();
        flightsService = new FlightsService();
    }

    public void getFlights(String[] args) {
        logger.log("Verifying input values: " + Arrays.toString(args), ApplicationLogger.LogLevel.INFO);
        var origin = Verification.basicStringVerification("source location",args[1], 3,null,null);
        var destination = Verification.basicStringVerification("destination location",args[2], 3,null,null);
        var date = Verification.basicDateVerification("date",args[3]);


        var errors = MessageFormatter.multipleErrorsFormatter(origin,destination,date);
        if(!errors.isEmpty()){
            logger.log("Input validation failed: \n"+errors, ApplicationLogger.LogLevel.WARNING);
            return;
        }
        logger.log("Attempting to get flights!", ApplicationLogger.LogLevel.INFO);
        try{
            var flightsOptional = flightsService.getFlights(origin.result(),
                    destination.result(),
                    date.result());

            if(flightsOptional.isEmpty()){
                logger.log("No flights found with these parameters...", ApplicationLogger.LogLevel.INFO);
                return;
            }
            var flights = flightsOptional.get();
            var stringBuilder =  new StringBuilder();
            stringBuilder.append("Successfully retrieved flights!\n");
            for(var flight: flights){
                getFlightsMessage(flight, stringBuilder);
            }

            logger.log(stringBuilder.toString(), ApplicationLogger.LogLevel.INFO);

        }catch(NullPointerException e){
            logger.log(e.toString(), ApplicationLogger.LogLevel.ERROR);
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
