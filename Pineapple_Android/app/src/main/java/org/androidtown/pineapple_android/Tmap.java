package org.androidtown.pineapple_android;

import android.content.Context;
import android.os.Message;

import com.skt.Tmap.TMapData;
import com.skt.Tmap.TMapPOIItem;
import com.skt.Tmap.TMapPoint;
import com.skt.Tmap.TMapTapi;
import com.skt.Tmap.TMapView;

import java.util.ArrayList;

/**
 * Created by hanhb on 2018-04-11.
 */

public class Tmap {

    private TMapData tMapData;
    private Context context;
    private TMapView tMapView;
    private TMapTapi tmaptapi;

    Tmap(Context context) {
        this.context = context;
        tMapView = new TMapView(context);
        String tmapKey = context.getResources().getString(R.string.TMAP_API_KEY);
        tMapView.setSKTMapApiKey(tmapKey);
        tmaptapi = new TMapTapi(context);
        tmaptapi.setSKTMapAuthentication(tmapKey);
        tMapData = new TMapData();


    }

    //키워드를 입력받고 가장 유사도가 높은 결과를 출력한다.
    public void getPOIItem(final String keyword) {
        if(keyword == null || keyword.length()==0)
            return;

        // Thread로 통신을 비동기 처리
        new Thread() {
            @Override
            public void run() {
                try {
                    ArrayList<TMapPOIItem> POIItems= tMapData.findAllPOI(keyword);
                    double minDistance = Double.MAX_VALUE;
                    TMapPOIItem minTMapPOIItem = null;

                    for(TMapPOIItem item : POIItems ){
                        //가까운 것부터 찾는다.
                        double distanceNow = item.getDistance(getCurrentTMapPoint());
                        if(distanceNow < minDistance) {
                            minDistance = distanceNow;
                            minTMapPOIItem = item;
                        }

                    }

                    //찾은 위치 정보를 반환
                    if(minTMapPOIItem !=null) {
                        Message message = new Message();
                        message.what = GroupConstants.MSG_TEST;
                        message.obj = minTMapPOIItem;
                        ((MainActivity)context).mainHandler.sendMessage(message);
                    }

                    //목적지를 찾을 수 없습니다.
                } catch (Exception e) {
                    Message message = new Message();
                    message.what = GroupConstants.MSG_TOAST;
                    message.obj = context.getResources().getString(R.string.cannot_find_destination);

                    ((MainActivity)context).mainHandler.sendMessage(message);
                    e.printStackTrace();
                }

            }
        }.start();


    }

    //현재 위치 반환
    public TMapPoint getCurrentTMapPoint() {
        if(tMapView == null)
            return null;
        try {
            //TODO 현재 위치 반환하도록 해야 함.
            return tMapView.getLocationPoint();
        } catch (Exception e) {
            e.printStackTrace();
            return null;
        }

    }
}
