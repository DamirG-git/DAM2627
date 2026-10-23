package dg2627activities.U2.Ex13;

import javax.xml.parsers.SAXParser;
import javax.xml.parsers.SAXParserFactory;

// 8.2 Activities 1

public class U2Ex8n1 {

    final String folder = "Exercises/src/dg2627activities/U2/Ex13/contacts.xml";
    public U2Ex8n1() {
        try {
            SAXParser saxParser = SAXParserFactory.newInstance().newSAXParser();
            saxParser.parse(folder, new myXMLContactsHandler());
        } catch (Exception e) {
            System.err.println(e.getMessage());
        }
    }

}
