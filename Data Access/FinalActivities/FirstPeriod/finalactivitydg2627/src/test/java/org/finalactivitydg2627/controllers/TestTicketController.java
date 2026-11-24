package org.finalactivitydg2627.controllers;

import org.finalactivitydg2627.AbstractTest;
import org.finalactivitydg2627.controller.TicketController;
import org.finalactivitydg2627.repository.TicketRepo;
import org.finalactivitydg2627.service.TicketService;
import org.finalactivitydg2627.utils.ApplicationLogger;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.Mock;

import java.sql.SQLException;
import java.time.LocalDate;

import static org.mockito.Mockito.*;

public class TestTicketController  extends AbstractTest {


    @Mock
    private TicketService ticketService;

    private TicketController controller;
    private final TestInputs testInput = new TestInputs();
    private final TicketRepo.TicketPurchaseValues ticketPurchaseValues  = new TicketRepo.TicketPurchaseValues();

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
        ticketPurchaseValues.ticketPurchaseResult = TicketRepo.TicketPurchaseResult.SUCCESS;
        ticketPurchaseValues.ticketCode = 0;
        when(ticketService.buyTicket(testInput.passport, testInput.flightCode, testInput.date)).thenReturn(ticketPurchaseValues);

        controller.purchaseTicket(testInput.inputs);
        verify(ticketService).buyTicket(testInput.passport, testInput.flightCode, testInput.date);
        verify(logger).log(contains(TicketController.INFO_TICKET_PURCHASED), eq(ApplicationLogger.LogLevel.INFO));
    }

    @Test
    void addTicket_failure_invalidInputValidation() throws SQLException {
        controller.purchaseTicket(testInputInvalid);

        verify(ticketService, never()).buyTicket(anyString(), anyString(), any());
        verify(logger).log(contains(TicketController.WARN_INPUT_VALIDATION_FAILED), eq(ApplicationLogger.LogLevel.WARNING));
    }


    @Test
    void addTicket_failure_passHasTicketOnSameDate() throws SQLException {
        ticketPurchaseValues.ticketPurchaseResult = TicketRepo.TicketPurchaseResult.PASSENGER_ALREADY_HAS_TICKET_ON_SAME_DATE;
        when(ticketService.buyTicket(testInput.passport, testInput.flightCode, testInput.date)).thenReturn(ticketPurchaseValues);

        controller.purchaseTicket(testInput.inputs);
        verify(ticketService).buyTicket(testInput.passport, testInput.flightCode, testInput.date);
        verify(logger).log(contains(String.format(TicketController.WARN_DUPLICATE_TICKET_PURCHASE_FORMAT,testInput.passport, testInput.date)), eq(ApplicationLogger.LogLevel.WARNING));

    }

    @Test
    void addTicket_failure_flightNotFound() throws SQLException {
        ticketPurchaseValues.ticketPurchaseResult = TicketRepo.TicketPurchaseResult.FLIGHT_NOT_FOUND;
        when(ticketService.buyTicket(testInput.passport, testInput.flightCode, testInput.date)).thenReturn(ticketPurchaseValues);

        controller.purchaseTicket(testInput.inputs);
        verify(ticketService).buyTicket(testInput.passport, testInput.flightCode, testInput.date);
        verify(logger).log(contains(String.format(TicketController.WARN_FLIGHT_NOT_FOUND_FORMAT_IATA, testInput.flightCode)), eq(ApplicationLogger.LogLevel.WARNING));

    }

    @Test
    void addTicket_failure_noSeatsAvailableOnFlight() throws SQLException {
        ticketPurchaseValues.ticketPurchaseResult = TicketRepo.TicketPurchaseResult.NO_SEATS_AVAILABLE;
        when(ticketService.buyTicket(testInput.passport, testInput.flightCode, testInput.date)).thenReturn(ticketPurchaseValues);


        controller.purchaseTicket(testInput.inputs);
        verify(ticketService).buyTicket(testInput.passport, testInput.flightCode, testInput.date);
        verify(logger).log(contains(String.format(TicketController.WARN_NO_SEATS_AVAILABLE_FORMAT_FLIGHT_DATE, testInput.flightCode, testInput.date)), eq(ApplicationLogger.LogLevel.WARNING));
    }

    @Test
    void addTicket_failure_passengerNotFound() throws SQLException {
        ticketPurchaseValues.ticketPurchaseResult = TicketRepo.TicketPurchaseResult.PASSENGER_DOES_NOT_EXIST;
        when(ticketService.buyTicket(testInput.passport, testInput.flightCode, testInput.date)).thenReturn(ticketPurchaseValues);

        controller.purchaseTicket(testInput.inputs);
        verify(ticketService).buyTicket(testInput.passport, testInput.flightCode, testInput.date);
        verify(logger).log(contains(String.format(TicketController.WARN_PASSENGER_NOT_FOUND_FORMAT_PASSPORT, testInput.passport)), eq(ApplicationLogger.LogLevel.WARNING));
    }

    @Test
    void addTicket_failure_failureOnInsert() throws SQLException {
        ticketPurchaseValues.ticketPurchaseResult = TicketRepo.TicketPurchaseResult.FAILED_TO_CREATE_NEW_TICKET;
        when(ticketService.buyTicket(testInput.passport, testInput.flightCode, testInput.date)).thenReturn(ticketPurchaseValues);

        controller.purchaseTicket(testInput.inputs);
        verify(ticketService).buyTicket(testInput.passport, testInput.flightCode, testInput.date);
        verify(logger).log(contains(TicketController.WARN_TICKET_CREATION_FAILED), eq(ApplicationLogger.LogLevel.WARNING));
    }

}
