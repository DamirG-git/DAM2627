package org.finalactivitydg2627.service;

import org.finalactivitydg2627.Utils.ApplicationLogger;
import org.finalactivitydg2627.repository.TicketRepo;

import java.time.LocalDate;

public class TicketService {


    private final ApplicationLogger logger;
    private final TicketRepo ticketRepo;

    public TicketService() {
        this.logger = ApplicationLogger.getInstance();
        this.ticketRepo = new TicketRepo();
    }

    public String buyTicket(String passno, String flight_code, LocalDate date) {




        return "";


    }

}
