package org.finalactivitydg2627.Utils;

import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.time.LocalDate;
import java.time.format.DateTimeParseException;
import java.util.regex.Pattern;

public class Verification {


    private Verification() {
    }


    public record ResultValue<T>(T result, String message) {
    }


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
            return new ResultValue<>(null, label + " is too short.\n");
        }

        if (maxLength != null && input.length() > maxLength) {
            return new ResultValue<>(null, label + " is too long.\n");
        }

        if (pattern != null && !pattern.matcher(input).matches()) {
            return new ResultValue<>(null, label + " has an invalid format.\n");
        }

        return new ResultValue<>(input, null);
    }


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
            return new ResultValue<>(null, label + " is invalid number.\n");
        }
        if (min != null && rawInt < min) {
            return new ResultValue<>(null, label + " is too low.\n");
        }
        if (max != null && rawInt > max) {
            return new ResultValue<>(null, label + " is too high.\n");
        }
        return new ResultValue<>(rawInt, null);
    }


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
            return new ResultValue<>(null, label + " is invalid path.\n");
        }
        if (!Files.exists(file)) {
            return new ResultValue<>(null, label + " does not exist.\n");
        }
        if(!Files.isRegularFile(file)) {
            return new ResultValue<>(null, label + " is not a regular file.\n");
        }
        if (!Files.isReadable(file)) {
            return new ResultValue<>(null, label + " is not a readable file.\n");
        }
        if (!file.getFileName().toString().toLowerCase().endsWith(".xml")) {
            return new ResultValue<>(null, label + " is not a valid XML file.\n");
        }
        return new ResultValue<>(file, null);
    }

    public static ResultValue<LocalDate> basicDateVerification(String label, String input) {
        var rs = isEmptyInput(label, input);

        if (rs.result() == null) {
            return new ResultValue<>(null, rs.message());
        }

        input = rs.result();

        try {
            LocalDate localDate = LocalDate.parse(input);
            return new ResultValue<>(localDate, "");
        } catch (DateTimeParseException e) {
            return new ResultValue<>(null, label + " is an invalid date.\n");
        }
    }

    private static ResultValue<String> isEmptyInput(String label, String input){
        if (input == null) {
            return new ResultValue<>(null, label + " was null.\n");
        }
        input = input.trim();

        if (input.isEmpty()) {
            return new ResultValue<>(null, label + " was empty.\n");
        }
        return new ResultValue<>(input, null);
    }
}
