package com.da.dg2526.Ex8;

import com.da.dg2526.Utility;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.SQLException;
import java.util.Scanner;

public class U3Ex8 {


    public U3Ex8(Scanner scanner) {
        addNewEmployeeToDepartment(scanner);
    }


    private void addNewEmployeeToDepartment(Scanner scanner) {
        System.out.print("To exit the application at any point write -exit.\n");
        try (var conn = Utility.getConnectionEmployees();) {
            try (
                    var prepStateCreateEmployee = conn.prepareStatement("INSERT INTO public.employee(empno, ename, job, deptno) values (?,?,?,?)");
                    var prepStateGetDepartmentNumber = conn.prepareStatement("SELECT deptno FROM public.dept WHERE dname = ?");
                    var prepStateSearchValidId = conn.prepareStatement("SELECT COALESCE(MAX(deptno), 0) + 10 FROM public.dept");
                    var prepStateCreateDepartment = conn.prepareStatement("INSERT into public.dept(deptno, dname, loc) values (?,?,?)");
                    var prepStateCheckIdUniq = conn.prepareStatement("SELECT employee.empno FROM employee where empno=?");
                    // Specifically left a bug that crashes showing rollback. Un-comment to fix it.

            ) {
                conn.setAutoCommit(false);
                var employeeInput = validateEmployee(scanner);
                if (employeeInput == null) {
                    System.out.println("Exiting...");
                    conn.rollback();
                    return;
                }
                System.out.println("Getting department with name: " + employeeInput.departmentName);
                prepStateGetDepartmentNumber.setString(1, employeeInput.departmentName());

                //Get department ID.
                Integer depId;
                try (var rsDepId = prepStateGetDepartmentNumber.executeQuery()) {
                    depId = rsDepId.next() ? rsDepId.getInt(1) : null;
                }

                // If not found its null. So I create the department.
                if (depId == null) {
                    // Department needs location, ask for it.
                    var depLoc = getDepartmentLocation(scanner);
                    if (depLoc == null) {
                        conn.rollback();
                        return;
                    }
                    // Null is always exit result.

                    //Creating new department
                    System.out.println("Creating new department");
                    try (var rsNewId = prepStateSearchValidId.executeQuery()) {
                        rsNewId.next();
                        depId = rsNewId.getInt(1);
                    }
                    System.out.println("Creating new department with id: " + depId);

                    prepStateCreateDepartment.setInt(1, depId);
                    prepStateCreateDepartment.setString(2, employeeInput.departmentName());
                    prepStateCreateDepartment.setString(3, depLoc);
                    System.out.printf("Creating new department with name: [%s], department Id: [%d], and department location: [%s] \n", employeeInput.departmentName, depId, depLoc);
                    prepStateCreateDepartment.executeUpdate();
                }

                // Create new employee
                System.out.printf("Creating new employee with id: [%d], Name [%s] in Department: [%s] with job [%s] \n", employeeInput.id, employeeInput.name, employeeInput.departmentName, employeeInput.job);
                var validateId = validateEmployeeId(employeeInput.id, scanner, prepStateCheckIdUniq);
                if (validateId == null) {
                    System.out.println("Exiting...");
                    conn.rollback();
                    return;
                }
                //prepStateCreateEmployee.setInt(1, employeeInput.id());
                prepStateCreateEmployee.setInt(1, validateId);
                prepStateCreateEmployee.setString(2, employeeInput.name());
                prepStateCreateEmployee.setString(3, employeeInput.job());
                prepStateCreateEmployee.setInt(4, depId);
                prepStateCreateEmployee.executeUpdate();
                System.out.println("Done!");
                conn.commit();
                System.out.println("Transaction committed successfully!");


            } catch (SQLException e) {
                try {
                    System.out.println("Caught SQL Exception, rolling back.");
                    conn.rollback();
                } catch (SQLException ex) {
                    throw new RuntimeException(ex);
                }
                throw new RuntimeException(e);
            }
        } catch (ClassNotFoundException | SQLException e) {
            throw new RuntimeException(e);
        }

        // I do know that app crashes if employe id is incorrect. But i'm gonna leave it just to show that rollback works.


    }

    private String getDepartmentLocation(Scanner scanner) {
        while (true) {
            System.out.print("Department does not exist, input location name: ");
            var inRaw = scanner.nextLine().trim();
            if (inRaw.isEmpty()) {
                System.out.println("Location name is empty.");
                continue;
            }
            if (inRaw.equalsIgnoreCase("-exit")) {
                return null;
            }
            if (inRaw.length() > 13) {
                System.out.println("Location name is too long: " + inRaw.length() + "/13: " + inRaw);
                continue;
            }

            return inRaw;

        }
    }


    private record EmployeeInput(int id, String name, String job, String departmentName) {
    }

    private Integer validateEmployeeId(Integer rawIdInput, Scanner scanner, PreparedStatement statement) throws SQLException {


        statement.setInt(1, rawIdInput);
        try (var idSearch = statement.executeQuery()) {
            if (idSearch.next()) {
                System.out.println("Id is taken, input new id.");
            } else {
                return rawIdInput;
            }
        }

        while (true) {
            var result = basicIntValueValidation("id", scanner);
            if (result == null) {
                return null;
            }
            statement.setInt(1, result);
            try (var idSearch = statement.executeQuery()) {
                if (idSearch.next()) {
                    System.out.println("Id is taken, input new id.");
                    continue;
                }
            }
            System.out.printf("New employee id: [%d]\n", result);
            return result;
        }
    }

    private EmployeeInput validateEmployee(Scanner scanner) {
        // Null only possible with exit command.
        var name = basicStingValueValidation("name", scanner, 10);
        if (name == null) return null;

        var job = basicStingValueValidation("job", scanner, 9);
        if (job == null) return null;

        var department = basicStingValueValidation("department", scanner, 14);
        if (department == null) return null;

        var id = basicIntValueValidation("id", scanner);
        if (id == null) return null;

        return new EmployeeInput(id, name, job, department);


    }

    private Integer basicIntValueValidation(String label, Scanner scanner) {
        while (true) {
            System.out.print("Enter employee " + label + ": ");
            var input = scanner.nextLine().trim();
            if (input.isEmpty()) {
                System.out.print("Employee " + label + " is empty: " + input);
                continue;
            }
            if (input.equalsIgnoreCase("-exit")) {
                return null;
            }
            try {
                return Integer.parseInt(input);
            } catch (NumberFormatException e) {
                System.out.print("Employee " + label + " is invalid: " + input);
            }
        }
    }

    private String basicStingValueValidation(String label, Scanner scanner, int maxSize) {
        while (true) {
            System.out.print("Enter employee " + label + ": ");
            var input = scanner.nextLine().trim();
            if (input.isEmpty()) {
                System.out.print("Employee " + label + " is empty: " + input);
                continue;
            }
            if (input.equalsIgnoreCase("-exit")) {
                return null;
            }
            if (input.length() > maxSize) {
                System.out.print("Employee " + label + " is too long " + input.length() + ": " + input);
                continue;
            }
            return input;
        }

    }

}

