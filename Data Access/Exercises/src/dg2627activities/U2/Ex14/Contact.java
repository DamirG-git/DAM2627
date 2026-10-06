package dg2627activities.U2.Ex14;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.regex.Pattern;

public class Contact implements Serializable {
    private String name;
    private String surname;
    private ArrayList<Emails> emails;
    private ArrayList<Phones> phones;

    public record Emails(String typeOfMail, String email) {
    }

    public record Phones(String typeOfPhone, String phone) {
    }

    public ArrayList<Phones> getPhone() {
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
        phones.add(new Phones(typeOfEmail.trim(), trimmed));
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
        return !Pattern.matches("^[A-Za-z ]+$", name);
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
        return !Pattern.matches("^[A-Za-z ]+$", surname);
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
}
