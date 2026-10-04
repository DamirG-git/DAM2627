package dg2627activities.U2.Ex4;

import dg2627activities.U2.Ex3.U2Ex3;

import java.util.Scanner;

public class U2Ex4 extends U2Ex3 {
    public U2Ex4(Scanner scanner) {
        super(scanner);
    }

    @Override
    protected String DateLogic(String dayRaw, String monthRaw, String yearRaw) {
        var error = "";
        var myDate = new MyDateTime();
        myDate.setDay(dayRaw);
        myDate.setMonth(monthRaw);
        myDate.setYear(yearRaw);

        if (myDate.isInvalid()) {
            return myDate.getError();
        }


        var arrayMonth = myDate.getMonth() - 1;

        isLeapYear = (myDate.getYear() % 400 == 0) || (myDate.getYear() % 4 == 0 && myDate.getYear() % 100 != 0);
        var leapYearText = isLeapYear ? "is a leap year" : "is not a leap year";

        if (daysInAMonth[arrayMonth] < myDate.getDay()) {
            if (myDate.getMonth() != 2 || !isLeapYear || myDate.getDay() != 29) {
                var daysMessageEdge = isLeapYear&&myDate.getMonth() == 2 ? 29:daysInAMonth[arrayMonth];
                error += "Invalid date, in " + monthNames[arrayMonth] + " there are only " + daysMessageEdge + " days.\n";
                error += "The " + myDate.getYear() + " " + leapYearText + "\n";
            }
        }
        if (error.isBlank()) {
            System.out.println("Day:" + myDate.getDay() + " Month:" + monthNames[myDate.getMonth() - 1] + " Year:" + myDate.getYear());
            System.out.println("The year " + myDate.getYear() + " " + leapYearText + "\n");
        }
        return error;
    }
}
