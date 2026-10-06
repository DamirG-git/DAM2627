package dg2627activities.U2.Ex9;

import dg2627activities.U2.Utility;

import java.io.*;
import java.nio.file.Path;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Scanner;

// 5.4. Activities 2
public class U2Ex9 {

    final String folder = "Exercises/src/dg2627activities/U2/Ex9/output.txt";

    public U2Ex9(Scanner sc) {


        var pathResult = new Utility.PathResult();
        var loc1 = getPath(pathResult, "Enter location of file 1:", sc);
        var loc2 = getPath(pathResult, "Enter location of file 2:", sc);

        var file1 = new ArrayList<String>();
        try (var brFile1 = new BufferedReader(new FileReader(loc1.toString())); var brFile2 = new BufferedReader(new FileReader(loc2.toString()))) {

            file1 = new ArrayList<>(brFile1.lines().toList());
            var file2 = (brFile2.lines().toList());
            file1.addAll(file2);
            Collections.sort(file1);

        } catch (IOException e) {
            System.err.println(e.getMessage());
            return;
        }
        try (var bw = new BufferedWriter(new FileWriter(folder))) {
            for (var line : file1) {
                bw.write(line);
                bw.newLine();
            }
        } catch (IOException e) {
            System.err.println(e.getMessage());
        }


    }

    public Path getPath(Utility.PathResult pathResult, String message, Scanner sc) {
        while (true) {
            System.out.println(message);
            pathResult.setPath(sc.nextLine());
            if (!pathResult.getError().isBlank()) {
                System.out.println(pathResult.getError());
                continue;
            }
            return pathResult.getPath();
        }
    }
}
