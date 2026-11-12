package org.finalactivitydg2627.controller;

import org.finalactivitydg2627.Utils.ApplicationLogger;
import org.finalactivitydg2627.Utils.Verification;
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
        var origin = Verification.basicIntegerVerification("source location",args[1], null,null);
        var destination = Verification.basicIntegerVerification("destination location",args[2], null,null);
        var date = Verification.basicDateVerification("date",args[3]);
        var errors = new StringBuilder();

        if(origin.result() == null){
            errors.append(origin.message());
        }
        if(destination.result() == null){
            errors.append(destination.message());
        }
        if(date.result() == null){
            errors.append(date.message());
        }
        if(!errors.isEmpty()){
            logger.log(errors.toString(), ApplicationLogger.LogLevel.WARNING);
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
            for(var flight: flights){
                stringBuilder.append("Flight code: ").append(flight.flightCode()).append(" has ").append(flight.availableSeats()).append(" available seats out of ").append(flight.totalSeats()).append(" seats.\n");
            }

            logger.log(stringBuilder.toString(), ApplicationLogger.LogLevel.INFO);
            System.out.println(stringBuilder);

        }catch(NullPointerException e){
            logger.log(e.toString(), ApplicationLogger.LogLevel.ERROR);
        }


    }

    public void getOrigin(String[] args) {
        logger.log("Verifying input values: " + Arrays.toString(args), ApplicationLogger.LogLevel.INFO);
        var origin = Verification.basicIntegerVerification("source location",args[1],null,null);

    }
}
