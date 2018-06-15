package org.androidtown.pineapple_android;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.speech.RecognitionListener;
import android.speech.RecognizerIntent;
import android.speech.SpeechRecognizer;
import android.widget.Toast;

import java.util.Locale;


/*
    챗봇 기능 중 STT를 팝업 없이 가능하도록 구현
 */

public class CustomizedSTT implements RecognitionListener {

    private Context context;
    private Intent intent;
    private SpeechRecognizer speechRecognizer;

    public CustomizedSTT(Context context) {
        this.context = context;

        this.intent = new Intent(RecognizerIntent.ACTION_RECOGNIZE_SPEECH);
        intent.putExtra(RecognizerIntent.EXTRA_LANGUAGE_MODEL, RecognizerIntent.LANGUAGE_MODEL_FREE_FORM);
        intent.putExtra(RecognizerIntent.EXTRA_LANGUAGE, Locale.getDefault());
        intent.putExtra(RecognizerIntent.EXTRA_PROMPT, context.getString(R.string.speech_prompt));

        this.speechRecognizer =  SpeechRecognizer.createSpeechRecognizer(context);
        speechRecognizer.setRecognitionListener(this);

    }

    //음성인식을 받는다
    public void startListening() {
        if(intent == null || speechRecognizer == null) {
            return;
        }
        speechRecognizer.startListening(intent);
    }

    private void stopListening() {
        if(speechRecognizer != null) {
            speechRecognizer.stopListening();
        }
    }

    //밑으로는 오버라이드된 콜백메서드
    //성공했을 때
    @Override
    public void onResults(Bundle bundle) {
        //System.out.println("onResults");
        try {
            ((MainActivity) context).inferSentence(bundle.getStringArrayList(SpeechRecognizer.RESULTS_RECOGNITION).get(0));
        } catch (Exception e) {
            onError(0); //Exception일 경우 음성입력 재요청
            e.printStackTrace();
        } finally {
            MainActivity.inputSpeech = true;
        }
    }

    //시간 초과, 못 알아들었을 땐 다시 알려달라고 요청
    @Override
    public void onError(int i) {
        //System.out.println("onError");
        MainActivity.inputSpeech = true; //다시 음성입력 받을 수 있도록 set
        MainActivity.responseTextView.setText("\"다시 한 번 말씀해주세요.\"");
        ((MainActivity)context).speak("다시 한 번 말씀해주세요.");
    }

    @Override
    public void onEndOfSpeech() {
        MainActivity.inputSpeech = true; //다시 음성입력 받을 수 있도록 set
        stopListening();
        //System.out.println("onEndOfSpeech");
    }

    @Override
    public void onReadyForSpeech(Bundle bundle) {
        //System.out.println("onReadyForSpeech");
    }

    @Override
    public void onBeginningOfSpeech() {
        //System.out.println("onBeginningOfSpeech");
    }

    @Override
    public void onRmsChanged(float v) {
        //System.out.println("onRmsChanged");
    }

    @Override
    public void onBufferReceived(byte[] bytes) {
        //System.out.println("onBufferReceived");
    }

    @Override
    public void onPartialResults(Bundle bundle) {
        //System.out.println("onPartialResults");
    }

    @Override
    public void onEvent(int i, Bundle bundle) {
        //System.out.println("onEvent");
    }

}
