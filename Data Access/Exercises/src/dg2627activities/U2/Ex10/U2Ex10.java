package dg2627activities.U2.Ex10;

import dg2627activities.U2.Utility;

import java.io.BufferedReader;
import java.io.FileReader;
import java.io.IOException;
import java.util.Scanner;

// 5.4. Activities 3
public class U2Ex10 {


    public U2Ex10(Scanner sc) {

        System.out.println("Give me location of a file to read:");
        var pathResult = new Utility.PathResult();
        while (true) {
            pathResult.setPath(sc.nextLine());
            if (!pathResult.getError().isBlank()) {
                System.out.println(pathResult.getError());
                continue;
            }
            break;
        }
        try (var br = new BufferedReader(new FileReader(pathResult.getPath().toString()))) {
            var count = 0;

            for (var line : br.lines().toList()) {
                if (count == 23) {
                    System.out.println();
                    System.out.println("Press enter to continue reading the file.");
                    sc.nextLine();
                    count = 0;
                }
                count++;
                System.out.println(line);
            }
        } catch (IOException e) {
            System.out.println(e.getMessage());
        }


    }

}
