package org.finalactivitydg2627.controller;

import org.finalactivitydg2627.service.PassengerService;
import org.finalactivitydg2627.utils.ApplicationLogger;
import org.finalactivitydg2627.utils.ApplicationValidator;
import org.xml.sax.SAXException;

import javax.xml.parsers.ParserConfigurationException;
import java.io.IOException;
import java.nio.file.Path;
import java.sql.SQLException;

public class PassengerController extends AbstractController  {

    private final PassengerService passengerService;

    public PassengerController(ApplicationLogger logger) {
        super(logger);
        passengerService = new PassengerService(logger);
    }

    public PassengerController(ApplicationLogger logger, PassengerService passengerService) {
        super(logger);
        this.passengerService = passengerService;
    }

    public static final String INFO_VALIDATING_XML_FILE = "Validating XML file: ";
    public static final String INFO_IMPORTING_PASSENGERS_FROM_XML = "Calling service for passengers import: ";
    public static final String INFO_PASSENGERS_IMPORTED = "Successfully added passengers: ";

    public static final String WARN_INVALID_XML_FILE_PATH = "XML file path is invalid!\n";
    public static final String WARN_DUPLICATE_PASSENGER_PASSPORT_FORMAT_PASSPORT = "The passenger with passport: %s already exists!";
    public static final String WARN_XML_PASSENGER_VALIDATION_FAILED = "XML file contained errors in passenger data.\n";

    public static final String ERROR_PARSER_CONFIGURATION = "Parser configuration error:";
    public static final String ERROR_XML_PARSING = "SAX parser exception on reading file:";
    public static final String ERROR_XML_FILE_IO = "Xml file IO exception:";
    public static final String ERROR_INSERTING_PASSENGER = "Database error on inserting passenger: ";
    public static final String ERROR_MISSING_SQL_STATE = "The sql state is null: ";


    public void importPassengersFromXml(String input) {

        logger.log(INFO_VALIDATING_XML_FILE + input, ApplicationLogger.LogLevel.INFO);

        var rs = ApplicationValidator.basicPathVerification("xml file", input);

        if (rs.message() != null) {
            logger.log(
                    WARN_INVALID_XML_FILE_PATH + formatErrorMessage(rs.message()),
                    ApplicationLogger.LogLevel.WARNING);
            return;
        }

        logger.log(
                INFO_IMPORTING_PASSENGERS_FROM_XML + input,
                ApplicationLogger.LogLevel.INFO);
        addPassenger(rs);
    }

    private void addPassenger(ApplicationValidator.ResultValue<Path> rs) {
        try {
            var result = passengerService.addPassengerFromXML(rs.result());
            if(result != null) {
                logger.log(INFO_PASSENGERS_IMPORTED + result, ApplicationLogger.LogLevel.INFO);
            }
        } catch (ParserConfigurationException e) {
            logger.log(
                    ERROR_PARSER_CONFIGURATION + e.getMessage(),
                    ApplicationLogger.LogLevel.ERROR);

        } catch (SAXException e) {
            logger.log(
                    ERROR_XML_PARSING + e.getMessage(),
                    ApplicationLogger.LogLevel.ERROR);

        } catch (IOException e) {
            logger.log(
                    ERROR_XML_FILE_IO + e.getMessage(),
                    ApplicationLogger.LogLevel.ERROR);

        } catch (SQLException e) {
            handleSQLException(e);

        } catch (PassengerService.PassengerValidationException e) {
            logger.log(
                    WARN_XML_PASSENGER_VALIDATION_FAILED + e.getMessage(),
                    ApplicationLogger.LogLevel.WARNING);
        }
    }

    private String formatErrorMessage(String message) {
        return "    - " + message;
    }

    private void handleSQLException(SQLException e) {
        logger.log(
                ERROR_INSERTING_PASSENGER + e.getMessage(),
                ApplicationLogger.LogLevel.ERROR);
        if (e.getSQLState() == null) {
            logger.log(
                    ERROR_MISSING_SQL_STATE + e.getMessage(),
                    ApplicationLogger.LogLevel.ERROR);
            return;
        }
        handleDuplicatePassenger(e);
    }

    private void handleDuplicatePassenger(SQLException e) {
        var message = e.getMessage();

        var start = message.indexOf("=(") + 2;
        var end = message.indexOf(")", start);

        if (start >= 2 && end > start) {
            var value = message.substring(start, end);

            logger.log(
                    String.format(WARN_DUPLICATE_PASSENGER_PASSPORT_FORMAT_PASSPORT, value),
                    ApplicationLogger.LogLevel.WARNING
            );
        }
    }

}
