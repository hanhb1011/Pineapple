package org.androidtown.pineapple_android;

import android.Manifest;
import android.content.Intent;
import android.content.IntentSender;
import android.content.pm.PackageManager;
import android.location.Location;
import android.os.Build;
import android.os.Bundle;
import android.speech.RecognizerIntent;
import android.speech.tts.TextToSpeech;
import android.support.annotation.Nullable;
import android.support.v4.app.ActivityCompat;
import android.support.v4.content.ContextCompat;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.util.Pair;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.gms.common.ConnectionResult;
import com.google.android.gms.common.api.GoogleApiClient;
import com.google.android.gms.common.api.PendingResult;
import com.google.android.gms.common.api.ResultCallback;
import com.google.android.gms.common.api.Status;
import com.google.android.gms.location.LocationRequest;
import com.google.android.gms.location.LocationServices;
import com.google.android.gms.location.LocationSettingsRequest;
import com.google.android.gms.location.LocationSettingsResult;
import com.google.android.gms.location.LocationSettingsStatusCodes;
import com.skt.Tmap.TMapGpsManager;

import org.androidtown.pineapple_android.Model.FindTheWay;
import org.androidtown.pineapple_android.Retrofit.RetrofitService;
import org.androidtown.pineapple_android.Util.ApiUtils;
import org.androidtown.pineapple_android.Util.GpsInfoService;
import org.androidtown.pineapple_android.Util.Navigation;
import org.androidtown.pineapple_android.Util.NavigationBody;

import java.util.ArrayList;
import java.util.Calendar;
import java.util.Locale;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

import static org.androidtown.pineapple_android.GroupConstants.REQ_CODE_SPEECH_INPUT;

