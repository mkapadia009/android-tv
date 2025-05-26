package com.app.itaptv.structure;

public class LocationData {
    String latitude="";
    String longitude="";
    String city="";
    String state="";

    public LocationData(String latitude, String longitude, String city, String state) {
        this.latitude = latitude;
        this.longitude = longitude;
        this.city = city;
        this.state = state;
    }

    public String getLatitude() {
        return latitude;
    }

    public void setLatitude(String latitude) {
        this.latitude = latitude;
    }

    public String getLongitude() {
        return longitude;
    }

    public void setLongitude(String longitude) {
        this.longitude = longitude;
    }

    public String getCity() {
        return city;
    }

    public void setCity(String city) {
        this.city = city;
    }

    public String getState() {
        return state;
    }

    public void setState(String state) {
        this.state = state;
    }
}
