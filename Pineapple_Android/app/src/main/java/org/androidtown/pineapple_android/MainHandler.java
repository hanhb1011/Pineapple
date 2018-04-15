package org.androidtown.pineapple_android;

import android.content.Context;
import android.os.Handler;
import android.os.Message;
import android.widget.Toast;

import com.skt.Tmap.TMapPOIItem;

/**
 * Created by hanhb on 2018-04-11.
 */

//메인액티비티 UI 컨트롤용 핸들러
public class MainHandler extends Handler {

    Context context;
    MainHandler(Context context) {
        this.context = context;
    }

    @Override
    public void handleMessage(Message msg) {
        super.handleMessage(msg);

        switch(msg.what) {

            case GroupConstants.MSG_TOAST :
                Toast.makeText(context, (String)msg.obj, Toast.LENGTH_SHORT).show();
                break;

            case GroupConstants.MSG_TEST :
                StringBuilder str = new StringBuilder();
                TMapPOIItem item = (TMapPOIItem) msg.obj;

                str.append("장소명 : ").append(item.getPOIName()).append("\n")
                        .append("주소 : ").append(item.getPOIAddress()).append("\n")
                        .append("위도 : ").append(item.getPOIPoint().getLatitude()).append("\n")
                        .append("경도 : ").append(item.getPOIPoint().getLongitude()).append("\n")
                        .append("거리 : ").append(item.getDistance(((MainActivity)context).tmap.getCurrentTMapPoint())).append("\n")
                        .append("현재위도 : ").append(((MainActivity)context).tmap.getCurrentTMapPoint().getLatitude()).append("\n")
                        .append("현재경도 : ").append(((MainActivity)context).tmap.getCurrentTMapPoint().getLongitude()).append("\n");

                ((MainActivity)context).testTextView.setText(str);
                break;

            case GroupConstants.MSG_BLUETOOTH_CONNECTED :
                //TODO 이미지뷰 전환
                break;

            case GroupConstants.MSG_BLUETOOTH_DISCONNECTED :
                //TODO 이미지뷰 전환
                break;


        }


    }
}
