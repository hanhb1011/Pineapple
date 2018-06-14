package org.androidtown.pineapple_android;

/**
 * Created by hanhb on 2018-06-01.
 */

public class TrainData {


    double prevLatitude;
    double prevLongitude;
    double dstLatitude;
    double dstLongitude;
    double currentLatitude;
    double currentLongitude;

    public TrainData(){

    }

    public TrainData(double prevLatitude, double prevLongitude, double dstLatitude, double dstLongitude, double currentLatitude, double currentLongitude) {
        this.prevLatitude = prevLatitude;
        this.prevLongitude = prevLongitude;
        this.dstLatitude = dstLatitude;
        this.dstLongitude = dstLongitude;
        this.currentLatitude = currentLatitude;
        this.currentLongitude = currentLongitude;
    }

    public double getDstLatitude() {
        return dstLatitude;
    }

    public void setDstLatitude(double dstLatitude) {
        this.dstLatitude = dstLatitude;
    }

    public double getDstLongitude() {
        return dstLongitude;
    }

    public void setDstLongitude(double dstLongitude) {
        this.dstLongitude = dstLongitude;
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

}
