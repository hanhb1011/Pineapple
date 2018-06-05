package com.example.hanhb.caretaker_android;

import android.content.Context;
import android.os.Message;

import com.skt.Tmap.TMapData;
import com.skt.Tmap.TMapTapi;
import com.skt.Tmap.TMapView;

import org.xml.sax.SAXException;

import java.io.IOException;

import javax.xml.parsers.ParserConfigurationException;

/**
 * Created by hanhb on 2018-06-03.
 */

public class TmapHelper {

    private TMapData tMapData;
    private Context context;
    private TMapView tMapView;
    private TMapTapi tmaptapi;
    private MyHandler myHandler;


    public TmapHelper(Context context) {
        this.context = context;
        tMapView = new TMapView(context);
        String tmapKey = context.getResources().getString(R.string.TMAP_API_KEY);
        tMapView.setSKTMapApiKey(tmapKey);
        tmaptapi = new TMapTapi(context);
        tmaptapi.setSKTMapAuthentication(tmapKey);
        tMapData = new TMapData();
        myHandler = new MyHandler(context);
    }


    //위도와 경도 정보를 받은 뒤 UI를 업데이트한다
    public void getCurrentAddressAndUpdateUI(final double latitude, final double longitude) {
        new Thread(){

            @Override
            public void run() {
                super.run();

                try {
                    String address = tMapData.convertGpsToAddress(latitude,longitude);
                    if(address == null) {
                        return;
                    }

                    Message message = new Message();
                    message.what = 1;
                    message.obj = address;
                    myHandler.sendMessage(message);

                } catch (IOException e) {
                    e.printStackTrace();
                } catch (ParserConfigurationException e) {
                    e.printStackTrace();
                } catch (SAXException e) {
                    e.printStackTrace();
                }


            }
        }.start();


    }

}
