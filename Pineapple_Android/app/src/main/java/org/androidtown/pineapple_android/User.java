package org.androidtown.pineapple_android;

import java.util.HashMap;

/**
 * Created by hanhb on 2018-06-01.
 */

public class User {

    private String uid;
    private double currentLatitude;
    private double currentLongitude;
    private HashMap<String, VoiceMessage> messageToBlind;
    private HashMap<String, VoiceMessage> messageToCareTaker;
    private HashMap<String, RouteNavigation> navigationLog;

    public User (){

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

    public HashMap<String, VoiceMessage> getMessageToBlind() {
        return messageToBlind;
    }

    public void setMessageToBlind(HashMap<String, VoiceMessage> messageToBlind) {
        this.messageToBlind = messageToBlind;
    }

    public HashMap<String, VoiceMessage> getMessageToCareTaker() {
        return messageToCareTaker;
    }

    public void setMessageToCareTaker(HashMap<String, VoiceMessage> messageToCareTaker) {
        this.messageToCareTaker = messageToCareTaker;
    }

    public HashMap<String, RouteNavigation> getNavigationLog() {
        return navigationLog;
    }

    public void setNavigationLog(HashMap<String, RouteNavigation> navigationLog) {
        this.navigationLog = navigationLog;
    }
}
