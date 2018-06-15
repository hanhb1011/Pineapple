package org.androidtown.pineapple_android;

import android.content.Context;
import android.os.Handler;
import android.os.Message;
import android.view.View;
import android.widget.Toast;

import com.skt.Tmap.TMapPOIItem;

import org.androidtown.pineapple_android.Util.Navigation;

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
                        .append("주소 : ").append(item.getPOIAddress().replace("null","")).append("\n")
                        .append("거리 : ").append(item.getDistance(((MainActivity)context).tmap.getCurrentTMapPoint())).append("\n");

                Navigation navi = Navigation.getInstance();

                navi.terminate();

                ((MainActivity)context).testTextView.setText(str);
                navi.setdSync(true);
                navi.setEndX(item.getPOIPoint().getLongitude());
                navi.setEndY(item.getPOIPoint().getLatitude());
                if(navi.isFirstLocation()){
                    navi.setsSync(true);
                    ((MainActivity)context).loadAnswer(navi.getEndX(),navi.getEndY());
                }else{
                    ((MainActivity)context).getMyLocation();
                }
                break;

            case GroupConstants.MSG_TEST_BT :
                String s = (String) msg.obj;
                ((MainActivity)context).testTextView.append(s+"\n");
                break;
            case GroupConstants.MSG_BLUETOOTH_CONNECTED :
                if(MainActivity.bluetoothImageView != null) {
                    MainActivity.bluetoothImageView.setVisibility(View.VISIBLE);
                }
                break;

            case GroupConstants.MSG_BLUETOOTH_DISCONNECTED :
                if(MainActivity.bluetoothImageView != null) {
                    MainActivity.bluetoothImageView.setVisibility(View.GONE);
                }
                break;

            case GroupConstants.MSG_INPUT_SPEECH :
                ((MainActivity)context).inputSpeechProcess();
                break;


        }


    }
}
