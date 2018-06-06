package com.example.hanhb.caretaker_android;

import com.example.hanhb.caretaker_android.RouteNavigation;

import java.util.HashMap;

/**
 * Created by hanhb on 2018-06-01.
 */

public class User {

    private String uid;
    private double currentLatitude;
    private double currentLongitude;
    private String messageToBlind;
    private HashMap<String, RouteNavigation> navigationLog;

    public User (){

    }

    public String getMessageToBlind() {
        return messageToBlind;
    }

    public void setMessageToBlind(String messageToBlind) {
        this.messageToBlind = messageToBlind;
    }

    public User(String uid) {
        this.uid = uid;
    }

    public String getUid() {
        return uid;
    }

    public void setUid(String uid) {
        this.uid = uid;
    }

    public double getCurrentLatitude() {
        return currentLatitude;
    }

    public void setCurrentLatitude(double currentLatitude) {
        this.currentLatitude = currentLatitude;
    }

    public double getCurrentLongitude() {
        return currentLongitude;
    }

    public void setCurrentLongitude(double currentLongitude) {
        this.currentLongitude = currentLongitude;
    }

    public HashMap<String, RouteNavigation> getNavigationLog() {
        return navigationLog;
    }

    public void setNavigationLog(HashMap<String, RouteNavigation> navigationLog) {
        this.navigationLog = navigationLog;
    }

}
