package com.hugopicado.examples.storm.domain;

public class PhoneNumber {

    private int countryCallingCode;
    private long nationalNumber;
    private String countryCode;
    private String numberType;

    public int getCountryCallingCode() {
        return countryCallingCode;
    }

    public void setCountryCallingCode(int countryCallingCode) {
        this.countryCallingCode = countryCallingCode;
    }

    public long getNationalNumber() {
        return nationalNumber;
    }

    public void setNationalNumber(long nationalNumber) {
        this.nationalNumber = nationalNumber;
    }

    public String getCountryCode() {
        return countryCode;
    }

    public void setCountryCode(String countryCode) {
        this.countryCode = countryCode;
    }

    public String getNumberType() {
        return numberType;
    }

    public void setNumberType(String numberType) {
        this.numberType = numberType;
    }
}
