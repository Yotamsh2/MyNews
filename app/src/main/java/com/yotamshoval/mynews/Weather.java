package com.yotamshoval.mynews;

public class Weather {

    private String location;
    public String area;
    public String degrees;
    private String longitude, latitude;
    private int weatherKind; //sunny, cloudy, rainy

    public Weather(String location, String area, String degrees, String longitude, String latitude, int weatherKind) {
        this.location = location;
        this.area = area;
        this.degrees = degrees;
        this.longitude = longitude;
        this.latitude = latitude;
        this.weatherKind = weatherKind;
    }

    public String getLocation() {
        return location;
    }

    public void setLocation(String location) {
        this.location = location;
    }

    public String getLongitude() {
        return longitude;
    }

    public void setLongitude(String longitude) {
        this.longitude = longitude;
    }

    public String getLatitude() {
        return latitude;
    }

    public void setLatitude(String latitude) {
        this.latitude = latitude;
    }

    public int getWeatherKind() {
        return weatherKind;
    }

    public void setWeatherKind(int weatherKind) {
        this.weatherKind = weatherKind;
    }

    public String getArea() {
        return area;
    }

    public void setArea(String area) {
        this.area = area;
    }

    public String getDegrees() {
        return degrees;
    }

    public void setDegrees(String degrees) {
        this.degrees = degrees;
    }
}
