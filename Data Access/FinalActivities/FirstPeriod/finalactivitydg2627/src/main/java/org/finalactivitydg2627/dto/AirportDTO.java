package org.finalactivitydg2627.dto;

public class AirportDTO {

    public AirportDTO() {}

    public AirportDTO(String airportIata, String airportName, String cityName, String flightCode) {
        this.airportIata = airportIata;
        this.airportName = airportName;
        this.cityName = cityName;
        this.flightCode = flightCode;
    }

    public String getAirportIata() {
        return airportIata;
    }

    public void setAirportIata(String airportIata) {
        this.airportIata = airportIata;
    }

    public String getAirportName() {
        return airportName;
    }

    public void setAirportName(String airportName) {
        this.airportName = airportName;
    }

    public String getCityName() {
        return cityName;
    }

    public void setCityName(String cityName) {
        this.cityName = cityName;
    }

    public String getFlightCode() {
        return flightCode;
    }

    public void setFlightCode(String flightCode) {
        this.flightCode = flightCode;
    }

    private String airportIata;
    private String airportName;
    private String cityName;
    private String flightCode;
}