public class MainActivity extends AppCompatActivity
        implements TMapGpsManager.onLocationChangedCallback, GoogleApiClient.ConnectionCallbacks,
        GoogleApiClient.OnConnectionFailedListener {


    private static final int REQUEST_CHECK_SETTINGS_GPS = 0x1;

    @Override
    public void onLocationChange(Location location) {
        double lat = location.getLatitude();
        double lon = location.getLongitude();
        if(lat>1 && !navi.isFirstLocation()) {
            navi.setFirstLocation(true);
            Log.d("onLocationChange","setFirstLocation");
        }

        //GPS가 활성화되면 GPS이미지뷰를 Visible로 바꾸기
        if(gpsImageView != null && gpsImageView.getVisibility()!=View.VISIBLE) {
            gpsImageView.setVisibility(View.VISIBLE);
        }

        firebaseHelper.updateCurrentLocation(lat,lon);

        navi.setCurrentX(lon);
        navi.setCurrentY(lat);
        navi.setPreX(navi.getCurrentX());
        navi.setPreY(navi.getCurrentY());

        if(navi.calcAngle()) {
            try {
                int data = (int) navi.getDestinationAngle();
                bluetoothHelper.sendData(data + ""); //목적지 방위각 전송
            } catch (Exception e) {
                Toast.makeText(MainActivity.this, "TYPE ERROR", Toast.LENGTH_SHORT).show();
            }
        }

        if(navi.isStarted()){
            navi.stateCheck(lat,lon);
            if(navi.getPrePlace()!=null){
                firebaseHelper.addTrainData(new TrainData(
                        navi.getPrePlace().getY(),navi.getPrePlace().getX(),
                        navi.getCurrentPlace().getY(),navi.getCurrentPlace().getX(),
                        navi.getCurrentY(),navi.getCurrentX()));
            }
            if(navi.getLeaveWayCount()>0){//거리가 멀어진 경우
                //진동모터
                bluetoothHelper.sendData("700");
            }
            naviTextView.append("f : " + navi.getFeatureNumber() + " dis : " + navi.getDistance() + " angle : " +
                    (int)navi.getDestinationAngle() + "\n");
        }else if(navi.isdSync() && !navi.issSync() && navi.isFirstLocation()){ //네비 시작 x, 목적지 o, 시작위치 o
            navi.setsSync(true);
            loadAnswer(navi.getEndX(),navi.getEndY());
        }
    }

    private View micImageView;
    private TextView speechTextView;
    private TextView responseTextView;
    public TextView testTextView;
    public TextView naviTextView;
    private BluetoothHelper bluetoothHelper;
    private VoiceRecognizer voiceRecognizer;
    public Tmap tmap;
    public MainHandler mainHandler;
    public TextToSpeech tts;
    public static ArrayList<Message> messageList;
    public static User user;
    public static FirebaseHelper firebaseHelper;
    public static boolean speech = true;
    public static ImageView gpsImageView;
    public static ImageView bluetoothImageView;
    public static ImageView helpImageView;

    private GpsInfoService gps1;
    private GoogleApiClient googleApiClient;



    TMapGpsManager gps2=null;


    private RetrofitService mService;
    FindTheWay mWay;
    public static Navigation navi;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);


        initView();
        init();
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
                String inputString = testEditText.getText().toString();
                if(inputString.length()>0){
                    try {
                        bluetoothHelper.sendData(inputString);
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
        setupGClient();

        //TextToSpeech 초기화
        tts = getTTSInstance();

        //파이어베이스 초기화
        firebaseHelper = new FirebaseHelper(this);

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

        //Navigation 초기화
        navi = Navigation.getInstance();

        //RetrofitService 초기화
        mService = ApiUtils.getRetrofitService();

        gps1 = new GpsInfoService(MainActivity.this);
        //gps 퍼미션, gps객체 초기화
//        if(!checkLocationPermission()) {
//            ActivityCompat.requestPermissions(MainActivity.this, new String[]{Manifest.permission.ACCESS_FINE_LOCATION}, 1);
//        }else{
//            GpsInfoService gps = new GpsInfoService(this);
//            if(gps.isGetLocation()){
//                navi.setCurrentX(gps.getLongitude());
//                navi.setCurrentY(gps.getLatitude());
//                navi.setFirstLocation(true);
//            }else{
//                gps.showSettingsAlert(); //gps 꺼져있을 시에 실행부분
//            }
//
//            gps2 = new TMapGpsManager(this);
//            gps2.setMinTime(1000);
//            gps2.setMinDistance(5);
//            gps2.setProvider(gps2.GPS_PROVIDER);
//            gps2.OpenGps();
//        }
    }

    private synchronized void setupGClient() {
        googleApiClient = new GoogleApiClient.Builder(this)
                .enableAutoManage(this, 0, this)
                .addConnectionCallbacks(this)
                .addOnConnectionFailedListener(this)
                .addApi(LocationServices.API)
                .build();
        googleApiClient.connect();
    }

    @Override
    public void onConnected(@Nullable Bundle bundle) {
        if(!checkLocationPermission()) {
            ActivityCompat.requestPermissions(MainActivity.this, new String[]{Manifest.permission.ACCESS_FINE_LOCATION}, 1);
        }else {
            getMyLocation();
        }
    }

    public void getMyLocation(){
        if(googleApiClient != null && googleApiClient.isConnected()) {
            LocationRequest locationRequest = new LocationRequest();
            locationRequest.setInterval(3000);
            locationRequest.setFastestInterval(3000);
            locationRequest.setPriority(LocationRequest.PRIORITY_HIGH_ACCURACY);
            LocationSettingsRequest.Builder builder = new LocationSettingsRequest.Builder()
                    .addLocationRequest(locationRequest);

            builder.setAlwaysShow(true);

            PendingResult<LocationSettingsResult> result =
                    LocationServices.SettingsApi
                            .checkLocationSettings(googleApiClient, builder.build());
            result.setResultCallback(new ResultCallback<LocationSettingsResult>() {

                @Override
                public void onResult(LocationSettingsResult result) {
                    final Status status = result.getStatus();
                    switch (status.getStatusCode()) {
                        case LocationSettingsStatusCodes.SUCCESS:
                            // All location settings are satisfied.
                            // You can initialize location requests here.
                            int permissionLocation = ContextCompat
                                    .checkSelfPermission(MainActivity.this,
                                            Manifest.permission.ACCESS_FINE_LOCATION);
                            if (permissionLocation == PackageManager.PERMISSION_GRANTED) {
                                for(int i=0;i<10;i++) {
                                    Location location = gps1.getLocation();
                                    if (!navi.isFirstLocation() && gps1.isGetLocation() && location!=null) {
                                        navi.setCurrentX(gps1.getLongitude());
                                        navi.setCurrentY(gps1.getLatitude());
                                        navi.setFirstLocation(true);
                                        Log.d("GpsInfoService", "setFirstLocation");
                                        gpsImageView.setVisibility(View.VISIBLE);
                                    }
                                }
                            }
                            gps2 = new TMapGpsManager(MainActivity.this);
                            gps2.setMinTime(1000);
                            gps2.setMinDistance(5);
                            gps2.setProvider(gps2.GPS_PROVIDER);
                            gps2.OpenGps();
                            break;
                        case LocationSettingsStatusCodes.RESOLUTION_REQUIRED:
                            // Location settings are not satisfied.
                            // But could be fixed by showing the user a dialog.
                            try {
                                // Show the dialog by calling startResolutionForResult(),
                                // and check the result in onActivityResult().
                                // Ask to turn on GPS
                                status.startResolutionForResult(MainActivity.this, REQUEST_CHECK_SETTINGS_GPS);
                            } catch (IntentSender.SendIntentException e) {
                                // Ignore the error.
                            }
                            break;
                        case LocationSettingsStatusCodes.SETTINGS_CHANGE_UNAVAILABLE:
                            // Location settings are not satisfied.
                            // However, we have no way
                            // to fix the
                            // settings so we won't show the dialog.
                            //finish();
                            Toast.makeText(MainActivity.this, "NOT SATISFIED", Toast.LENGTH_SHORT).show();
                            break;
                    }
                }
            });
        }

    }

    @Override
    public void onConnectionSuspended(int i) {

    }

    @Override
    public void onConnectionFailed(ConnectionResult connectionResult){
        
    }

    //뷰 바인딩
    private void initView() {
        micImageView = findViewById(R.id.mic_iv);
        speechTextView = findViewById(R.id.speech_tv);
        responseTextView = findViewById(R.id.response_tv);
        testTextView = findViewById(R.id.test_tv);
        naviTextView = findViewById(R.id.navi);
        gpsImageView = findViewById(R.id.gps_iv);
        bluetoothImageView = findViewById(R.id.bluetooth_iv);
        helpImageView = findViewById(R.id.help_iv);
    }

    //뷰 초기화
    private void setView() {

        getSupportActionBar().hide(); //액션바 숨김


        //마이크 이미지뷰를 클릭했을 때
        micImageView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                inputSpeechProcess(); //음성입력을 받는다.
            }
        });

        helpImageView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                HelpFragment helpFragment= new HelpFragment();
                helpFragment.show(getFragmentManager(),"");
            }
        });
    }

    public void inputSpeechProcess() {
        //인터넷 연결을 확인하고, 연결이 되어있으면 서비스 실행
        if(voiceRecognizer.isConnectedToInternet()) {
            voiceRecognizer.inputSpeech(); //음성 입력을 받는다.
        } else {
            //연결이 되어있지 않으면 토스트 메시지 출력
            Toast.makeText(MainActivity.this, R.string.internet_connection_failed, Toast.LENGTH_SHORT).show();
        }
    }

    @Override
    protected void onStop() {
        super.onStop();
        //tts 중지 (Clear the buffer)
        if(tts != null){
            tts.stop();
            tts.shutdown();
            tts = null;
        }

        speech = false;
    }

    @Override
    protected void onResume() {
        super.onResume();
        if(tts == null){
            tts = getTTSInstanceWithNewMessages();
        }

        speech = true;
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        switch (requestCode) {
            case REQUEST_CHECK_SETTINGS_GPS:
                if(resultCode==0) { //취소
                    finish();
                }else if(resultCode == -1){
                    getMyLocation();
                }
                break;
            //음성인식이 완료되었을 때
            case REQ_CODE_SPEECH_INPUT :
                VoiceRecognizer.isAvailable = true; //음성입력이 다시 가능하게 하도록 set

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

                        if(navi.isFirstLocation()) {
                            response.append(resultPair.second);
                            response.append("안내를 시작합니다.\"");
                            tmap.getPOIItem(resultPair.second); //네비게이션 시작
                        } else {
                            response.append("위치정보를 받아올 수 없습니다.\n 잠시 뒤에 실행해주세요.\"");
                        }
                        break;
                    case GroupConstants.INTENTION_CANCELLATION :
                        response.append("안내를 중단합니다.\"");
                        if(Navigation.getInstance().isStarted()) {
                            Navigation.getInstance().terminate();
                        }
                        break;

                    case GroupConstants.INTENTION_INVALID :
                        response.append("다시 한 번 말씀해주세요.\"");
                        break;

                    case GroupConstants.INTENTION_CHATLOG :
                        response.append("채팅 기록.\"");
                        ChatLogFragment chatLogFragment = new ChatLogFragment();
                        chatLogFragment.show(getFragmentManager(),"");
                        break;

                    case GroupConstants.INTENTION_MAP :
                        response.append("지도.\"");
                        PathFragment pathFragment = new PathFragment();
                        pathFragment.show(getFragmentManager(),"");
                        break;

                    case GroupConstants.INTENTION_SEND_MESSAGE :
                        response.append(resultPair.second+". 라고 전송했습니다.\"");
                        firebaseHelper.sendMessageToCareTaker(resultPair.second);
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
                tts.speak(text, TextToSpeech.QUEUE_ADD, null, utteranceId);
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
                        tts.speak("반갑습니다.", TextToSpeech.QUEUE_ADD, null, utteranceId);

                    }

                } else {
                    Toast.makeText(MainActivity.this, "TTS Init Failed", Toast.LENGTH_SHORT).show();
                }
            }
        });
    }


    public void loadAnswer(double endX, double endY) {
        if(gps2!=null) {
            if(navi.isFirstLocation()) {
                double startX = navi.getCurrentX();
                double startY = navi.getCurrentY();

                NavigationBody navigationBody = new NavigationBody();

                String s = "시작위도 : " + startY + "\n" + "시작경도" + startX + "\n";
                testTextView.append(s);
                testTextView.append("네비게이션 시작\n");

                navigationBody.setStartPoint(startX, startY); //출발지 설정
                navigationBody.setEndPoint(endX, endY); //목적지 설정

                navi.setStartX(startX);
                navi.setStartY(startY);
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
                                navi.stateCheck(navi.getStartY(), navi.getStartX());
                                naviTextView.append("fs : " + navi.getFeatureSize() + "\n");
                                naviTextView.append("f : " + navi.getFeatureNumber() + " dis : " + navi.getDistance() + " angle : " +
                                        (int) navi.getDestinationAngle() + "\n");
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
            }
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
                    getMyLocation();
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

    public TextToSpeech getTTSInstanceWithNewMessages() {
        return new TextToSpeech(this, new TextToSpeech.OnInitListener() {
            @Override
            public void onInit(int status) {
                if(status!=TextToSpeech.ERROR){
                    tts.setLanguage(Locale.KOREAN);
                    //minimum SDK version is 21
                    if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
                        String utteranceId=this.hashCode() + "";
                        tts.speak("안녕하세요. 목적지를 말씀해주세요.", TextToSpeech.QUEUE_ADD, null, utteranceId);

                        if(firebaseHelper !=null) {
                            firebaseHelper.getMessageAndSpeakAtOnce();
                        }
                    }

                } else {
                    Toast.makeText(MainActivity.this, "TTS Init Failed", Toast.LENGTH_SHORT).show();
                }
            }
        });
    }
}
