package com.example.travelhelper.bean;

public class PositionBean {
    private double latitude;
    private double longitude;
    private String pos_name;
    private String pos_address;
    private String pos_district;
    private String city;

    @Override
    public String toString() {
        return "PositionBean{" +
                "latitude=" + latitude +
                ", longitude=" + longitude +
                ", pos_name='" + pos_name + '\'' +
                ", pos_address='" + pos_address + '\'' +
                ", pos_district='" + pos_district + '\'' +
                ", city='" + city + '\'' +
                '}';
    }

    public double getLatitude() {
        return latitude;
    }

    public void setLatitude(double latitude) {
        this.latitude = latitude;
    }

    public double getLongitude() {
        return longitude;
    }

    public void setLongitude(double longitude) {
        this.longitude = longitude;
    }

    public String getPos_name() {
        return pos_name;
    }

    public void setPos_name(String pos_name) {
        this.pos_name = pos_name;
    }

    public String getPos_address() {
        return pos_address;
    }

    public void setPos_address(String pos_address) {
        this.pos_address = pos_address;
    }

    public String getCity() {
        return city;
    }

    public void setCity(String city) {
        this.city = city;
    }

    public PositionBean() {
    }

    public String getPos_district() {
        return pos_district;
    }

    public void setPos_district(String pos_district) {
        this.pos_district = pos_district;
    }

    public PositionBean(double latitude, double longitude, String pos_name, String pos_address, String pos_district, String city) {
        this.latitude = latitude;
        this.longitude = longitude;
        this.pos_name = pos_name;
        this.pos_address = pos_address;
        this.pos_district = pos_district;
        this.city = city;
    }
}
