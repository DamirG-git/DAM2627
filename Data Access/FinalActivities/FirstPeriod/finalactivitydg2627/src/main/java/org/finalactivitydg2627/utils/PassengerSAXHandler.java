package org.finalactivitydg2627.utils;

import org.finalactivitydg2627.dto.PassengerRawDTO;
import org.xml.sax.Attributes;
import org.xml.sax.helpers.DefaultHandler;

import java.util.ArrayList;
import java.util.List;

public class PassengerSAXHandler extends DefaultHandler {

    private final StringBuilder contextBuilder = new StringBuilder();

    private final List<PassengerRawDTO> passengers = new ArrayList<>();
    private PassengerRawDTO currentPassenger;

    public List<PassengerRawDTO> getPassengers() {
        return passengers;
    }

    @Override
    public void startElement(String uri, String localName, String qName, Attributes attributes) {
        contextBuilder.setLength(0);
        if (qName.equals("passenger")) {
            currentPassenger = new PassengerRawDTO();
        }
    }

    @Override
    public void endElement(String uri, String localName, String qName) {
        var text = contextBuilder.toString().trim();
        switch (qName) {
            case "passenger" -> {
                passengers.add(currentPassenger);
                currentPassenger = null;
            }
            case "passportno" -> currentPassenger.setPassno(text);
            case "firstname" -> currentPassenger.setFirstName(text);
            case "lastname" -> currentPassenger.setLastName(text);
            case "phone" -> currentPassenger.setPhone(text);
            case "address" -> currentPassenger.setAddress(text);
            default -> {
                // No other valid tags.
                }
        }
    }

    @Override
    public void characters(char[] ch, int start, int length) {
        contextBuilder.append(new String(ch, start, length));
    }
}
