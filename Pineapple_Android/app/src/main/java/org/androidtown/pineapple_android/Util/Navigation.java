package org.androidtown.pineapple_android.Util;

import android.graphics.Color;

import com.skt.Tmap.TMapPoint;
import com.skt.Tmap.TMapPolyLine;

import org.androidtown.pineapple_android.MainActivity;
import org.androidtown.pineapple_android.Model.Feature;
import org.androidtown.pineapple_android.Model.FindTheWay;
import org.androidtown.pineapple_android.Model.Place;
import org.androidtown.pineapple_android.Node;
import org.androidtown.pineapple_android.RouteNavigation;
import org.androidtown.pineapple_android.Tmap;

import java.util.ArrayList;
import java.util.LinkedList;
import java.util.List;


/**
 * Created by MSI on 2018-05-10.
 */

public class Navigation {
    private static Navigation navi = new Navigation();
    public static Navigation getInstance(){
        return navi;
    }
    private FindTheWay way;
    private boolean isStarted = false;
    private static String mKey = "345ff3b1-839d-47a2-860f-de2d9dc3acd8";
    private int lineNumber;
    private int featureNumber;
    private List<Feature> features;
    private Feature currentFeature;
    private List<Place> currentPlaces;
    private Place currentPlace;
    private Place prePlace;
    private String description;
    private int featureSize;
    private int type;
    private boolean dSync = false;
    private boolean sSync = false;
    private int leaveWayCount = 0;

    private double preDistance = 1000;
    private double distance = 0.0d;

    private final int boundary = 10;

    private static boolean firstLocation=false;

    private RouteNavigation routeNavigation;

    public RouteNavigation getRouteNavigation() {
        return routeNavigation;
    }

    public void setRouteNavigation(RouteNavigation routeNavigation) {
        this.routeNavigation = routeNavigation;
    }

    private TMapPolyLine[] tMapPolyLines;

    private final int type_point=1;
    private final int type_line=2;


    private double startX=0,startY=0,endX=0,endY=0,currentX=0,currentY=0,preX=0,preY=0;
    private double destinationAngle=0.0d;

    public Place getCurrentPlace() {
        return currentPlace;
    }

    public void setCurrentPlace(Place currentPlace) {
        this.currentPlace = currentPlace;
    }

    public Place getPrePlace() {
        return prePlace;
    }

    public void setPrePlace(Place prePlace) {
        this.prePlace = prePlace;
    }

    public static boolean isFirstLocation() {
        return firstLocation;
    }

    public static void setFirstLocation(boolean firstLocation) {
        Navigation.firstLocation = firstLocation;
    }

    public int getLeaveWayCount() {
        return leaveWayCount;
    }

    public void setLeaveWayCount(int leaveWayCount) {
        this.leaveWayCount = leaveWayCount;
    }

    public int getFeatureSize() {
        return featureSize;
    }

    public void setFeatureSize(int featureSize) {
        this.featureSize = featureSize;
    }

    public double getDistance() {
        return distance;
    }

    public void setDistance(double distance) {
        this.distance = distance;
    }

    public double getDestinationAngle() {
        return destinationAngle;
    }

    public void setDestinationAngle(double destinationAngle) {
        this.destinationAngle = destinationAngle;
    }

    public double getPreX() {
        return preX;
    }

    public void setPreX(double preX) {
        this.preX = preX;
    }

    public double getPreY() {
        return preY;
    }

    public void setPreY(double preY) {
        this.preY = preY;
    }

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

    public Navigation(){
        places = new LinkedList<>();
    }

    public FindTheWay getWay() {
        return way;
    }

