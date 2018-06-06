package com.example.hanhb.caretaker_android;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.util.Map;

public class MainActivity extends Activity {
    public static String key;
    public static User user;
    private FirebaseDatabase firebaseDatabase;
    private DatabaseReference userRef;
    private Button firstButton, secondButton, thirdButton;

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


}
