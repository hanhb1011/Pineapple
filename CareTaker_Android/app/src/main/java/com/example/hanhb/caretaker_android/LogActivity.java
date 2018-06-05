package com.example.hanhb.caretaker_android;

import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;

public class LogActivity extends AppCompatActivity {

    Button backButton;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_log);

        initView();


    }

    private void initView() {

        //액션바 숨김
        try {
            getSupportActionBar().hide();
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

    }


}
