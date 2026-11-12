package org.finalactivitydg2627.controller;

import org.finalactivitydg2627.Utils.ApplicationLogger;
import org.finalactivitydg2627.Utils.Verification;
import org.finalactivitydg2627.service.PassengerService;

public class PassengerController {

    private final ApplicationLogger logger = ApplicationLogger.getInstance();
    private final PassengerService passengerService = new PassengerService();

    public void AddPassengerFromXML(String input) {
        var rs = Verification.basicPathVerification("xml file", input);
        if (rs.message() != null) {
            logger.log(rs.message(), ApplicationLogger.LogLevel.WARNING);
        }
        logger.log("Reading passenger from XML: " + input, ApplicationLogger.LogLevel.INFO);
        passengerService.addPassengerFromXML(rs.result());
    }


}
