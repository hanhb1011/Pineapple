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
    private User user;

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

                        

                    } catch (Exception e){
                        Toast.makeText(context, e.getMessage(), Toast.LENGTH_SHORT).show();
                    }
                }

                @Override
                public void onCancelled(DatabaseError databaseError) {

                }
            });


        }

    }

    //실시간 위치
    public void updateCurrentLocation(double currentLatitude, double currentLongitude){
        if(user==null){
            return;
        }



    }


    //경로 안내 시 기록을 서버에 저장한다.
    public void addRouteNavigation(RouteNavigation routeNavigation){
        if(user==null){
            Toast.makeText(context, "잠시 후 다시 시도해주십시오", Toast.LENGTH_SHORT).show();
            return;
        }

        userRef.child(user.getUid()).child("navigationLog").push().setValue(routeNavigation);
    }

}
