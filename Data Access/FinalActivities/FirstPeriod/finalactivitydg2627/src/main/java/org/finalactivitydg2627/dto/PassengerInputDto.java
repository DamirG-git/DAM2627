package org.finalactivitydg2627.dto;

public class PassengerInputDto {
    public String getPassno() {
        return passno;
    }

    public void setPassno(String passno) {
        this.passno = passno;
    }

    public String getFirstName() {
        return firstName;
    }

    public void setFirstName(String firstName) {
        this.firstName = firstName;
    }

    public String getLastName() {
        return lastName;
    }

    public void setLastName(String lastName) {
        this.lastName = lastName;
    }

    public String getAddress() {
        return address;
    }

    public void setAddress(String address) {
        this.address = address;
    }

    public String getPhone() {
        return phone;
    }

    public void setPhone(String phone) {
        this.phone = phone;
    }

    private String passno;
    private String firstName;
    private String lastName;
    private String address;
    private String phone;


    @Override
    public String toString() {
        return firstName + " " + lastName + " " + passno + " " + address + " " + phone ;
    }
}
