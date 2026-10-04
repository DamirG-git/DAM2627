package dg2627activities.U2;

import java.nio.file.Files;
import java.nio.file.Path;

public class Utility {

    public static class PathResult {
        Path path;
        String error = "";

        public void setPath(String fileIn) {
            error = "";
            if (fileIn.isBlank()) {
                error = "Error: The file path is empty.";
            }
            if (fileIn.startsWith("\"") && fileIn.endsWith("\"")){
                fileIn = fileIn.substring(1, fileIn.length() - 1);
            }
            var pathRaw = Path.of(fileIn);
            if (!Files.exists(pathRaw)) {
                error = "No file with this path found: " + fileIn;
                return;
            }
            if (!Files.isReadable(pathRaw)) {
                error = "The file is not readable: " + fileIn;
            }
            this.path = pathRaw;
        }

        public Path getPath() {
            return path;
        }

        public String getError() {
            return error;
        }

    }

}
