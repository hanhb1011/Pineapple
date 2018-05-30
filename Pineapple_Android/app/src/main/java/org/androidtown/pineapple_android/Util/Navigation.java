package org.androidtown.pineapple_android.Util;

import android.content.Context;
import android.graphics.Color;

import com.skt.Tmap.TMapMarkerItem;
import com.skt.Tmap.TMapPoint;
import com.skt.Tmap.TMapPolyLine;
import com.skt.Tmap.TMapView;

import org.androidtown.pineapple_android.Model.Feature;
import org.androidtown.pineapple_android.Model.FindTheWay;
import org.androidtown.pineapple_android.Model.Place;

import java.util.ArrayList;
import java.util.LinkedList;
import java.util.List;


/**
 * Created by MSI on 2018-05-10.
 */

public class Navigation {
    private FindTheWay way;
    private TMapView tMapView;
    private boolean isStarted = false;
    private int currentState;
    private static String mKey = "345ff3b1-839d-47a2-860f-de2d9dc3acd8";
    private int lineNumber;
    private int featureNumber;
    private List<Feature> features;
    private Feature currentFeature;
    private List<Place> currentPlaces;
    private Place currentPlace;
    private String description;
    private int featureSize;
    private int type;
    private boolean dSync = false;
    private boolean sSync = false;

    private TMapPolyLine[] tMapPolyLines;

    private final int type_point=1;
    private final int type_line=2;


    private double startX,startY,endX,endY,currentX,currentY;

    public double getCurrentX() {
        return currentX;
    }

    public void setCurrentX(double currentX) {
        this.currentX = currentX;
    }

    public double getCurrentY() {
        return currentY;
    }

    public void setCurrentY(double currentY) {
        this.currentY = currentY;
    }

    public boolean isdSync() {
        return dSync;
    }

    public void setdSync(boolean dSync) {
        this.dSync = dSync;
    }

    public boolean issSync() {
        return sSync;
    }

    public void setsSync(boolean sSync) {
        this.sSync = sSync;
    }

    public double getStartX() {
        return startX;
    }

    public void setStartX(double startX) {
        this.startX = startX;
    }

    public double getStartY() {
        return startY;
    }

    public void setStartY(double startY) {
        this.startY = startY;
    }

    public double getEndX() {
        return endX;
    }

    public void setEndX(double endX) {
        this.endX = endX;
    }

    public double getEndY() {
        return endY;
    }

    public void setEndY(double endY) {
        this.endY = endY;
    }

    public void setStartEndXY(double sx,double sy, double ex, double ey){
        this.startX = sx;
        this.startY = sy;
        this.endX = ex;
        this.endY = ey;
    }

    private List<Place> places;

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public int getFeatureNumber() {
        return featureNumber;
    }

    public void setFeatureNumber(int featureNumber) {
        this.featureNumber = featureNumber;
    }

    public int getLineNumber() {
        return lineNumber;
    }

    public void setLineNumber(int lineNumber) {
        this.lineNumber = lineNumber;
    }

    public Navigation(Context context){
        places = new LinkedList<>();
        tMapView = new TMapView(context);
        tMapView.setSKTMapApiKey(mKey);
    }

    public FindTheWay getWay() {
        return way;
    }

    public void setWay(FindTheWay way) {
        this.way = way;
    }

    public TMapView gettMapView() {
        return tMapView;
    }

    public void settMapView(TMapView tMapView) {
        this.tMapView = tMapView;
    }

    public List<Place> getPlaces() {
        return places;
    }

    public void setPlaces(List<Place> places) {
        this.places = places;
    }

    public boolean isStarted() {
        return isStarted;
    }

    public void setStarted(boolean started) {
        isStarted = started;
    }

    public int getCurrentState() {
        return currentState;
    }

    public void setCurrentState(int currentState) {
        this.currentState = currentState;
    }

    public TMapPolyLine[] gettMapPolyLines() {
        return tMapPolyLines;
    }

    public void setLineInfo(){
        ArrayList<TMapPoint> alTMapPoint = new ArrayList<>();

        for(Feature f : features) {
            if(f.getGeometry().getType().equals("LineString")){
                for(Object o : f.getGeometry().getCoordinates()){
                    List<Double> l = (List<Double>)o;
                    alTMapPoint.add(new TMapPoint(l.get(1),l.get(0)));
                }
            }

        }

        int mNodeSize = alTMapPoint.size();
        tMapPolyLines = new TMapPolyLine[mNodeSize-1];
        for( int i=0; i<mNodeSize-1; i++ ) {
            TMapPolyLine tMapPolyLine = new TMapPolyLine();
            tMapPolyLine.setLineColor(Color.BLUE);
            tMapPolyLine.setLineWidth(2);
            tMapPolyLine.addLinePoint(alTMapPoint.get(i));
            tMapPolyLine.addLinePoint(alTMapPoint.get(i+1));

            tMapPolyLines[i] = tMapPolyLine;

            //tMapView.addTMapPolyLine("line"+i,tMapPolyLine);
        }
    }
    public void drawWayInMap() { //수정
        setMarker();
        for(int i=0;i<tMapPolyLines.length;i++){
            tMapView.addTMapPolyLine("line"+i,tMapPolyLines[i]);
        }
        tMapView.refreshMap();
    }

