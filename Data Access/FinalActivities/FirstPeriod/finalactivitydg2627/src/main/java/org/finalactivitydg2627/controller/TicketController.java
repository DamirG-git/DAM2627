package org.finalactivitydg2627.controller;

import org.finalactivitydg2627.model.Tickets;
import org.finalactivitydg2627.repository.TicketRepo;
import org.finalactivitydg2627.service.TicketService;
import org.finalactivitydg2627.utils.ApplicationLogger;
import org.finalactivitydg2627.utils.MessageFormatter;

import java.sql.SQLException;
import java.util.Arrays;

public class TicketController extends AbstractController {

    private final TicketService ticketService;

    public TicketController(ApplicationLogger logger) {
        super(logger);
        ticketService = new TicketService(logger);
    }

    public TicketController(ApplicationLogger logger, TicketService ticketService) {
        super(logger);
        this.ticketService = ticketService;
    }


    public static final String INFO_TICKET_PURCHASED = "Ticket successfully purchased.";

    public static final String WARN_INPUT_VALIDATION_FAILED = "Input validation failed: \n";
    public static final String WARN_DUPLICATE_TICKET_PURCHASE_FORMAT =
            "Passenger (%s) already has a ticket for %s.";
    public static final String WARN_FLIGHT_NOT_FOUND_FORMAT_IATA =
            "Flight (%s) does not exist.";
    public static final String WARN_NO_SEATS_AVAILABLE_FORMAT_FLIGHT_DATE =
            "No seats are available on flight ( %s ) on  %s .";
    public static final String WARN_TICKET_CREATION_FAILED = "Failed to create a new ticket.";
    public static final String WARN_PASSENGER_NOT_FOUND_FORMAT_PASSPORT = "Passenger (%s) does not exist!";

    public static final String ERROR_TICKET_PURCHASE = "Error on purchasing new ticket: ";


    public void purchaseTicket(String[] args) {
        logger.log("Validating ticket purchase input: " + Arrays.toString(args), ApplicationLogger.LogLevel.INFO);
        var ticket = new Tickets();

        var passportNumResult = ticket.setPassportCode(args[1]);
        var flightResult = ticket.setFlightCode(args[2]);
        var dateResult = ticket.setTravelDate(args[3]);

        var errors = MessageFormatter.multipleErrorsFormatter(passportNumResult, flightResult, dateResult);

        if (!errors.isEmpty()) {
            logger.log(WARN_INPUT_VALIDATION_FAILED + errors, ApplicationLogger.LogLevel.WARNING);
            return;
        }

        TicketRepo.TicketPurchaseResult result;
        try {
            result = ticketService.buyTicket(ticket.getPassportCode(), ticket.getFlightCode(), ticket.getTravelDate());
        } catch (SQLException e) {
            logger.log(ERROR_TICKET_PURCHASE + e.getMessage(), ApplicationLogger.LogLevel.ERROR);
            return;
        }
        displayPurchaseResult(result, ticket);
    }

    private void displayPurchaseResult(TicketRepo.TicketPurchaseResult result, Tickets ticket) {
        switch (result) {
            case PASSENGER_ALREADY_HAS_TICKET_ON_SAME_DATE ->
                    logger.log(String.format(WARN_DUPLICATE_TICKET_PURCHASE_FORMAT, ticket.getPassportCode(), ticket.getTravelDate()),
                            ApplicationLogger.LogLevel.WARNING);

            case FLIGHT_NOT_FOUND -> logger.log(String.format(WARN_FLIGHT_NOT_FOUND_FORMAT_IATA, ticket.getFlightCode()),
                    ApplicationLogger.LogLevel.WARNING);

            case NO_SEATS_AVAILABLE -> logger.log(
                    String.format(WARN_NO_SEATS_AVAILABLE_FORMAT_FLIGHT_DATE, ticket.getFlightCode(), ticket.getTravelDate()),
                    ApplicationLogger.LogLevel.WARNING);

            case SUCCESS -> logger.log(INFO_TICKET_PURCHASED,
                    ApplicationLogger.LogLevel.INFO);

            case PASSENGER_DOES_NOT_EXIST -> logger.log(
                    String.format(WARN_PASSENGER_NOT_FOUND_FORMAT_PASSPORT, ticket.getPassportCode()),
                    ApplicationLogger.LogLevel.WARNING);

            case FAILED_TO_CREATE_NEW_TICKET -> logger.log(WARN_TICKET_CREATION_FAILED,
                    ApplicationLogger.LogLevel.WARNING);

            default -> logger.log("Should not be possible." + result, ApplicationLogger.LogLevel.ERROR);
        }
    }

}
