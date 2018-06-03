package com.example.hanhb.caretaker_android;

import android.app.Activity;
import android.os.Bundle;
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
    private FirebaseDatabase firebaseDatabase;
    private DatabaseReference userRef;

    private TextView tempTextView;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        initView();
        init();


    }

    private void init() {
        if(key == null){
            Toast.makeText(this, "인증 오류", Toast.LENGTH_SHORT).show();
            finish();
        }

        firebaseDatabase = FirebaseDatabase.getInstance();
        userRef = firebaseDatabase.getReference().child("user").child(key);

        //temp
        userRef.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                User user = dataSnapshot.getValue(User.class);

                if(user == null) {
                    return;
                }

                StringBuilder stringBuilder = new StringBuilder();
                stringBuilder.append("위도").append(user.getCurrentLatitude())
                        .append("\n경도").append(user.getCurrentLongitude()).append("\n");

                Map<String, RouteNavigation> map = user.getNavigationLog();
                if(map !=null) {
                    stringBuilder.append("\n경로 안내 기록");
                    for(RouteNavigation routeNavigation : map.values()){
                        stringBuilder.append("\n경로 이름: ").append(routeNavigation.getDstName())
                                .append("\n경로 주소: ").append(routeNavigation.getDstAddress());
                    }
                }

                tempTextView.setText(stringBuilder.toString());
            }

            @Override
            public void onCancelled(DatabaseError databaseError) {

            }
        });

    }

    private void initView() {
        tempTextView = findViewById(R.id.temp_tv);

    }


}
