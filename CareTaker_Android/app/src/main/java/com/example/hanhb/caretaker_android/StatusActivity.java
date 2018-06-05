package com.example.hanhb.caretaker_android;

import android.app.Activity;
import android.content.Context;
import android.os.Bundle;
import android.os.Message;
import android.view.View;
import android.widget.Button;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.skt.Tmap.TMapData;
import com.skt.Tmap.TMapMarkerItem;
import com.skt.Tmap.TMapPoint;
import com.skt.Tmap.TMapTapi;
import com.skt.Tmap.TMapView;

import org.xml.sax.SAXException;

import java.io.IOException;

import javax.xml.parsers.ParserConfigurationException;

public class StatusActivity extends Activity {

    private Button backButton;
    public TextView statusTextView;
    private LinearLayout tmapLayout;

    private TMapData tMapData;
    private TMapView tMapView;
    private TMapTapi tmaptapi;
    private MyHandler myHandler;
    private boolean doUpdate = true;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_status);

        validate();
        initView();
        initTmap();
        setView();

    }

    private void validate() {
        if(MainActivity.user == null) {
            Toast.makeText(this, "인터넷 연결 상태를 확인해 주십시오.", Toast.LENGTH_SHORT).show();
            finish();
        }
    }

    private void initTmap() {

        tMapView = new TMapView(this);
        String tmapKey = getResources().getString(R.string.TMAP_API_KEY);
        tMapView.setSKTMapApiKey(tmapKey);
        tMapView.setCenterPoint(MainActivity.user.getCurrentLongitude(), MainActivity.user.getCurrentLatitude());
        tmapLayout.addView(tMapView);

        tmaptapi = new TMapTapi(this);
        tmaptapi.setSKTMapAuthentication(tmapKey);
        tMapData = new TMapData();
        myHandler = new MyHandler(this);



    }

    private void setView() {

        new Thread(){

            @Override
            public void run() {
                super.run();

                while(doUpdate) {
                    try {
                        if(MainActivity.user == null){

                        }

                        String address = tMapData.convertGpsToAddress(MainActivity.user.getCurrentLatitude(),
                                MainActivity.user.getCurrentLongitude());
                        if (address == null) {
                            return;
                        }
                        StringBuilder stringBuilder = new StringBuilder();
                        stringBuilder.append("현재위치: ").append(address).append("\n")
                                .append("\n현재위도: ").append(MainActivity.user.getCurrentLatitude())
                                .append("\n현재경도: ").append(MainActivity.user.getCurrentLongitude());
                        Message message = new Message();
                        message.what = 1;
                        message.obj = stringBuilder.toString();
                        myHandler.sendMessage(message);

                        tMapView.removeAllMarkerItem();
                        TMapPoint tpoint = new TMapPoint(MainActivity.user.getCurrentLatitude(), MainActivity.user.getCurrentLongitude());
                        TMapMarkerItem tItem = new TMapMarkerItem();

                        tItem.setTMapPoint(tpoint);
                        tItem.setName("보행자 위치");
                        tItem.setVisible(TMapMarkerItem.VISIBLE);

                        tMapView.addMarkerItem("location", tItem);
                        //tMapView.setCenterPoint(MainActivity.user.getCurrentLongitude(), MainActivity.user.getCurrentLongitude(), true);
                        tMapView.refreshMap();

                    } catch (IOException e) {
                        e.printStackTrace();
                    } catch (ParserConfigurationException e) {
                        e.printStackTrace();
                    } catch (SAXException e) {
                        e.printStackTrace();
                    }
                    try {
                        sleep(1000);
                    } catch (InterruptedException e) {
                        e.printStackTrace();
                    }
                }
            }
        }.start();

    }

    private void initView() {

        //액션바 숨김
        try {
            getActionBar().hide();
        } catch (Exception e){
            e.printStackTrace();
        }

        backButton = findViewById(R.id.back_btn_in_status);
        backButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                finish();
            }
        });

        statusTextView = findViewById(R.id.status_tv);
        tmapLayout = findViewById(R.id.tmap_layout);


    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        doUpdate = false;

    }
}
