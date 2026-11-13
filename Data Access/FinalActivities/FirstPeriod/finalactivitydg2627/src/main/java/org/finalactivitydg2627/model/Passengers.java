package org.finalactivitydg2627.model;

import org.finalactivitydg2627.Utils.Verification;

import java.util.regex.Pattern;

public class Passengers {
    private String address;//max100
    public String getAddress() {
        return address;
    }

    public Verification.ResultValue<String> setAddress(String address) {
        var rs = Verification.basicStringVerification("address",address,null,100,null);
        this.address = rs.result();
        return rs;
    }

    private String passno; //not null max10
    public String getPassno() {
        return passno;
    }

    public Verification.ResultValue<String> setPassno(String passno) {
        var rs = Verification.basicStringVerification("passport number",passno,null,10, Pattern.compile("^[A-Za-z\\d]{6,10}$"));
        this.passno = rs.result();
        return rs;
    }

    private String firstName; //not null max20
    public String getFirstName() {
        return firstName;
    }

    public Verification.ResultValue<String> setFirstName(String firstName) {
        var rs = Verification.basicStringVerification("first name",firstName,null,20, null);
        this.firstName = rs.result();
        return rs;
    }


    private String lastName; //not null max20
    public String getLastName() {
        return lastName;
    }

    public Verification.ResultValue<String> setLastName(String lastName) {
        var rs = Verification.basicStringVerification("last name",lastName,null,20,null);
        this.lastName = rs.result();
        return rs;
    }

    private String phone; //max12
    public String getPhone() {
        return phone;
    }

    public Verification.ResultValue<String> setPhone(String phone) {
        var rs = Verification.basicStringVerification("phone",phone,null,20,Pattern.compile("^\\+?\\d{7,15}$"));
        this.phone = rs.result();
        return rs;
    }
}
