package org.androidtown.pineapple_android;

import android.content.Context;

import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;

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

    public FirebaseHelper(Context context){
        this.context = context;
        database = FirebaseDatabase.getInstance();
        userRef = database.getReference("user");
        init(); //initialize uid

    }


    /*
        init
        1. uid가 없으면 uid 생성 후 SharedPreference에 저장
        2. 저장된 uid를 MainActivity의 uid 변수에 저장

     */
    private void init() {
        boolean isInitialState = SharedPreferenceHelper.getBoolean(context,
                context.getResources().getString(R.string.isInitialState));
        if(isInitialState){
            //uid 생성 후 sharedPreference에 저장
            String uid = userRef.getKey(); //uid 생성
            SharedPreferenceHelper.putString(context,
                    context.getResources().getString(R.string.uid),uid);




            MainActivity.uid = uid;

        } else {


        }

    }


}
