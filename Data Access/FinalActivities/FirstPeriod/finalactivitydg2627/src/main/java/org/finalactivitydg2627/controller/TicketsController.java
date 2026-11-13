package org.finalactivitydg2627.controller;

import java.util.Arrays;
import org.finalactivitydg2627.Utils.ApplicationLogger;
import org.finalactivitydg2627.Utils.MessageFormatter;
import org.finalactivitydg2627.Utils.Verification;

public class TicketsController {

    private final ApplicationLogger logger;

    public TicketsController() {
        logger = ApplicationLogger.getInstance();
    }

    public void addTicket(String[] args) {
        logger.log("Verifying buying input: "+ Arrays.toString(args), ApplicationLogger.LogLevel.INFO);
        var passnoResult = Verification.basicStringVerification("passport",args[1],null,10,null);
        var flightResult = Verification.basicStringVerification("flight code",args[2],null,10,null);
        var dateResult = Verification.basicDateVerification("date",args[3]);

        var errors = MessageFormatter.multipleErrorsFormatter(passnoResult, flightResult, dateResult);

        if(!errors.isEmpty()){
            logger.log("Input validation failed: \n"+errors, ApplicationLogger.LogLevel.WARNING);
            return;
        }




    }

}
