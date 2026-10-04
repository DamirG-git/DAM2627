package dg2627activities.U2.Ex6;

import dg2627activities.U2.Utility;

import java.io.FileInputStream;
import java.nio.file.Path;
import java.util.Scanner;

public class U2Ex6 {

    public U2Ex6(Scanner sc) {

        var pathResult = new Utility.PathResult();
        while (true) {
            try {
                System.out.println("Give me a path to an image:");
                var raw = sc.nextLine();
                pathResult.setPath(raw);
                if (!pathResult.getError().isBlank()) {
                    System.out.println(pathResult.getError());
                    continue;
                }
                var path = pathResult.getPath();
                var imageExtension = fileExtensionDetection(path);
                if (imageExtension == null || imageExtension.isBlank()) {
                    System.out.println("The image extension was not detected.");
                    continue;
                }
                System.out.println("The image extension is: " + imageExtension);
                break;
            } catch (Exception e) {
                System.out.println("Error: " + e.getMessage());
            }
        }


    }

//BMP 42 4D
//GIF 47 49 46 38 39 61
//GIF 47 49 46 38 37 61
//.ICO 00 00 01 00
//.JPEG FF D8 FF
//.PNG 89 50 4E 47

    private final String[] imageFormatNames = new String[]{".BMP", ".GIF", ".GIF", ".ICO", ".JPEG", ".PNG"};
    private final byte[][] imageFormatSignatures = new byte[][]{{(byte) 0x42, (byte) 0x4D}, // BMP
            {(byte) 0x47, (byte) 0x49, (byte) 0x46, (byte) 0x38, (byte) 0x39, (byte) 0x61}, //GIF
            {(byte) 0x47, (byte) 0x49, (byte) 0x46, (byte) 0x38, (byte) 0x37, (byte) 0x61}, //GIF
            {(byte) 0x00, (byte) 0x00, (byte) 0x01, (byte) 0x00}, //ICO
            {(byte) 0xFF, (byte) 0xD8, (byte) 0xFF}, //JPEG
            {(byte) 0x89, (byte) 0x50, (byte) 0x4E, (byte) 0x47} //PNG
    };

    private String fileExtensionDetection(Path file) {
        try (var fIn = new FileInputStream(file.toFile())) {
            var buffer = new byte[6]; //6 is the longest given.
            var read = fIn.read(buffer);
            if (read == -1) return null;
            for (var i = 0; i < imageFormatSignatures.length; i++) {
                if (read < imageFormatSignatures[i].length) {
                    continue;
                }

                for (var j = 0; j < imageFormatSignatures[i].length; j++) {
                    if (imageFormatSignatures[i][j] != buffer[j]) {
                        break;
                    }

                    if (j == imageFormatSignatures[i].length - 1) {
                        return imageFormatNames[i];
                    }
                }
            }


        } catch (Exception e) {
            e.printStackTrace();
        }

        return "";
    }
}
