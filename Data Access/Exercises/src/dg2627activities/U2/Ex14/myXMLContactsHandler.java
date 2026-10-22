package dg2627activities.U2.Ex14;

import org.xml.sax.Attributes;
import org.xml.sax.SAXException;
import org.xml.sax.helpers.DefaultHandler;

import java.util.ArrayList;
import java.util.List;

public class myXMLContactsHandler extends DefaultHandler {

    List<Contact> contacts = new ArrayList<>();
    List<MyLabelValue> values = new ArrayList<>();
    StringBuilder lineValue = new StringBuilder();

    private record MyLabelValue(String label, String value) {
    }


    @Override
    public void startElement(String uri, String localName, String qName, Attributes attributes) throws SAXException {
        if (qName.equals("contact")) {
            contacts.add(new Contact());
        }
        lineValue.setLength(0);
    }

    @Override
    public void endElement(String uri, String localName, String qName) throws SAXException {
        var value = lineValue.toString().trim();
        switch (qName) {
            case "name" -> contacts.getLast().setName(value);
            case "surname" -> contacts.getLast().setSurname(value);
            case "cell", "work", "home" -> values.add(new MyLabelValue(qName, value));
            case "phones" -> {
                var contactPhone = contacts.getLast();
                for (MyLabelValue v : values) {
                    contactPhone.setPhone(v.label(), v.value());
                }
                values.clear();
            }
            case "emails" -> {
                var contactEmail = contacts.getLast();
                for (MyLabelValue v : values) {
                    contactEmail.setEmail(v.label(), v.value());
                }
                values.clear();
            }
        }
        lineValue.setLength(0);
    }

    @Override
    public void characters(char[] ch, int start, int length) throws SAXException {
        var line = new String(ch, start, length);
        lineValue.append(line);
    }

    public List<Contact> getContacts() {
        return contacts;
    }
}
