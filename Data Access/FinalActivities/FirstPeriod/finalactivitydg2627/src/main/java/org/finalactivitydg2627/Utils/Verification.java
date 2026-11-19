package org.finalactivitydg2627.Utils;

import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.time.LocalDate;
import java.time.format.DateTimeParseException;
import java.util.regex.Pattern;

public class Verification {

    public record ResultValue<T>(T result, String message) {
    }


    private Verification() {
    }


    public static final String STR_TOO_SHORT = " is too short.\n";
    public static final String STR_TOO_LONG = " is too long.\n";
    public static final String STR_INVALID_PATTERN = " has an invalid format.\n";

    public static ResultValue<String> basicStringVerification(
            String label,
            String input,
            Integer minLength,
            Integer maxLength,
            Pattern pattern
    ) {
        var rs = isEmptyInput(label, input);
        if (rs.result == null) {
            return new ResultValue<>(null, rs.message());
        }
        input = rs.result;

        if (minLength != null && input.length() < minLength) {
            return new ResultValue<>(null, label + " " + input +  STR_TOO_SHORT);
        }

        if (maxLength != null && input.length() > maxLength) {
            return new ResultValue<>(null, label + " " + input +  STR_TOO_LONG);
        }

        if (pattern != null && !pattern.matcher(input).matches()) {
            return new ResultValue<>(null, label + " " + input +  STR_INVALID_PATTERN);
        }

        return new ResultValue<>(input, null);
    }

    public static final String VALUE_INVALID_INT = " is invalid number.\n";
    public static final String INT_VALUE_TOO_HIGH = " is too high.\n";
    public static final String INT_VALUE_TOO_LOW = " is too low.\n";

    public static ResultValue<Integer> basicIntegerVerification(
            String label,
            String input,
            Integer min,
            Integer max) {

        var rs = isEmptyInput(label, input);
        if (rs.result == null) {
            return new ResultValue<>(null, rs.message());
        }
        input = rs.result;
        int rawInt;
        try {
            rawInt = Integer.parseInt(input);
        } catch (NumberFormatException e) {
            return new ResultValue<>(null, label + " " + input +  VALUE_INVALID_INT);
        }
        if (min != null && rawInt < min) {
            return new ResultValue<>(null, label + " " + input +  INT_VALUE_TOO_LOW);
        }
        if (max != null && rawInt > max) {
            return new ResultValue<>(null, label + " " + input +  INT_VALUE_TOO_HIGH);
        }
        return new ResultValue<>(rawInt, null);
    }


    public static final String VALUE_INVALID_PATH = " is invalid path.\n";
    public static final String PATH_NOT_EXISTING_FILE = " does not exist.\n";
    public static final String PATH_NOT_REGULAR_FILE = " is not a regular file.\n";
    public static final String PATH_NOT_READABLE_FILE = " is not a readable file.\n";
    public static final String VALUE_INVALID_XML_FILE = " is not a valid XML file.\n";


    public static ResultValue<Path> basicPathVerification(String label, String input) {
        var rs = isEmptyInput(label, input);
        if (rs.result == null) {
            return new ResultValue<>(null, rs.message());
        }
        input = rs.result;
        Path file;
        try {
            file = Paths.get(input);
        } catch (Exception e) {
            return new ResultValue<>(null, label + " " + input +  VALUE_INVALID_PATH);
        }
        if (!Files.exists(file)) {
            return new ResultValue<>(null, label + " " + input +  PATH_NOT_EXISTING_FILE);
        }
        if (!Files.isRegularFile(file)) {
            return new ResultValue<>(null, label + " " + input +  PATH_NOT_REGULAR_FILE);
        }
        if (!Files.isReadable(file)) {
            return new ResultValue<>(null, label + " " + input +  PATH_NOT_READABLE_FILE);
        }
        if (!file.getFileName().toString().toLowerCase().endsWith(".xml")) {
            return new ResultValue<>(null, label + " " + input +  VALUE_INVALID_XML_FILE);
        }
        return new ResultValue<>(file, null);
    }


    public static final String VALUE_INVALID_DATE = " is an invalid date.\n";
    public static final String DATE_BELOW_MIN = " the date cannot be earlier than: %s.%n";
    public static final String DATE_ABOVE_MAX = " the date cannot be later than: %s.%n";

    public static ResultValue<LocalDate> basicDateVerification(String label, String input, LocalDate min, LocalDate max) {
        var rs = isEmptyInput(label, input);
        if (rs.result() == null) {
            return new ResultValue<>(null, rs.message());
        }
        input = rs.result();
        LocalDate date;
        try {
            date = LocalDate.parse(input);

        } catch (DateTimeParseException e) {
            return new ResultValue<>(null, label + " " + input +  VALUE_INVALID_DATE);
        }
        if (min != null && date.isBefore(min)) {
            return new ResultValue<>(null, label + " " + input +  String.format(DATE_BELOW_MIN, min));
        }
        if (max != null && date.isAfter(max)) {
            return new ResultValue<>(null, label + " " + input +  String.format(DATE_ABOVE_MAX, max));
        }

        return new ResultValue<>(date, null);
    }

    public static final String VALUE_IS_NULL = " is null.\n";
    public static final String VALUE_IS_EMPTY = " is empty.\n";

    private static ResultValue<String> isEmptyInput(String label, String input) {
        if (input == null) {
            return new ResultValue<>(null, label + VALUE_IS_NULL);
        }
        input = input.trim();

        if (input.isEmpty()) {
            return new ResultValue<>(null, label + VALUE_IS_EMPTY);
        }
        return new ResultValue<>(input, null);
    }
}