    public void setWay(FindTheWay way) {
        this.way = way;
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

    public TMapPolyLine[] gettMapPolyLines() {
        return tMapPolyLines;
    }

    public void setLineInfo(){
        ArrayList<TMapPoint> alTMapPoint = new ArrayList<>();

        //서버에 저장할 경로안내 클래스 정의
        routeNavigation = new RouteNavigation(startX,startY,endX,endY, Tmap.lastPOIItem.getPOIName(),
                Tmap.lastPOIItem.getPOIAddress(),navi.getDistanceFromLatLon(startX,startY,endX,endY));


        List<Node> nodes = new ArrayList<>(); // 경로상의 노드들을 저장할 List

        for(Feature f : features) {
            if(f.getGeometry().getType().equals("LineString")){
                for(Object o : f.getGeometry().getCoordinates()){
                    List<Double> l = (List<Double>)o;
                    alTMapPoint.add(new TMapPoint(l.get(1),l.get(0)));
                    nodes.add(new Node(l.get(1),l.get(0))); //노드정보 추가
                }
            }

        }

        //완성된 노드 리스트와 함께 서버에 경로정보 저장
        routeNavigation.setNodes(nodes);
        if(MainActivity.firebaseHelper != null) {
            MainActivity.firebaseHelper.addRouteNavigation(routeNavigation);

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

        }
    }

    public void nextFeature(){
        featureNumber++;
        lineNumber = 1;
        if(featureNumber==featureSize){//네비게이션 종료
            isStarted = false;
            sSync = false;
            dSync = false;

            MainActivity.speak("목적지에 도착했습니다.\n 안내를 종료합니다.");
            terminate();
        }else {
            currentFeature = features.get(featureNumber);
            if (currentFeature.getGeometry().getType().equals("Point")) { //type : Point
                type = type_point;
                description = currentFeature.getProperties().getDescription();
                double x = (double)currentFeature.getGeometry().getCoordinates().get(0);
                double y = (double)currentFeature.getGeometry().getCoordinates().get(1);
                prePlace = currentPlace;
                currentPlace = new Place(x,y,featureNumber);

                distance = getDistanceFromLatLon(x,y,currentX,currentY);
                if(distance<=boundary){
                    nextFeature();
                }
            } else { //type : LineString
                type = type_line;
                List<Place> temp = new LinkedList<>();
                for (Object o : currentFeature.getGeometry().getCoordinates()) {
                    List<Double> l = (List<Double>) o;
                    temp.add(new Place(l.get(0), l.get(1), featureNumber));
                }
                currentPlaces = temp;
                lineNumber = 1;
                prePlace = currentPlace;
                currentPlace = currentPlaces.get(lineNumber);

                distance = getDistanceFromLatLon(currentPlace.getX(),currentPlace.getY(),currentX,currentY);
            }
        }
    }


    public void startNavigation(FindTheWay w){
        way = w;
        isStarted = true;
        featureNumber = 0;
        features = way.getFeatures();
        currentFeature = features.get(featureNumber);
        setLineInfo();
        lineNumber = 1;
        featureSize = features.size();
        if(currentFeature.getGeometry().getType().equals("Point")) {
            type = type_point;
            description = currentFeature.getProperties().getDescription();
            double x = (double)currentFeature.getGeometry().getCoordinates().get(0);
            double y = (double)currentFeature.getGeometry().getCoordinates().get(1);
            prePlace = new Place(currentX,currentY,-1);
            currentPlace = new Place(x,y,featureNumber);
        }
        else{
            type = type_line;
            List<Place> temp = new LinkedList<>();
            for (Object o : currentFeature.getGeometry().getCoordinates()) {
                List<Double> l = (List<Double>) o;
                temp.add(new Place(l.get(0), l.get(1), featureNumber));
            }
            currentPlaces = temp;
            lineNumber = 1;
            prePlace = new Place(currentX,currentY,-1);
            currentPlace = currentPlaces.get(lineNumber);
        }
    }

    public void stateCheck(double lat1, double lon1) {
        if(type == type_point){ //type : Point
            double lon2 = currentPlace.getX();//목표 x
            double lat2 = currentPlace.getY();//목표 y

            if(distance!=0)
                preDistance = distance;
            distance = (int)getDistanceFromLatLon(lon1,lat1,lon2,lat2);

            if(distance > preDistance) {
                leaveWayCount++;
            }else{
                leaveWayCount = 0;
            }

            if(distance<=boundary){
                nextFeature();
            }
        }else{//type : LineString
            currentPlace = currentPlaces.get(lineNumber);
            double lon2 = currentPlace.getX();
            double lat2 = currentPlace.getY();

            if(distance!=0)
                preDistance = distance;
            distance = (int)getDistanceFromLatLon(lon1,lat1,lon2,lat2);

            if(distance > preDistance) {
                leaveWayCount++;
            }else{
                leaveWayCount = 0;
            }


            if(distance <= boundary){ //LineString 진행중
                lineNumber++;
                if(currentPlaces.size() == lineNumber){//linestring 끝 도달
                    nextFeature();
                }else {
                    prePlace = currentPlace;
                    currentPlace = currentPlaces.get(lineNumber);
                }
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


//    private double getAngle(double x, double y){
//        double angle =  Math.atan2(y,x) - Math.atan2(1,0);
//        if(angle<0) angle += 2*Math.PI;
//        return Math.toDegrees(angle);
//    }

    private double getAngle(double lon1, double lat1, double lon2, double lat2){
        double y = Math.sin(lon2-lon1) * Math.cos(lat2);
        double x = Math.cos(lat1)*Math.sin(lat2) - Math.sin(lat1)*Math.cos(lat2)*Math.cos(lon2-lon1);
        double bearing = Math.toDegrees(Math.atan2(y, x));

        bearing = ( bearing + 360 ) % 360;
        return bearing;
    }


    public boolean calcAngle() {
        //컴퍼스 센서 방위각 계산
        //시선 방위각 계산
        //목적지 방위각 계산
        if(currentPlace!=null) {
            double dX = currentPlace.getX();
            double dY = currentPlace.getY();

            //destinationAngle = getAngle(currentX - dX, currentY - dY);
            destinationAngle = getAngle(currentX, currentY, dX, dY);
            return true;
        }
        return false;
    }


    //네비게이션 종료
    public void terminate() {
        isStarted = false;
        sSync = false;
        dSync = false;
    }
}
