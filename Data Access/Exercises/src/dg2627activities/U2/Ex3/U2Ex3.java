package dg2627activities.U2.Ex3;

import java.util.Scanner;

// 3.3. Activities 3
public class U2Ex3 {
    public U2Ex3(Scanner scanner) {
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

    protected final int[] daysInAMonth = {31, 28, 31, 30, 31, 30, 31, 31, 30, 31, 30, 31};
    protected final String[] monthNames = {"January", "February", "March", "April", "May", "June", "July", "August", "September", "October", "November", "December"};

    public static class MyDateTime {
        private int day;
        private int month;
        private int year;
        private String error = "";
        private boolean isInvalid;


        public int getDay() {
            return day;
        }

        public int getMonth() {
            return month;
        }

        public int getYear() {
            return year;
        }

        public String getError() {
            return error;
        }

        public boolean isInvalid() {
            return isInvalid;
        }

        public void setDay(String dayRaw) {

            try {
                var dayIn = Integer.parseInt(dayRaw);
                if (dayIn < 1 || dayIn > 31) {
                    error += "Day out of limits 1-31: " + dayRaw + "\n";
                    isInvalid = true;
                }
                this.day = dayIn;
            } catch (NumberFormatException e) {
                error += "Invalid day input: " + dayRaw + "\n";
                isInvalid = true;
            }

        }


        public void setMonth(String monthRaw) {
            try {
                var monthIn = Integer.parseInt(monthRaw);
                if (monthIn < 1) {
                    error += "Month cannot be negative: " + monthIn + "\n";
                    isInvalid = true;
                    return;
                }
                if (monthIn > 12) {
                    error += "Month cannot be greater than 12: " + monthIn + "\n";
                    isInvalid = true;
                    return;
                }
                this.month = monthIn;
            } catch (NumberFormatException e) {
                error += "Invalid month input: " + monthRaw + "\n";
                isInvalid = true;
            }
        }


        public void setYear(String yearRaw) {
            try {
                var yearIn = Integer.parseInt(yearRaw);
                if (yearIn < 1) {
                    error += "Year cannot be negative:" + yearIn + "\n";
                    isInvalid = true;
                    return;
                }
                this.year = yearIn;
            } catch (NumberFormatException e) {
                error += "Invalid year input: " + yearRaw + "\n";
                isInvalid = true;
            }

        }

    }

    protected boolean isLeapYear = false;

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
        if (daysInAMonth[arrayMonth] < myDate.getDay()) {
            if (myDate.getMonth() != 2 || !isLeapYear || myDate.getDay() != 29) {
                var daysMessageEdge = isLeapYear && myDate.getMonth() == 2 ? 29 : daysInAMonth[arrayMonth];
                error += "Invalid date, in " + monthNames[arrayMonth] + " there are only " + daysMessageEdge + " days.\n";
            }
        }
        if (error.isBlank()) {
            System.out.println("Day:" + myDate.getDay() + " Month:" + monthNames[myDate.getMonth() - 1] + " Year:" + myDate.getYear());
        }
        return error;
    }
}
