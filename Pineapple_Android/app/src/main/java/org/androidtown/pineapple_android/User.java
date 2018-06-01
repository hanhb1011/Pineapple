package org.androidtown.pineapple_android;

import java.util.HashMap;

/**
 * Created by hanhb on 2018-06-01.
 */

public class User {

    private String uid;
    private double currentLatitue;
    private double currentLongitude;
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

    public double getCurrentLatitue() {
        return currentLatitue;
    }

    public void setCurrentLatitue(double currentLatitue) {
        this.currentLatitue = currentLatitue;
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
