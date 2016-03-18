package com.harrykristi.hangapp.model;


public class Location {
    String address;
    String crossStreet;
    float lat;
    float lng;
    String postalCode;
    String [] formattedAddress;

    public float getLat() {
        return lat;
    }

    public void setLat(float lat) {
        this.lat = lat;
    }

    public float getLng() {
        return lng;
    }

    public void setLng(float lng) {
        this.lng = lng;
    }

    public String getPostalCode() {
        return postalCode;
    }

    public void setPostalCode(String postalCode) {
        this.postalCode = postalCode;
    }

    public String[] getFormattedAddress() {
        return formattedAddress;
    }

}
