package org.finalactivitydg2627.controllers;

import org.finalactivitydg2627.AbstractTest;
import org.finalactivitydg2627.controller.AirportController;
import org.finalactivitydg2627.dto.AirportDTO;
import org.finalactivitydg2627.repository.AbstractRepo;
import org.finalactivitydg2627.repository.AirportRepo;
import org.finalactivitydg2627.service.AirportService;
import org.finalactivitydg2627.utils.ApplicationLogger;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.Mock;

import java.sql.SQLException;
import java.util.Collections;
import java.util.List;

import static org.mockito.Mockito.*;

public class TestAirportController extends AbstractTest {

    @Mock
    private AirportService airportService;

    private AirportController controller;
    private final TestInputs testInputs = new TestInputs();

    public static final String TEST_EXCEPTION = "Test exception";

    @BeforeEach
    void setUp() {
        controller = new AirportController(logger, airportService);
    }

    static class TestInputs{
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

        controller.displayOriginAirports();

        verify(airportService).getOriginAirports();
        verify(logger).log(contains(AirportController.INFO_FETCHING_ORIGIN_AIRPORTS),eq(ApplicationLogger.LogLevel.INFO));
        verify(logger).log(contains(AirportController.INFO_ORIGIN_AIRPORTS_RETRIEVED), eq(ApplicationLogger.LogLevel.INFO));
        verify(logger).log(contains("FRA"), eq(ApplicationLogger.LogLevel.INFO));
        verify(logger).log(contains("SFO"), eq(ApplicationLogger.LogLevel.INFO));
    }

    @Test
    void getOriginAirports_fail_noAirportsFound() throws SQLException {
        when(airportService.getOriginAirports()).thenReturn(Collections.emptyList());

        controller.displayOriginAirports();
        verify(airportService).getOriginAirports();
        verify(logger).log(contains(AirportController.INFO_FETCHING_ORIGIN_AIRPORTS),eq(ApplicationLogger.LogLevel.INFO));
        verify(logger).log(contains(AirportController.WARN_NO_ORIGIN_AIRPORTS_FOUND), eq(ApplicationLogger.LogLevel.WARNING)
        );
    }

    @Test
    void getOriginAirports_fail_serviceException() throws SQLException {
        when(airportService.getOriginAirports()).thenThrow(new SQLException(TEST_EXCEPTION));
        controller.displayOriginAirports();
        verify(airportService).getOriginAirports();
        verify(logger).log(contains(TEST_EXCEPTION), eq(ApplicationLogger.LogLevel.ERROR));
    }

    @Test
    void getDestinationAirports_success() throws SQLException {
        var destination = "TST";
        testInputs.airportDto1.setAirportIata(destination);
        var returnValue = new AbstractRepo.RepoResultValue<>(Collections.singletonList(testInputs.airportDto1), AirportRepo.AirportStatus.FOUND);
        when(airportService.getDestinationAirports(destination)).thenReturn(returnValue);

        controller.displayDestinationAirports(destination);
        verify(logger).log(contains(AirportController.INFO_FETCHING_DESTINATION_AIRPORTS),eq(ApplicationLogger.LogLevel.INFO));
        verify(airportService).getDestinationAirports(destination);
        verify(logger).log(contains(AirportController.INFO_RETRIEVED_DESTINATIONS), eq(ApplicationLogger.LogLevel.INFO));
        verify(logger, times(2)).log(contains("TST"), eq(ApplicationLogger.LogLevel.INFO));
    }
    @Test
    void getDestinationAirports_fail_invalidAirportCode() throws SQLException {
        var destination = "000";
        controller.displayDestinationAirports(destination);
        verify(airportService,never()).getDestinationAirports(destination);
        verify(logger).log(contains(AirportController.WARN_INVALID_ORIGIN_AIRPORT_IATA), eq(ApplicationLogger.LogLevel.WARNING));
    }

    @Test
    void getDestinationAirports_fail_noDestinationFound() throws SQLException {
        var destination = "TST";
        var returnValue = new AbstractRepo.RepoResultValue<List<AirportDTO>,AirportRepo.AirportStatus>(Collections.emptyList(), AirportRepo.AirportStatus.FOUND);
        when(airportService.getDestinationAirports(destination)).thenReturn(returnValue);
        controller.displayDestinationAirports(destination);
        verify(airportService).getDestinationAirports(destination);
        verify(logger).log(contains(AirportController.WARN_NO_DESTINATIONS_FOUND + destination), eq(ApplicationLogger.LogLevel.WARNING));
    }

    @Test
    void getDestinationAirports_fail_noDestinationExists() throws SQLException {
        var destination = "TST";
        var returnValue = new AbstractRepo.RepoResultValue<List<AirportDTO>,AirportRepo.AirportStatus>(null, AirportRepo.AirportStatus.NOT_FOUND);
        when(airportService.getDestinationAirports(destination)).thenReturn(returnValue);
        controller.displayDestinationAirports(destination);
        verify(airportService).getDestinationAirports(destination);
        verify(logger).log(contains(AirportController.WARN_NO_AIRPORT_FOUND_IN_DB + destination), eq(ApplicationLogger.LogLevel.WARNING));
    }

    @Test
    void getDestinationAirports_fail_serviceException() throws SQLException {
        var destination = "TST";
        when(airportService.getDestinationAirports(destination)).thenThrow(new SQLException(TEST_EXCEPTION));
        controller.displayDestinationAirports(destination);
        verify(airportService).getDestinationAirports(destination);
        verify(logger).log(contains(AirportController.ERROR_FETCHING_AIRPORTS + TEST_EXCEPTION), eq(ApplicationLogger.LogLevel.ERROR));
    }


}
