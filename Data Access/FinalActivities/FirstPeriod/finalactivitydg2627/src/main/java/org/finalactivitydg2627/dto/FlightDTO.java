package org.finalactivitydg2627.dto;

public class FlightDTO {
    private String flightCode;

    public FlightDTO() {}

    public FlightDTO(String flightCode, String iataCode, String cityName, int totalSeats, long freeSeats) {
        this.flightCode = flightCode;
        this.iataCode = iataCode;
        this.cityName = cityName;
        this.totalSeats = totalSeats;
        this.freeSeats = freeSeats;
    }


    public long getFreeSeats() {
        return freeSeats;
    }

    public void setFreeSeats(long freeSeats) {
        this.freeSeats = freeSeats;
    }

    public int getTotalSeats() {
        return totalSeats;
    }

    public void setTotalSeats(int totalSeats) {
        this.totalSeats = totalSeats;
    }

    public String getCityName() {
        return cityName;
    }

    public void setCityName(String cityName) {
        this.cityName = cityName;
    }

    public String getIataCode() {
        return iataCode;
    }

    public void setIataCode(String iataCode) {
        this.iataCode = iataCode;
    }

    public String getFlightCode() {
        return flightCode;
    }

    public void setFlightCode(String flightCode) {
        this.flightCode = flightCode;
    }

    private String iataCode;
    private String cityName;
    private int totalSeats;
    private long freeSeats;
}
