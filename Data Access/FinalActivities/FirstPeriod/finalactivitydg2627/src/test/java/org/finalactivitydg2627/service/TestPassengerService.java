package org.finalactivitydg2627.service;

import org.finalactivitydg2627.Utils.ApplicationLogger;
import org.finalactivitydg2627.repository.PassengerRepo;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.xml.sax.SAXException;

import javax.xml.parsers.ParserConfigurationException;
import java.io.IOException;
import java.nio.file.Path;
import java.sql.SQLException;

import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.Mockito.*;


public class TestPassengerService {


    private PassengerRepo passengerRepo;
    private ApplicationLogger logger;

    private PassengerService passengerService;

    private final String testXmlSuccess = "src/test/resources/passengers_success.xml";
    private final String testXmlFail = "src/test/resources/passengers_fail.xml";
    private final Path testLocSuccess = Path.of(testXmlSuccess);
    private final Path testLocFail = Path.of(testXmlFail);

    @BeforeEach
    void setUp() {
        passengerRepo = mock(PassengerRepo.class);
        logger = mock(ApplicationLogger.class);
        passengerService = new PassengerService(logger, passengerRepo);
    }

    @Test
    void importPassenger_success()
            throws PassengerService.PassengerValidationException,
            SQLException, ParserConfigurationException,
            IOException, SAXException {
        passengerService.addPassengerFromXML(testLocSuccess);
        verify(passengerRepo).addPassengers(anyList());
        verify(logger).log(String.format(PassengerService.INFO_PASSENGER_VERIFIED, "John Smith AB123456, Alicante, Spain, +34612345678"),
                ApplicationLogger.LogLevel.INFO);
    }

    @Test
    void importPassenger_validationFail()
            throws SQLException {
        assertThrows(PassengerService.PassengerValidationException.class,
                () -> passengerService.addPassengerFromXML(testLocFail));

        verify(passengerRepo, never()).addPassengers(anyList());
        verify(logger).log(contains(PassengerService.DEBUG_PASSENGER_VALID), eq(ApplicationLogger.LogLevel.DEBUG));

    }


}
