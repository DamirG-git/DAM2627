package dg2627activities.U2.Ex16;

import dg2627activities.U2.Ex14.Contact;
import dg2627activities.U2.Ex14.U2Ex14;
import org.w3c.dom.Document;
import org.w3c.dom.Element;
import org.w3c.dom.Node;
import org.xml.sax.SAXException;

import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.parsers.ParserConfigurationException;
import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.ObjectOutputStream;
import java.util.ArrayList;
import java.util.Scanner;

// 8.3 Activities 2
public class U2Ex16 extends U2Ex14 {

    public U2Ex16(Scanner sc) {
        super(sc);
    }


    @Override
    protected void ToObject() {

        Document doc;
        try {
            var docParser = DocumentBuilderFactory.newInstance().newDocumentBuilder();
            doc = docParser.parse(xmlFileLoc);
        } catch (ParserConfigurationException | IOException | SAXException e) {
            throw new RuntimeException(e);
        }
        ArrayList<Contact> contacts = new ArrayList<>();

        var contactNodes = doc.getElementsByTagName("contact");
        for (var i = 0; i < contactNodes.getLength(); i++) {
            var contactElement = (Element) contactNodes.item(i);
            var contact = new Contact();
            contact.setName(getElementText(contactElement, "name"));
            contact.setSurname(getElementText(contactElement, "surname"));


            var phonesNode = getElementNode(contactElement, "phones");
            if (phonesNode != null) {
                var phoneChildren = phonesNode.getChildNodes();
                for (var j = 0; j < phoneChildren.getLength(); j++) {
                    if (phoneChildren.item(j).getNodeType() != Node.ELEMENT_NODE) continue;
                    var node = (Element) phoneChildren.item(j);

                    if (node.getNodeType() == Node.ELEMENT_NODE) {
                        var type = node.getNodeName();
                        var text = node.getTextContent().trim();
                        contact.setPhone(type, text);
                    }
                }

            }


            var emailsNode = getElementNode(contactElement, "emails");
            if (emailsNode != null) {
                var emailsChildren = emailsNode.getChildNodes();
                for (var j = 0; j < emailsChildren.getLength(); j++) {
                    if (emailsChildren.item(j).getNodeType() != Node.ELEMENT_NODE) continue;
                    var node = (Element) emailsChildren.item(j);

                    if (node.getNodeType() == Node.ELEMENT_NODE) {
                        var type = node.getNodeName();
                        var text = node.getTextContent().trim();
                        contact.setEmail(type, text);
                    }
                }

            }
            contacts.add(contact);
        }

        System.out.println("Parsed contacts: " + contacts);
        try (var objWriter = new ObjectOutputStream(new FileOutputStream(objFileLoc))) {
            objWriter.writeInt(contacts.size());
            for (var contact : contacts) {
                objWriter.writeObject(contact);
            }

        } catch (Exception e) {
            System.err.println(e.getMessage());
            return;
        }
        var file = new File(xmlFileLoc);
        if (file.exists()) if (file.delete()) System.out.println("Old file deleted successfully.");

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
        return result.getTextContent().trim();
    }

}
