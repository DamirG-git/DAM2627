package dg2627activities.U2.Ex13;

import org.xml.sax.Attributes;
import org.xml.sax.SAXException;
import org.xml.sax.helpers.DefaultHandler;

import java.util.ArrayList;
import java.util.Comparator;

class myXMLContactsHandler extends DefaultHandler {
    protected String tagContent = "";

    private static class MyTuple {
        public int getPriority() {
            return priority;
        }

        public String getType() {
            return type;
        }

        public String getValue() {
            return value;
        }

        //cell, work, home.
        public void SetType(String type) {
            this.type = type;
            switch (type) {
                case "cell" -> priority = 0;
                case "work" -> priority = 1;
                case "home" -> priority = 2;
            }
        }

        public void SetValue(String value) {
            this.value = value;
        }

        private int priority;
        private String type;
        private String value;


        @Override
        public String toString() {
            return type + ": " + value;
        }
    }

    private final ArrayList<MyTuple> values = new ArrayList<>(); // Contains phones/mail values


    private String name;
    private String surname;

    // Tag opening found
    //
    public void startElement(String uri, String localName, String qName, Attributes attributes) throws SAXException {

        if (qName.equals("contact")) {
            name = "";
            surname = "";
            values.clear(); // Clear values from last contact.

            System.out.println("ID: " + attributes.getValue("id")); // Print ID on start of contact.
        }

        if (qName.equals("cell") || qName.equals("work") || qName.equals("home")) { // Create tuple to sort it laser.

            MyTuple tuple = new MyTuple();
            tuple.SetType(qName);
            values.add(tuple);
        }
    }

    // Tag content, usually CDATA
    //
    public void characters(char[] ch, int start, int length) {
        var text = new String(ch, start, length).trim(); // Make sure empty text is not valid
        if (text.isEmpty()) return;
        if (!values.isEmpty()) { // If values is not empty its reading phones.
            values.getLast().SetValue(text); // Not sorted, last is always the new.
            return;
        }
        tagContent = text;
    }

    // Tag ending
    //
    public void endElement(String uri, String localName, String qName) {
        // Saves values only on end tag.
        if (qName.equals("name")) {
            name = tagContent;
        }
        if (qName.equals("surname")) {
            surname = tagContent;
        }
        if (qName.equals("contact")) {
            values.sort(Comparator.comparingInt(MyTuple::getPriority));

            var phone = values.isEmpty() ? "" : values.getFirst().getValue(); //In-case phones did not exist.

            System.out.println(name + " " + surname + " — " + phone); // Output the phone based on priority.
        }
    }
}

