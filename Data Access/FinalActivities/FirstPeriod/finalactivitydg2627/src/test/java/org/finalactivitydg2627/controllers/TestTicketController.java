package org.finalactivitydg2627.controllers;

import org.finalactivitydg2627.Utils.ApplicationLogger;
import org.finalactivitydg2627.controller.TicketController;
import org.finalactivitydg2627.repository.TicketRepo;
import org.finalactivitydg2627.service.TicketService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.sql.SQLException;
import java.time.LocalDate;

import static org.mockito.Mockito.*;

public class TestTicketController {

    private TicketController controller;
    private TicketService ticketService;
    private ApplicationLogger logger;
    private final TestInputs testInput = new TestInputs();

    @BeforeEach
    void setup() {
        ticketService = mock(TicketService.class);
        logger = mock(ApplicationLogger.class);

        controller = new TicketController(logger, ticketService);
    }

    private static class TestInputs {

        public String passport = "TEST123456";
        public String flightCode = "TES";
        public LocalDate date = LocalDate.now();
        public String dateRaw = date.toString();

        public String[] inputs = {"COMMAND_IRRELEVANT", passport, flightCode, dateRaw};
    }

    private static final String[] testInputInvalid = {"COMMAND_IRRELEVANT", "invalid", "tsts", "2001/13/30"};

    @Test
    void addTicket_success() throws SQLException {

        when(ticketService.buyTicket(testInput.passport, testInput.flightCode, testInput.date)).thenReturn(TicketRepo.TicketPurchaseResult.SUCCESS);

        controller.insertTicket(testInput.inputs);
        verify(ticketService).buyTicket(testInput.passport, testInput.flightCode, testInput.date);
        verify(logger).log(contains(TicketController.INFO_SUCCESS_TICKET), eq(ApplicationLogger.LogLevel.INFO));
    }

    @Test
    void addTicket_failure_invalidInputValidation() throws SQLException {
        controller.insertTicket(testInputInvalid);

        verify(ticketService, never()).buyTicket(anyString(), anyString(), any());
        verify(logger).log(contains(TicketController.WARN_INPUT_VALIDATION_FAIL), eq(ApplicationLogger.LogLevel.WARNING));
    }


    @Test
    void addTicket_failure_passHasTicketOnSameDate() throws SQLException {
        when(ticketService.buyTicket(testInput.passport, testInput.flightCode, testInput.date)).thenReturn(TicketRepo.TicketPurchaseResult.PASSENGER_ALREADY_HAS_TICKET_ON_SAME_DATE);

        controller.insertTicket(testInput.inputs);
        verify(ticketService).buyTicket(testInput.passport, testInput.flightCode, testInput.date);
        verify(logger).log(contains(String.format(TicketController.WARN_PASS_HAS_TICKET_ON_SAME_DATE_FORMATTED_PASSPORT_DATE,testInput.passport, testInput.date)), eq(ApplicationLogger.LogLevel.WARNING));

    }

    @Test
    void addTicket_failure_flightNotFound() throws SQLException {
        when(ticketService.buyTicket(testInput.passport, testInput.flightCode, testInput.date)).thenReturn(TicketRepo.TicketPurchaseResult.FLIGHT_NOT_FOUND);

        controller.insertTicket(testInput.inputs);
        verify(ticketService).buyTicket(testInput.passport, testInput.flightCode, testInput.date);
        verify(logger).log(contains(String.format(TicketController.WARN_FLIGHT_NOT_FOUND_FORMATTED, testInput.flightCode)), eq(ApplicationLogger.LogLevel.WARNING));

    }

    @Test
    void addTicket_failure_noSeatsAvailableOnFlight() throws SQLException {
        when(ticketService.buyTicket(testInput.passport, testInput.flightCode, testInput.date)).thenReturn(TicketRepo.TicketPurchaseResult.NO_SEATS_AVAILABLE);

        controller.insertTicket(testInput.inputs);
        verify(ticketService).buyTicket(testInput.passport, testInput.flightCode, testInput.date);
        verify(logger).log(contains(String.format(TicketController.WARN_NO_SEATS_AVAILABLE_FORMATTED_FLIGHT_DATE, testInput.flightCode, testInput.date)), eq(ApplicationLogger.LogLevel.WARNING));
    }

    @Test
    void addTicket_failure_failureOnInsert() throws SQLException {
        when(ticketService.buyTicket(testInput.passport, testInput.flightCode, testInput.date)).thenReturn(TicketRepo.TicketPurchaseResult.FAILED_TO_CREATE_NEW_TICKET);

        controller.insertTicket(testInput.inputs);
        verify(ticketService).buyTicket(testInput.passport, testInput.flightCode, testInput.date);
        verify(logger).log(contains(TicketController.WARN_FAILED_TO_CREATE_NEW_TICKET), eq(ApplicationLogger.LogLevel.WARNING));
    }

}
