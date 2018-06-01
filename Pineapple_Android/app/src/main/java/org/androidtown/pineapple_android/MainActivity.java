package org.androidtown.pineapple_android;

import android.Manifest;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.location.Location;
import android.os.Build;
import android.os.Bundle;
import android.speech.RecognizerIntent;
import android.speech.tts.TextToSpeech;
import android.support.v4.app.ActivityCompat;
import android.support.v4.content.ContextCompat;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.util.Pair;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import com.skt.Tmap.TMapGpsManager;

import org.androidtown.pineapple_android.Model.FindTheWay;
import org.androidtown.pineapple_android.Retrofit.RetrofitService;
import org.androidtown.pineapple_android.Util.ApiUtils;
import org.androidtown.pineapple_android.Util.Navigation;
import org.androidtown.pineapple_android.Util.NavigationBody;

import java.util.ArrayList;
import java.util.Calendar;
import java.util.Locale;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

import static org.androidtown.pineapple_android.GroupConstants.REQ_CODE_SPEECH_INPUT;

public class MainActivity extends AppCompatActivity implements TMapGpsManager.onLocationChangedCallback {

    @Override
    public void onLocationChange(Location location) {
        double lat = location.getLatitude();
        double lon = location.getLongitude();
        navi.setPreX(navi.getCurrentX());
        navi.setPreY(navi.getCurrentY());
        navi.setCurrentX(lon);
        navi.setCurrentY(lat);
        navi.calcAngle();
        // 전송할 데이터
        // 1. 시선 방위각 - 컴퍼스 센서 바위각
        // 2. 목적지 방위각

        if(navi.isStarted()){
            navi.stateCheck(lat,lon);
            switch(navi.getCurrentState()){
                case 2: //type : LineString

                    break;
                case 3: //type : 이탈
                    break;
                default:
                    break;
                case 1: //type : Point
                    //음성안내
                    speak(navi.getDescription());
                    testTextView.append(navi.getDescription() + "\n");
                    navi.nextFeature();
            }
        }else if(navi.isdSync() && !navi.issSync()){ //네비 시작 x, 목적지 o, 시작위치 o
            navi.setsSync(true);
            loadAnswer(navi.getEndX(),navi.getEndY());
        }
    }

    private View micImageView;
    private TextView speechTextView;
    private TextView responseTextView;
    public TextView testTextView;
    private BluetoothHelper bluetoothHelper;
    private VoiceRecognizer voiceRecognizer;
    public Tmap tmap;
    public MainHandler mainHandler;
    public TextToSpeech tts;
    public static ArrayList<Message> messageList;
    public static String uid;


    TMapGpsManager gps2=null;


    private RetrofitService mService;
    FindTheWay mWay;
    public static Navigation navi;

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

