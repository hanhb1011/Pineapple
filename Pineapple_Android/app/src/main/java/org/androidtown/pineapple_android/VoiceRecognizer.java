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

import java.util.Arrays;
import java.util.List;
import java.util.Locale;

/**
 * Created by hanhb on 2018-04-11.
 */


/*
    음성인식 기능 수행
    음성을 받은 뒤, Speech To Text API로  String 결과를 생성한다.
 */
public class VoiceRecognizer {

    private Context context;
    public static boolean isAvailable = true; // 실행 중인 음성 입력 액티비티가 없을 때만 사용 가능하도록

    public VoiceRecognizer(Context context) {
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
            if(isAvailable) {
                isAvailable = false;
                //Critical section
                ((Activity) context).startActivityForResult(intent, GroupConstants.REQ_CODE_SPEECH_INPUT);

            }
        } catch (ActivityNotFoundException a) {
            isAvailable = true;
            //음성인식 지원을 하지 않는 경우, 구글 서비스 업데이트를 유도한다
            String appPackageName = context.getResources().getString(R.string.url_google_services);
            Toast.makeText(context, context.getString(R.string.speech_not_supported), Toast.LENGTH_SHORT).show();
            Intent browserIntent = new Intent(Intent.ACTION_VIEW,   Uri.parse("https://market.android.com/details?id="+appPackageName));
            context.startActivity(browserIntent);

        } catch (Exception e) {
            isAvailable = true;
        }

    }



    /*

        문장을 전달받으면 키워드 매칭을 통해 의도를 파악
        결과는 Pair<Integer, String>으로 반환한다
        Pair의 첫 번째 값은 의도를 나타내는 정수값
        두 번째는 목적지 값이다. (중지하는 경우, 이 부분은 공백이다.)

        상황
        1. 목적지를 찾는 경우 (ex : "동국대 찾아줘")
        2. 가까운 목적지 찾는 경우 (ex : "가까운 병원 어디야?")
        3. 안내 중단 요구 (ex : "안내 그만")


    */
    public android.util.Pair<Integer, String> process(String sentence) {
        //우선 의도에 따른 키워드를 분류한다
        List<String> ADJECTIVE_KEYWORDS = Arrays.asList("가까운", "근처");
        List<String> DESTINATION_KEYWORDS = Arrays.asList("안내해", "어딨", "어디", "어떻게", "알려", "찾아");
        List<String> CANCELLATION_KEYWORDS = Arrays.asList("중단", "그만", "중지", "취소");
        List<String> CHATLOG_KEYWORDS = Arrays.asList("기록","채팅","로그");
        List<String> MAP_KEYWORDS = Arrays.asList("맵", "지도");
        List<String> MESSAGE_START_KEYWORDS = Arrays.asList("보호자", "보호자에게","보호자한테");
        List<String> MESSAGE_END_KEYWORDS = Arrays.asList("해","줘","말해", "보내", "라고해", "전송해", "해줘", "해주세요", "보내줘","보내주세요",
                "말해줘", "말해주세요", "말해요", "보내요");

        //문장을 단어별로 분리시킨다 -> String[]
        String[] words = sentence.split(" ");

        //목적지 탐색인 경우
        for(String keyword : DESTINATION_KEYWORDS) {
            for (int i=0; i<words.length; i++) {
                if (words[i].contains(keyword)) {
                    //목적지 탐색 키워드와 일치하는 경우, 자연어에서 목적지 정보를 필터링해서 리턴한다.
                    StringBuilder destination = new StringBuilder();

                    for(int j=0; j<i; j++) {

                        //형용사인 경우 ("가까운", "근처" 등)를 필터링
                        boolean isAdjective = false;
                        for(String adjective : ADJECTIVE_KEYWORDS) {
                            if(words[j].contains(adjective)) {
                                isAdjective = true;
                                break;
                            }
                        }

                        //형용사가 아닌 경우 StringBuilder에 append
                        if(!isAdjective) {
                            destination.append(words[j]);
                        }
                    }

                    //목적지 정보가 유효한 경우(length > 0), 결과 리턴
                    if(destination.length()>0)
                        return new android.util.Pair<>(GroupConstants.INTENTION_DESTINATION, destination.toString());
                }
            }
        }

        //탐색 중단인 경우
        for(String keyword : CANCELLATION_KEYWORDS) {
            for(String word : words) {
                if(word.contains(keyword)) {
                    //중단이라 판단
                    return new android.util.Pair<>(GroupConstants.INTENTION_CANCELLATION,  null);
                }
            }
        }

        //채팅 로그 보이기
        for(String keyword : CHATLOG_KEYWORDS) {
            for(String word : words) {
                if(word.contains(keyword)) {
                    return new android.util.Pair<>(GroupConstants.INTENTION_CHATLOG,  null);
                }
            }
        }

        //지도 보여주기
        for(String keyword : MAP_KEYWORDS) {
            for(String word : words) {
                if(word.contains(keyword)) {
                    //중단이라 판단
                    return new android.util.Pair<>(GroupConstants.INTENTION_MAP,  null);
                }
            }
        }

        //보호자에게 메시지 전송
        if(words.length > 0) {
            for (String startKeyword : MESSAGE_START_KEYWORDS) {
                for(String endKeyword : MESSAGE_END_KEYWORDS) {
                    if (words[0].contains(startKeyword) && words[words.length - 1].contains(endKeyword)) {
                        return new android.util.Pair<>(GroupConstants.INTENTION_SEND_MESSAGE, sentence);
                    }
                }
            }
        }

        //아무것도 아닌 경우 (Invalid 할 경우)
        return new android.util.Pair<>(GroupConstants.INTENTION_INVALID, null);
    }

}
