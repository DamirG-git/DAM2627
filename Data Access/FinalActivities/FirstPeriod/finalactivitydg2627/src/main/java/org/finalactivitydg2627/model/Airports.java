package org.finalactivitydg2627.model;

import org.finalactivitydg2627.utils.ApplicationValidator;

import java.util.regex.Pattern;

public class Airports {

    private int code;
    private String iata;


    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public int getCityCode() {
        return cityCode;
    }

    public void setCityCode(int cityCode) {
        this.cityCode = cityCode;
    }

    private String name;
    private int cityCode;


    public String getIata() {
        return iata;
    }

    public void setIata(String iata) {
        this.iata = iata;
    }

    public int getCode() {
        return code;
    }

    public void setCode(int code) {
        this.code = code;
    }

    public static ApplicationValidator.ResultValue<String> validateIata(String iata){
        return ApplicationValidator.basicStringVerification("IATA", iata, 3, 3, Pattern.compile("^[A-Z]{3}$"));
    }
}
