package com.example.smartpark;

import java.util.HashMap;
import java.util.Map;

/**
 * Class to save userInfo
 */
public class UserInfo {

    private String userName;
    private double latitude;
    private double longitude;
    // maybe location ehre

    public UserInfo(String userName,double latitude,double longitude){
        this.userName = userName;
        this.latitude = latitude;
        this.longitude = longitude;

    }

    public String getUserName() {
        return userName;
    }

    public void setUserName(String userName) {
        this.userName = userName;
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

    /**
     * function to create a map with the user info that is setp
     * @return the map created
     */
    public Map<String,Object> toMap() {
        Map<String,Object> result = new HashMap<>();

        result.put("userName",userName);
        result.put("latitude",latitude);
        result.put("longitude",longitude);

        return result;
    }
}

