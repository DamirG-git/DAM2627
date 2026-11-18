package org.finalactivitydg2627.service;

import org.finalactivitydg2627.Utils.ApplicationLogger;
import org.finalactivitydg2627.repository.TicketRepo;

import java.sql.SQLException;
import java.time.LocalDate;

public class TicketService  extends AbstractService {


    private final TicketRepo ticketRepo;

    public TicketService() {
        this.ticketRepo = new TicketRepo();
    }

    public TicketService(ApplicationLogger logger, TicketRepo ticketRepo) {
        super(logger);
        this.ticketRepo = ticketRepo;
    }

    public TicketRepo.TicketPurchaseResult buyTicket(String passportNum, String flightCode, LocalDate date) throws SQLException {

        return ticketRepo.buyTicket(passportNum, flightCode, date);

    }

}
