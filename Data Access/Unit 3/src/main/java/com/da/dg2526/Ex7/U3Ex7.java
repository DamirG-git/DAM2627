package com.da.dg2526.Ex7;

import com.da.dg2526.Utility;

import java.sql.ResultSet;
import java.sql.SQLException;
import java.text.MessageFormat;
import java.util.Scanner;

public class U3Ex7 {

    public U3Ex7(Scanner scanner, int variation) {

        switch (variation) {
            case 1 -> createStoredProcedureListAllEmployees();
            case 2 -> createStoredProcedureListAllEmployeesBelongingToSpecificDepartment(scanner);
            case 3 -> createStoredProcedureListAllEmployersWithNamePassedAsPattern(scanner);
            default -> {
            }
        }

    }

    private void createStoredProcedureListAllEmployees() {
        var function = """
                CREATE OR REPLACE FUNCTION public.get_all_employees_on_a_specific_job (p_job VARCHAR)
                RETURNS SETOF employee
                LANGUAGE 'plpgsql'
                    AS $BODY$
                        BEGIN
                        RETURN query
                            SELECT *
                            FROM employee
                            WHERE employee.job = p_job;
                        end;
                    $BODY$;
                """;
        try (var conn = Utility.getConnectionEmployees(); var prepStateCreateFunct = conn.prepareStatement(function); var prepStateEmployeesOnSpecificJob = conn.prepareStatement("SELECT * FROM public.get_all_employees_on_a_specific_job(?)")) {
            System.out.println("Creating stored function.");
            prepStateCreateFunct.execute();
            System.out.println("Testing created function.");
            prepStateEmployeesOnSpecificJob.setString(1, "CLERK");
            try (var rs = prepStateEmployeesOnSpecificJob.executeQuery()) {
                while (rs.next()) {
                    System.out.println(MessageFormat.format("Employee number: {0}, name: {1}, job: {2}, dept: {3}", rs.getString("empno"), rs.getString("ename"), rs.getString("job"), rs.getString("deptno")));
                }
            } catch (SQLException ex) {
                System.out.println(ex.getMessage());
            }
            System.out.println("Done!");
        } catch (SQLException | ClassNotFoundException e) {
            throw new RuntimeException(e);
        }
    }

    private void createStoredProcedureListAllEmployeesBelongingToSpecificDepartment(Scanner scanner) {
        var statement = """
                CREATE OR REPLACE FUNCTION public.get_all_employees_belonging_to_a_department(p_dname VARCHAR)
                RETURNS SETOF employee
                LANGUAGE 'plpgsql'
                AS $BODY$
                BEGIN
                    RETURN QUERY
                    SELECT employee.* FROM employee
                        INNER JOIN dept on employee.deptno = dept.deptno
                                      WHERE dept.dname = p_dname;
                END;
                $BODY$;
                """;
        try (var conn = Utility.getConnectionEmployees(); var prepStateCreateFunc = conn.prepareStatement(statement); var prepStatementGetEmployeesOnJob = conn.prepareStatement("SELECT * FROM public.get_all_employees_belonging_to_a_department(?)")) {
            System.out.println("Creating stored function.");
            prepStateCreateFunc.execute();
            System.out.println("Provide a valid department name.");
            while (true) {
                var result = nameValidation(scanner.next());
                if (result.passed) {
                    prepStatementGetEmployeesOnJob.setString(1, result.name);
                    try (var rs = prepStatementGetEmployeesOnJob.executeQuery()) {
                        printEmployees(rs);
                        break;
                    }
                }

            }
            System.out.println("Done!");

        } catch (
                SQLException | ClassNotFoundException e) {
            throw new RuntimeException(e);
        }


    }

    private void createStoredProcedureListAllEmployersWithNamePassedAsPattern(Scanner scanner) {
        var statement = """
                CREATE OR REPLACE FUNCTION public.get_all_employees_by_name_with_pattern(p_pattern VARCHAR)
                RETURNS SETOF employee
                LANGUAGE 'plpgsql'
                AS $BODY$
                BEGIN
                    RETURN QUERY
                    SELECT employee.* FROM employee
                        WHERE employee.ename  ILIKE p_pattern;
                END;
                $BODY$;
                """;
        try (var conn = Utility.getConnectionEmployees(); var prepStateCreateFunc = conn.prepareStatement(statement); var prepStatementGetEmployeesOnJob = conn.prepareStatement("SELECT * FROM public.get_all_employees_by_name_with_pattern(?)")) {
            System.out.println("Creating stored function.");
            prepStateCreateFunc.execute();
            System.out.println("Enter a name pattern (SQL pattern rules): \n" + "To exit write -exit");
            while (true) {
                var input = scanner.nextLine().trim();
                if (input.equalsIgnoreCase("-exit")) {
                    System.out.println("Exiting...");
                    return;
                }
                System.out.println("Searching employees with pattern: " + input);
                prepStatementGetEmployeesOnJob.setString(1, input);
                try (var rs = prepStatementGetEmployeesOnJob.executeQuery()) {
                    printEmployees(rs);
                }
                System.out.println("If you wish to exit write -exit, otherwise provide different pattern.");
            }

        } catch (
                SQLException | ClassNotFoundException e) {
            throw new RuntimeException(e);
        }


    }

    private void printEmployees(ResultSet rs) throws SQLException {
        var found = false;
        while (rs.next()) {
            found = true;
            System.out.println(MessageFormat.format("Employee number: {0}, name: {1}, job: {2}, dept: {3}", rs.getString("empno"), rs.getString("ename"), rs.getString("job"), rs.getString("deptno")));
        }
        if (!found) {
            System.out.println("No employees found.");
        }
    }


    private record nameValRes(String name, boolean passed) {
    }

    private nameValRes nameValidation(String name) {
        name = name == null ? "" : name.trim();
        var failResponse = new nameValRes("", false);
        var error = new StringBuilder();
        if (name.isEmpty()) {
            error.append("Name is empty").append("\n");
        }
        if (name.length() > 14) {
            error.append("Name is too long: ").append(name).append("\n");
        }
        if (!error.isEmpty()) {
            System.out.println(error);
            return failResponse;
        }
        return new nameValRes(name, true);
    }


}
