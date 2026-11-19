package org.finalactivitydg2627.controller;

import org.finalactivitydg2627.utils.ApplicationLogger;

public abstract class AbstractController {

    protected final ApplicationLogger logger;

    protected AbstractController(ApplicationLogger logger) {
        this.logger = logger;
    }
}
