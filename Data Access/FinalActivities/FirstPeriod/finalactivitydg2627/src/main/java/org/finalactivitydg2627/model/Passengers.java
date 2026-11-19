package org.finalactivitydg2627.model;

import org.finalactivitydg2627.utils.ApplicationValidator;

import java.util.regex.Pattern;

public class Passengers {

    public Passengers() {
    }


    private String address;//max100

    public Passengers(String passno, String name, String lastName, String address, String phone) {
        this.address = address;
        this.firstName = name;
        this.lastName = lastName;
        this.passno = passno;
        this.phone = phone;
    }

    public String getAddress() {
        return address;
    }

    public ApplicationValidator.ResultValue<String> setAddress(String address) {
        var rs = ApplicationValidator.basicStringVerification("address", address, null, 100, null);
        this.address = rs.result();
        return rs;
    }

    private String passno; //not null max10

    public String getPassno() {
        return passno;
    }

    public ApplicationValidator.ResultValue<String> setPassno(String passno) {
        var rs = ApplicationValidator.basicStringVerification("passport number", passno, null, 10, Pattern.compile("^[A-Za-z\\d]{6,10}$"));
        this.passno = rs.result();
        return rs;
    }

    private String firstName; //not null max20

    public String getFirstName() {
        return firstName;
    }

    public ApplicationValidator.ResultValue<String> setFirstName(String firstName) {
        var rs = ApplicationValidator.basicStringVerification("first name", firstName, null, 20, null);
        this.firstName = rs.result();
        return rs;
    }


    private String lastName; //not null max20

    public String getLastName() {
        return lastName;
    }

    public ApplicationValidator.ResultValue<String> setLastName(String lastName) {
        var rs = ApplicationValidator.basicStringVerification("last name", lastName, null, 20, null);
        this.lastName = rs.result();
        return rs;
    }

    private String phone; //max12

    public String getPhone() {
        return phone;
    }

    public ApplicationValidator.ResultValue<String> setPhone(String phone) {
        var rs = ApplicationValidator.basicStringVerification("phone", phone, null, 20, Pattern.compile("^\\+?\\d{7,15}$"));
        this.phone = rs.result();
        return rs;
    }


    @Override
    public String toString() {
        var result = firstName + " " + lastName + " " + passno;
        result = address == null ? result : result + ", " + address;
        result = phone == null ? result : result + ", " + phone;
        return result;
    }
}
