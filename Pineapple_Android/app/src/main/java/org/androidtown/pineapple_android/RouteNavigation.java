package org.androidtown.pineapple_android;

import java.util.ArrayList;
import java.util.Calendar;
import java.util.List;

/**
 * Created by hanhb on 2018-06-01.
 */

public class RouteNavigation {

    private long timestamp;
    private double srcLatitude;
    private double srcLongitude;
    private double dstLatitude;
    private double dstLongitude;
    private String dstName;
    private String dstAddress;
    private double dstDistance;
    private List<Node> nodes;


    public RouteNavigation() {

    }

    public RouteNavigation(double srcLongitude,double srcLatitude, double dstLongitude, double dstLatitude,String dstName, String dstAddress, double dstDistance) {
        this.timestamp = Calendar.getInstance().getTimeInMillis();
        this.srcLatitude = srcLatitude;
        this.srcLongitude = srcLongitude;
        this.dstLatitude = dstLatitude;
        this.dstLongitude = dstLongitude;
        this.dstName = dstName;
        this.dstAddress = dstAddress;
        this.dstDistance = dstDistance;
    }

    public long getTimestamp() {
        return timestamp;
    }

    public void setTimestamp(long timestamp) {
        this.timestamp = timestamp;
    }

    public double getSrcLatitude() {
        return srcLatitude;
    }

    public void setSrcLatitude(double srcLatitude) {
        this.srcLatitude = srcLatitude;
    }

    public double getSrcLongitude() {
        return srcLongitude;
    }

    public void setSrcLongitude(double srcLongitude) {
        this.srcLongitude = srcLongitude;
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

    public String getDstName() {
        return dstName;
    }

    public void setDstName(String dstName) {
        this.dstName = dstName;
    }

    public String getDstAddress() {
        return dstAddress;
    }

    public void setDstAddress(String dstAddress) {
        this.dstAddress = dstAddress;
    }

    public double getDstDistance() {
        return dstDistance;
    }

    public void setDstDistance(double dstDistance) {
        this.dstDistance = dstDistance;
    }

    public List<Node> getNodes() {
        return nodes;
    }

    public void setNodes(List<Node> nodes) {
        this.nodes = nodes;
    }
}