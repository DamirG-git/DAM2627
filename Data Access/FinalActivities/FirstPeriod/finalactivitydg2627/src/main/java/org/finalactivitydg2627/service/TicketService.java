package org.finalactivitydg2627.service;

import org.finalactivitydg2627.Utils.ApplicationLogger;
import org.finalactivitydg2627.repository.TicketRepo;

import java.sql.SQLException;
import java.time.LocalDate;

public class TicketService {


    private final ApplicationLogger logger;
    private final TicketRepo ticketRepo;

    public TicketService() {
        this.logger = ApplicationLogger.getInstance();
        this.ticketRepo = new TicketRepo();
    }

    public TicketRepo.TicketPurchaseResult buyTicket(String passno, String flightCode, LocalDate date) {

        try {
            return ticketRepo.buyTicket(passno, flightCode, date);
        } catch (SQLException e) {
            logger.log("Error on purchasing new ticket: " + e.getMessage(), ApplicationLogger.LogLevel.ERROR);
            return TicketRepo.TicketPurchaseResult.EXCEPTION_ON_INSERT;
        }
    }

}
