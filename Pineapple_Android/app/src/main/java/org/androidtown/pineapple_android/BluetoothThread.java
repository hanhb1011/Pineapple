package org.androidtown.pineapple_android;

import android.bluetooth.BluetoothSocket;
import android.content.Context;

import java.io.DataInputStream;

/**
 * Created by hanhb on 2018-04-15.
 */

/*
    블루투스 통신으로부터 데이터를 주기적으로 받는 스레드
 */

public class BluetoothThread extends Thread{

    private Context context;
    private BluetoothSocket bluetoothSocket;
    private MainHandler handler;

    public boolean isOn= true;

    public BluetoothThread(Context context, BluetoothSocket bluetoothSocket) {
        this.context = context;
        this.bluetoothSocket = bluetoothSocket;
        handler = new MainHandler(context);
    }


    @Override
    public void run() {
        //다른 액티비티는 destroyed 되기 때문에 메인액티비티에서 실행시켜야함
        if(!bluetoothSocket.isConnected()) {
            return;
        }

        while(isOn) {

            try {
                byte[] buffer = new byte[256];
                int bytes;
                String str;

                DataInputStream dataInputStream = new DataInputStream(bluetoothSocket.getInputStream());
                bytes = dataInputStream.read(buffer);
                str = new String(buffer,0, bytes); //방위각


                //temp (1초마다 토스트메시지출력)
                android.os.Message message = new android.os.Message();
                message.what = GroupConstants.MSG_TOAST;
                message.obj = str;

                if(str.length()>0)
                    handler.sendMessage(message);

                //end of temp
                sleep(1000);


            } catch (Exception e){
                e.printStackTrace();
            }

        }

    }


}
