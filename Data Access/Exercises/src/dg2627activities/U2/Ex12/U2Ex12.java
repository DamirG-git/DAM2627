package dg2627activities.U2.Ex12;

import java.io.*;
import java.util.ArrayList;
import java.util.Scanner;

// 7.1 Activities 1

public class U2Ex12 {


    // Test data.
    private final ArrayList<Contact> debugContacts = new ArrayList<>() {{
        Contact c1 = new Contact();
        c1.setName("John");
        c1.setSurname("Smith");
        c1.setEmail("john.smith@gmail.com");
        c1.setPhone("+34612345678");
        c1.setDescription("Friend from university");
        add(c1);

        Contact c2 = new Contact();
        c2.setName("Maria");
        c2.setSurname("Garcia");
        c2.setEmail("maria.garcia@example.es");
        c2.setPhone("612345678");
        c2.setDescription("Work colleague");
        add(c2);

        Contact c3 = new Contact();
        c3.setName("Peter");
        c3.setSurname("Johnson");
        c3.setEmail("peter.johnson@company.com");
        c3.setPhone("+441234567890");
        c3.setDescription("Business contact");
        add(c3);

        Contact c4 = new Contact();
        c4.setName("Anna");
        c4.setSurname("Lopez");
        c4.setEmail("anna.lopez@hotmail.com");
        c4.setPhone("722345678");
        c4.setDescription("Neighbour");
        add(c4);

        Contact c5 = new Contact();
        c5.setName("Robert");
        c5.setSurname("Brown");
        c5.setEmail("robert.brown@example.co.uk");
        c5.setPhone("+442012345678");
        c5.setDescription("Former colleague");
        add(c5);

        Contact c6 = new Contact();
        c6.setName("Laura");
        c6.setSurname("Martin");
        c6.setEmail("laura.martin@outlook.com");
        c6.setPhone("912345678");
        c6.setDescription("Family friend");
        add(c6);
    }};
    final String folder = "Exercises/src/dg2627activities/U2/Ex12/contacts.obj";


    public U2Ex12(Scanner sc) {
        var fileObj = new File(folder);
        if (!fileObj.exists()) {
            try {
                if (!fileObj.createNewFile()) {
                    System.out.println("File creation failed.");
                    return;
                }
                try (var objOut = new ObjectOutputStream(new FileOutputStream(folder))) {
                    objOut.writeInt(0);
                }
            } catch (IOException e) {
                System.err.println(e.getMessage());
                return;
            }
        }
        if (!FillWithDebugData(sc, fileObj)) return; // Purely for debug.

        // Logic loop
        while (true) {
            switch (userOptionLogic(sc)) {
                case EXIT:
                    System.out.println("Exiting...");
                    return;
                case ADD:
                    var newContact = addContact(sc);
                    if (writeContactToFile(newContact, fileObj)) System.out.println("New contact added successfully.");
                    break;
                case SEARCH:
                    var foundContact = findContact_UserInput(sc, fileObj);
                    if (foundContact == null) {
                        System.out.println("No such contact found.");
                        return;
                    }
                    System.out.println("Found contact: " + foundContact.toString());
                    break;
                case DISPLAY:
                    var contactList = getAllContactsFromFile(fileObj);
                    if (contactList == null) {
                        System.out.println("No contacts found.");
                        return;
                    }
                    for (Contact contact : contactList) {
                        System.out.println(contact.toString());
                    }
                    break;
                case DELETE:
                    var response = deleteContact(sc, fileObj);
                    switch (response) {
                        case FAILURE_NO_CONTACT_LIST -> System.out.println("Deletion failed, no contact list found.");
                        case FAILURE_NO_CONTACT_FOUND -> System.out.println("Deletion failed, contact not found.");
                        case ERROR -> System.out.println("Deletion failed, error.");
                        case SUCCESS -> System.out.println("Deletion successful.");
                    }
                    break;
            }
        }

    }


    private enum userInputCommandOption {
        ADD, REMOVE, SEARCH, DISPLAY, DELETE, EXIT

    }