        findViewById(R.id.activity_btn).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                ChatLogFragment chatLogFragment = new ChatLogFragment();
                chatLogFragment.show(getFragmentManager(),"");
            }
        });

        findViewById(R.id.path_btn).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                PathFragment pathFragment = new PathFragment();
                pathFragment.show(getFragmentManager(),"");
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

        //TextToSpeech 초기화
        tts = getTTSInstance();

        //Navigation 초기화
        navi = new Navigation(this);

        //RetrofitService 초기화
        mService = ApiUtils.getRetrofitService();

        //gps 퍼미션, gps객체 초기화
        if(!checkLocationPermission()) {
            ActivityCompat.requestPermissions(MainActivity.this, new String[]{Manifest.permission.ACCESS_FINE_LOCATION}, 1);
        }else{
            gps2 = new TMapGpsManager(this);
            gps2.setMinTime(1000);
            gps2.setMinDistance(5);
            gps2.setProvider(gps2.GPS_PROVIDER);
            gps2.OpenGps();
        }


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
    protected void onDestroy() {
        super.onDestroy();

        //tts 중지 (Clear the buffer)
        if(tts != null){
            tts.stop();
            tts.shutdown();
            tts = null;
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

                    case GroupConstants.INTENTION_CHATLOG :
                        response.append("채팅 기록.\"");
                        break;

                    case GroupConstants.INTENTION_MAP :
                        response.append("지도.\"");
                        break;
                }


                //응답 출력
                responseTextView.setText(response.toString());

                //메시지 리스트에 응답메시지 추가
                addMessageToList(GroupConstants.BOT_MESSAGE, response.toString());

                //음성 출력
                speak(response.substring(1, response.length() - 1));

        }

    }

    //채팅 로그에 띄울 ArrayList<Message>에 Message를 add한다.
    public void addMessageToList(int messageType, String content){

        Calendar calendar = Calendar.getInstance();
        long currentTimeStamp = calendar.getTimeInMillis();
        Message message = new Message(messageType, content, currentTimeStamp);
        messageList.add(message);

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

    //TextToSpeech Instance를 생성해서 리턴
    private TextToSpeech getTTSInstance(){
        return new TextToSpeech(this, new TextToSpeech.OnInitListener() {
            @Override
            public void onInit(int status) {
                if(status!=TextToSpeech.ERROR){
                    tts.setLanguage(Locale.KOREAN);
                    //minimum SDK version is 21
                    if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
                        String utteranceId=this.hashCode() + "";
                        tts.speak("안녕하세요. 목적지를 말씀해주세요.", TextToSpeech.QUEUE_FLUSH, null, utteranceId);
                    }

                } else {
                    Toast.makeText(MainActivity.this, "TTS Init Failed", Toast.LENGTH_SHORT).show();
                }
            }
        });
    }



    public void loadAnswer(double endX, double endY) {
        if(gps2!=null) {
            double startX=126.9823439963945;
            double startY=37.56461982743129;
            if(gps2!=null) {
                startX = gps2.getLocation().getLongitude();
                startY = gps2.getLocation().getLatitude();
            }
            NavigationBody navigationBody = new NavigationBody();

            String s = "시작위도 : " + startY + "\n" + "시작경도" + startX + "\n";
            testTextView.append(s);

            navigationBody.setStartPoint(startX, startY); //출발지 설정
            navigationBody.setEndPoint(endX, endY); //목적지 설정

            navi.gettMapView().setCenterPoint(startX, startY); //중심지 설정
            navi.setStartEndXY(startX, startY, endX, endY); //출발지, 목적지 알리기

            mService.getFindTheWay(navigationBody.getRequestBody()).enqueue(new Callback<FindTheWay>() {
                @Override
                public void onResponse(Call<FindTheWay> call, Response<FindTheWay> response) {
                    if (response.isSuccessful()) {
                        Log.d("MainActivity", "posts loaded from API");
                        mWay = response.body();
                        if (mWay == null)
                            Log.d("mWay", "null");
                        else {
                            navi.startNavigation(mWay);
                            if (navi.getCurrentState() == 1) {
                                speak(navi.getDescription()); //음성안내
                                testTextView.append(navi.getDescription() + "\n");
                                navi.nextFeature();
                            } else {

                            }
                        }
                    } else {
                        int statusCode = response.code();
                    }
                }

                @Override
                public void onFailure(Call<FindTheWay> call, Throwable t) {
                    Log.d("MainActivity", "error loading from API\n" + call.request() + "\n" + t.getMessage());

                }
            });
        }else{
            ActivityCompat.requestPermissions(MainActivity.this, new String[]{Manifest.permission.ACCESS_FINE_LOCATION}, 1);
        }
    }


    public void onRequestPermissionsResult(int requestCode, String permissions[], int[] grantResults) {
        switch (requestCode) {
            case 1: {
                // If request is cancelled, the result arrays are empty.
                if (grantResults.length > 0
                        && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                    gps2 = new TMapGpsManager(this);
                    gps2.setMinTime(1000*60);
                    gps2.setMinDistance(5);
                    gps2.setProvider(gps2.GPS_PROVIDER);
                    gps2.OpenGps();
                } else {
                    // permission denied, boo! Disable the
                    // functionality that depends on this permission.
                }
                return;
            }
            // other 'case' lines to check for other
            // permissions this app might request
        }
    }

    public boolean checkLocationPermission()
    {
        if ( Build.VERSION.SDK_INT >= 23 &&
                ContextCompat.checkSelfPermission( this, android.Manifest.permission.ACCESS_FINE_LOCATION ) != PackageManager.PERMISSION_GRANTED &&
                ContextCompat.checkSelfPermission( this, android.Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
            return false;
        }else
            return true;
    }

}
