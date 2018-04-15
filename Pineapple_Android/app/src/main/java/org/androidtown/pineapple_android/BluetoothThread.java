package org.androidtown.pineapple_android;

import android.content.Context;

/**
 * Created by hanhb on 2018-04-15.
 */

/*


 */

public class BluetoothThread implements Runnable {

    private Context context;

    BluetoothThread(Context context) {
        this.context = context;


    }


    @Override
    public void run() {
        //다른 액티비티는 destroyed 되기 때문에 메인액티비티에서 실행시켜야함
        if((context instanceof MainActivity))
            return;

        while(true) {

            if(isConneced()) {


            } else {


            }

        }

    }


    private boolean isConneced() {

        //TODO 블루투스 연결 확인 후 True / False 리턴
        return false;
    }

}
