package com.example.hanhb.caretaker_android;

import android.app.Activity;
import android.os.Bundle;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.View;
import android.widget.Button;
import android.widget.Toast;

public class VoiceMessageActivity extends Activity {

    RecyclerView recyclerView;
    Button backButton;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_voice_message);

        validate();
        initView();


    }

    private void validate() {
        if(MainActivity.user == null) {
            Toast.makeText(this, "인터넷 연결 상태를 확인해 주십시오.", Toast.LENGTH_SHORT).show();
            finish();
        }
    }

    private void initView() {

        //액션바 숨김
        try {
            getActionBar().hide();
        } catch (Exception e){
            e.printStackTrace();
        }

        backButton = findViewById(R.id.back_btn_in_log);
        backButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                finish();
            }
        });

        recyclerView = findViewById(R.id.voice_message_recycler_view);
        recyclerView.setLayoutManager(new LinearLayoutManager(this));
        recyclerView.setAdapter(new VoiceMessageAdapter(this));

    }
}