    public void nextFeature(){
        featureNumber++;
        if(featureNumber==featureSize){//네비게이션 종료
            isStarted = false;
            sSync = false;
            dSync = false;
        }else {
            currentFeature = features.get(featureNumber);
            if (currentFeature.getGeometry().getType().equals("Point")) { //type : Point
                //음성안내
                type = type_point;
                currentState = 1;
                description = currentFeature.getProperties().getDescription();
                double x = (double)currentFeature.getGeometry().getCoordinates().get(0);
                double y = (double)currentFeature.getGeometry().getCoordinates().get(1);
                currentPlace = new Place(x,y,featureNumber);
            } else { //type : LineString
                type = type_line;
                List<Place> temp = new LinkedList<>();
                for (Object o : currentFeature.getGeometry().getCoordinates()) {
                    List<Double> l = (List<Double>) o;
                    temp.add(new Place(l.get(0), l.get(1), featureNumber));
                }
                currentPlaces = temp;
                lineNumber = 1;
                currentState = 2;
            }
        }
    }

    public void setMarker(){
        TMapMarkerItem startItem = new TMapMarkerItem();
        startItem.setTMapPoint(new TMapPoint(startY,startX));
        startItem.setName("출발지");
        startItem.setVisible(TMapMarkerItem.VISIBLE);

        TMapMarkerItem endItem = new TMapMarkerItem();
        endItem.setTMapPoint(new TMapPoint(endY,endX));
        endItem.setName("도착지");
        endItem.setVisible(TMapMarkerItem.VISIBLE);

        tMapView.addMarkerItem("출발지",startItem);
        tMapView.addMarkerItem("도착지",endItem);
    }

    public void setCurrentMarker(double currentX, double currentY){
        try {
            tMapView.removeMarkerItem("현재위치");
        }catch(Exception e){}
        TMapMarkerItem currentItem = new TMapMarkerItem();
        currentItem.setName("현재위치");
        currentItem.setTMapPoint(new TMapPoint(currentX, currentY));
        currentItem.setVisible(TMapMarkerItem.VISIBLE);
        tMapView.addMarkerItem("현재위치",currentItem);

        tMapView.refreshMap();
    }

    public void startNavigation(FindTheWay w){
        way = w;
        isStarted = true;
        featureNumber = 0;
        features = way.getFeatures();
        currentFeature = features.get(featureNumber);
        if(currentFeature.getGeometry().getType().equals("Point")) type = type_point;
        else type = type_line;
        lineNumber = 1;
        featureSize = features.size();
        if(type == type_point){ //type : Point
            description = currentFeature.getProperties().getDescription();
            currentState = 1;
        }else{ //type : LineString
            currentState = 2;
        }
    }

    public void stateCheck(double lat1, double lon1) {
        if(type == type_point){ //type : Point
            double lon2 = currentPlace.getX();
            double lat2 = currentPlace.getY();
            int dis = (int)getDistanceFromLatLon(lon1,lat1,lon2,lat2);
            if(dis<=5){
                nextFeature();
            }
        }else{//type : LineString
            currentPlace = currentPlaces.get(lineNumber);
            double lon2 = currentPlace.getX();
            double lat2 = currentPlace.getY();
            int dis = (int)getDistanceFromLatLon(lon1,lat1,lon2,lat2);
            if( (currentPlaces.size() == lineNumber-1) && (dis <= 5) ){ //LineString 마지막 노드 도달, nextFeature
                nextFeature();
            }else if(dis <= 5){ //LineString 진행중
                lineNumber++;
                currentState = 2;
            }
        }
    }

    private double getDistanceFromLatLon(double lon1, double lat1, double lon2, double lat2){
        double R = 6371;
        double dLat = Math.toRadians(lat2-lat1);
        double dLon = Math.toRadians(lon2-lon1);
        double a = Math.sin(dLat/2) * Math.sin(dLat/2) +
                Math.cos(Math.toRadians(lat1)) *Math.cos(Math.toRadians(lat2)) *
                        Math.sin(dLon/2) *Math.sin(dLon/2);
        double c = 2 * Math.atan2(Math.sqrt(a), Math.sqrt(1-a));
        double d = R * c * 1000;
        return d;
    }

}
