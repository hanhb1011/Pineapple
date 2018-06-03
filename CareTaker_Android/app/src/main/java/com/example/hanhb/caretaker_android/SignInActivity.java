package com.example.hanhb.caretaker_android;

import android.content.Intent;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

public class SignInActivity extends AppCompatActivity {

    private EditText inputEditText;
    private Button inputButton;
    private FirebaseDatabase firebaseDatabase;
    private DatabaseReference userRef;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_sign_in);

        initFirebase();
        initView();
    }

    private void initFirebase() {

        firebaseDatabase = FirebaseDatabase.getInstance();
        userRef = firebaseDatabase.getReference().child("user");

    }

    private void initView() {
        inputButton = findViewById(R.id.input_btn);
        inputEditText = findViewById(R.id.input_et);

        //클릭리스너 설정 (인풋된 키가 Valid한지 확인하고 다음 단계로 넘어간다)
        inputButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String inputString = inputEditText.getText().toString().trim();
                attemptSignin(inputString);

            }
        });


        //이미 입력된 키가 있으면 자동으로 EditText에 입력
        String existingKey = SharedPreferenceHelper.getString(this, "key");
        if(existingKey.length()>0){
            inputEditText.setText(existingKey);
        }

        //액션바 숨김
        try {
            getActionBar().hide();
        } catch (Exception e){
            e.printStackTrace();
        }

    }

    private void attemptSignin(final String inputString) {

        if(inputString.length() == 0 ){
            Toast.makeText(SignInActivity.this, "key값을 입력해 주세요.", Toast.LENGTH_SHORT).show();
            return;
        }

        userRef.child(inputString).addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                User user = dataSnapshot.getValue(User.class);

                if(user == null) {
                    Toast.makeText(SignInActivity.this, "key가 존재하지 않습니다.", Toast.LENGTH_SHORT).show();

                } else {
                    //If user exists
                    MainActivity.key = inputString;
                    SharedPreferenceHelper.putString(SignInActivity.this, "key", inputString);


                    Intent intent = new Intent(SignInActivity.this, MainActivity.class);
                    startActivity(intent);

                }
            }

            @Override
            public void onCancelled(DatabaseError databaseError) {

            }
        });

    }


}
