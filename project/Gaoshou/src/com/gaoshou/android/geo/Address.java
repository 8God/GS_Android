package com.gaoshou.android.geo;

public class Address {

    private String locationName;
    private String province;
    private String city;
    private String district;
    private String street;
    private String streetNumber;
    private double latitude;
    private double longitude;
    private double altitude;

    public String getProvince() {
        return province;
    }

    public void setProvince(String province) {
        this.province = province;
    }

    public String getCity() {
        return city;
    }

    public void setCity(String city) {
        this.city = city;
    }

    public String getDistrict() {
        return district;
    }

    public void setDistrict(String district) {
        this.district = district;
    }

    public String getStreet() {
        return street;
    }

    public void setStreet(String street) {
        this.street = street;
    }

    public String getStreetNumber() {
        return streetNumber;
    }

    public void setStreetNumber(String streetNumber) {
        this.streetNumber = streetNumber;
    }

    public String getLocationName() {
        if (null == locationName) {
            StringBuffer locationNameStrBuf = new StringBuffer();
            locationNameStrBuf.append(getProvince());
            locationNameStrBuf.append(getCity());
            locationNameStrBuf.append(getDistrict());
            locationNameStrBuf.append(getStreet());
            locationNameStrBuf.append(getStreetNumber());
            locationName = locationNameStrBuf.toString();
        } // if (null == locationName)
        return locationName;
    }

    public void setLocationName(String locationName) {
        this.locationName = locationName;
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

    public double getAltitude() {
        return altitude;
    }

    public void setAltitude(double altitude) {
        this.altitude = altitude;
    }

}
