package org.finalactivitydg2627.model;

import org.finalactivitydg2627.Utils.Verification;

import java.time.LocalDate;

public class Tickets {

    private int code;
    private String name;
    private LocalDate bookingDate;
    private LocalDate travelDate;
    private LocalDate cancellationDate;
    private String passportCode;
    private String flightCode;
    private int price;

    public int getCode() {
        return code;
    }

    public void setCode(int code) {
        this.code = code;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public LocalDate getBookingDate() {
        return bookingDate;
    }

    public void setBookingDate(LocalDate bookingDate) {
        this.bookingDate = bookingDate;
    }

    public LocalDate getTravelDate() {
        return travelDate;
    }

    public Verification.ResultValue<LocalDate> setTravelDate(String travelDate) {
        var result = validateTravelDate(travelDate);
        this.travelDate = result.result();
        return result;
    }

    public Verification.ResultValue<LocalDate> validateTravelDate(String travelDate) {
        return Verification.basicDateVerification("date", travelDate, LocalDate.now(), null);
    }

    public LocalDate getCancellationDate() {
        return cancellationDate;
    }

    public void setCancellationDate(LocalDate cancellationDate) {
        this.cancellationDate = cancellationDate;
    }

    public String getPassportCode() {
        return passportCode;
    }

    public Verification.ResultValue<String> setPassportCode(String passportCode) {
        var result = validatePassportCode(passportCode);
        this.passportCode = result.result();
        return result;
    }

    public Verification.ResultValue<String> validatePassportCode(String passportCode) {
        return Verification.basicStringVerification("passport", passportCode, null, 10, null);
    }

    public String getFlightCode() {
        return flightCode;
    }

    public Verification.ResultValue<String> setFlightCode(String flightCode) {
        var result = validateFlightCode(flightCode);
        this.flightCode = result.result();
        return result;
    }

    public Verification.ResultValue<String> validateFlightCode(String flightCode) {
        return Verification.basicStringVerification("flight code", flightCode, null, 10, null);
    }

    public int getPrice() {
        return price;
    }

    public void setPrice(int price) {
        this.price = price;
    }
}
