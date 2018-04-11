package org.androidtown.pineapple_android;

import android.content.ActivityNotFoundException;
import android.content.Context;
import android.content.Intent;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.net.Uri;
import android.os.Bundle;
import android.speech.RecognizerIntent;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.widget.TextView;
import android.widget.Toast;

import java.util.ArrayList;
import java.util.Locale;

public class MainActivity extends AppCompatActivity {

    private View micImageView;
    private TextView speechTextView;
    private BluetoothHelper bluetoothHelper;

    public static final int REQ_CODE_SPEECH_INPUT = 100;
    public static final int REQ_CODE_BLUETOOTH_CONN= 101;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        bindView();
        setView();
        initBlueTooth(); //블루투스 디바이스 페어링

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
                if(isConnectedToInternet()) {
                    inputSpeech(); //음성 입력을 받는다.
                } else {
                    //연결이 되어있지 않으면 토스트 메시지 출력
                    Toast.makeText(MainActivity.this, R.string.internet_connection_failed, Toast.LENGTH_SHORT).show();
                }
            }
        });

    }

    //인터넷에 연결되어있는지 확인.
    private boolean isConnectedToInternet() {
        ConnectivityManager cm = (ConnectivityManager) getSystemService(Context.CONNECTIVITY_SERVICE);
        NetworkInfo net = cm.getActiveNetworkInfo();
        if (net!=null && net.isAvailable() && net.isConnected()) {
            return true;
        } else {
            return false;
        }
    }

    //음성 입력을 받는다
    private void inputSpeech() {
        Intent intent = new Intent(RecognizerIntent.ACTION_RECOGNIZE_SPEECH);
        intent.putExtra(RecognizerIntent.EXTRA_LANGUAGE_MODEL, RecognizerIntent.LANGUAGE_MODEL_FREE_FORM);

        intent.putExtra(RecognizerIntent.EXTRA_LANGUAGE, Locale.getDefault());
        intent.putExtra(RecognizerIntent.EXTRA_PROMPT, getString(R.string.speech_prompt));

        try {
            startActivityForResult(intent, REQ_CODE_SPEECH_INPUT);

        } catch (ActivityNotFoundException a) {

            //음성인식 지원을 하지 않는 경우, 구글 서비스 업데이트를 유도한다
            String appPackageName = "com.google.android.googlequicksearchbox";
            Toast.makeText(getApplicationContext(), getString(R.string.speech_not_supported), Toast.LENGTH_SHORT).show();
            Intent browserIntent = new Intent(Intent.ACTION_VIEW,   Uri.parse("https://market.android.com/details?id="+appPackageName));
            startActivity(browserIntent);
        }
        
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        switch (requestCode) {
            case REQ_CODE_SPEECH_INPUT :
                //validate
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