    private userInputCommandOption userOptionLogic(Scanner sc) {
        System.out.print("""
                
                To add new contact: -ADD / -A
                To delete contact: -DELETE / -DEL
                To display all contacts: -DISPLAY / -D
                To search for a contact: -SEARCH / -S
                To exit the application: -EXIT / -E
                
                """);
        //               WIP. Not sure how to do it cleanly.

        while (true) {
            var option = sc.nextLine().trim().toUpperCase();

            switch (option.toUpperCase()) {
                case "-ADD":
                case "-A":
                    return userInputCommandOption.ADD;

                case "-DISPLAY":
                case "-D":
                    return userInputCommandOption.DISPLAY;

                case "-SEARCH":
                case "-S":
                    return userInputCommandOption.SEARCH;

                case "-DELETE":
                case "-DEL":
                    return userInputCommandOption.DELETE;

                case "-EXIT":
                case "-E":
                    return userInputCommandOption.EXIT;

                default:
                    System.out.println("Invalid option. Try again.");
            }
        }
    }


    private Contact addContact(Scanner sc) {
        var contact = new Contact();

        while (true) {
            System.out.print("Name: ");
            var name = sc.nextLine().trim();
            if (name.isEmpty()) break;
            if (name.equalsIgnoreCase("-exit")) continue;
            if (contact.setName(name)) break;
        }
        while (true) {
            System.out.print("Surname: ");
            var surname = sc.nextLine().trim();
            if (surname.isEmpty()) break;
            if (surname.equalsIgnoreCase("-exit")) continue;
            if (contact.setSurname(surname)) break;
        }
        while (true) {
            System.out.print("Email: ");
            var email = sc.nextLine().trim();
            if (email.isEmpty()) break;
            if (email.equalsIgnoreCase("-exit")) continue;
            if (contact.setEmail(email)) break;
        }

        while (true) {
            System.out.print("Phone number: ");
            var phone = sc.nextLine().trim();
            if (phone.isEmpty()) break;
            if (phone.equalsIgnoreCase("-exit")) continue;
            if (contact.setPhone(phone)) break;
        }

        System.out.print("Description: ");
        String description = sc.nextLine();
        contact.setDescription(description);

        return contact;
    }

    private enum DeletionResponse {
        SUCCESS, FAILURE_NO_CONTACT_LIST, FAILURE_NO_CONTACT_FOUND, ERROR
    }

    private DeletionResponse deleteContact(Scanner sc, File folder) {
        var contactToDelete = findContact_UserInput(sc, folder);

        if (contactToDelete == null) return DeletionResponse.FAILURE_NO_CONTACT_FOUND;

        var contactsList = getAllContactsFromFile(folder);
        if (contactsList == null) return DeletionResponse.FAILURE_NO_CONTACT_LIST;

        try (var objOut = new ObjectOutputStream(new FileOutputStream(folder))) {
            contactsList.remove(contactToDelete);
            objOut.writeInt(contactsList.size());
            for (Contact contact : contactsList) {
                objOut.writeObject(contact);
            }
            System.out.println(contactToDelete + " was deleted.");
        } catch (IOException e) {
            System.err.println(e.getMessage());
            return DeletionResponse.ERROR;
        }
        return DeletionResponse.SUCCESS;

    }

    private enum userInputSearchCondition {
        PHONE, NAME
    }

    private Contact findContact_UserInput(Scanner sc, File file) {

        var tempContactObj = new Contact();
        userInputSearchCondition userInputSearchCondition;

        // Deals with user input and validation
        while (true) {
            System.out.println("Please input full name or phone number to find the contact information: ");
            var nameOrPhone = sc.nextLine();
            if (nameOrPhone.isEmpty()) {
                System.out.println("No full name or phone number was given.");
                continue;
            }
            var trimmed = nameOrPhone.trim();
            var split = trimmed.split(" ");

            switch (split.length) {
                case 1:
                    var phone = split[0].trim();
                    if (tempContactObj.isPhoneInvalid(phone)) {
                        System.out.println("Phone number was given, but it's invalid: " + trimmed);
                        continue;
                    }
                    System.out.println("Searching for contact with phone number " + phone);
                    userInputSearchCondition = U2Ex12.userInputSearchCondition.PHONE;
                    tempContactObj.setPhone(phone);
                    break;

                case 2:
                    var name = split[0].trim();
                    var surname = split[1].trim();
                    if (tempContactObj.isInvalidName(name) || tempContactObj.isInvalidSurname(surname)) {
                        System.out.println("The full name given is invalid: " + trimmed);
                        continue;
                    }

                    System.out.println("Searching for contact with name: " + name + " and surname: " + surname);

                    userInputSearchCondition = U2Ex12.userInputSearchCondition.NAME;
                    tempContactObj.setName(name);
                    tempContactObj.setSurname(surname);
                    break;

                default:
                    System.out.println("Invalid amount of arguments was given: " + split.length + " out of max 2.");
                    continue;
            }
            break;
        }
        // Searches the file based on input.
        return findContact(tempContactObj, file, userInputSearchCondition);
    }

