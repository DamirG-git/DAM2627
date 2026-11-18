package org.finalactivitydg2627.controllers;

import org.finalactivitydg2627.Utils.ApplicationLogger;
import org.finalactivitydg2627.controller.PassengerController;
import org.finalactivitydg2627.service.PassengerService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.xml.sax.SAXException;

import javax.xml.parsers.ParserConfigurationException;
import java.io.IOException;
import java.nio.file.Path;
import java.sql.SQLException;

import static org.mockito.Mockito.*;

public class TestPassengerController {


    private PassengerController controller;
    private PassengerService passengerService;
    private ApplicationLogger logger;


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
        controller.importPassengerFromXml(testXml);

        verify(passengerService).addPassengerFromXML(testLoc);
        verify(logger).log(contains(PassengerController.INFO_CALLING_SERVICE), eq(ApplicationLogger.LogLevel.INFO));
    }

    @Test
    void addPassengers_fail_xmlFileInvalid() throws ParserConfigurationException,
            IOException, SAXException,
            PassengerService.PassengerValidationException, SQLException {
        controller.importPassengerFromXml("invalid.xml");

        verify(passengerService, never()).addPassengerFromXML(testLoc);
        verify(logger).log(contains(PassengerController.WARN_XML_PATH_VALIDATION_FAILED), eq(ApplicationLogger.LogLevel.WARNING));

    }

    @Test
    void addPassenger_fail_exception_DBPassengerInsertion() throws PassengerService.PassengerValidationException, SQLException, ParserConfigurationException, IOException, SAXException {
        doThrow(new SQLException("Test Exception")).when(passengerService).addPassengerFromXML(testLoc);
        controller.importPassengerFromXml(testXml);


        verify(logger).log(contains(PassengerController.ERROR_DB_EXCEPTION_ON_INSERT_PASSENGER + "Test Exception"), eq(ApplicationLogger.LogLevel.ERROR));
        verify(logger).log(contains(PassengerController.ERROR_SQL_STATE_NULL + "Test Exception"), eq(ApplicationLogger.LogLevel.ERROR));
    }


    @Test
    void addPassenger_fail_exception_ParserExc() throws ParserConfigurationException,
            IOException, SAXException,
            PassengerService.PassengerValidationException, SQLException {
        doThrow(new ParserConfigurationException("Test Exception")).when(passengerService).addPassengerFromXML(testLoc);

        controller.importPassengerFromXml(testXml);

        verify(logger).log(contains("Test Exception"), eq(ApplicationLogger.LogLevel.ERROR));
    }

    @Test
    void addPassenger_fail_exception_SaxExc() throws ParserConfigurationException,
            IOException, SAXException,
            PassengerService.PassengerValidationException, SQLException {
        doThrow(new SAXException("Test Exception")).when(passengerService).addPassengerFromXML(testLoc);

        controller.importPassengerFromXml(testXml);

        verify(logger).log(contains("Test Exception"), eq(ApplicationLogger.LogLevel.ERROR));
    }

    @Test
    void addPassenger_fail_exception_IOExc()
            throws ParserConfigurationException,
            IOException, SAXException,
            PassengerService.PassengerValidationException, SQLException {
        doThrow(new IOException("Test Exception")).when(passengerService).addPassengerFromXML(testLoc);

        controller.importPassengerFromXml(testXml);


        verify(logger).log(contains("Test Exception"), eq(ApplicationLogger.LogLevel.ERROR));
    }

    @Test
    void addPassenger_fail_exception_PassValidExc()
            throws ParserConfigurationException,
            IOException, SAXException,
            PassengerService.PassengerValidationException, SQLException {
        doThrow(new PassengerService.PassengerValidationException("Test Exception")).when(passengerService).addPassengerFromXML(testLoc);
        controller.importPassengerFromXml(testXml);
        verify(logger).log(contains(PassengerController.ERROR_XML_FILE_PASSENGERS_HAD_ERROR + "Test Exception"), eq(ApplicationLogger.LogLevel.WARNING));
    }
}

