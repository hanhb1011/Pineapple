package org.androidtown.pineapple_android.Model;

/**
 * Created by MSI on 2018-05-04.
 */

public class Place {
    private double x;
    private double y;
    private int featureNumber;

    public Place(double x, double y,int featureNumber) {
        this.x = x;
        this.y = y;
        this.featureNumber = featureNumber;
    }

    public double getX() {
        return x;
    }

    public void setX(double x) {
        this.x = x;
    }

    public double getY() {
        return y;
    }

    public void setY(double y) {
        this.y = y;
    }
}
