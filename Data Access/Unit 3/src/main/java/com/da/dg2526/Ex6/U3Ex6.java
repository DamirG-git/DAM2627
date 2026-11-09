package com.da.dg2526.Ex6;

import java.sql.SQLException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Random;
import java.util.Scanner;

import static com.da.dg2526.Utility.getConnectionVTInstitute;

public class U3Ex6 {

    final String text = """
            Possible commands: -exit (closes app), -end (concludes the input)
            Input subject and a year its supposed to belong to.
            Example: SubjectName, 2
            """;

    public U3Ex6(Scanner sc) {
        System.out.println(text);

        var listInputs = new ArrayList<inputValidationResponse>();
        var loop = true;
        while (loop) {
            var line = sc.nextLine();
            line = line.trim();
            if (line.isEmpty()) {
                System.out.println("Input is empty.");
                continue;
            }
            var inpArray = line.trim().split("\\s*,\\s*", -1);
            switch (inpArray.length) {
                case 1:
                    if (inpArray[0].equalsIgnoreCase("-exit")) {
                        return;
                    }
                    if (inpArray[0].equalsIgnoreCase("-end")) {
                        loop = false;
                        break;
                    }
                    System.out.println("Invalid command: " + inpArray[0]);
                    break;

                case 2:
                    var name = inpArray[0];
                    var year = inpArray[1];


                    var result = inputValidation(name, year);
                    if (result.passed) {
                        System.out.println(result);
                        listInputs.add(result);
                        System.out.println("Continue input or end it with \"-end\"");
                    }
                    break;
                default:
                    System.out.println("Invalid input: " + Arrays.toString(inpArray));

            }
        }
        try (var conn = getConnectionVTInstitute(); var statement = conn.prepareStatement("INSERT into subjects(name,year) values (?,?)")) {
            for (var input : listInputs) {
                statement.setString(1, input.name());
                statement.setInt(2, input.year());
                statement.addBatch();
            }
            System.out.println("Affected rows: " + statement.executeBatch().length);
            statement.executeBatch();

        } catch (ClassNotFoundException | SQLException e) {
            System.out.println(e.getMessage());
        }

    }

    public U3Ex6(int variation) {
        switch (variation) {
            case 2:
                createTable();
                break;
            case 3:
                addNewTableToCourses();
                break;
        }
    }


    // 6.2 requirement
    public void createTable() {
        try (var conn = getConnectionVTInstitute();
             var tableStatement = conn.prepareStatement("CREATE TABLE IF NOT EXISTS   courses(code SERIAL PRIMARY KEY, name VARCHAR(90))");
             var insertStatement = conn.prepareStatement("INSERT INTO courses(name) values (?)")) {

            tableStatement.executeUpdate();
            insertStatement.setString(1, "Multiplatform app development");
            insertStatement.executeUpdate();
            insertStatement.setString(1, "Web development");
            insertStatement.executeUpdate();
            System.out.println("Table created, 2 values created.");

        } catch (ClassNotFoundException | SQLException e) {
            System.out.println(e.getMessage());
        }
    }

    public void addNewTableToCourses() {
        try (var conn = getConnectionVTInstitute();
             var prepStateAddColumn = conn.prepareStatement("ALTER TABLE subjects ADD COLUMN IF NOT EXISTS course INTEGER");
             var prepStateSetValuesCourse = conn.prepareStatement("UPDATE subjects SET course = ? WHERE code = ?");
             var prepStateSetExternalKey = conn.prepareStatement("ALTER TABLE subjects ADD CONSTRAINT fk_subject_courses FOREIGN KEY (course) REFERENCES courses(code)");
             var statement = conn.createStatement()) {
            System.out.println("Adding new column to subjects.");
            var columns = prepStateAddColumn.executeUpdate();
            System.out.println(columns + " columns affected.");
            System.out.println("Obtaining all subjects");
            var SQLSentence = "SELECT code FROM subjects WHERE course IS NULL ORDER BY code;";
            var random = new Random();
            try (var resultSet = statement.executeQuery(SQLSentence);) {
                while (resultSet.next()) {
                    var id = resultSet.getInt(1);
                    var courseID = random.nextInt(1, 3);
                    System.out.println("Setting subject with id " + id + " to course " + courseID);
                    prepStateSetValuesCourse.setInt(1, courseID);
                    prepStateSetValuesCourse.setInt(2, id);
                    prepStateSetValuesCourse.addBatch();
                }

            }

            var results = prepStateSetValuesCourse.executeBatch();
            System.out.println(results.length + " columns affected.");
            System.out.println("Setting up foreign key");
            prepStateSetExternalKey.executeUpdate();
            System.out.println("Done.");


        } catch (ClassNotFoundException | SQLException e) {
            System.out.println(e.getMessage());
        }
    }


    record inputValidationResponse(String name, int year, boolean passed) {
    }

    private inputValidationResponse inputValidation(String name, String year) {
        name = name == null ? "" : name.trim();
        year = year == null ? "" : year.trim();
        var failResponse = new inputValidationResponse("", 0, false);
        var error = new StringBuilder();
        if (name.isEmpty()) {
            error.append("Name is empty").append("\n");
        }
        if (year.isEmpty()) {
            error.append("Year is empty").append("\n");
        }
        if (name.length() > 50) {
            error.append("Name is too long: ").append(name).append("\n");
        }

        if (name.matches(".*\\d.*")) {
            error.append("Name cannot contain numbers: ").append(name).append("\n");
        }
        int yearInt;
        try {
            yearInt = Integer.parseInt(year);
        } catch (NumberFormatException e) {
            error.append("Invalid year: ").append(year);
            System.out.println(error);
            return failResponse;
        }
        if (yearInt < 1 || yearInt > 2) {
            error.append("Out of bounds (1-2) year: ").append(year);
        }
        if (!error.isEmpty()) {
            System.out.println(error);
            return failResponse;
        }
        return new inputValidationResponse(name, yearInt, true);
    }
}
