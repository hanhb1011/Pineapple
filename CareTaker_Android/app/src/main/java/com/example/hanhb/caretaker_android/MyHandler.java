package com.example.hanhb.caretaker_android;

import android.content.Context;
import android.os.Handler;
import android.os.Message;
import android.widget.Toast;

import com.skt.Tmap.TMapPOIItem;

/**
 * Created by hanhb on 2018-04-11.
 */

//메인액티비티 UI 컨트롤용 핸들러
public class MyHandler extends Handler {

    Context context;
    MyHandler(Context context) {
        this.context = context;
    }

    @Override
    public void handleMessage(Message msg) {
        super.handleMessage(msg);

        switch(msg.what) {

            case 1 :
                String status = (String)msg.obj;
                ((StatusActivity)context).statusTextView.setText(status);
                break;

            case 2 :


                break;

        }


    }
}
