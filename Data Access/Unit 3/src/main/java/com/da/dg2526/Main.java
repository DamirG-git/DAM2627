package com.da.dg2526;

import com.da.dg2526.Ex5.U3Ex5n3;
import com.da.dg2526.Ex6.U3Ex6n1;

import java.util.Arrays;
import java.util.Scanner;

public class Main {


    //final static int UNIT_COUNT = 2;
    final static int[] RUNNABLE_EXERCISES_U3 = new int[]{5, 6};
    final static int[] RUNNABLE_EXERCISES_U3_SUB6 = new int[]{1, 2,3};

    public static void main(String[] args) {


        var scan = new Scanner(System.in);

        boolean exit = false;
        while (!exit) {

            System.out.println("Select runnable exercises in unit 2: " + Arrays.toString(RUNNABLE_EXERCISES_U3));
            var u3 = InputValidation(scan, RUNNABLE_EXERCISES_U3[RUNNABLE_EXERCISES_U3.length - 1]);
            exit = switch (u3) {
                case 5 -> {
                    new U3Ex5n3();
                    yield true;
                }
                case 6 -> {
                    System.out.println("Sub-unit " + u3 + " has " + RUNNABLE_EXERCISES_U3_SUB6.length + " subunits, chose one.");
                    var sixChoice = InputValidation(scan, RUNNABLE_EXERCISES_U3[RUNNABLE_EXERCISES_U3.length - 1]);
                    switch (sixChoice) {
                        case 1 -> {
                            new U3Ex6n1(scan);
                            yield true;
                        }
                        case 2 -> {
                            new U3Ex6n1(2);
                            yield true;
                        }
                        case 3 -> {
                            new U3Ex6n1(3);
                            yield true;
                        }
                    }
                    yield true;
                }

                default -> {
                    System.out.println("Invalid input!");
                    yield false;
                }

            };
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