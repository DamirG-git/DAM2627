package dg2627activities.U2.Ex7;

import dg2627activities.U2.Utility;

import java.io.FileInputStream;
import java.nio.file.Path;
import java.util.Scanner;


// 4.4. Activities 3
public class U2Ex7 {

    public static final String ERROR_HEADER_VALUE_NULL = "Header result was null, the provided file had an error.";
    public static final String ERROR_READ_BYTES_INSUFFICIENT = "Error: amount of bytes read is too small: %d bytes read out of 54";

    public U2Ex7(){}

    public U2Ex7(Scanner sc) {
        var pathResult = new Utility.PathResult();
        while (true) {
            try {
                System.out.println("Give me a path to a header of BMP file:");
                var raw = sc.nextLine();
                pathResult.setPath(raw);
                if (!pathResult.getError().isBlank()) {
                    System.out.println(pathResult.getError());
                    continue;
                }
                var path = pathResult.getPath();
                if(readBmpFileHeader(path)) break;

            } catch (Exception e) {
                System.out.println("Error: " + e.getMessage());
            }
        }
    }

    public boolean readBmpFileHeader(Path path) {

        var bmpFileHeader = headerSizeReader(path);
        if (bmpFileHeader == null) {
            System.out.println(ERROR_HEADER_VALUE_NULL);
            return false;
        }
        System.out.printf("Header size is %d bytes, width=%d, height=%d, number of bits per pixel %d", bmpFileHeader.fileSizeUnreliable(), bmpFileHeader.width(), bmpFileHeader.height(), bmpFileHeader.bitsPerPixel());
        return true;
    }

    // BMP file header / structure
    //
    // Offset | Size | Description
    // -------|------|------------------------------------------------------------
    // 0      | 2    | signature, must be 4D42 hex
    // 2      | 4    | size of BMP file in bytes (unreliable)
    // 6      | 2    | reserved, must be zero
    // 8      | 2    | reserved, must be zero
    // 10     | 4    | offset to start of image data in bytes
    // 14     | 4    | size of BITMAPINFOHEADER structure, must be 40
    // 18     | 4    | image width in pixels
    // 22     | 4    | image height in pixels
    // 26     | 2    | number of planes in the image, must be 1
    // 28     | 2    | number of bits per pixel (1, 4, 8, or 24)
    // 30     | 4    | compression type (0=none, 1=RLE-8, 2=RLE-4)
    // 34     | 4    | size of image data in bytes (including padding)
    // 38     | 4    | horizontal resolution in pixels per meter (unreliable)
    // 42     | 4    | vertical resolution in pixels per meter (unreliable)
    // 46     | 4    | number of colors in image, or zero
    // 50     | 4    | number of important colors, or zero

    public record BMPFileData(int fileSizeUnreliable, int width, int height, int bitsPerPixel) {
    }

    public BMPFileData headerSizeReader(Path path) {
        try (var inFile = new FileInputStream(path.toFile());) {
            var buffer = new byte[54];
            var readBytes = inFile.read(buffer);
            if (readBytes < 54) {
                System.out.printf(ERROR_READ_BYTES_INSUFFICIENT,readBytes);
                return null;
            }
            var fileSizeUnreliable = byteToInt(buffer, 2);
            var width = byteToInt(buffer, 18);
            var height = byteToInt(buffer, 22);
            var bitsPerPixel = byteToShort(buffer, 28);

            return new BMPFileData(fileSizeUnreliable, width, height, bitsPerPixel);
        } catch (Exception e) {
            System.out.println("Error: " + e.getMessage());
        }

        return null;
    }


    public int byteToInt(byte[] buffer, int start) {
        return (buffer[start] & 0xFF | (buffer[start + 1] & 0xFF) << 8) | (buffer[start + 2] & 0xFF << 16) | (buffer[start + 3] & 0xFF << 24);
    }

    public int byteToShort(byte[] buffer, int start) {
        return (buffer[start] & 0xFF | (buffer[start + 1] & 0xFF << 8));
    }

}
