package org.finalactivitydg2627.controllers;

import org.finalactivitydg2627.Utils.ApplicationLogger;
import org.finalactivitydg2627.controller.FlightsController;
import org.finalactivitydg2627.dto.FlightDTO;
import org.finalactivitydg2627.service.FlightsService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.sql.SQLException;
import java.time.LocalDate;
import java.util.List;
import java.util.Optional;

import static org.mockito.Mockito.*;

public class TestFlightsController {


    private FlightsController controller;
    private FlightsService flightsService;
    private ApplicationLogger logger;

    private final TestInput testInput = new TestInput();

    private static class TestInput {
        public final String command = "COMMAND IRRELEVANT";
        public final String origin = "ORG";
        public final String destination = "DES";
        public final String dateRaw = "2001-01-01";
        public final LocalDate date = LocalDate.parse(dateRaw);

        public final String[] testInputsRawValid = {
                command,
                origin,
                destination,
                dateRaw
        };
        private final String[] testInputsRawInvalid = {
                command,
                "2",
                "2",
                "2-12-1314"
        };
    }


    @BeforeEach
    void setUp() {
        flightsService = mock(FlightsService.class);
        logger = mock(ApplicationLogger.class);

        controller = new FlightsController(logger, flightsService);
    }

    @Test
    void getFlights_success() throws SQLException {
        FlightDTO output = new FlightDTO("TST", "TST", "TestCityName", 10, 1);
        when(flightsService.getFlights(testInput.origin, testInput.destination, testInput.date)).thenReturn(Optional.of(List.of(output)));

        controller.findFlights(testInput.testInputsRawValid);

        verify(flightsService).getFlights(testInput.origin, testInput.destination, testInput.date);
        verify(logger).log(contains(FlightsController.INFO_SUCCESS_GET_FLIGHTS), eq(ApplicationLogger.LogLevel.INFO));
        verify(logger).log(contains("TST"), eq(ApplicationLogger.LogLevel.INFO));

    }

    @Test
    void getFlights_failure_invalidInputs() throws SQLException {
        when(flightsService.getFlights(testInput.origin, testInput.destination, testInput.date)).thenReturn(Optional.empty());

        controller.findFlights(testInput.testInputsRawInvalid);

        verify(flightsService, never()).getFlights(testInput.origin, testInput.destination, testInput.date);
        verify(logger).log(contains(FlightsController.WARN_VALIDATION_FAILED), eq(ApplicationLogger.LogLevel.WARNING));
    }

    @Test
    void getFlights_failure_noFlightsFound() throws SQLException {
        when(flightsService.getFlights(testInput.origin, testInput.destination, testInput.date)).thenReturn(Optional.empty());
        controller.findFlights(testInput.testInputsRawValid);

        verify(flightsService).getFlights(testInput.origin, testInput.destination, testInput.date);

        verify(logger).log(contains(
                        String.format(
                                FlightsController.WARN_NO_FLIGHTS_FOUND,
                                testInput.testInputsRawValid[1],
                                testInput.testInputsRawValid[2],
                                testInput.testInputsRawValid[3])),
                eq(ApplicationLogger.LogLevel.WARNING));
    }

    @Test
    void getFlights_failure_Exception() throws SQLException {
        when(flightsService.getFlights(testInput.origin, testInput.destination, testInput.date)).thenThrow(new SQLException("Test Exception"));

        controller.findFlights(testInput.testInputsRawValid);

        verify(flightsService).getFlights(testInput.origin, testInput.destination, testInput.date);
        verify(logger).log(contains("Test Exception"), eq(ApplicationLogger.LogLevel.ERROR));
    }
}
