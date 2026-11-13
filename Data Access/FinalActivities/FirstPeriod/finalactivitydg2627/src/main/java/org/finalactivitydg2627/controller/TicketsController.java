package org.finalactivitydg2627.controller;

import org.finalactivitydg2627.Utils.ApplicationLogger;
import org.finalactivitydg2627.Utils.MessageFormatter;
import org.finalactivitydg2627.Utils.Verification;
import org.finalactivitydg2627.service.TicketService;

import java.util.Arrays;

public class TicketsController {

    private final ApplicationLogger logger;
    private final TicketService ticketService;

    public TicketsController() {
        logger = ApplicationLogger.getInstance();
        ticketService = new TicketService();
    }

    public void addTicket(String[] args) {
        logger.log("Verifying buying input: " + Arrays.toString(args), ApplicationLogger.LogLevel.INFO);
        var passnoResult = Verification.basicStringVerification("passport", args[1], null, 10, null);
        var flightResult = Verification.basicStringVerification("flight code", args[2], null, 10, null);
        var dateResult = Verification.basicDateVerification("date", args[3]);

        var errors = MessageFormatter.multipleErrorsFormatter(passnoResult, flightResult, dateResult);

        if (!errors.isEmpty()) {
            logger.log("Input validation failed: \n" + errors, ApplicationLogger.LogLevel.WARNING);
            return;
        }
        var passport = passnoResult.result();
        var flight = flightResult.result();
        var date = dateResult.result();

        var result = ticketService.buyTicket(passport, flight, date);

        switch (result) {
            case PASSENGER_ALREADY_HAS_TICKET_ON_SAME_DATE ->
                    logger.log("Passenger (" +passport +") already has a ticket for " + date + ".",
                    ApplicationLogger.LogLevel.WARNING);

            case FLIGHT_NOT_FOUND ->
                    logger.log("Flight (" + flight + ") does not exist.",
                    ApplicationLogger.LogLevel.WARNING);

            case NO_SEATS_AVAILABLE ->
                    logger.log("No seats are available on flight (" +flight + ") on " + date + ".",
                    ApplicationLogger.LogLevel.WARNING);

            case SUCCESS ->
                    logger.log("Ticket successfully purchased.",
                    ApplicationLogger.LogLevel.INFO);

            case FAILURE_ON_INSERT ->
                    logger.log("Failed to create a new ticket.",
                    ApplicationLogger.LogLevel.WARNING);

            case EXCEPTION_ON_INSERT ->
                    logger.log("Exception on adding new ticket.",
                    ApplicationLogger.LogLevel.ERROR);

            default -> logger.log("Unknown result " + result, ApplicationLogger.LogLevel.WARNING);
        }


    }

}
