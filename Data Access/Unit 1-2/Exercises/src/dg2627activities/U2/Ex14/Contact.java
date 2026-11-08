package dg2627activities.U2.Ex14;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.regex.Pattern;

public class Contact implements Serializable {
    private String name;
    private String surname;
    private final ArrayList<Emails> emails;
    private final ArrayList<Phones> phones;

    public Contact() {
        emails = new ArrayList<>();
        phones = new ArrayList<>();
    }

    public record Emails(String typeOfMail, String email) implements Serializable {
    }

    public record Phones(String typeOfPhone, String phone) implements Serializable {
    }

    public ArrayList<Phones> getPhones() {
        return phones;
    }

    public boolean setPhone(String typeOfPhone, String phone) {
        var trimmed = phone.trim();
        if (isPhoneInvalid(trimmed)) {
            System.out.println("Invalid phone number: " + phone + " type: " + typeOfPhone);
            return false;
        }
        phones.add(new Phones(typeOfPhone.trim(), trimmed));
        return true;

    }

    public boolean isPhoneInvalid(String phone) {
        return !Pattern.matches("^\\+?[0-9]{9,15}$", phone);
    }

    public ArrayList<Emails> getEmails() {
        return emails;
    }

    public boolean setEmail(String typeOfEmail, String email) {
        var trimmed = email.trim();
        if (isInvalidEmail(trimmed)) {
            System.out.println("Invalid email: " + trimmed);
            return false;
        }
        emails.add(new Emails(typeOfEmail.trim(), trimmed));
        return true;
    }

    public boolean isInvalidEmail(String email) {
        return !Pattern.matches("^[A-Za-z0-9._%+-]+@[A-Za-z0-9.-]+\\.[A-Za-z]{2,}$", email);
    }

    public String getName() {
        return name;
    }

    public boolean setName(String name) {
        var trimmed = name.trim();
        if (isInvalidName(trimmed)) {
            System.out.println("Invalid name: " + trimmed);
            return false;
        }
        this.name = trimmed;
        return true;
    }

    public boolean isInvalidName(String name) {
        return !Pattern.matches("^[\\p{L} ]+$", name);
    }


    public String getSurname() {
        return surname;
    }

    public boolean setSurname(String surname) {
        var trimmed = surname.trim();
        if (isInvalidSurname(trimmed)) {
            System.out.println("Invalid name: " + trimmed);
            return false;
        }
        this.surname = trimmed;
        return true;
    }

    public boolean isInvalidSurname(String surname) {
        return !Pattern.matches("^[\\p{L} ]+$", surname);
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (!(o instanceof Contact contact)) return false;

        return java.util.Objects.equals(name, contact.name) && java.util.Objects.equals(surname, contact.surname) && java.util.Objects.equals(emails, contact.emails) && java.util.Objects.equals(phones, contact.phones);
    }

    @Override
    public int hashCode() {
        return java.util.Objects.hash(name, surname, emails, phones);
    }

    @Override
    public String toString() {
        return "Contact: " + name + " " + surname + ", email: " + emails.toString() + ", phone: " + phones.toString();
    }

    public String toXML(String id) {
        var builder = new StringBuilder();
        builder.append(generateSpacing(4)).append("<contact id=").append("'").append(id).append("'").append(">\n");
        if (!name.isEmpty()) builder.append(generateSpacing(8)).append("<name>").append(name).append("</name>\n");
        if (!surname.isEmpty()) builder.append(generateSpacing(8)).append("<surname>").append(surname).append("</surname>\n");
        if (!emails.isEmpty()) {
            builder.append(generateSpacing(8)).append("<emails>\n");
            for (Emails email : emails) {
                builder.append(generateSpacing(12)).append(xmlStartFormat(email.typeOfMail)).append(email.email).append(xmlEndFormat(email.typeOfMail));
            }
            builder.append(generateSpacing(8)).append("</emails>\n");
        }
        if (!phones.isEmpty()) {
            builder.append(generateSpacing(8)).append("<phones>\n");
            for (Phones phone : phones) {
                builder.append(generateSpacing(12)).append(xmlStartFormat(phone.typeOfPhone)).append(phone.phone).append(xmlEndFormat(phone.typeOfPhone));
            }
            builder.append(generateSpacing(8)).append("</phones>\n");
        }
        builder.append(generateSpacing(4)).append("</contact>\n");
        return builder.toString();
    }

    private String xmlStartFormat(String input) {
        return "<" + input + ">";
    }

    private String xmlEndFormat(String input) {
        return "</" + input + ">\n";
    }

    private String generateSpacing(int amount){
        return " ".repeat(Math.max(0, amount));
    }

}
