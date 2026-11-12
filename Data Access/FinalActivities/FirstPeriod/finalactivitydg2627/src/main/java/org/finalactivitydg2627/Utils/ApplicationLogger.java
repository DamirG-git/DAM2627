package org.finalactivitydg2627.Utils;

import java.io.BufferedWriter;
import java.io.FileWriter;
import java.io.IOException;
import java.io.PrintWriter;
import java.nio.file.Files;
import java.nio.file.Paths;

public class ApplicationLogger {

    private final PrintWriter fileWriter;
    private static final ApplicationLogger instance = new ApplicationLogger();

    private ApplicationLogger() {
        var logDirectory = Paths.get(
                System.getProperty("user.dir"),
                "logs"
        );
        try {
            Files.createDirectories(logDirectory);
        } catch (IOException e) {
            throw new RuntimeException(e);
        }


        try {
            fileWriter = new PrintWriter(new BufferedWriter(new FileWriter(logDirectory.resolve("application.log").toFile(), true)), true);
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }

    public static ApplicationLogger getInstance() {
        return instance;
    }

    public enum LogLevel {
        INFO, WARNING, ERROR, DEBUG
    }

    private boolean debug = false;

    public void setDebug(boolean debug) {
        this.debug = debug;
    }

    public synchronized void log(String message, LogLevel logLevel) {
        if (logLevel != LogLevel.DEBUG || debug) {
            inLog(message, logLevel);
        }
    }

    private void inLog(String message, LogLevel logLevel) {
        switch (logLevel) {
            case INFO -> fileWriter.println("INFO: " + message);
            case ERROR -> fileWriter.println("!ERROR!: " + message);
            case DEBUG -> fileWriter.println("#DEBUG#: " + message);
            case WARNING -> fileWriter.println("<WARNING>: " + message);
        }
        fileWriter.flush();
    }


    public void close() {
        fileWriter.close();
    }


}
