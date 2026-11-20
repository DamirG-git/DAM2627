package org.finalactivitydg2627.controllers;

import org.finalactivitydg2627.AbstractTest;
import org.finalactivitydg2627.controller.PassengerController;
import org.finalactivitydg2627.service.PassengerService;
import org.finalactivitydg2627.utils.ApplicationLogger;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.Mock;
import org.xml.sax.SAXException;

import javax.xml.parsers.ParserConfigurationException;
import java.io.IOException;
import java.nio.file.Path;
import java.sql.SQLException;

import static org.mockito.Mockito.*;

public class TestPassengerController  extends AbstractTest {


    @Mock
    private PassengerService passengerService;

    private PassengerController controller;

    @BeforeEach
    void setUp() {
        passengerService = mock(PassengerService.class);
        logger = mock(ApplicationLogger.class);
        controller = new PassengerController(logger, passengerService);
    }


    private final String testXml = "src/test/resources/test.xml";
    private final Path testLoc = Path.of(testXml);

    @Test
    void addPassengers_success() throws ParserConfigurationException,
            IOException, SAXException,
            PassengerService.PassengerValidationException, SQLException {
        controller.importPassengersFromXml(testXml);

        verify(passengerService).addPassengerFromXML(testLoc);
        verify(logger).log(contains(PassengerController.INFO_IMPORTING_PASSENGERS_FROM_XML), eq(ApplicationLogger.LogLevel.INFO));
    }

    @Test
    void addPassengers_fail_xmlFileInvalid() throws ParserConfigurationException,
            IOException, SAXException,
            PassengerService.PassengerValidationException, SQLException {
        controller.importPassengersFromXml("invalid.xml");

        verify(passengerService, never()).addPassengerFromXML(testLoc);
        verify(logger).log(contains(PassengerController.WARN_INVALID_XML_FILE_PATH), eq(ApplicationLogger.LogLevel.WARNING));

    }

    @Test
    void addPassenger_fail_exception_DBPassengerInsertion() throws PassengerService.PassengerValidationException, SQLException, ParserConfigurationException, IOException, SAXException {
        doThrow(new SQLException("Test Exception")).when(passengerService).addPassengerFromXML(testLoc);
        controller.importPassengersFromXml(testXml);


        verify(logger).log(contains(PassengerController.ERROR_INSERTING_PASSENGER + "Test Exception"), eq(ApplicationLogger.LogLevel.ERROR));
        verify(logger).log(contains(PassengerController.ERROR_MISSING_SQL_STATE + "Test Exception"), eq(ApplicationLogger.LogLevel.ERROR));
    }


    @Test
    void addPassenger_fail_exception_ParserExc() throws ParserConfigurationException,
            IOException, SAXException,
            PassengerService.PassengerValidationException, SQLException {
        doThrow(new ParserConfigurationException("Test Exception")).when(passengerService).addPassengerFromXML(testLoc);

        controller.importPassengersFromXml(testXml);

        verify(logger).log(contains("Test Exception"), eq(ApplicationLogger.LogLevel.ERROR));
    }

    @Test
    void addPassenger_fail_exception_SaxExc() throws ParserConfigurationException,
            IOException, SAXException,
            PassengerService.PassengerValidationException, SQLException {
        doThrow(new SAXException("Test Exception")).when(passengerService).addPassengerFromXML(testLoc);

        controller.importPassengersFromXml(testXml);

        verify(logger).log(contains("Test Exception"), eq(ApplicationLogger.LogLevel.ERROR));
    }

    @Test
    void addPassenger_fail_exception_IOExc()
            throws ParserConfigurationException,
            IOException, SAXException,
            PassengerService.PassengerValidationException, SQLException {
        doThrow(new IOException("Test Exception")).when(passengerService).addPassengerFromXML(testLoc);

        controller.importPassengersFromXml(testXml);


        verify(logger).log(contains("Test Exception"), eq(ApplicationLogger.LogLevel.ERROR));
    }

    @Test
    void addPassenger_fail_exception_PassValidExc()
            throws ParserConfigurationException,
            IOException, SAXException,
            PassengerService.PassengerValidationException, SQLException {
        doThrow(new PassengerService.PassengerValidationException("Test Exception")).when(passengerService).addPassengerFromXML(testLoc);
        controller.importPassengersFromXml(testXml);
        verify(logger).log(contains(PassengerController.WARN_XML_PASSENGER_VALIDATION_FAILED + "Test Exception"), eq(ApplicationLogger.LogLevel.WARNING));
    }
}

