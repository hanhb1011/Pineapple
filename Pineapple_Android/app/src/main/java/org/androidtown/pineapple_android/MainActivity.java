package org.androidtown.pineapple_android;

import android.content.Intent;
import android.os.Bundle;
import android.speech.RecognizerIntent;
import android.support.v7.app.AppCompatActivity;
import android.util.Pair;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import java.util.ArrayList;
import java.util.Calendar;

import static org.androidtown.pineapple_android.GroupConstants.REQ_CODE_SPEECH_INPUT;

public class MainActivity extends AppCompatActivity {

    private View micImageView;
    private TextView speechTextView;
    private TextView responseTextView;
    public TextView testTextView;
    private BluetoothHelper bluetoothHelper;
    private VoiceRecognizer voiceRecognizer;
    public Tmap tmap;
    public MainHandler mainHandler;

    public static ArrayList<Message> messageList;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        init();
        bindView();
        setView();
        testInit();//테스트

    }

    private void testInit() {
        //아두이노에 데이터 전송
        final EditText testEditText = findViewById(R.id.test_et);
        Button testButton = findViewById(R.id.test_btn);

        testButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if(testEditText.getText().toString().length()>0){

                    try {
                        bluetoothHelper.sendData(Integer.valueOf(testEditText.getText().toString().trim()));
                    } catch (Exception e) {
                        Toast.makeText(MainActivity.this, "TYPE ERROR", Toast.LENGTH_SHORT).show();
                    }

                }
            }
        });

        //액티비티 전환
        addMessageToList(GroupConstants.BOT_MESSAGE, "ㅁㅈ럊머ㅑㄹㅈㅁㄹ");
        addMessageToList(GroupConstants.MY_MESSAGE, "ㅁasㅁㄴㅇㅎㅁㅇㄶ");
        addMessageToList(GroupConstants.BOT_MESSAGE, "ㅁㅈ럊머ㅑㄹㅈㅁㄹ");
        addMessageToList(GroupConstants.MY_MESSAGE, "ㅇㅎㄷㅎ21ㄷㅇ");
        addMessageToList(GroupConstants.BOT_MESSAGE, "ㅁㅈ럊머ㅑㄹㅈㅁㄹ");
        addMessageToList(GroupConstants.MY_MESSAGE, "ㅁasㅁㄴㅇㅎㅁㅇㄶ");
        addMessageToList(GroupConstants.BOT_MESSAGE, "ㅁㅈ럊머ㅑㄹㅈㅁㄹ");
        addMessageToList(GroupConstants.MY_MESSAGE, "ㅇㅎㄷㅎ21ㄷㅇ");

        findViewById(R.id.activity_btn).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(MainActivity.this, ChatLogActivity.class);
                startActivity(intent);
            }
        });

    }

    private void init() {
        //핸들러 초기화
        mainHandler = new MainHandler(this);

        //블루투스 디바이스 페어링
        bluetoothHelper = new BluetoothHelper(this);
        bluetoothHelper.connect();

        //음성인식 기능 초기화
        voiceRecognizer = new VoiceRecognizer(this);

        //Tmap 초기화
        tmap = new Tmap(this);

        //message List 초기화
        messageList = new ArrayList<>();
    }


    //뷰 바인딩
    private void bindView() {
        micImageView = findViewById(R.id.mic_iv);
        speechTextView = findViewById(R.id.speech_tv);
        responseTextView = findViewById(R.id.response_tv);
        testTextView = findViewById(R.id.test_tv);


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
            //음성인식이 완료되었을 때
            case REQ_CODE_SPEECH_INPUT :
                //check validity
                if(resultCode!=RESULT_OK || data == null)
                    return;

                ArrayList<String> result = data
                        .getStringArrayListExtra(RecognizerIntent.EXTRA_RESULTS);

                //입력, 응답 스트링 정의, update UI : 스트링빌더로 성능향상 유도
                StringBuilder speech = new StringBuilder("\"");
                if(result.size()>0)
                    speech.append(result.get(0)).append("\"");
                speechTextView.setText(speech.toString());

                //메시지 리스트에 메시지 추가 (채팅 로그)
                addMessageToList(GroupConstants.MY_MESSAGE, speech.toString());

                StringBuilder response = new StringBuilder("\"");

                //문장을 처리해서 의도와 목적지를 파악한다.
                Pair<Integer, String> resultPair = voiceRecognizer.process(result.get(0));

                switch (resultPair.first) {
                    case GroupConstants.INTENTION_DESTINATION :

                        response.append(resultPair.second);
                        response.append(" 안내를 시작합니다.\"");
                        tmap.getPOIItem(resultPair.second);

                        //TODO 안내

                        break;
                    case GroupConstants.INTENTION_CANCELLATION :
                        response.append("안내를 중단합니다.\"");

                        //TODO 안내 중단

                        break;
                    case GroupConstants.INTENTION_INVALID :
                        response.append("다시 한 번 말씀해주세요.\"");


                        break;
                }


                //응답 출력
                responseTextView.setText(response.toString());

                //메시지 리스트에 응답메시지 추가
                addMessageToList(GroupConstants.BOT_MESSAGE, response.toString());


        }

    }

    //채팅 로그에 띄울 ArrayList<Message>에 Message를 add한다.
    public void addMessageToList(int messageType, String content){

        Calendar calendar = Calendar.getInstance();
        long currentTimeStamp = calendar.getTimeInMillis();
        Message message = new Message(messageType, content, currentTimeStamp);
        messageList.add(message);

    }
}
