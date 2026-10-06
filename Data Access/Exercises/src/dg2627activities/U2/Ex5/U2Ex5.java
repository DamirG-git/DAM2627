package dg2627activities.U2.Ex5;

import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;

// 4.4. Activities 1
public class U2Ex5 {
    final String folder = "Exercises/src/dg2627activities/U2/Ej5/";

    public U2Ex5() {
        try (FileInputStream fIn = new FileInputStream(folder + "testin.txt"); FileOutputStream fOut = new FileOutputStream(folder + "testout.txt")) {
            System.out.println("Reading testin.txt");
            var buffer = new byte[128];
            int readBytes;
            int totalLoops = 0;
            while ((readBytes = fIn.read(buffer)) != -1) {
                System.out.println("Current Loop: " + totalLoops);
                System.out.println(new String(buffer, 0, readBytes));
                fOut.write(buffer, 0, readBytes);
                totalLoops++;
            }
            System.out.println("Finished copying to testout.txt");
        } catch (IOException e) {
            System.out.println("Error: " + e.getMessage());
        }
    }
}

