package dg2627activities.U2.Ex1;

import java.util.Scanner;

// 3.3. Activities 1
public class U2Ex1 {


    public U2Ex1(Scanner scan) {
        String name;
        String surname;

        do {
            System.out.println("Input your name:");
            name = scan.nextLine();
        } while (name.isEmpty());

        do {
            System.out.println("Input your surname:");
            surname = scan.nextLine();
        } while (surname.isEmpty());
        System.out.println("Hello," + name +" "+ surname + "!");
    }


}
