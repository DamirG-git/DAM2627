package dg2627activities.U2.Ex15;

import org.w3c.dom.Document;
import org.w3c.dom.Element;
import org.w3c.dom.Node;
import org.xml.sax.SAXException;

import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.parsers.ParserConfigurationException;
import java.io.IOException;

// 8.3 Activities 1
public class U2Ex8n4n1 {


    /*
    * DocumentTraversal td = (DocumentTraversal) doc;
        NodeIterator iterator = td.createNodeIterator(doc.getDocumentElement(), NodeFilter.SHOW_TEXT, null, true);
        for (var n = iterator.nextNode(); n != null; n = iterator.nextNode()) {
            String text = n.getTextContent().trim();
            if (!text.isEmpty()) System.out.println(text);
        }*/

    final String folder = "Exercises/src/dg2627activities/U2/Ex15/";
    final String xmlFileLoc = folder + "contacts.xml";

    public U2Ex8n4n1() {

        Document doc;
        DocumentBuilderFactory dbf = DocumentBuilderFactory.newInstance();
        try {
            DocumentBuilder db = dbf.newDocumentBuilder();
            try {
                doc = db.parse(xmlFileLoc);
            } catch (SAXException | IOException e) {
                System.out.println(e.getMessage());
                return;
            }
        } catch (ParserConfigurationException e) {
            System.out.println(e.getMessage());
            return;
        }
        var contacts = doc.getElementsByTagName("contact");
        if (contacts.getLength() == 0) {
            System.out.println("No contact found");
            return;
        }
        for (int i = 0; i < contacts.getLength(); i++) {
            var contact = (Element) contacts.item(i);
            var name = getElementText(contact, "name");
            var surname = getElementText(contact, "surname");
            var phones = (Element) getElementNode(contact, "phones");
            var phone = getPhoneElement(phones);
            System.out.println(name + " " + surname + " " + phone);
        }


    }


    private Node getElementNode(Element elem, String searchedTag) {
        var node = elem.getElementsByTagName(searchedTag);
        if (node.getLength() > 0) {
            return node.item(0);
        }
        return null;
    }

    private String getElementText(Element elem, String searchedTag) {
        var result = getElementNode(elem, searchedTag);
        if (result == null) return "";
        return result.getTextContent();
    }

    private String getPhoneElement(Element elem) {
        if (elem == null) return "";
        var cell = elem.getElementsByTagName("cell");
        if (cell.getLength() > 0) {
            return "Cell phone: " + cell.item(0).getTextContent();
        }
        var work = elem.getElementsByTagName("work");
        if (work.getLength() > 0) {
            return "Work phone: " + work.item(0).getTextContent();
        }
        var home = elem.getElementsByTagName("home");
        if (home.getLength() > 0) {
            return "Home phone: " + home.item(0).getTextContent();
        }
        return "";
    }
}
