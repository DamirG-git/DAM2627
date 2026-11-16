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
import java.util.List;

public class PassengerService {


    private final ApplicationLogger logger;
    private final PassengerRepo passengerRepo;

    public PassengerService() {
        logger = ApplicationLogger.getInstance();
        passengerRepo = new PassengerRepo();
    }

    public PassengerService(ApplicationLogger logger, PassengerRepo passengerRepo) {
        this.logger = logger;
        this.passengerRepo = passengerRepo;
    }


    public void addPassengerFromXML(Path input) throws ParserConfigurationException, SAXException, IOException, PassengerValidationException, SQLException {
        var factory = SAXParserFactory.newInstance();

        var saxParser = factory.newSAXParser();
        var handler = new PassengerSAXHandler();
        saxParser.parse(input.toFile(), handler);

        var inputXml = handler.getPassengers();
        addPassengers(inputXml);
    }

    public static class PassengerValidationException extends Exception {
        public PassengerValidationException(String message) {
            super(message);
        }
    }

    public static final String INFO_PASSENGER_VERIFIED = "Passenger: %s passed verification.";
    public static final String DEBUG_PASSENGER_VALID = "Passenger validation failed, commit canceled.";

    private void addPassengers(List<PassengerInputDto> passengersRawInput) throws PassengerValidationException, SQLException {
        var passengers = new ArrayList<Passengers>();
        var errors = new StringBuilder();
        var commitTransaction = true;
        for (PassengerInputDto passengerInputDto : passengersRawInput) {
            var result = processPassenger(passengerInputDto, errors);
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
        if (commitTransaction) passengerRepo.addPassengers(passengers);
    }

    private Passengers processPassenger(PassengerInputDto passengerInputDto, StringBuilder errors) {
        var passModel = new Passengers();
        var nameValidation = passModel.setFirstName(passengerInputDto.getFirstName());
        var lastNameValidation = passModel.setLastName(passengerInputDto.getLastName());
        var passportNoValidation = passModel.setPassno(passengerInputDto.getPassno());
        var phoneValidation = passengerInputDto.getPhone() != null ?
                passModel.setPhone(passengerInputDto.getPhone()) : null;
        var addressValidation = passengerInputDto.getAddress() != null ?
                passModel.setAddress(passengerInputDto.getAddress()) : null;
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
