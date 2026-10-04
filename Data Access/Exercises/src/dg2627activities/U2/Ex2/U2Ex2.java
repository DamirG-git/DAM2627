package dg2627activities.U2.Ex2;

import java.util.Scanner;

public class U2Ex2 {

    public U2Ex2(Scanner scanner) {
        while (true) {
            System.out.println("Give me a date in format DD/MM/YYYY.");
            System.out.print("Day:");
            var day = scanner.nextLine();
            System.out.print("Month:");
            var month = scanner.nextLine();
            System.out.print("Year:");
            var year = scanner.nextLine();
            var error = DateLogic(day, month, year);
            if (error.isBlank()) {
                break;
            } else {
                System.out.println(error);
            }
        }
    }

    private final int[] daysInAMonth = {31, 28, 31, 30, 31, 30, 31, 31, 30, 31, 30, 31};
    private final String[] monthNames = {"January", "February", "March", "April", "May", "June", "July", "August", "September", "October", "November", "December"};

    String DateLogic(String dayRaw, String monthRaw, String yearRaw) {
        var error = "";
        int day = 0;
        try {
            day = Integer.parseInt(dayRaw);
        } catch (NumberFormatException e) {
            error += "Invalid day input.\n";
        }
        int month = 0;
        try {
            month = Integer.parseInt(monthRaw);
        } catch (NumberFormatException e) {
            error += "Invalid month input.\n";
        }
        int year = 0;
        try {
            year = Integer.parseInt(yearRaw);
        } catch (NumberFormatException e) {
            error += "Invalid year input.\n";
        }

        if (!error.isBlank()) {
            return error;
        }

        if (day < 1 || day > 31) {
            error += "Invalid day date " + dayRaw + "\n";
        }
        boolean monthError = false;
        if (month < 1 || month > 12) {
            error += "Invalid month " + monthRaw + "; limit is 1-12.\n";
            monthError = true;
        }

        if (year < 1) {
            error += "Invalid year " + yearRaw + "; can't be negative.\n";
        }
        var arrayMonth = month - 1;
        if (!monthError && daysInAMonth[arrayMonth] < day) {
            boolean isLeapYear = (year % 400 == 0) || (year % 4 == 0 && year % 100 != 0);
            if (month != 2 || !isLeapYear || day != 29)
                error += "Invalid date, in " + monthNames[arrayMonth] + " only has " + daysInAMonth[arrayMonth] + "days.\n";
        }
        if (error.isBlank()) {
            System.out.println("Day: " + day + " Month: " + monthNames[month - 1] + " Year: " + year);
        }
        return error;
    }


}
