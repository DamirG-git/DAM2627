package org.finalactivitydg2627.controller;

import org.finalactivitydg2627.Utils.ApplicationLogger;
import org.finalactivitydg2627.Utils.Verification;
import org.finalactivitydg2627.service.PassengerService;
import org.xml.sax.SAXException;

import javax.xml.parsers.ParserConfigurationException;
import java.io.IOException;
import java.sql.SQLException;

public class PassengerController {

    private final ApplicationLogger logger;
    private final PassengerService passengerService;

    public PassengerController() {
        logger = ApplicationLogger.getInstance();
        passengerService = new PassengerService();
    }

    public PassengerController(ApplicationLogger logger, PassengerService passengerService) {
        this.logger = logger;
        this.passengerService = passengerService;
    }

    public static final String INFO_VALIDATING_XML_FILE = "Validating XML file: ";
    public static final String WARN_VALIDATION_FAILED = "XML file is invalid!\n";
    public static final String ERROR_XML_FILE_PASSENGERS_HAD_ERROR = "XML file contained errors in passenger data.\n";
    public static final String INFO_CALLING_SERVICE = "Calling service for file reading: ";
    public static final String ERROR_PARSER_CONFIGURATION_EXCEPTION = "Parser configuration error:";
    public static final String ERROR_SAX_EXCEPTION = "SAX parser exception:";
    public static final String ERROR_IO_EXCEPTION = "IO exception:";

    public void importPassengerFromXml(String input) {
        try {
            logger.log(INFO_VALIDATING_XML_FILE + input, ApplicationLogger.LogLevel.INFO);

            var rs = Verification.basicPathVerification("xml file", input);

            if (rs.message() != null) {
                logger.log(WARN_VALIDATION_FAILED + errorMessageFormatter(rs.message()), ApplicationLogger.LogLevel.WARNING);
                return;
            }

            logger.log(INFO_CALLING_SERVICE + input, ApplicationLogger.LogLevel.INFO);
            passengerService.addPassengerFromXML(rs.result());

        } catch (ParserConfigurationException e) {
            logger.log(ERROR_PARSER_CONFIGURATION_EXCEPTION + e.getMessage(), ApplicationLogger.LogLevel.ERROR);

        } catch (SAXException e) {
            logger.log(ERROR_SAX_EXCEPTION + e.getMessage(), ApplicationLogger.LogLevel.ERROR);

        } catch (IOException e) {
            logger.log(ERROR_IO_EXCEPTION + e.getMessage(), ApplicationLogger.LogLevel.ERROR);
        } catch (SQLException e) {
            logger.log("Database error on adding passenger: " + e.getMessage(), ApplicationLogger.LogLevel.ERROR);
            var next = e.getNextException();

            while (next != null) {
                if (!next.getMessage().equals(e.getMessage())) {
                    logger.log(
                            "Additional database error: " + next.getMessage(),
                            ApplicationLogger.LogLevel.ERROR
                    );
                }
                next = next.getNextException();
            }

        } catch (PassengerService.PassengerValidationException e) {
            logger.log(ERROR_XML_FILE_PASSENGERS_HAD_ERROR + e.getMessage(), ApplicationLogger.LogLevel.ERROR);
        }
    }

    private String errorMessageFormatter(String message) {
        return "    - " + message;
    }


}
