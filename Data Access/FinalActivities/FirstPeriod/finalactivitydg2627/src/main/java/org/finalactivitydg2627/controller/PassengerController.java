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
    public static final String WARN_XML_PATH_VALIDATION_FAILED = "XML file is invalid!\n";
    public static final String ERROR_XML_FILE_PASSENGERS_HAD_ERROR = "XML file contained errors in passenger data.\n";
    public static final String INFO_CALLING_SERVICE = "Calling service for file reading: ";
    public static final String ERROR_PARSER_CONFIGURATION_EXCEPTION = "Parser configuration error:";
    public static final String ERROR_SAX_EXCEPTION = "SAX parser exception:";
    public static final String ERROR_IO_EXCEPTION = "IO exception:";
    public static final String ERROR_DB_EXCEPTION_ON_INSERT_PASSENGER = "Database error on adding passenger: ";
    public static final String WARN_DUPLICATED_KEY_FORMATTED = "The passenger with passport: %s already exists!";
    public static final String ERROR_SQL_STATE_NULL = "The sql state is null: ";

    public void importPassengerFromXml(String input) {
        try {
            logger.log(INFO_VALIDATING_XML_FILE + input, ApplicationLogger.LogLevel.INFO);

            var rs = Verification.basicPathVerification("xml file", input);

            if (rs.message() != null) {
                logger.log(WARN_XML_PATH_VALIDATION_FAILED + errorMessageFormatter(rs.message()), ApplicationLogger.LogLevel.WARNING);
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
            logger.log(ERROR_DB_EXCEPTION_ON_INSERT_PASSENGER + e.getMessage(), ApplicationLogger.LogLevel.ERROR);
            if (e.getSQLState() == null) {
                logger.log(ERROR_SQL_STATE_NULL + e.getMessage(), ApplicationLogger.LogLevel.ERROR);
                return;
            }
            if (e.getSQLState().equals("23505")) {
                var start = e.getMessage().indexOf("=(") + 2;
                var end = e.getMessage().indexOf(")", start);
                var value = e.getMessage().substring(start, end);
                logger.log(String.format(WARN_DUPLICATED_KEY_FORMATTED, value), ApplicationLogger.LogLevel.WARNING);

            }

            var errorMessage = e.getMessage();
            var sqlState = e.getSQLState();
            var next = e.getNextException();

            while (next != null) {
                if (!next.getMessage().equals(errorMessage) && !next.getSQLState().equals(sqlState)) {
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
