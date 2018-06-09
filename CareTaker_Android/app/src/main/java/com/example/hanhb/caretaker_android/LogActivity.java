package com.example.hanhb.caretaker_android;

import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.View;
import android.widget.Button;
import android.widget.LinearLayout;
import android.widget.Toast;

import com.skt.Tmap.TMapData;
import com.skt.Tmap.TMapTapi;
import com.skt.Tmap.TMapView;

import static java.security.AccessController.getContext;

public class LogActivity extends AppCompatActivity {

    private Button backButton;
    private RecyclerView recyclerView;
    private LinearLayout tmapLayout;

    public static TMapData tMapData;
    public static TMapView tMapView;
    public static TMapTapi tmaptapi;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_log);

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

        recyclerView = findViewById(R.id.recycler_view);
        recyclerView.setLayoutManager(new LinearLayoutManager(this));
        recyclerView.setAdapter(new LogAdapter(this));

        tmapLayout = findViewById(R.id.tmap_layout_log);
        tMapView = new TMapView(this);
        String tmapKey = getResources().getString(R.string.TMAP_API_KEY);
        tMapView.setSKTMapApiKey(tmapKey);
        tMapView.setCenterPoint(MainActivity.user.getCurrentLongitude(), MainActivity.user.getCurrentLatitude());
        tmapLayout.addView(tMapView);


    }


}
