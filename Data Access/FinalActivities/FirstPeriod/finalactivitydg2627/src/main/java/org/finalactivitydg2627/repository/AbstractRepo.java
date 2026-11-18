package org.finalactivitydg2627.repository;

import org.finalactivitydg2627.Utils.ApplicationLogger;
import org.finalactivitydg2627.database.DBConnection;

public abstract class AbstractRepo {

    protected final DBConnection connection;
    protected final ApplicationLogger logger;

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
