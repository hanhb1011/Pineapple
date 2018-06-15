package org.androidtown.pineapple_android;

import android.bluetooth.BluetoothAdapter;
import android.bluetooth.BluetoothHeadset;
import android.bluetooth.BluetoothSocket;
import android.content.Context;
import android.view.View;

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
    private BluetoothAdapter bluetoothAdapter;
    private MainHandler handler;


    public BluetoothThread(Context context, BluetoothAdapter bluetoothAdapter, BluetoothSocket bluetoothSocket) {
        this.context = context;
        this.bluetoothAdapter = bluetoothAdapter;
        this.bluetoothSocket = bluetoothSocket;
        handler = new MainHandler(context);
    }

    @Override
    public void run() {
        //다른 액티비티는 destroyed 되기 때문에 메인액티비티에서 실행시켜야함

        while(true) {
            try {
                sleep(1000);
                if(bluetoothAdapter.isEnabled()) {
                    android.os.Message message = new android.os.Message();
                    message.what = GroupConstants.MSG_BLUETOOTH_CONNECTED;
                    handler.sendMessage(message);
                } else {
                    android.os.Message message = new android.os.Message();
                    message.what = GroupConstants.MSG_BLUETOOTH_DISCONNECTED;
                    handler.sendMessage(message);
                }

                byte[] buffer = new byte[256];
                int bytes;
                String inputString;

                DataInputStream dataInputStream = new DataInputStream(bluetoothSocket.getInputStream());

                bytes = dataInputStream.read(buffer);
                inputString = new String(buffer,0, bytes); // 아두이노로부터 입력받은 스트링

                /*temp (1초마다 토스트메시지출력)
                android.os.Message message = new android.os.Message();
                message.what = GroupConstants.MSG_TOAST;
                message.obj = inputString;
                */

                if(inputString.length() > 0) {
                    //handler.sendMessage(message); // temp
                    //입력받을 경우 STT 메서드 실행

                    System.out.println(inputString);
                    android.os.Message message = new android.os.Message();
                    message.what = GroupConstants.MSG_INPUT_SPEECH;
                    handler.sendMessage(message);

                }

            } catch (Exception e){
                e.printStackTrace();
            }

        }

    }


}
