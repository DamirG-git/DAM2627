package dg2627activities.U2.Ex12;

import java.io.Serializable;
import java.util.regex.Pattern;

public class Contact implements Serializable {
    private String name;
    private String surname;
    private String email;
    private String phone;
    private String description;

    public Contact() {
        name = "";
        surname = "";
        email = "";
        phone = "";
        description = "";
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public String getPhone() {
        return phone;
    }

    public boolean setPhone(String phone) {
        var trimmed = phone.trim();
        if (isPhoneInvalid(trimmed)) {
            System.out.println("Invalid phone number: " + phone);
            return false;
        }
        this.phone = trimmed;
        return true;

    }

    public boolean isPhoneInvalid(String phone) {
        return !Pattern.matches("^\\+?[0-9]{9,15}$", phone);
    }

    public String getEmail() {
        return email;
    }

    public boolean setEmail(String email) {
        var trimmed = email.trim();
        if (isInvalidEmail(trimmed)) {
            System.out.println("Invalid email: " + trimmed);
            return false;
        }
        this.email = trimmed;
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

        return java.util.Objects.equals(name, contact.name) && java.util.Objects.equals(surname, contact.surname) && java.util.Objects.equals(email, contact.email) && java.util.Objects.equals(phone, contact.phone) && java.util.Objects.equals(description, contact.description);
    }

    @Override
    public int hashCode() {
        return java.util.Objects.hash(name, surname, email, phone, description);
    }

    @Override
    public String toString() {
        return "Contact: " + name + " " + surname + ", email: " + email + ", phone: " + phone + ", description: " + description;
    }
}
