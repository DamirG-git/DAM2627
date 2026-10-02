import dg2627activities.U1.EJ2.U1Ex2;
import dg2627activities.U2.EJ1.U2Ex1;
import dg2627activities.U2.EJ2.U2Ex2;
import dg2627activities.U2.EJ3.U2Ex3;

import java.util.Scanner;

//TIP To <b>Run</b> code, press <shortcut actionId="Run"/> or
// click the <icon src="AllIcons.Actions.Execute"/> icon in the gutter.
public class Main {


    final static int UNIT_COUNT = 2;
    final static int EXERCISE_LIMIT_UNIT2 = 3;

    public static void main(String[] args) {


        var scan = new Scanner(System.in);

        System.out.println("Current unit count: " + UNIT_COUNT);

        boolean exit = false;
        while (!exit) {
            System.out.println("Which unit is the exercise from?");

            int unit = InputValidation(scan, UNIT_COUNT);

            switch (unit) {
                case 1:
                    System.out.println("No runnable exercises in unit 1");
                    break;
                case 2:
                    System.out.println("Select runnable exercises in unit 2: " + EXERCISE_LIMIT_UNIT2);
                    var u2 = InputValidation(scan, EXERCISE_LIMIT_UNIT2);
                    if (u2 == 1) {
                        new U2Ex1(scan);
                        exit = true;
                    }
                    if (u2 == 2) {
                        new U2Ex2(scan);
                        exit = true;
                    }
                    if(u2 == 3) {
                        new U2Ex3(scan);
                        exit = true;
                    }
                    break;
                default:
                    System.out.println("Invalid input!");
                    break;

            }
        }
        scan.close();

    }

    public static int InputValidation(Scanner scan, int limit) {
        int unit;
        while (true) {
            var input = scan.nextLine();
            if (input.equals("Exit")) {
                throw new RuntimeException("Application exit called");
            }
            try {
                unit = Integer.parseInt(input);
                if (unit < 0 || unit > limit) {
                    System.out.println("Value is out of range: " + limit + " is the limit.");
                } else {
                    break;
                }

            } catch (NumberFormatException e) {
                System.out.println("Invalid input!");
            }
        }
        return unit;
    }

}