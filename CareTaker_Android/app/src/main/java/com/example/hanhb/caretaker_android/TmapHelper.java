package com.example.hanhb.caretaker_android;

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

public class TmapHelper {

    private TMapData tMapData;
    private Context context;
    private TMapView tMapView;
    private TMapTapi tmaptapi;


    public TmapHelper(Context context) {
        this.context = context;
        tMapView = new TMapView(context);
        String tmapKey = context.getResources().getString(R.string.TMAP_API_KEY);
        tMapView.setSKTMapApiKey(tmapKey);
        tmaptapi = new TMapTapi(context);
        tmaptapi.setSKTMapAuthentication(tmapKey);
        tMapData = new TMapData();
    }

    

}
