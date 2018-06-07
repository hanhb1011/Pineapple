package com.example.hanhb.caretaker_android;

import android.app.Activity;
import android.content.ActivityNotFoundException;
import android.content.Context;
import android.content.Intent;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.speech.RecognizerIntent;
import android.speech.tts.TextToSpeech;
import android.support.annotation.NonNull;
import android.util.Pair;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.util.ArrayList;
import java.util.Locale;
import java.util.Map;

public class MainActivity extends Activity {
    public static String key;
    public static User user;
    private FirebaseDatabase firebaseDatabase;
    private DatabaseReference userRef;
    private Button firstButton, secondButton, thirdButton;
    public TextToSpeech tts;
    public static final int REQ_CODE_SPEECH_INPUT = 1000;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        initView();
        init();

    }

    //클래스 초기화
    private void init() {
        if(key == null){
            Toast.makeText(this, "인증 오류", Toast.LENGTH_SHORT).show();
            finish();
        }

        firebaseDatabase = FirebaseDatabase.getInstance();
        userRef = firebaseDatabase.getReference().child("user").child(key);

        //서버로부터 사용자의 정보를 주기적으로 불러온다.
        userRef.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                MainActivity.user = dataSnapshot.getValue(User.class);

                if(user == null) {
                    return;
                }

            }

            @Override
            public void onCancelled(DatabaseError databaseError) {

            }
        });

        //TextToSpeech 초기화
        tts = getTTSInstance();
    }

    private void initView() {

        //액션바 숨김
        try {
            getActionBar().hide();
        } catch (Exception e){
            e.printStackTrace();
        }

        firstButton = findViewById(R.id.first_btn);
        secondButton = findViewById(R.id.second_btn);
        thirdButton = findViewById(R.id.third_btn);


        //사용자 상태 보기
        firstButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(MainActivity.this, StatusActivity.class);
                startActivity(intent);


            }
        });

        //음성메시지 보내기
        secondButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if(isConnectedToInternet()) {

                    inputSpeech(); //google STT를 통해 음성 입력을 받는다.

                } else {
                    Toast.makeText(MainActivity.this, "인터넷 연결을 확인해주세요.", Toast.LENGTH_SHORT).show();
                }

            }
        });

        //경로탐색 기록 보기
        thirdButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(MainActivity.this, LogActivity.class);
                startActivity(intent);


            }
        });
    }

    //음성 출력
    public void speak(String text){
        if(tts==null)
            return;

        //음성 출력 (Minimum SDK version is 21)
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
            String utteranceId = String.valueOf(this.hashCode());
            try {
                tts.speak(text, TextToSpeech.QUEUE_FLUSH, null, utteranceId);
            } catch (Exception e){
                e.printStackTrace();
            }
        }

    }

    @Override
    protected void onDestroy() {
        super.onDestroy();

        //tts 중지 (Clear the buffer)
        if(tts != null){
            tts.stop();
            tts.shutdown();
            tts = null;
        }

    }

    //TextToSpeech Instance를 생성해서 리턴
    private TextToSpeech getTTSInstance(){
        return new TextToSpeech(this, new TextToSpeech.OnInitListener() {
            @Override
            public void onInit(int status) {
                if(status!=TextToSpeech.ERROR){
                    tts.setLanguage(Locale.KOREAN);
                } else {
                    Toast.makeText(MainActivity.this, "TTS Init Failed", Toast.LENGTH_SHORT).show();
                }
            }
        });
    }

    //인터넷에 연결되어있는지 확인.
    public boolean isConnectedToInternet() {
        ConnectivityManager cm = (ConnectivityManager) getSystemService(Context.CONNECTIVITY_SERVICE);
        NetworkInfo net = cm.getActiveNetworkInfo();
        if (net!=null && net.isAvailable() && net.isConnected()) {
            return true;
        } else {
            return false;
        }
    }


    //음성 입력을 받는다
    public void inputSpeech() {
        Intent intent = new Intent(RecognizerIntent.ACTION_RECOGNIZE_SPEECH);
        intent.putExtra(RecognizerIntent.EXTRA_LANGUAGE_MODEL, RecognizerIntent.LANGUAGE_MODEL_FREE_FORM);

        intent.putExtra(RecognizerIntent.EXTRA_LANGUAGE, Locale.getDefault());
        intent.putExtra(RecognizerIntent.EXTRA_PROMPT, "말씀해주세요.");

        try {
            //입력받은 음성은 onActivityResult 콜백에서 처리한다
            startActivityForResult(intent, REQ_CODE_SPEECH_INPUT);

        } catch (ActivityNotFoundException a) {

            //음성인식 지원을 하지 않는 경우, 구글 서비스 업데이트를 유도한다
            String appPackageName = getResources().getString(R.string.url_google_services);
            Toast.makeText(this, getString(R.string.speech_not_supported), Toast.LENGTH_SHORT).show();
            Intent browserIntent = new Intent(Intent.ACTION_VIEW,   Uri.parse("https://market.android.com/details?id="+appPackageName));
            startActivity(browserIntent);
        }

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

                final String speech = result.get(0);

                VoiceMessage voiceMessage = new VoiceMessage(speech);

                userRef.child("messageToBlind").push().setValue(voiceMessage).addOnCompleteListener(new OnCompleteListener<Void>() {
                    @Override
                    public void onComplete(@NonNull Task<Void> task) {
                        Toast.makeText(MainActivity.this, "\""+speech+"\""+"\n전송 완료.", Toast.LENGTH_SHORT).show();
                    }
                });

        }

    }

}
