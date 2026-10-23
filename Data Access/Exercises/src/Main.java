import dg2627activities.U2.Ex1.U2Ex3n1;
import dg2627activities.U2.Ex10.U2Ex5n3;
import dg2627activities.U2.Ex11.U2Ex5n4;
import dg2627activities.U2.Ex12.U2Ex7n1;
import dg2627activities.U2.Ex13.U2Ex8n1;
import dg2627activities.U2.Ex14.U2Ex8n2;
import dg2627activities.U2.Ex15.U2Ex8n4n1;
import dg2627activities.U2.Ex16.U2Ex8n4n2;
import dg2627activities.U2.Ex2.U2Ex3n2;
import dg2627activities.U2.Ex3.U2Ex3n3;
import dg2627activities.U2.Ex4.U2Ex3n4;
import dg2627activities.U2.Ex5.U2Ex4n1;
import dg2627activities.U2.Ex6.U2Ex4n2;
import dg2627activities.U2.Ex7.U2Ex4n3;
import dg2627activities.U2.Ex8.U2Ex5n1;
import dg2627activities.U2.Ex9.U2Ex5n2;

import java.util.Arrays;
import java.util.Scanner;

//TIP To <b>Run</b> code, press <shortcut actionId="Run"/> or
// click the <icon src="AllIcons.Actions.Execute"/> icon in the gutter.
public class Main {


    final static int UNIT_COUNT = 2;
    final static int[] RUNNABLE_EXERCISES_U2 = new int[]{1, 2, 3, 4, 5, 6, 7, 8, 9, 10, 11, 12, 13, 14,15,16};

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
                            new U2Ex3n1(scan);
                            yield true;
                        }
                        case 2 -> {
                            new U2Ex3n2(scan);
                            yield true;
                        }
                        case 3 -> {
                            new U2Ex3n3(scan);
                            yield true;
                        }
                        case 4 -> {
                            new U2Ex3n4(scan);
                            yield true;
                        }
                        case 5 -> {
                            new U2Ex4n1();
                            yield true;
                        }

                        case 6 -> {
                            new U2Ex4n2(scan);
                            yield true;
                        }

                        case 7 -> {
                            new U2Ex4n3(scan);
                            yield true;
                        }

                        case 8 -> {
                            new U2Ex5n1(scan);
                            yield true;
                        }

                        case 9 -> {
                            new U2Ex5n2(scan);
                            yield true;
                        }

                        case 10 -> {
                            new U2Ex5n3(scan);
                            yield true;
                        }

                        case 11 -> {
                            new U2Ex5n4(scan);
                            yield true;
                        }
                        case 12 -> {
                            new U2Ex7n1(scan);
                            yield true;
                        }
                        case 13 -> {
                            new U2Ex8n1();
                            yield true;
                        }
                        case 14 -> {
                            new U2Ex8n2(scan);
                            yield true;
                        }

                        case 15 -> {
                            new U2Ex8n4n1();
                            yield true;
                        }
                        case 16 -> {
                            new U2Ex8n4n2(scan);
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