package org.androidtown.pineapple_android;

import android.app.Activity;
import android.content.ActivityNotFoundException;
import android.content.Context;
import android.content.Intent;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.net.Uri;
import android.speech.RecognizerIntent;
import android.widget.Toast;

import java.util.Locale;

/**
 * Created by hanhb on 2018-04-11.
 */


/*
    음성인식 기능 수행
    음성을 받은 뒤, Speech To Text API로  String 결과를 생성한다.
 */
public class VoiceRecognizer {

    Context context;

    VoiceRecognizer(Context context) {
        this.context = context;

    }

    //인터넷에 연결되어있는지 확인.
    public boolean isConnectedToInternet() {
        ConnectivityManager cm = (ConnectivityManager) context.getSystemService(Context.CONNECTIVITY_SERVICE);
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
        intent.putExtra(RecognizerIntent.EXTRA_PROMPT, context.getString(R.string.speech_prompt));

        try {
            ((Activity)context).startActivityForResult(intent, MainActivity.REQ_CODE_SPEECH_INPUT);

        } catch (ActivityNotFoundException a) {

            //음성인식 지원을 하지 않는 경우, 구글 서비스 업데이트를 유도한다
            String appPackageName = "com.google.android.googlequicksearchbox";
            Toast.makeText(context, context.getString(R.string.speech_not_supported), Toast.LENGTH_SHORT).show();
            Intent browserIntent = new Intent(Intent.ACTION_VIEW,   Uri.parse("https://market.android.com/details?id="+appPackageName));
            context.startActivity(browserIntent);
        }

    }

}
