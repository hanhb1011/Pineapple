package org.androidtown.pineapple_android.Util;

import okhttp3.MediaType;
import okhttp3.RequestBody;

/**
 * Created by MSI on 2018-04-30.
 */

public class NavigationBody {
    //String body = "version=1&startX=126.9823439963945&startY=37.56461982743129&angle=1&speed=60&endPoiId=334852&endRpFlag=8&endX=126.98031634883303&endY=37.57007473965354&reqCoordType=WGS84GEO&gpsTime=15000&startName=%EC%B6%9C%EB%B0%9C&endName=%EB%B3%B8%EC%82%AC&searchOption=0&resCoordType=WGS84GEO";
    /* 필수 인자
    http://tmapapi.sktelecom.com/main.html#webservice/docs/tmapRoutePedestrianDoc
    startX
    startY
    endX
    endY
    startName=%EC%B6%9C%EB%B0%9C
    endName=%EB%B3%B8%EC%82%AC
    version=1
     */
    //TMapPoint point = tmapgps.getLocation();

    String body = "version=1&startName=%EC%B6%9C%EB%B0%9C&endName=%EB%B3%B8%EC%82%AC";

    public void setEndPoint(double endX, double endY) {
        body += "&endX=" + endX + "&endY=" + endY;
    }

    public void setStartPoint(double startX, double startY) {
        body += "&startX=" + startX + "&startY=" + startY;
    }

    public RequestBody getRequestBody(){
        RequestBody requestBody = RequestBody.create(MediaType.parse("application/x-www-form-urlencoded"),body);
        return requestBody;
    }

}
