package org.finalactivitydg2627.controllers;

import org.finalactivitydg2627.Utils.ApplicationLogger;
import org.finalactivitydg2627.controller.AirportController;
import org.finalactivitydg2627.dto.AirportDTO;
import org.finalactivitydg2627.repository.AbstractRepo;
import org.finalactivitydg2627.repository.AirportRepo;
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
    private final TestInputs testInputs = new TestInputs();

    @BeforeEach
    void setUp() {
        airportService = mock(AirportService.class);
        logger = mock(ApplicationLogger.class);

        controller = new AirportController(logger, airportService);
    }

    class TestInputs{
        AirportDTO airportDto1;
        AirportDTO airportDto2;


        public TestInputs(){
            airportDto1 = new AirportDTO("Frankfurt Airport","FRA","Frankfurt","LH123");
            airportDto2 = new AirportDTO("San Francisco International","SFO","San Francisco","UA456");
        }
    }

    @Test
    void getOriginAirports_success() throws SQLException {



        var airports = List.of(testInputs.airportDto1,testInputs.airportDto2);

        when(airportService.getOriginAirports()).thenReturn(airports);

        controller.findOriginAirports();

        verify(airportService).getOriginAirports();
        verify(logger).log(contains(AirportController.INFO_CALLING_SERVICE_ORIGIN_AIRPORTS),eq(ApplicationLogger.LogLevel.INFO));
        verify(logger).log(contains(AirportController.INFO_RETRIEVED_ORIGINS), eq(ApplicationLogger.LogLevel.INFO));
        verify(logger).log(contains("FRA"), eq(ApplicationLogger.LogLevel.INFO));
        verify(logger).log(contains("SFO"), eq(ApplicationLogger.LogLevel.INFO));
    }

    @Test
    void getOriginAirports_fail_noAirportsFound() throws SQLException {
        when(airportService.getOriginAirports()).thenReturn(Collections.emptyList());

        controller.findOriginAirports();
        verify(airportService).getOriginAirports();
        verify(logger).log(contains(AirportController.INFO_CALLING_SERVICE_ORIGIN_AIRPORTS),eq(ApplicationLogger.LogLevel.INFO));
        verify(logger).log(contains(AirportController.WARN_ORIGIN_AIRPORTS_NOT_FOUND), eq(ApplicationLogger.LogLevel.WARNING)
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
        testInputs.airportDto1.setAirportIata(destination);
        var returnValue = new AbstractRepo.RepoResultValue<>(Collections.singletonList(testInputs.airportDto1), AirportRepo.AirportStatus.FOUND);
        when(airportService.getDestinationAirports(destination)).thenReturn(returnValue);

        controller.findDestinationAirports(destination);
        verify(logger).log(contains(AirportController.INFO_CALLING_SERVICE_DESTINATION_AIRPORTS),eq(ApplicationLogger.LogLevel.INFO));
        verify(airportService).getDestinationAirports(destination);
        verify(logger).log(contains(AirportController.INFO_RETRIEVED_DESTINATIONS), eq(ApplicationLogger.LogLevel.INFO));
        verify(logger).log(contains("TST"), eq(ApplicationLogger.LogLevel.INFO));
    }
    @Test
    void getDestinationAirports_fail_invalidAirportCode() throws SQLException {
        var destination = "000";
        controller.findDestinationAirports(destination);
        verify(airportService,never()).getDestinationAirports(destination);
        verify(logger).log(contains(AirportController.WARN_INVALID_AIRPORT_CODE), eq(ApplicationLogger.LogLevel.WARNING));
    }

    @Test
    void getDestinationAirports_fail_noDestinationFound() throws SQLException {
        var destination = "TST";
        var returnValue = new AbstractRepo.RepoResultValue<List<AirportDTO>,AirportRepo.AirportStatus>(Collections.emptyList(), AirportRepo.AirportStatus.FOUND);
        when(airportService.getDestinationAirports(destination)).thenReturn(returnValue);
        controller.findDestinationAirports(destination);
        verify(airportService).getDestinationAirports(destination);
        verify(logger).log(contains(AirportController.WARN_NO_DESTINATIONS_FOUND + destination), eq(ApplicationLogger.LogLevel.WARNING));
    }

    @Test
    void getDestinationAirports_fail_noDestinationExists() throws SQLException {
        var destination = "TST";
        var returnValue = new AbstractRepo.RepoResultValue<List<AirportDTO>,AirportRepo.AirportStatus>(null, AirportRepo.AirportStatus.NOT_FOUND);
        when(airportService.getDestinationAirports(destination)).thenReturn(returnValue);
        controller.findDestinationAirports(destination);
        verify(airportService).getDestinationAirports(destination);
        verify(logger).log(contains(AirportController.WARN_NO_SUCH_AIRPORT_EXISTS + destination), eq(ApplicationLogger.LogLevel.WARNING));
    }

    @Test
    void getDestinationAirports_fail_serviceException() throws SQLException {
        var error = "Test exception";
        var destination = "TST";
        when(airportService.getDestinationAirports(destination)).thenThrow(new SQLException(error));
        controller.findDestinationAirports(destination);
        verify(airportService).getDestinationAirports(destination);
        verify(logger).log(contains(AirportController.ERROR_ON_OBTAINING_AIRPORTS + error), eq(ApplicationLogger.LogLevel.ERROR));
    }


}
