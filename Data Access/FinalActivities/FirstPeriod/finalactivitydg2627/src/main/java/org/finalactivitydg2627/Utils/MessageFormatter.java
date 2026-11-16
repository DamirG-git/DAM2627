package org.finalactivitydg2627.Utils;

public class MessageFormatter {


    private MessageFormatter() {
    }


    public static String multipleErrorsFormatter(Verification.ResultValue<?>... messages) {
        var builder = new StringBuilder();
        boolean first = true;
        for (var message : messages) {
            if (message == null || message.message() == null) continue;
            if (first) {
                builder.append("\n");
                first = false;
            }
            builder.append("    - ").append(message.message());
        }
        return builder.toString();
    }
}
