package org.finalactivitydg2627.utils;

import org.finalactivitydg2627.model.Tickets;

import java.io.BufferedWriter;
import java.io.FileWriter;
import java.io.IOException;
import java.io.PrintWriter;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.time.LocalDateTime;

public class TicketPrinter {


    private final ApplicationLogger logger;

    public TicketPrinter(ApplicationLogger logger) {
        this.logger = logger;
    }


    public static final String INFO_TICKET_WRITING_COMPLETE = "Ticket has been written, file name: ";

    public static final String WARN_FAILED_TO_WRITE_TICKET = "Ticket writing to file has failed.";

    public static final String ERROR_FAILED_TO_CREATE_LOCATION = "Error on creating folder: ";


    public void printTicket(Tickets ticket, int number) {
        var ticketsDirectory = Paths.get(
                System.getProperty("user.dir"),
                "tickets"
        );
        try {
            Files.createDirectories(ticketsDirectory);
        } catch (IOException e) {
            logger.log(WARN_FAILED_TO_WRITE_TICKET, ApplicationLogger.LogLevel.WARNING);
            logger.log(ERROR_FAILED_TO_CREATE_LOCATION, ApplicationLogger.LogLevel.ERROR);
            return;
        }

        var ticketFile = "Ticket" + number + ".txt";
        try (var fileWriter = new PrintWriter(
                new BufferedWriter(
                        new FileWriter(
                                ticketsDirectory.resolve(ticketFile).
                                        toFile(), true)), true)) {
            var ticketString = formatTicket(ticket, number);
            fileWriter.println(ticketString);
            logger.log(INFO_TICKET_WRITING_COMPLETE + ticketFile, ApplicationLogger.LogLevel.INFO);
        } catch (IOException e) {
            logger.log(WARN_FAILED_TO_WRITE_TICKET, ApplicationLogger.LogLevel.WARNING);
            logger.log(ERROR_FAILED_TO_CREATE_LOCATION, ApplicationLogger.LogLevel.ERROR);

        }




    }

    private String formatTicket(Tickets ticket, int number) {
        return "Ticket with number " + number + "\n" +
                "Successfully purchased on " + LocalDateTime.now() + "\n" +
                "For passenger: " + ticket.getPassportCode() +
                " on flight " + ticket.getFlightCode() +
                " for date: " + ticket.getTravelDate();
    }

}



