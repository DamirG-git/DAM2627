package org.finalactivitydg2627.Utils;

public class MessageFormatter {


    private MessageFormatter() {
    }


    public static String multipleErrorsFormatter(Verification.ResultValue<?>... messages){
        var builder = new StringBuilder();
        for(var message : messages){
            if(message == null) continue;
            if(message.message() == null) continue;
            builder.append("    - ").append(message.message());
        }
        return builder.toString();
    }
}
