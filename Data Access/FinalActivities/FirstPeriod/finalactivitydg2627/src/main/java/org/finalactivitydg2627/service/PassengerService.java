package org.finalactivitydg2627.service;

import org.finalactivitydg2627.Utils.ApplicationLogger;
import org.finalactivitydg2627.Utils.MessageFormatter;
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
            var passengerModel = new Passengers();
            var addNew = true;
            var firstNameResult = passengerModel.setFirstName(passenger.getFirstName());
            var lastNameResult = passengerModel.setLastName(passenger.getLastName());
            var passportNumberResult = passengerModel.setPassno(passenger.getPassno());
            var phoneResult = passenger.getPhone() == null ||
                    passenger.getPhone().isEmpty() ?
                    null : passengerModel.setPhone(passenger.getPhone());
            var addressResult = passenger.getAddress() == null ||
                    passenger.getAddress().isEmpty() ?
                    null : passengerModel.setAddress(passenger.getAddress());

            errors.append(MessageFormatter.multipleErrorsFormatter(firstNameResult, lastNameResult, passportNumberResult, phoneResult, addressResult));
            var isValid =
                    firstNameResult.result() != null &&
                            lastNameResult.result() != null &&
                            passportNumberResult.result() != null &&
                            (phoneResult == null || phoneResult.result() != null) &&
                            (addressResult == null || addressResult.result() != null);
            if (!isValid) {
                commitTransaction = addNew = false;
            }
            if (addNew) {
                passengers.add(passengerModel);
                logger.log("Passenger: " + passenger + " passed verification.", ApplicationLogger.LogLevel.INFO);
            }

        }
        if (!errors.isEmpty()) {
            logger.log(errors.toString(), ApplicationLogger.LogLevel.ERROR);
        }
        if (commitTransaction) return commitTransaction(passengers);
        return Collections.emptyList();
    }


    private List<Passengers> commitTransaction(ArrayList<Passengers> passengers) {
        try {
            passengerRepo.addPassengers(passengers);
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

            return Collections.emptyList();
        }
        return passengers;
    }

}
