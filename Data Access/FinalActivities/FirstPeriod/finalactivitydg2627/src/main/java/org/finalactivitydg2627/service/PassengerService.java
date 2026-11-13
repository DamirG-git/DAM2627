package org.finalactivitydg2627.service;

import org.finalactivitydg2627.Utils.ApplicationLogger;
import org.finalactivitydg2627.Utils.PassengerSAXHandler;
import org.finalactivitydg2627.dto.PassengerInputDto;
import org.finalactivitydg2627.model.Passengers;
import org.finalactivitydg2627.repository.PassengerRepo;
import org.xml.sax.SAXException;

import javax.xml.parsers.ParserConfigurationException;
import javax.xml.parsers.SAXParserFactory;
import java.io.IOException;
import java.nio.file.Path;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Optional;

public class PassengerService {


    private final ApplicationLogger logger = ApplicationLogger.getInstance();
    private final PassengerRepo passengerRepo = new PassengerRepo();


    public void addPassengerFromXML(Path input) {
        var factory = SAXParserFactory.newInstance();
        try {
            var saxParser = factory.newSAXParser();
            var handler = new PassengerSAXHandler();
            saxParser.parse(input.toFile(), handler);

            var inputXml = handler.getPassengers();
            var results = validatePassengers(inputXml);
            if (results == null) {
                logger.log("Input failed on service level.", ApplicationLogger.LogLevel.ERROR);
                return;
            }
            addPassengers(results);

        } catch (ParserConfigurationException e) {
            logger.log("Parser configuration error:" + e.getMessage(), ApplicationLogger.LogLevel.ERROR);
            throw new RuntimeException(e);
        } catch (SAXException e) {
            logger.log("Sax parser error:" + e.getMessage(), ApplicationLogger.LogLevel.ERROR);
            throw new RuntimeException(e);
        } catch (IOException e) {
            logger.log("IO error:" + e.getMessage(), ApplicationLogger.LogLevel.ERROR);
            throw new RuntimeException(e);
        }
    }

    private void addPassengers(List<Passengers> results) {
        try {
            passengerRepo.addPassengers(results);
        } catch (SQLException e) {
            logger.log("Error while inserting passenger into DB:" + e.getMessage(), ApplicationLogger.LogLevel.ERROR);
        }
    }

    private List<Passengers> validatePassengers(List<PassengerInputDto> passengersRawInput) {
        var passengers = new ArrayList<Passengers>();
        var errors = new StringBuilder();
        boolean commitTransaction = true;
        for (PassengerInputDto passenger : passengersRawInput) {
            var pas = new Passengers();
            var addNew = true;
            var firstnameError = pas.setFirstName(passenger.getFirstName());
            var lastnameError = pas.setLastName(passenger.getLastName());
            var passnoError = pas.setPassno(passenger.getPassno());
            var phoneError = pas.setPhone(passenger.getPhone());
            var addressError = pas.setAddress(passenger.getAddress());



            if (firstnameError != null) {
                appendError(errors, passenger.getPassno(), firstnameError);
                commitTransaction = addNew = false;
            }
            if (lastnameError != null) {
                appendError(errors, passenger.getPassno(), lastnameError);
                commitTransaction = addNew = false;
            }
            if (passnoError != null) {
                appendError(errors, passenger.getPassno(), passnoError);
                commitTransaction = addNew = false;
            }
            if (phoneError != null) {
                appendError(errors, passenger.getPassno(), phoneError);
            }
            if (addressError != null) {
                appendError(errors, passenger.getPassno(), addressError);
            }
            if (addNew) {
                passengers.add(pas);
                logger.log("Passenger: " + passenger + " passed verification.", ApplicationLogger.LogLevel.INFO);
            }

        }
        if (!errors.isEmpty()) {
            logger.log(errors.toString(), ApplicationLogger.LogLevel.ERROR);
        }
        if (commitTransaction) {
            try {
                passengerRepo.addPassengers(passengers);
            }catch (SQLException e) {
                logger.log("Database error on adding passenger: "+e.getMessage(), ApplicationLogger.LogLevel.ERROR);
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

                return Collections.emptyList();
            }
            return passengers;
        }
        return Collections.emptyList();
    }

    private void appendError(StringBuilder errors, String passno, String error) {
        errors.append("Passenger: ")
                .append(passno)
                .append(" - ")
                .append(error);
    }

}
