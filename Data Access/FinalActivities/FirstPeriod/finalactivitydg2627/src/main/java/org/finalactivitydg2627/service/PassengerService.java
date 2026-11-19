package org.finalactivitydg2627.service;

import org.finalactivitydg2627.dto.PassengerRawDTO;
import org.finalactivitydg2627.model.Passengers;
import org.finalactivitydg2627.repository.PassengerRepo;
import org.finalactivitydg2627.utils.ApplicationLogger;
import org.finalactivitydg2627.utils.MessageFormatter;
import org.finalactivitydg2627.utils.PassengerSAXHandler;
import org.xml.sax.SAXException;

import javax.xml.parsers.ParserConfigurationException;
import javax.xml.parsers.SAXParserFactory;
import java.io.IOException;
import java.nio.file.Path;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;

public class PassengerService extends AbstractService {


    private final PassengerRepo passengerRepo;

    public PassengerService() {

        passengerRepo = new PassengerRepo();
    }

    public PassengerService(ApplicationLogger logger) {
        super(logger);
        passengerRepo = new PassengerRepo(logger);
    }

    public PassengerService(ApplicationLogger logger, PassengerRepo passengerRepo) {
        super(logger);
        this.passengerRepo = passengerRepo;
    }


    public Integer addPassengerFromXML(Path input) throws ParserConfigurationException, SAXException, IOException, PassengerValidationException, SQLException {
        var factory = SAXParserFactory.newInstance();

        var saxParser = factory.newSAXParser();
        var handler = new PassengerSAXHandler();
        saxParser.parse(input.toFile(), handler);

        var inputXml = handler.getPassengers();
        return addPassengers(inputXml);
    }

    public static class PassengerValidationException extends Exception {
        public PassengerValidationException(String message) {
            super(message);
        }
    }

    public static final String INFO_PASSENGER_VERIFIED = "Passenger: %s passed verification.";
    public static final String DEBUG_PASSENGER_VALID = "Passenger validation failed, commit canceled.";

    private Integer addPassengers(List<PassengerRawDTO> passengersRawInput) throws PassengerValidationException, SQLException {
        var passengers = new ArrayList<Passengers>();
        var errors = new StringBuilder();
        var commitTransaction = true;
        for (PassengerRawDTO passengerRawDTO : passengersRawInput) {
            var result = processPassenger(passengerRawDTO, errors);
            if (result != null) {
                passengers.add(result);
                logger.log(String.format(INFO_PASSENGER_VERIFIED, result), ApplicationLogger.LogLevel.INFO);
            } else if (commitTransaction) {
                commitTransaction = false;
                logger.log(DEBUG_PASSENGER_VALID, ApplicationLogger.LogLevel.DEBUG);
            }
        }
        if (!errors.isEmpty()) {
            throw new PassengerValidationException(errors.toString());
        }
        if (commitTransaction) return passengerRepo.addPassengers(passengers);
        return null;
    }

    private Passengers processPassenger(PassengerRawDTO passengerRawDTO, StringBuilder errors) {
        var passModel = new Passengers();
        var nameValidation = passModel.setFirstName(passengerRawDTO.getFirstName());
        var lastNameValidation = passModel.setLastName(passengerRawDTO.getLastName());
        var passportNoValidation = passModel.setPassno(passengerRawDTO.getPassno());
        var phoneValidation = passengerRawDTO.getPhone() != null ?
                passModel.setPhone(passengerRawDTO.getPhone()) : null;
        var addressValidation = passengerRawDTO.getAddress() != null ?
                passModel.setAddress(passengerRawDTO.getAddress()) : null;
        var isValid =
                nameValidation.result() != null &&
                        lastNameValidation.result() != null &&
                        passportNoValidation.result() != null &&
                        (phoneValidation == null || phoneValidation.result() != null) &&
                        (addressValidation == null || addressValidation.result() != null);
        errors.append(MessageFormatter.multipleErrorsFormatter(
                nameValidation, lastNameValidation, passportNoValidation, phoneValidation, addressValidation));
        return isValid ? passModel : null;

    }

}
