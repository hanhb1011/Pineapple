package org.androidtown.pineapple_android;

import android.app.Activity;
import android.bluetooth.BluetoothAdapter;
import android.bluetooth.BluetoothDevice;
import android.bluetooth.BluetoothSocket;
import android.content.Context;
import android.content.Intent;
import android.view.View;
import android.widget.Toast;

import java.io.IOException;
import java.util.Set;
import java.util.UUID;

/**
 * Created by hanhb on 2018-04-10.
 */

// 스마트 지팡이를 연결시켜주는 클래스
public class BluetoothHelper {

    private Context context;
    private BluetoothAdapter bluetoothAdapter;
    private BluetoothDevice bluetoothDevice;
    private BluetoothSocket bluetoothSocket;
    private BluetoothThread bluetoothThread;

    //생성자에서 context를 받는다
    BluetoothHelper(Context context){
        this.context = context;
    }

    public void connect(){
        if(bluetoothAdapter == null) {
            bluetoothAdapter = BluetoothAdapter.getDefaultAdapter();
        }

        //블루투스 어댑터가 존재하지 않을 경우 메시지 띄우고 리턴
        if(bluetoothAdapter == null) {
            Toast.makeText(context, R.string.bluetooth_not_support, Toast.LENGTH_SHORT).show();
            return;
        }


        //블루투스 어댑터가 비활성화 되어있을 경우 요청 액티비티 띄워서 활성화 유도
        if(!bluetoothAdapter.isEnabled()) {
            Intent enableAdapter = new Intent(BluetoothAdapter.ACTION_REQUEST_ENABLE);
            Toast.makeText(context, R.string.bluetooth_disabled, Toast.LENGTH_SHORT).show();
            ((Activity)context).startActivityForResult(enableAdapter, GroupConstants.REQ_CODE_BLUETOOTH_CONN);
            return;
        }

        Set<BluetoothDevice> bondedDevices = bluetoothAdapter.getBondedDevices();
        boolean found = false;

        //TODO : strings.xml에 맥주소 입력하기, PORT_UUID 입력하기
        if(bondedDevices.isEmpty()) {
            Toast.makeText(context,R.string.bluetooth_req_paring,Toast.LENGTH_SHORT).show();
        } else {
            for (BluetoothDevice device : bondedDevices) {

                if(device.getName().equals(context.getResources().getString(R.string.DEVICE_NAME))){

                    bluetoothDevice=device; //device is an object of type BluetoothDevice
                    found = true;
                    break;

                }
            }
        }

        //기기를 찾을 수 없으면 리턴
        if(!found) {
            Toast.makeText(context, R.string.bluetooth_not_found, Toast.LENGTH_SHORT).show();

            return;
        }

        //마지막으로 블루투스 소켓 통신 활성화
        UUID PORT_UUID = UUID.fromString(context.getResources().getString(R.string.UUID));
        try {

            bluetoothSocket = bluetoothDevice.createRfcommSocketToServiceRecord(PORT_UUID);
            bluetoothSocket.connect();

            //블루투스 소켓이 연결상태면 블루투스쓰레드 실행
            if(bluetoothSocket.isConnected()) {
                bluetoothThread = new BluetoothThread(context, bluetoothSocket);
                bluetoothThread.start();

                //블루투스 통신 성공을 알리기 위해 블루투스 이미지뷰 Visibility 바꾸기
                if(MainActivity.bluetoothImageView != null){
                    MainActivity.bluetoothImageView.setVisibility(View.VISIBLE);
                }
            }

        } catch (IOException e){
            Toast.makeText(context, e.getLocalizedMessage(), Toast.LENGTH_SHORT).show();
        }


    }

    //Integer 형태로 데이터 전송
    public void sendData(int inputData){
        if(bluetoothSocket == null || !bluetoothSocket.isConnected()) {
            Toast.makeText(context, R.string.bluetooth_device_not_found, Toast.LENGTH_SHORT).show();
            return;
        }
        //outputStream을 통해 데이터 전송
        try {
            bluetoothSocket.getOutputStream().write(inputData);
        } catch (IOException e){
            e.printStackTrace();
        }

    }

    //byte[] 형태로 데이터 전송
    public void sendData(byte[] inputData){
        if(bluetoothSocket == null || !bluetoothSocket.isConnected()) {
            Toast.makeText(context, R.string.bluetooth_not_found, Toast.LENGTH_SHORT).show();
            return;
        }

        //outputStream을 통해 데이터 전송
        try {
            bluetoothSocket.getOutputStream().write(inputData);
        } catch (IOException e){
            e.printStackTrace();
        }

    }
}
