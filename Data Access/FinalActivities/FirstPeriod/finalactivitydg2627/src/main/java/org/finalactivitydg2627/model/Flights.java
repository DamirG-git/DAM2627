package org.finalactivitydg2627.model;

import org.finalactivitydg2627.utils.ApplicationValidator;

import java.time.LocalDate;
import java.time.LocalTime;

public class Flights {
    private String code;
    private String codeSourceAirport;
    private String codeDestinationAirport;
    private LocalTime arrivalTime;
    private LocalTime departureTime;
    private int duration;
    private String airlineCode;
    private int distance;
    private int planeCode;
    private LocalDate travelDate;

    public String getCode() {
        return code;
    }

    public void setCode(String code) {
        this.code = code;
    }

    public String getCodeSourceAirport() {
        return codeSourceAirport;
    }

    public ApplicationValidator.ResultValue<String> setCodeSourceAirport(String codeSourceAirport) {
        var result = validateAirportCode("source location", codeSourceAirport);
        this.codeSourceAirport = result.result();
        return result;
    }

    public String getCodeDestinationAirport() {
        return codeDestinationAirport;
    }

    public ApplicationValidator.ResultValue<String> setCodeDestinationAirport(String codeDestinationAirport) {
        var result = validateAirportCode("destination location", codeDestinationAirport);
        this.codeDestinationAirport = result.result();
        return result;
    }

    public ApplicationValidator.ResultValue<String> validateAirportCode(String label, String input){
        return ApplicationValidator.basicStringVerification(label, input == null ? null : input.toUpperCase(), 3, null, null);
    }

    public LocalTime getArrivalTime() {
        return arrivalTime;
    }

    public void setArrivalTime(LocalTime arrivalTime) {
        this.arrivalTime = arrivalTime;
    }

    public LocalTime getDepartureTime() {
        return departureTime;
    }

    public void setDepartureTime(LocalTime departureTime) {
        this.departureTime = departureTime;
    }

    public int getDuration() {
        return duration;
    }

    public void setDuration(int duration) {
        this.duration = duration;
    }

    public String getAirlineCode() {
        return airlineCode;
    }

    public void setAirlineCode(String airlineCode) {
        this.airlineCode = airlineCode;
    }

    public int getDistance() {
        return distance;
    }

    public void setDistance(int distance) {
        this.distance = distance;
    }

    public int getPlaneCode() {
        return planeCode;
    }

    public void setPlaneCode(int planeCode) {
        this.planeCode = planeCode;
    }

    public ApplicationValidator.ResultValue<LocalDate> validateFlightDate(String input){
        return ApplicationValidator.basicDateVerification("date", input, LocalDate.now(), null);
    }

    public LocalDate getTravelDate() {
        return travelDate;
    }

    public ApplicationValidator.ResultValue<LocalDate> setTravelDate(String travelDate) {
        var result = validateFlightDate(travelDate);
        this.travelDate = result.result();
        return result;
    }
}
