package org.finalactivitydg2627.controller;

import org.finalactivitydg2627.Utils.ApplicationLogger;

public abstract class AbstractController {

    protected final ApplicationLogger logger;

    protected AbstractController() {
        logger = ApplicationLogger.getInstance();
    }

    protected AbstractController(ApplicationLogger logger) {
        this.logger = logger;
    }
}
