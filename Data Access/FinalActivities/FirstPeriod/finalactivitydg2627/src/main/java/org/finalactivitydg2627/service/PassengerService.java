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
        for (PassengerInputDto passenger : passengersRawInput) {
            var pas = new Passengers();
            var addNew = true;
            var firstnameError = pas.setFirstName(passenger.getFirstName());
            var lastnameError = pas.setLastName(passenger.getLastName());
            var passnoError = pas.setPassno(passenger.getPassno());
            var phoneError = pas.setPhone(passenger.getPhone());
            var addressError = pas.setAddress(passenger.getAddress());

            if (firstnameError != null) {
                errors.append(firstnameError);
                addNew = false;
            }
            if (lastnameError != null) {
                errors.append(lastnameError);
                addNew = false;
            }
            if (passnoError != null) {
                errors.append(passnoError);
                addNew = false;
            }
            if (phoneError != null) {
                errors.append(phoneError);
                addNew = false;
            }
            if (addressError != null) {
                errors.append(addressError);
                addNew = false;
            }
            if (addNew) {
                passengers.add(pas);
            }

        }
        if (!errors.isEmpty()) {
            logger.log(errors.toString(), ApplicationLogger.LogLevel.ERROR);
            return null;
        }
        return passengers;
    }

}
