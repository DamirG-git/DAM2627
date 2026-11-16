package org.finalactivitydg2627.controller;

import org.finalactivitydg2627.Utils.ApplicationLogger;
import org.finalactivitydg2627.Utils.MessageFormatter;
import org.finalactivitydg2627.Utils.Verification;
import org.finalactivitydg2627.repository.TicketRepo;
import org.finalactivitydg2627.service.TicketService;

import java.sql.SQLException;
import java.util.Arrays;

public class TicketController {

    private final ApplicationLogger logger;
    private final TicketService ticketService;

    public TicketController(ApplicationLogger logger, TicketService ticketService) {
        this.logger = logger;
        this.ticketService = ticketService;
    }

    public TicketController() {
        logger = ApplicationLogger.getInstance();
        ticketService = new TicketService();
    }

    public static final String WARN_INPUT_VALIDATION_FAIL = "Input validation failed: \n";
    public static final String WARN_PASS_HAS_TICKET_ON_SAME_DATE_FORMATTED =
            "Passenger (%s) already has a ticket for %s.";
    public static final String WARN_FLIGHT_NOT_FOUND_FORMATTED =
            "Flight (%s) does not exist.";
    public static final String WARN_NO_SEATS_AVAILABLE_FORMATTED =
            "No seats are available on flight ( %s ) on  %s .";
    public static final String INFO_SUCCESS_TICKET = "Ticket successfully purchased.";
    public static final String WARN_FAILED_TO_CREATE_NEW_TICKET = "Failed to create a new ticket.";
    public static final String ERROR_ON_PURCHASE = "Error on purchasing new ticket: ";


    public void insertTicket(String[] args) {
        logger.log("Verifying buying input: " + Arrays.toString(args), ApplicationLogger.LogLevel.INFO);
        var passportNumResult = Verification.basicStringVerification("passport", args[1], null, 10, null);
        var flightResult = Verification.basicStringVerification("flight code", args[2], null, 10, null);
        var dateResult = Verification.basicDateVerification("date", args[3], null, null);

        var errors = MessageFormatter.multipleErrorsFormatter(passportNumResult, flightResult, dateResult);

        if (!errors.isEmpty()) {
            logger.log( WARN_INPUT_VALIDATION_FAIL + errors, ApplicationLogger.LogLevel.WARNING);
            return;
        }
        var passport = passportNumResult.result();
        var flight = flightResult.result();
        var date = dateResult.result();

        TicketRepo.TicketPurchaseResult result;
        try {
            result = ticketService.buyTicket(passport, flight, date);
        } catch (SQLException e) {
            logger.log(ERROR_ON_PURCHASE + e.getMessage(), ApplicationLogger.LogLevel.ERROR);
            return;
        }

        switch (result) {
            case PASSENGER_ALREADY_HAS_TICKET_ON_SAME_DATE ->
                    logger.log(String.format(WARN_PASS_HAS_TICKET_ON_SAME_DATE_FORMATTED,passport, date),
                            ApplicationLogger.LogLevel.WARNING);

            case FLIGHT_NOT_FOUND -> logger.log(String.format(WARN_FLIGHT_NOT_FOUND_FORMATTED,flight),
                    ApplicationLogger.LogLevel.WARNING);

            case NO_SEATS_AVAILABLE -> logger.log(
                    String.format(WARN_NO_SEATS_AVAILABLE_FORMATTED,flight,date),
                    ApplicationLogger.LogLevel.WARNING);

            case SUCCESS -> logger.log(INFO_SUCCESS_TICKET,
                    ApplicationLogger.LogLevel.INFO);

            case FAILED_TO_CREATE_NEW_TICKET -> logger.log(WARN_FAILED_TO_CREATE_NEW_TICKET,
                    ApplicationLogger.LogLevel.WARNING);

            default -> logger.log("Should not be possible." + result, ApplicationLogger.LogLevel.ERROR);
        }


    }

}
