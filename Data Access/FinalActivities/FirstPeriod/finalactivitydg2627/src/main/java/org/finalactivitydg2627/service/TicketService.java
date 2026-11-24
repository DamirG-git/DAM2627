package org.finalactivitydg2627.service;

import org.finalactivitydg2627.repository.TicketRepo;
import org.finalactivitydg2627.utils.ApplicationLogger;

import java.sql.SQLException;
import java.time.LocalDate;

public class TicketService  extends AbstractService {


    private final TicketRepo ticketRepo;

    public TicketService() {
        this.ticketRepo = new TicketRepo();
    }

    public TicketService(ApplicationLogger logger) {
        super(logger);
        ticketRepo = new TicketRepo(logger);
    }

    public TicketService(ApplicationLogger logger, TicketRepo ticketRepo) {
        super(logger);
        this.ticketRepo = ticketRepo;
    }

    public TicketRepo.TicketPurchaseValues buyTicket(String passportNum, String flightCode, LocalDate date) throws SQLException {
        return ticketRepo.buyTicket(passportNum, flightCode, date);
    }

    public TicketRepo.TicketCancellationResult cancelTicket(String passportNum, LocalDate date) throws SQLException {
        return ticketRepo.cancelTicket(passportNum,date);
    }

}
