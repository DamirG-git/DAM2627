package org.finalactivitydg2627.service;

import org.finalactivitydg2627.utils.ApplicationLogger;

public abstract class AbstractService {


    protected final ApplicationLogger logger;

    protected AbstractService() {
        logger = ApplicationLogger.getInstance();
    }

    protected AbstractService(ApplicationLogger logger) {
        this.logger = logger;
    }
}
