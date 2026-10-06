package dg2627activities.U2.Ex11;

import dg2627activities.U2.Utility;

import java.io.BufferedReader;
import java.io.FileReader;
import java.io.IOException;
import java.util.Scanner;

// 5.4. Activities 4
public class U2Ex11 {


    record FoundText(int linePos, String text) {
    }

    public U2Ex11(Scanner sc) {

        System.out.println("Give me location of a file to read:");
        var pathResult = new Utility.PathResult();
        while (true) {
            pathResult.setPath(sc.nextLine());
            if (!pathResult.getError().isBlank()) {
                System.err.println(pathResult.getError());
                continue;
            }
            break;
        }
        String text;
        while (true) {
            System.out.println("Give me text to search for:");
            text = sc.nextLine();

            if (text.isEmpty()) {
                System.out.println("Text to search is empty");
                continue;
            }

            System.out.println("Are you sure you want to search this (Y/N): " + text);

            while (true) {
                var response = sc.nextLine();

                switch (response.toLowerCase()) {
                    case "y":
                    case "yes":
                        break;

                    case "n":
                    case "no":
                        text = null;
                        break;

                    case "-exit":
                        return;

                    default:
                        System.out.println("Invalid input. Try again.");
                        continue;
                }

                break;
            }

            if (text != null) {
                break;
            }
        }

        try (var br = new BufferedReader(new FileReader(pathResult.getPath().toString()))) {
            int linePos = 1;

            String line;
            while ((line = br.readLine()) != null) {
                if (line.contains(text)) {
                    System.out.println(linePos + ": " + line);
                }
                linePos++;
            }
        } catch (IOException e) {
            System.err.println(e.getMessage());
        }

    }
}
