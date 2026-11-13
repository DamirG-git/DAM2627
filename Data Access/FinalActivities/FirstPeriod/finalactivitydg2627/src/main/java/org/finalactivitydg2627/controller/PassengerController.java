package org.finalactivitydg2627.controller;

import org.finalactivitydg2627.Utils.ApplicationLogger;
import org.finalactivitydg2627.Utils.Verification;
import org.finalactivitydg2627.service.PassengerService;

public class PassengerController {

    private final ApplicationLogger logger = ApplicationLogger.getInstance();
    private final PassengerService passengerService = new PassengerService();

    public void addPassengerFromXML(String input) {
        logger.log("Verifying XML file: " + input, ApplicationLogger.LogLevel.INFO);
        var rs = Verification.basicPathVerification("xml file", input);
        if (rs.message() != null) {
            logger.log(rs.message(), ApplicationLogger.LogLevel.WARNING);
            return;
        }
        logger.log("Calling service for file reading: " + input, ApplicationLogger.LogLevel.INFO);
        passengerService.addPassengerFromXML(rs.result());
    }


}
