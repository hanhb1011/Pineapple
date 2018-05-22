package org.androidtown.pineapple_android.Model;

/**
 * Created by MSI on 2018-04-24.
 */

public class FindTheWayRequest {
    String version;
    String startX;
    String startY;
    String angle;
    String speed;
    String endPoiId;
    String endRpFlag;
    String endX;
    String endY;
    String reqCoordType;
    String gpsTime;
    String startName;
    String endName;
    String searchOption;
    String resCoordType;

    public FindTheWayRequest(String version, String startX, String startY, String angle, String speed, String endPoiId, String endRpFlag, String endX, String endY, String reqCoordType, String gpsTime, String startName, String endName, String searchOption, String resCoordType) {
        this.version = version;
        this.startX = startX;
        this.startY = startY;
        this.angle = angle;
        this.speed = speed;
        this.endPoiId = endPoiId;
        this.endRpFlag = endRpFlag;
        this.endX = endX;
        this.endY = endY;
        this.reqCoordType = reqCoordType;
        this.gpsTime = gpsTime;
        this.startName = startName;
        this.endName = endName;
        this.searchOption = searchOption;
        this.resCoordType = resCoordType;
    }
}
