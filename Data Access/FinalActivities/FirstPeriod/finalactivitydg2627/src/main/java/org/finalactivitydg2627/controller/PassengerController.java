package org.finalactivitydg2627.controller;

import org.finalactivitydg2627.Utils.ApplicationLogger;
import org.finalactivitydg2627.Utils.Verification;
import org.finalactivitydg2627.service.PassengerService;
import org.xml.sax.SAXException;

import javax.xml.parsers.ParserConfigurationException;
import java.io.IOException;
import java.sql.SQLException;

public class PassengerController extends AbstractController  {

    private final PassengerService passengerService;

    public PassengerController() {
        passengerService = new PassengerService();
    }
    public PassengerController(ApplicationLogger logger) {
        super(logger);
        passengerService = new PassengerService(logger);
    }


    public PassengerController(ApplicationLogger logger, PassengerService passengerService) {
        super(logger);
        this.passengerService = passengerService;
    }

    public static final String INFO_VALIDATING_XML_FILE = "Validating XML file: ";
    public static final String INFO_CALLING_SERVICE = "Calling service for file reading: ";
    public static final String INFO_ADDED_PASSENGERS = "Successfully added passengers: ";

    public static final String WARN_XML_PATH_VALIDATION_FAILED = "XML file path is invalid!\n";
    public static final String WARN_DUPLICATED_KEY_FORMATTED_PASSPORT = "The passenger with passport: %s already exists!";

    public static final String ERROR_PARSER_CONFIGURATION_EXCEPTION = "Parser configuration error:";
    public static final String ERROR_SAX_EXCEPTION = "SAX parser exception:";
    public static final String ERROR_IO_EXCEPTION = "IO exception:";
    public static final String ERROR_DB_EXCEPTION_ON_INSERT_PASSENGER = "Database error on adding passenger: ";
    public static final String ERROR_SQL_STATE_NULL = "The sql state is null: ";
    public static final String WARN_XML_FILE_PASSENGERS_HAD_ERROR = "XML file contained errors in passenger data.\n";


    public void importPassengerFromXml(String input) {

        logger.log(INFO_VALIDATING_XML_FILE + input, ApplicationLogger.LogLevel.INFO);

        var rs = Verification.basicPathVerification("xml file", input);

        if (rs.message() != null) {
            logger.log(
                    WARN_XML_PATH_VALIDATION_FAILED + errorMessageFormatter(rs.message()),
                    ApplicationLogger.LogLevel.WARNING);
            return;
        }

        logger.log(
                INFO_CALLING_SERVICE + input,
                ApplicationLogger.LogLevel.INFO);
        try {
            var result = passengerService.addPassengerFromXML(rs.result());
            if(result != null) {
                logger.log(INFO_ADDED_PASSENGERS + result, ApplicationLogger.LogLevel.INFO);
            }
        } catch (ParserConfigurationException e) {
            logger.log(
                    ERROR_PARSER_CONFIGURATION_EXCEPTION + e.getMessage(),
                    ApplicationLogger.LogLevel.ERROR);

        } catch (SAXException e) {
            logger.log(
                    ERROR_SAX_EXCEPTION + e.getMessage(),
                    ApplicationLogger.LogLevel.ERROR);

        } catch (IOException e) {
            logger.log(
                    ERROR_IO_EXCEPTION + e.getMessage(),
                    ApplicationLogger.LogLevel.ERROR);

        } catch (SQLException e) {
            handleSqlException(e);

        } catch (PassengerService.PassengerValidationException e) {
            logger.log(
                    WARN_XML_FILE_PASSENGERS_HAD_ERROR + e.getMessage(),
                    ApplicationLogger.LogLevel.WARNING);
        }
    }

    private String errorMessageFormatter(String message) {
        return "    - " + message;
    }

    private void handleSqlException(SQLException e) {
        logger.log(
                ERROR_DB_EXCEPTION_ON_INSERT_PASSENGER + e.getMessage(),
                ApplicationLogger.LogLevel.ERROR);
        if (e.getSQLState() == null) {
            logger.log(
                    ERROR_SQL_STATE_NULL + e.getMessage(),
                    ApplicationLogger.LogLevel.ERROR);
            return;
        }
        handleDuplicateKey(e);
    }

    private void handleDuplicateKey(SQLException e) {
        var message = e.getMessage();

        var start = message.indexOf("=(") + 2;
        var end = message.indexOf(")", start);

        if (start >= 2 && end > start) {
            var value = message.substring(start, end);

            logger.log(
                    String.format(WARN_DUPLICATED_KEY_FORMATTED_PASSPORT, value),
                    ApplicationLogger.LogLevel.WARNING
            );
        }
    }

}
