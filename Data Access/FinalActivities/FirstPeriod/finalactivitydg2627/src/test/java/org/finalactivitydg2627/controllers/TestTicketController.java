package org.finalactivitydg2627.controllers;

import org.finalactivitydg2627.AbstractTest;
import org.finalactivitydg2627.controller.TicketController;
import org.finalactivitydg2627.repository.TicketRepo;
import org.finalactivitydg2627.service.TicketService;
import org.finalactivitydg2627.utils.ApplicationLogger;
import org.finalactivitydg2627.utils.TicketPrinter;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.Mock;

import java.sql.SQLException;
import java.time.LocalDate;

import static org.mockito.Mockito.*;

public class TestTicketController extends AbstractTest {


    @Mock
    private TicketService ticketService;

    @Mock
    private TicketPrinter ticketPrinter;

    private TicketController controller;
    private final TestInput testInput = new TestInput();
    private final TicketRepo.TicketPurchaseValues ticketPurchaseValues = new TicketRepo.TicketPurchaseValues();

    @BeforeEach
    void setup() {
        ticketService = mock(TicketService.class);
        logger = mock(ApplicationLogger.class);
        controller = new TicketController(logger, ticketService, ticketPrinter);
    }

    private static class TestInput {

        public final String passport = "TEST123456";
        public final String flightCode = "TES";

        public final LocalDate date = LocalDate.now();
        public final String dateRaw = date.toString();

        private final String commandIrrelevant = "COMMAND_IRRELEVANT";
        private final String printCommand = "[--print]";

        public final String[] inputsBuyTicket = {commandIrrelevant, passport, flightCode, dateRaw};
        public final String[] inputsBuyTicketPrint = {commandIrrelevant, passport, flightCode, dateRaw, printCommand};
        public final String[] inputsCancelTicket = {commandIrrelevant, passport, dateRaw};
        public final String[] inputsCancelTicketInvalid = {commandIrrelevant, "INVALID", "2001/13/30"};
        public final String[] testInputBuyInvalid = {commandIrrelevant, "INVALID", "TEST", "2001/13/30"};
    }

    @Test
    void addTicket_success() throws SQLException {
        ticketPurchaseValues.ticketPurchaseResult = TicketRepo.TicketPurchaseResult.SUCCESS;
        ticketPurchaseValues.ticketCode = 0;

        when(ticketService.buyTicket(testInput.passport, testInput.flightCode, testInput.date)).thenReturn(ticketPurchaseValues);

        controller.purchaseTicket(testInput.inputsBuyTicket);

        verify(ticketService).buyTicket(testInput.passport, testInput.flightCode, testInput.date);
        verify(logger).log(contains(TicketController.INFO_TICKET_PURCHASED), eq(ApplicationLogger.LogLevel.INFO));
    }

    @Test
    void addTicket_success_print() throws SQLException {
        ticketPurchaseValues.ticketPurchaseResult = TicketRepo.TicketPurchaseResult.SUCCESS;
        ticketPurchaseValues.ticketCode = 0;
        when(ticketService.buyTicket(testInput.passport, testInput.flightCode, testInput.date)).thenReturn(ticketPurchaseValues);

        controller.purchaseTicket(testInput.inputsBuyTicketPrint);

        verify(ticketService).buyTicket(testInput.passport, testInput.flightCode, testInput.date);
        verify(logger).log(contains(TicketController.INFO_TICKET_PURCHASED), eq(ApplicationLogger.LogLevel.INFO));
        verify(logger).log(contains(TicketController.INFO_PRINTING_TICKET), eq(ApplicationLogger.LogLevel.INFO));
    }

    @Test
    void addTicket_failure_invalidInputValidation() throws SQLException {
        controller.purchaseTicket(testInput.testInputBuyInvalid);

        verify(ticketService, never()).buyTicket(anyString(), anyString(), any());
        verify(logger).log(contains(TicketController.WARN_INPUT_VALIDATION_FAILED), eq(ApplicationLogger.LogLevel.WARNING));
    }


    @Test
    void addTicket_failure_passHasTicketOnSameDate() throws SQLException {
        ticketPurchaseValues.ticketPurchaseResult = TicketRepo.TicketPurchaseResult.PASSENGER_ALREADY_HAS_TICKET_ON_SAME_DATE;
        when(ticketService.buyTicket(testInput.passport, testInput.flightCode, testInput.date)).thenReturn(ticketPurchaseValues);

        controller.purchaseTicket(testInput.inputsBuyTicket);

        verify(ticketService).buyTicket(testInput.passport, testInput.flightCode, testInput.date);
        verify(logger).log(contains(String.format(TicketController.WARN_DUPLICATE_TICKET_PURCHASE_FORMAT, testInput.passport, testInput.date)), eq(ApplicationLogger.LogLevel.WARNING));
    }

    @Test
    void addTicket_failure_flightNotFound() throws SQLException {
        ticketPurchaseValues.ticketPurchaseResult = TicketRepo.TicketPurchaseResult.FLIGHT_NOT_FOUND;
        when(ticketService.buyTicket(testInput.passport, testInput.flightCode, testInput.date)).thenReturn(ticketPurchaseValues);

        controller.purchaseTicket(testInput.inputsBuyTicket);

        verify(ticketService).buyTicket(testInput.passport, testInput.flightCode, testInput.date);
        verify(logger).log(contains(String.format(TicketController.WARN_FLIGHT_NOT_FOUND_FORMAT_IATA, testInput.flightCode)), eq(ApplicationLogger.LogLevel.WARNING));
    }

