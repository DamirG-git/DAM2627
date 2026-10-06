package dg2627activities.U2.Ex8;

import java.io.*;
import java.util.Scanner;

// 5.4. Activities 1
public class U2Ex8 {

    final String fileLoc = "Exercises/src/dg2627activities/U2/Ex8/output.txt";

    public U2Ex8(Scanner sc) {
        boolean append = false;

        System.out.println("Write \"-exit\" to leave the program.");
        long lineNumber = 1;
        var file = new File(fileLoc);
        if (!file.exists()) {
            if (!createFile(file)) {
                return;
            }
        } else {
            System.out.println("File " + fileLoc + " already exists. Want to reset it?");
            while (true) {
                var response = sc.nextLine();
                switch (response.toLowerCase()) {
                    case "-exit":
                        return;
                    case "y":
                    case "yes":
                        break;
                    case "n":
                    case "no":
                        append = true;
                        lineNumber = countLinesInFile(file);
                        if (lineNumber == -1) return; // Error
                        System.out.println("Old file contained " + lineNumber + " lines.");
                        lineNumber++;
                        break;
                    default:
                        System.out.println("Invalid input. Try again.");
                        continue;
                }
                break;
            }
        }

        System.out.println("Input lines to save to a file. Write \"-exit\" to leave the program.");
        try (BufferedWriter bw = new BufferedWriter(new FileWriter(fileLoc, append))) {

            while (true) {
                String line = sc.nextLine();
                if (line.equals("-exit")) {
                    System.out.println("Exiting...");
                    break;
                }
                bw.write(lineNumber + ": " + line);
                bw.newLine();
                lineNumber++;
            }


        } catch (Exception e) {
            System.err.println(e.getMessage());
        }


    }

    public boolean createFile(File file) {
        System.out.println("File " + fileLoc + " does not exist, creating it.");
        try {
            if (file.createNewFile()) {
                System.out.println("File " + fileLoc + " created.");
                return true;
            } else {
                System.out.println("File " + fileLoc + " creation failed.");
                return false;
            }
        } catch (IOException e) {
            System.out.println("Error creating file " + fileLoc);
            System.err.println(e.getMessage());
            return false;
        }
    }

    public long countLinesInFile(File file) {
        try (var in = new BufferedReader(new FileReader(file))) {
            return in.lines().count();

        } catch (Exception e) {
            System.out.println("Error reading file " + fileLoc);
            System.err.println(e.getMessage());
            return -1;
        }
    }

}
