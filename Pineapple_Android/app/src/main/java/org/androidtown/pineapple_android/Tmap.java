package org.androidtown.pineapple_android;

import android.content.Context;
import android.os.Message;

import com.skt.Tmap.TMapData;
import com.skt.Tmap.TMapPOIItem;
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
        tMapView.setSKTMapApiKey("be4491c4-59e5-47d1-8760-1d59e552b03a");
        tmaptapi = new TMapTapi(context);
        tmaptapi.setSKTMapAuthentication("be4491c4-59e5-47d1-8760-1d59e552b03a");

        tMapData = new TMapData();

    }

    //키워드를 입력받고 가장 유사도가 높은 결과를 출력한다.
    public void getPOIItem(final String keyword) {
        if(keyword == null || keyword.length()==0)
            return;


        // Thread로 비동기 처리 구현
        new Thread() {
            @Override
            public void run() {
                try {
                    ArrayList<TMapPOIItem> POIItems= tMapData.findAllPOI(keyword, 1);
                    for(TMapPOIItem item : POIItems ){
                        Message message = new Message();
                        message.what = MainActivity.MSG_TEST;
                        message.obj = item;
                        ((MainActivity)context).mainHandler.sendMessage(message);

//                        ((MainActivity)context).mainHandler.sendMessage(message);
                        return;
                    }

                    //목적지를 찾을 수 없습니다.

                } catch (Exception e) {
                    Message message = new Message();
                    message.what = MainActivity.MSG_TOAST;
                    message.obj = "목적지를 찾을 수 없습니다.";

                    ((MainActivity)context).mainHandler.sendMessage(message);
                    e.printStackTrace();
                }

            }
        }.start();


    }


}
