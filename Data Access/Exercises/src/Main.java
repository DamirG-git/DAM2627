import dg2627activities.U2.Ex1.U2Ex1;
import dg2627activities.U2.Ex2.U2Ex2;
import dg2627activities.U2.Ex3.U2Ex3;
import dg2627activities.U2.Ex4.U2Ex4;
import dg2627activities.U2.Ex7.U2Ex7;
import dg2627activities.U2.Ex5.U2Ex5;
import dg2627activities.U2.Ex6.U2Ex6;

import java.util.Arrays;
import java.util.Scanner;

//TIP To <b>Run</b> code, press <shortcut actionId="Run"/> or
// click the <icon src="AllIcons.Actions.Execute"/> icon in the gutter.
public class Main {


    final static int UNIT_COUNT = 2;
    final static int[] RUNNABLE_EXERCISES_U2 = new int[]{1, 2, 3, 4, 5, 6, 7};

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
                    System.out.println("Select runnable exercises in unit 2: " + Arrays.toString(RUNNABLE_EXERCISES_U2));
                    var u2 = InputValidation(scan, RUNNABLE_EXERCISES_U2[RUNNABLE_EXERCISES_U2.length - 1]);
                    exit = switch (u2) {
                        case 1 -> {
                            new U2Ex1(scan);
                            yield true;
                        }
                        case 2 -> {
                            new U2Ex2(scan);
                            yield true;
                        }
                        case 3 -> {
                            new U2Ex3(scan);
                            yield true;
                        }
                        case 4 -> {
                            new U2Ex4(scan);
                            yield true;
                        }
                        case 5 -> {
                            new U2Ex5();
                            yield true;
                        }

                        case 6 -> {
                            new U2Ex6(scan);
                            yield true;
                        }

                        case 7 -> {
                            new U2Ex7(scan);
                            yield true;
                        }

                        default -> false;
                    };
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