package org.finalactivitydg2627.model;

import org.finalactivitydg2627.Utils.Verification;

public class Passengers {
    private String address;//max100
    public String getAddress() {
        return address;
    }

    public String setAddress(String address) {
        var rs = Verification.basicStringVerification("address",address,null,100,null);
        if(rs.result()==null) return rs.message();
        this.address = address;
        return null;
    }

    private String passno; //not null max10
    public String getPassno() {
        return passno;
    }

    public String setPassno(String passno) {
        var rs = Verification.basicStringVerification("passport number",passno,null,10,null);
        if(rs.result()==null) return rs.message();
        this.passno = passno;
        return null;
    }

    private String firstName; //not null max20
    public String getFirstName() {
        return firstName;
    }

    public String setFirstName(String firstName) {
        var rs = Verification.basicStringVerification("first name",firstName,null,20,null);
        if(rs.result()==null) return rs.message();
        this.firstName = firstName;
        return null;
    }


    private String lastName; //not null max20
    public String getLastName() {
        return lastName;
    }

    public String setLastName(String lastName) {
        var rs = Verification.basicStringVerification("last name",lastName,null,20,null);
        if(rs.result()==null) return rs.message();
        this.lastName = lastName;
        return null;
    }

    private String phone; //max12
    public String getPhone() {
        return phone;
    }

    public String setPhone(String phone) {
        var rs = Verification.basicStringVerification("phone",phone,null,20,null);
        if(rs.result()==null) return rs.message();
        this.phone = phone;
        return null;
    }
}
