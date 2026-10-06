package dg2627activities.U2.EJ7Test;

import dg2627activities.U2.Ex7.U2Ex7;
import org.junit.Test;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;

import static org.junit.Assert.*;

// 4.4. Activities 4
public class U2Ex7Test {

    @Test
    public void byteToShortTest_SUCCESS() {
        var ex7 = new U2Ex7();

        byte[] data = {(byte) 0x18, (byte) 0x00};

        assertEquals(24, ex7.byteToShort(data, 0));
    }

    @Test
    public void byteToIntTest_SUCCESS() {
        var ex7 = new U2Ex7();

        byte[] data = {
                0x18,
                0x00,
                0x00,
                0x00
        };

        assertEquals(24, ex7.byteToInt(data, 0));
    }

    @Test
    public void headerSizeReader_SUCCESS() {
        var ex7 = new U2Ex7();
        var path = Path.of(
                "Exercises",
                "src",
                "dg2627activities",
                "U2",
                "Ex7",
                "test.bmp"
        );

        var result = ex7.headerSizeReader(path);
        assertNotNull(result);

        assertEquals(31626, result.fileSizeUnreliable());
        assertEquals(640, result.width());
        assertEquals(426, result.height());
        assertEquals(24, result.bitsPerPixel());

    }

    @Test
    public void headerSizeReader_PATH_FAIL() {
        var ex7 = new U2Ex7();

        var path = Path.of("path that does not exist");
        var result = ex7.headerSizeReader(path);

        assertNull(result);
    }

    @Test
    public void headerSizeReader_fileTooSmall_FAIL() throws IOException {

        var ex7 = new U2Ex7();

        Path path = Files.createTempFile("test", ".bmp");
        Files.write(path, new byte[20]);
        var result = ex7.headerSizeReader(path);

        assertNull(result);
        Files.deleteIfExists(path);
    }

}
