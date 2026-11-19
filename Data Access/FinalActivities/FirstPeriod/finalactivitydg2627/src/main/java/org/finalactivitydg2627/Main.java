package org.finalactivitydg2627;

import org.finalactivitydg2627.database.DBConnection;
import org.finalactivitydg2627.utils.ApplicationLogger;

import java.sql.SQLException;

//TIP To <b>Run</b> code, press <shortcut actionId="Run"/> or
// click the <icon src="AllIcons.Actions.Execute"/> icon in the gutter.
public class Main {

    private static final ApplicationLogger logger = ApplicationLogger.getInstance();

    public static final String ERROR_SQL_CONNECTION = "Failed to connect to database: ";

    public static void main(String[] args) {
        try (var ignored = DBConnection.getInstance().connect()) {
            var commandExe = new CommandExecutor(logger);
            commandExe.executeCommand(args);
        } catch (SQLException e) {
            logger.log(ERROR_SQL_CONNECTION + e.getMessage(), ApplicationLogger.LogLevel.ERROR);
        }
        logger.close();

    }


}