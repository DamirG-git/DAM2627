package org.finalactivitydg2627.controllers;

import org.finalactivitydg2627.Utils.ApplicationLogger;
import org.finalactivitydg2627.controller.AirportController;
import org.finalactivitydg2627.dto.AirportDTO;
import org.finalactivitydg2627.service.AirportService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.sql.SQLException;
import java.util.Collections;
import java.util.List;

import static org.mockito.Mockito.*;

public class TestAirportController {

    private AirportService airportService;
    private ApplicationLogger logger;
    private AirportController controller;

    @BeforeEach
    void setUp() {
        airportService = mock(AirportService.class);
        logger = mock(ApplicationLogger.class);

        controller = new AirportController(logger, airportService);
    }

    @Test
    void getOriginAirports_success() throws SQLException {
        var airports = List.of(
                new AirportDTO("Frankfurt Airport", "FRA", "Frankfurt", "LH123"),
                new AirportDTO("San Francisco International", "SFO", "San Francisco", "UA456")
        );

        when(airportService.getOriginAirports()).thenReturn(airports);

        controller.findOriginAirports();

        verify(airportService).getOriginAirports();
        verify(logger).log(contains(AirportController.INFO_RETRIEVED_ORIGINS), eq(ApplicationLogger.LogLevel.INFO));
        verify(logger).log(contains("FRA"), eq(ApplicationLogger.LogLevel.INFO));
        verify(logger).log(contains("SFO"), eq(ApplicationLogger.LogLevel.INFO));
    }

    @Test
    void getOriginAirports_fail_noAirportsFound() throws SQLException {
        when(airportService.getOriginAirports()).thenReturn(Collections.emptyList());

        controller.findOriginAirports();
        verify(airportService).getOriginAirports();
        verify(logger).log(contains(AirportController.WARN_ORIGIN_AIRPORTS_NOT_FOUND),eq(ApplicationLogger.LogLevel.WARNING)
        );
    }

    @Test
    void getOriginAirports_fail_serviceException() throws SQLException {
        var error = "Test exception";
        when(airportService.getOriginAirports()).thenThrow(new SQLException(error));
        controller.findOriginAirports();
        verify(airportService).getOriginAirports();
        verify(logger).log(contains(error), eq(ApplicationLogger.LogLevel.ERROR));
    }

    @Test
    void getDestinationAirports_success() throws SQLException {
        var destination = "TST";
        when(airportService.getDestinationAirports(destination)).thenReturn(Collections.singletonList(new AirportDTO("San Francisco International", "TST", "San Francisco", "UA456")));

        controller.findDestinationAirports(destination);
        verify(airportService).getDestinationAirports(destination);
        verify(logger).log(contains("TST"), eq(ApplicationLogger.LogLevel.INFO));
    }

    @Test
    void getDestinationAirports_fail_noDestinationFound() throws SQLException {
        var destination = "TST";
        when(airportService.getDestinationAirports(destination)).thenReturn(Collections.emptyList());
        controller.findDestinationAirports(destination);
        verify(airportService).getDestinationAirports(destination);
        verify(logger).log(contains(AirportController.WARN_NO_DESTINATIONS_FOUND+destination), eq(ApplicationLogger.LogLevel.WARNING));
    }

    @Test
    void getDestinationAirports_fail_serviceException() throws SQLException {
        var error = "Test exception";
        var destination = "TST";
        when(airportService.getDestinationAirports(destination)).thenThrow(new SQLException(error));
        controller.findDestinationAirports(destination);
        verify(airportService).getDestinationAirports(destination);
        verify(logger).log(contains(AirportController.EXCEPTION_SERVICE_AIRPORTS+error), eq(ApplicationLogger.LogLevel.ERROR));
    }


}
