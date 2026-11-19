package org.finalactivitydg2627.repository;

import org.finalactivitydg2627.database.DBConnection;
import org.finalactivitydg2627.utils.ApplicationLogger;

public abstract class AbstractRepo {

    protected final DBConnection connection;
    protected final ApplicationLogger logger;

    protected AbstractRepo(ApplicationLogger logger) {
        this.connection = DBConnection.getInstance();
        this.logger = logger;
    }

    protected AbstractRepo() {
        this.connection = DBConnection.getInstance();
        logger = ApplicationLogger.getInstance();
    }

    protected AbstractRepo(DBConnection connection, ApplicationLogger logger) {
        this.connection = connection;
        this.logger = logger;
    }

    public record RepoResultValue<T, E extends Enum<E>>(T value, E codeEnum)  {
    }


}
