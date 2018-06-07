package org.androidtown.pineapple_android;

import android.content.Context;
import android.support.annotation.NonNull;
import android.widget.Toast;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.util.HashMap;
import java.util.Map;

/**
 * Created by hanhb on 2018-06-01.
 */

/*
    서버와 관련된 기능 수행

    1. 서버와 동기화 과정 후, 유저를 식별하는 unique key (uid) 생성
    2. 출발지, 목적지 정보 서버에 append
    3. 보호자로부터 음성 정보 실시간으로 받기

 */

public class FirebaseHelper {

    private Context context;
    private static FirebaseDatabase database;
    private static DatabaseReference userRef;
    private static DatabaseReference trainRef;
    private User user;

    public FirebaseHelper(Context context){

        this.context = context;
        database = FirebaseDatabase.getInstance();
        userRef = database.getReference("user");
        trainRef = database.getReference("train_dataset");
        init(); //initialize uid

    }

    /*
        init
        1. uid가 없으면 uid 생성 후 SharedPreference에 저장
        2. 저장된 uid를 MainActivity의 uid 변수에 저장
        3. TODO 시각장애인 앱에서 uid를 알 수 있는 방식 제시해야됨
     */
    private void init() {
        boolean hasUid = SharedPreferenceHelper.getBoolean(context,
                context.getResources().getString(R.string.hasUid));

        if(!hasUid){
            //앱 최초실행일 경우
            //uid 생성 후 sharedPreference에 저장
            final String uid = userRef.push().getKey(); //uid 생성
            final User user = new User(uid);


            //서버에 신규 사용자 등록
            userRef.child(uid).setValue(user).addOnCompleteListener(new OnCompleteListener<Void>() {
                @Override
                public void onComplete(@NonNull Task<Void> task) {
                    //등록 성공 시 로컬저장소에 uid 저장, initialState를 true로 저장
                    SharedPreferenceHelper.putString(context,
                            context.getResources().getString(R.string.uid),uid);
                    SharedPreferenceHelper.putBoolean(context,
                            context.getResources().getString(R.string.hasUid),true);

                    MainActivity.user = user; //메인액티비티에 user를 참조시킴
                    FirebaseHelper.this.user = user;

                    getMessageAndSpeak(); // 메시지를 주기적으로 받고, MainActivity의 tts를 통해 말한다.

                }
            });

        } else {
            //이미 생성한 uid가 존재할 경우, 서버에서 User를 불러온다
            String uid = SharedPreferenceHelper.getString(context,
                    context.getResources().getString(R.string.uid));
            if(uid.length()==0) { //if error,
                SharedPreferenceHelper.putBoolean(context,
                        context.getResources().getString(R.string.hasUid), false);
                return;
            }

            //user 정보를 서버로부터 받아온다
            userRef.child(uid).addListenerForSingleValueEvent(new ValueEventListener() {
                @Override
                public void onDataChange(DataSnapshot dataSnapshot) {
                    try {
                        User user = dataSnapshot.getValue(User.class);

                        MainActivity.user = user;
                        FirebaseHelper.this.user = user;
                        getMessageAndSpeak(); // 메시지를 주기적으로 받고, MainActivity의 tts를 통해 말한다.

                        /*
                        // 서버로부터 로그 읽기
                        HashMap<String, RouteNavigation> map = user.getNavigationLog();
                        if(map != null) {
                            for(RouteNavigation routeNavigation : map.values()){
                                Toast.makeText(context, routeNavigation.getDstName(), Toast.LENGTH_SHORT).show();
                            }
                        }
                        */

                    } catch (Exception e){
                        e.printStackTrace();
                    }
                }

                @Override
                public void onCancelled(DatabaseError databaseError) {

                }
            });


        }

    }

    //위치 이동 시 서버에 업데이트
    public void updateCurrentLocation(double currentLatitude, double currentLongitude){
        if(user==null){
            return;
        }

        //위도와 경도에 해당하는 HashMap을 구성
        Map<String, Object> map = new HashMap<>();
        map.put("currentLatitude", currentLatitude);
        map.put("currentLongitude", currentLongitude);

        //서버에 업데이트
        userRef.child(user.getUid()).updateChildren(map);

    }


    //경로 안내 시 기록을 서버에 저장한다.
    public void addRouteNavigation(RouteNavigation routeNavigation){
        if(user==null){
            Toast.makeText(context, "잠시 후 다시 시도해주십시오", Toast.LENGTH_SHORT).show();
            return;
        }

        //서버의 User - 해당 uid - navigationLog - 새로운 기록 추가
        userRef.child(user.getUid()).child("navigationLog").push().setValue(routeNavigation);
    }


    //Supervised learning용 데이터를 수집한다.
    public void addTrainData(final TrainData trainData) {

        trainRef.push().setValue(trainData);

        /*
        trainRef.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {

                //추후 파싱하기 편한 List 타입으로 저장
                List<TrainData> dataList = (List<TrainData>) dataSnapshot.getValue(Object.class);

                if(dataList == null){
                    dataList = new ArrayList<>();
                }
                dataList.add(trainData);

                trainRef.setValue(dataList);
            }

            @Override
            public void onCancelled(DatabaseError databaseError) {

            }
        });
        */

    }

    public void getMessageAndSpeak(){
        if(MainActivity.user == null){
            return;
        }

        userRef.child(MainActivity.user.getUid()).child("messageToBlind").addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                if(MainActivity.speech) {
                    if (dataSnapshot.getValue() != null) {
                        ((MainActivity) context).speak("보호자로부터 새로운 메시지가 도착했습니다.");

                        final Iterable<DataSnapshot> snapshots = dataSnapshot.getChildren();
                        userRef.child(MainActivity.user.getUid()).child("messageToBlind").removeValue()
                                .addOnCompleteListener(new OnCompleteListener<Void>() {
                                    @Override
                                    public void onComplete(@NonNull Task<Void> task) {
                                        for (DataSnapshot data : snapshots) {
                                            String s = data.child("message").getValue(String.class);
                                            ((MainActivity) context).speak(s);
                                        }
                                    }
                                });

                    }
                }
            }

            @Override
            public void onCancelled(DatabaseError databaseError) {

            }
        });


    }

    public void getMessageAndSpeakAtOnce(){
        if(MainActivity.user == null){
            return;
        }

        userRef.child(MainActivity.user.getUid()).child("messageToBlind").addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                if (dataSnapshot.getValue() != null) {
                    ((MainActivity) context).speak("보호자로부터 새로운 메시지가 도착했습니다.");

                    final Iterable<DataSnapshot> snapshots = dataSnapshot.getChildren();
                    userRef.child(MainActivity.user.getUid()).child("messageToBlind").removeValue()
                            .addOnCompleteListener(new OnCompleteListener<Void>() {
                                @Override
                                public void onComplete(@NonNull Task<Void> task) {
                                    for (DataSnapshot data : snapshots) {
                                        String message = data.child("message").getValue(String.class);
                                        ((MainActivity) context).speak(message);
                                    }
                                }
                            });

                }

            }

            @Override
            public void onCancelled(DatabaseError databaseError) {

            }
        });

    }

}