    private Contact findContact(Contact tempContactObj, File file, userInputSearchCondition condition) {
        try (var objIn = new ObjectInputStream(new FileInputStream(file))) {
            var objectsCount = objIn.readInt();
            if (objectsCount == 0) {
                System.out.println("No contact list found.");
                return null;
            }
            switch (condition) {

                case NAME:
                    var name = tempContactObj.getName();
                    var surname = tempContactObj.getSurname();
                    for (var i = 0; i < objectsCount; i++) {
                        var obj = objIn.readObject();
                        if (obj instanceof Contact contactIn && contactIn.getName().equalsIgnoreCase(name) && contactIn.getSurname().equalsIgnoreCase(surname)) {
                            return contactIn;
                        }
                    }
                    break;

                case PHONE:
                    var phone = tempContactObj.getPhone();
                    for (var i = 0; i < objectsCount; i++) {
                        var obj = objIn.readObject();
                        if (obj instanceof Contact contactIn && contactIn.getPhone().equals(phone)) {
                            return contactIn;
                        }
                    }
                    break;
            }

        } catch (IOException e) {
            System.err.println("IO error: " + e.getMessage());
        } catch (ClassNotFoundException e) {
            System.err.println("Class not found: " + e.getMessage());
        }
        return null;
    }


    // True is valid response, false is error response.
    private boolean FillWithDebugData(Scanner sc, File fileObj) {
        System.out.print("Debug option: Override the file with pre-made data. Y/N: ");
        while (true) {
            var response = sc.nextLine();
            switch (response.toLowerCase()) {
                case "-exit":
                    return false;
                case "y":
                case "yes":
                    try (var objOut = new ObjectOutputStream(new FileOutputStream(fileObj))) {
                        objOut.writeInt(debugContacts.size());
                        for (var obj : debugContacts) {
                            objOut.writeObject(obj);
                        }
                        return true;
                    } catch (IOException e) {
                        System.out.println(e.getMessage());
                        return false;
                    }
                case "n":
                case "no":
                    return true;
                default:
                    System.out.println("Invalid input. Try again.");
                    break;
            }
        }
    }


    private boolean writeContactToFile(Contact c, File file) {

        var tempCollection = new ArrayList<Contact>();
        try (var objIn = new ObjectInputStream(new FileInputStream(file))) {
            var count = objIn.readInt();
            for (var i = 0; i < count; i++) {
                tempCollection.add((Contact) objIn.readObject());
            }

        } catch (IOException | ClassNotFoundException e) {
            System.err.println(e.getMessage());
            return false;
        }
        System.out.println("Adding new contact: " + c);
        tempCollection.add(c);

        try (var objOut = new ObjectOutputStream(new FileOutputStream(file))) {

            objOut.writeInt(tempCollection.size());

            for (var obj : tempCollection) {
                objOut.writeObject(obj);
            }

        } catch (IOException e) {
            System.err.println(e.getMessage());
            return false;
        }

        return true;
    }

    private ArrayList<Contact> getAllContactsFromFile(File file) {
        ArrayList<Contact> contacts;
        try (var objIn = new ObjectInputStream(new FileInputStream(file))) {
            var numObj = objIn.readInt();
            if (numObj == 0) {
                return null;
            }
            contacts = new ArrayList<>();
            for (int i = 0; i < numObj; i++) {
                contacts.add((Contact) objIn.readObject());
            }
        } catch (IOException e) {
            System.err.println(e.getMessage());
            return null;
        } catch (ClassNotFoundException e) {
            System.err.println("Amount of supposed objects is not correct. Corrupted or wrong file.");
            System.err.println(e.getMessage());
            return null;
        }
        return contacts;
    }

}
