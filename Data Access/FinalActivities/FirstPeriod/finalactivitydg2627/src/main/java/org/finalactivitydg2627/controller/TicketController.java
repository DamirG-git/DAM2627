package org.finalactivitydg2627.controller;

import org.finalactivitydg2627.CommandExecutor;
import org.finalactivitydg2627.model.Tickets;
import org.finalactivitydg2627.repository.TicketRepo;
import org.finalactivitydg2627.service.TicketService;
import org.finalactivitydg2627.utils.ApplicationLogger;
import org.finalactivitydg2627.utils.ApplicationValidator;
import org.finalactivitydg2627.utils.MessageFormatter;
import org.finalactivitydg2627.utils.TicketPrinter;

import java.sql.SQLException;
import java.util.Arrays;

public class TicketController extends AbstractController {

    private final TicketService ticketService;
    private final TicketPrinter ticketPrinter;

    public TicketController(ApplicationLogger logger) {
        super(logger);
        ticketService = new TicketService(logger);
        ticketPrinter = new TicketPrinter(logger);
    }

    public TicketController(ApplicationLogger logger, TicketService ticketService, TicketPrinter ticketPrinter) {
        super(logger);
        this.ticketService = ticketService;
        this.ticketPrinter = ticketPrinter;
    }


    public static final String INFO_TICKET_PURCHASED = "Ticket successfully purchased.";
    public static final String INFO_PRINTING_TICKET = "Ticket information is being written to tickets folder...";


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

        TicketRepo.TicketPurchaseValues result;
        try {
            result = ticketService.buyTicket(ticket.getPassportCode(), ticket.getFlightCode(), ticket.getTravelDate());
        } catch (SQLException e) {
            logger.log(ERROR_TICKET_PURCHASE + e.getMessage(), ApplicationLogger.LogLevel.ERROR);
            return;
        }
        var print = args.length == CommandExecutor.BUY_MAX_ARGS;
        displayPurchaseResult(result, ticket, print);
    }

    public static final String INFO_TICKET_CANCELLED = "Ticket successfully canceled.";

    public static final String WARN_TICKET_ALREADY_CANCELLED = "Ticket was already cancelled.";
    public static final String WARN_TICKET_NOT_FOUND = "No ticket found on date: ";

    public static final String ERROR_TICKET_CANCELLATION_FAILED = "Ticket failed to cancel.\n";

    public void cancelTicket(String[] args) {

        var ticket = new Tickets();
        var passportNumResult = ticket.setPassportCode(args[1]);
        var dateResult = ApplicationValidator.basicDateVerification("travel date", args[2], null, null);

        var errors = MessageFormatter.multipleErrorsFormatter(passportNumResult, dateResult);

        if (!errors.isEmpty()) {
            logger.log(WARN_INPUT_VALIDATION_FAILED + errors, ApplicationLogger.LogLevel.WARNING);
            return;
        }
        try {
            var result = ticketService.cancelTicket(passportNumResult.result(), dateResult.result());
            switch (result) {
                case SUCCESS -> logger.log(
                        INFO_TICKET_CANCELLED,
                        ApplicationLogger.LogLevel.INFO);
                case PASSENGER_NOT_FOUND -> logger.log(
                        String.format(WARN_PASSENGER_NOT_FOUND_FORMAT_PASSPORT, passportNumResult.result()),
                        ApplicationLogger.LogLevel.INFO);
                case TICKET_ALREADY_CANCELLED -> logger.log(
                        WARN_TICKET_ALREADY_CANCELLED,
                        ApplicationLogger.LogLevel.WARNING);
                case TICKET_NOT_FOUND -> logger.log(
                        WARN_TICKET_NOT_FOUND + dateResult.result(),
                        ApplicationLogger.LogLevel.WARNING);
                default -> logger.log("Should not be possible." + result, ApplicationLogger.LogLevel.ERROR);
            }
        } catch (SQLException e) {
            logger.log(ERROR_TICKET_CANCELLATION_FAILED, ApplicationLogger.LogLevel.WARNING);
            logger.log(ERROR_TICKET_CANCELLATION_FAILED + e.getMessage(), ApplicationLogger.LogLevel.ERROR);
        }

    }

    private void displayPurchaseResult(TicketRepo.TicketPurchaseValues result, Tickets ticket, boolean print) {
        switch (result.ticketPurchaseResult) {
            case PASSENGER_ALREADY_HAS_TICKET_ON_SAME_DATE ->
                    logger.log(String.format(WARN_DUPLICATE_TICKET_PURCHASE_FORMAT, ticket.getPassportCode(), ticket.getTravelDate()),
                            ApplicationLogger.LogLevel.WARNING);

            case FLIGHT_NOT_FOUND ->
                    logger.log(String.format(WARN_FLIGHT_NOT_FOUND_FORMAT_IATA, ticket.getFlightCode()),
                            ApplicationLogger.LogLevel.WARNING);

            case NO_SEATS_AVAILABLE -> logger.log(
                    String.format(WARN_NO_SEATS_AVAILABLE_FORMAT_FLIGHT_DATE, ticket.getFlightCode(), ticket.getTravelDate()),
                    ApplicationLogger.LogLevel.WARNING);

            case SUCCESS -> {
                if (print) {
                    logger.log(INFO_PRINTING_TICKET, ApplicationLogger.LogLevel.INFO);
                    ticketPrinter.printTicket(ticket, result.ticketCode);
                }
                logger.log(INFO_TICKET_PURCHASED,
                        ApplicationLogger.LogLevel.INFO);
            }

            case PASSENGER_DOES_NOT_EXIST -> logger.log(
                    String.format(WARN_PASSENGER_NOT_FOUND_FORMAT_PASSPORT, ticket.getPassportCode()),
                    ApplicationLogger.LogLevel.WARNING);

            case FAILED_TO_CREATE_NEW_TICKET -> logger.log(WARN_TICKET_CREATION_FAILED,
                    ApplicationLogger.LogLevel.WARNING);

            default -> logger.log("Should not be possible." + result, ApplicationLogger.LogLevel.ERROR);
        }
    }

}