    @Test
    void addTicket_failure_noSeatsAvailableOnFlight() throws SQLException {
        ticketPurchaseValues.ticketPurchaseResult = TicketRepo.TicketPurchaseResult.NO_SEATS_AVAILABLE;
        when(ticketService.buyTicket(testInput.passport, testInput.flightCode, testInput.date)).thenReturn(ticketPurchaseValues);


        controller.purchaseTicket(testInput.inputsBuyTicket);

        verify(ticketService).buyTicket(testInput.passport, testInput.flightCode, testInput.date);
        verify(logger).log(contains(String.format(TicketController.WARN_NO_SEATS_AVAILABLE_FORMAT_FLIGHT_DATE, testInput.flightCode, testInput.date)), eq(ApplicationLogger.LogLevel.WARNING));
    }

    @Test
    void addTicket_failure_passengerNotFound() throws SQLException {
        ticketPurchaseValues.ticketPurchaseResult = TicketRepo.TicketPurchaseResult.PASSENGER_DOES_NOT_EXIST;
        when(ticketService.buyTicket(testInput.passport, testInput.flightCode, testInput.date)).thenReturn(ticketPurchaseValues);

        controller.purchaseTicket(testInput.inputsBuyTicket);

        verify(ticketService).buyTicket(testInput.passport, testInput.flightCode, testInput.date);
        verify(logger).log(contains(String.format(TicketController.WARN_PASSENGER_NOT_FOUND_FORMAT_PASSPORT, testInput.passport)), eq(ApplicationLogger.LogLevel.WARNING));
    }

    @Test
    void addTicket_failure_failureOnInsert() throws SQLException {
        ticketPurchaseValues.ticketPurchaseResult = TicketRepo.TicketPurchaseResult.FAILED_TO_CREATE_NEW_TICKET;
        when(ticketService.buyTicket(testInput.passport, testInput.flightCode, testInput.date)).thenReturn(ticketPurchaseValues);

        controller.purchaseTicket(testInput.inputsBuyTicket);

        verify(ticketService).buyTicket(testInput.passport, testInput.flightCode, testInput.date);
        verify(logger).log(contains(TicketController.WARN_TICKET_CREATION_FAILED), eq(ApplicationLogger.LogLevel.WARNING));
    }

    @Test
    void cancelTicket_success() throws SQLException {
        when(ticketService.cancelTicket(testInput.passport, testInput.date)).thenReturn(TicketRepo.TicketCancellationResult.SUCCESS);
        controller.cancelTicket(testInput.inputsCancelTicket);

        verify(ticketService).cancelTicket(testInput.passport, testInput.date);
        verify(logger).log(contains(TicketController.INFO_TICKET_CANCELLED), eq(ApplicationLogger.LogLevel.INFO));
    }

    @Test
    void cancelTicket_failure_validation() throws SQLException {
        controller.cancelTicket(testInput.inputsCancelTicketInvalid);

        verify(ticketService, never()).cancelTicket(any(), any());
        verify(logger).log(contains(TicketController.WARN_INPUT_VALIDATION_FAILED), eq(ApplicationLogger.LogLevel.WARNING));
    }

    @Test
    void cancelTicket_failure_ticketAlreadyCancelled() throws SQLException {
        when(ticketService.cancelTicket(testInput.passport, testInput.date)).thenReturn(TicketRepo.TicketCancellationResult.TICKET_ALREADY_CANCELLED);
        controller.cancelTicket(testInput.inputsCancelTicket);

        verify(ticketService).cancelTicket(testInput.passport, testInput.date);
        verify(logger).log(contains(TicketController.WARN_TICKET_ALREADY_CANCELLED), eq(ApplicationLogger.LogLevel.WARNING));
    }

    @Test
    void cancelTicket_failure_passengerNotFound() throws SQLException {
        when(ticketService.cancelTicket(testInput.passport, testInput.date)).thenReturn(TicketRepo.TicketCancellationResult.PASSENGER_NOT_FOUND);
        controller.cancelTicket(testInput.inputsCancelTicket);

        verify(ticketService).cancelTicket(testInput.passport, testInput.date);
        verify(logger).log(contains(String.format(TicketController.WARN_PASSENGER_NOT_FOUND_FORMAT_PASSPORT, testInput.passport)), eq(ApplicationLogger.LogLevel.WARNING));
    }

    @Test
    void cancelTicket_failure_ticketNotFound() throws SQLException {
        when(ticketService.cancelTicket(testInput.passport, testInput.date)).thenReturn(TicketRepo.TicketCancellationResult.TICKET_NOT_FOUND);
        controller.cancelTicket(testInput.inputsCancelTicket);

        verify(ticketService).cancelTicket(testInput.passport, testInput.date);
        verify(logger).log(contains(TicketController.WARN_TICKET_NOT_FOUND + testInput.date), eq(ApplicationLogger.LogLevel.WARNING));
    }


}
