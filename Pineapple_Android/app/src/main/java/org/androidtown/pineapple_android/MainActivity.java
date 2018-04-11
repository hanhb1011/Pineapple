package org.androidtown.pineapple_android;

import android.content.Intent;
import android.os.Bundle;
import android.speech.RecognizerIntent;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.widget.TextView;
import android.widget.Toast;

import java.util.ArrayList;

public class MainActivity extends AppCompatActivity {

    private View micImageView;
    private TextView speechTextView;
    private BluetoothHelper bluetoothHelper;
    private VoiceRecognizer voiceRecognizer;

    public static final int REQ_CODE_SPEECH_INPUT = 100;
    public static final int REQ_CODE_BLUETOOTH_CONN= 101;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        initBlueTooth(); //블루투스 디바이스 페어링
        initVoiceRecoder(); //음성인식 기능 초기화
        bindView();
        setView();

    }

    private void initVoiceRecoder() {
        voiceRecognizer = new VoiceRecognizer(this);

    }

    private void initBlueTooth() {
        bluetoothHelper = new BluetoothHelper(this);
        bluetoothHelper.connect();

    }

    //뷰 바인딩
    private void bindView() {
        micImageView = findViewById(R.id.mic_iv);
        speechTextView = findViewById(R.id.sppech_tv);

    }

    //뷰 초기화
    private void setView() {

        getSupportActionBar().hide(); //액션바 숨김

        micImageView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                //인터넷 연결을 확인하고, 연결이 되어있으면 서비스 실행
                if(voiceRecognizer.isConnectedToInternet()) {
                    voiceRecognizer.inputSpeech(); //음성 입력을 받는다.
                } else {
                    //연결이 되어있지 않으면 토스트 메시지 출력
                    Toast.makeText(MainActivity.this, R.string.internet_connection_failed, Toast.LENGTH_SHORT).show();
                }
            }
        });

    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        switch (requestCode) {
            case REQ_CODE_SPEECH_INPUT :
                //is valid?
                if(resultCode!=RESULT_OK || data == null)
                    return;

                ArrayList<String> result = data
                        .getStringArrayListExtra(RecognizerIntent.EXTRA_RESULTS);

                //스트링버퍼로 성능향상 유도
                StringBuffer sb = new StringBuffer("\"");
                if(result.size()>0)
                    sb.append(result.get(0)).append("\"");
                speechTextView.setText(sb.toString());

                break;

        }

    }
}
