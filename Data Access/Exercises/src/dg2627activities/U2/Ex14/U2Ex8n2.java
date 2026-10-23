package dg2627activities.U2.Ex14;

import javax.xml.parsers.SAXParserFactory;
import java.io.*;
import java.util.Scanner;

// 8.2 Activities 2
public class U2Ex8n2 {

    protected final String folder = "Exercises/src/dg2627activities/U2/Ex14/";
    protected final String xmlFileLoc = folder + "contacts.xml";
    protected final String objFileLoc = folder + "contacts.obj";

    public U2Ex8n2(Scanner sc) {

        System.out.println("XML to OBJ and inverse convertor.");
        var fileType = isFileXML(); // Check file type
        switch (fileType) {
            case XML -> System.out.println("Current file is XML, convert it to OBJ? Y/N");
            case OBJ -> System.out.println("Current file is OBJ, convert it to XML? Y/N");
            case INVALID -> {
                System.out.println("Current file does not exist or folder location is invalid.");
                return;
            }
        }

        while (true) {
            var in = sc.nextLine().trim().toUpperCase();
            switch (in) {
                case "Y", "YES" -> System.out.println("Converting...");
                case "NO", "N" -> {
                    System.out.println("Exiting...");
                    return;
                }
                default -> {
                    System.out.println("Invalid input. Please try again.");
                    continue;
                }
            }
            break;
        }
        switch (fileType) {
            case XML:
                ToObject();
                break;
            case OBJ:
                ToXML();
                break;
        }
        System.out.println("Converting complete...");
    }


    private enum fileType {
        XML, OBJ, INVALID
    }

    private fileType isFileXML() {
        var xmlFile = new File(xmlFileLoc);
        var objFile = new File(objFileLoc);

        if (xmlFile.isFile()) return fileType.XML;
        if (objFile.isFile()) return fileType.OBJ;

        return fileType.INVALID;
    }

    protected void ToObject() {
        var handler = new myXMLContactsHandler();
        try {
            var saxParser = SAXParserFactory.newInstance().newSAXParser();
            saxParser.parse(xmlFileLoc, handler);
        } catch (Exception e) {
            System.err.println(e.getMessage());
            return;
        }
        System.out.println("Parsed contacts: " + handler.getContacts());
        try (var objWriter = new ObjectOutputStream(new FileOutputStream(objFileLoc))) {
            var list = handler.getContacts();
            objWriter.writeInt(list.size());
            for (var contact : list) {
                objWriter.writeObject(contact);
            }

        } catch (Exception e) {
            System.err.println(e.getMessage());
            return;
        }
        var file = new File(xmlFileLoc);
        if (file.exists()) if (file.delete()) System.out.println("Old file deleted successfully.");

    }

    private void ToXML() {
        var xmlContact = new StringBuilder();
        try (var objReader = new ObjectInputStream(new FileInputStream(objFileLoc))) {
            var contactsCount = objReader.readInt();
            if (contactsCount == 0) {
                System.out.println("No contacts found.");
                return;
            }

            for (var i = 0; i < contactsCount; i++) {
                var contact = (Contact) objReader.readObject();
                var id = String.format("c%03d", i + 1);
                xmlContact.append(contact.toXML(id));
                System.out.println(xmlContact);
            }


        } catch (Exception e) {
            System.err.println(e.getMessage());
        }
        System.out.println("Parsed contacts: " + xmlContact);
        try (var bufWriter = new BufferedWriter(new OutputStreamWriter(new FileOutputStream(xmlFileLoc)))) {
            bufWriter.write("<contactList>\n");
            bufWriter.write(xmlContact.toString());
            bufWriter.write("</contactList>");

        } catch (Exception e) {
            System.err.println(e.getMessage());
            return;
        }
        var file = new File(objFileLoc);
        if (file.exists()) if (file.delete()) System.out.println("Old file deleted successfully.");
    }
}
