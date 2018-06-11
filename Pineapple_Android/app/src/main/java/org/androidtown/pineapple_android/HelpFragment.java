package org.androidtown.pineapple_android;


import android.content.ClipboardManager;
import android.os.Bundle;
import android.support.v4.app.DialogFragment;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.EditText;


/**
 * A simple {@link Fragment} subclass.
 */
public class HelpFragment extends android.app.DialogFragment{

    EditText keyEditText;

    public HelpFragment() {
        // Required empty public constructor
    }


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_help,container, false);
        keyEditText = view.findViewById(R.id.key_et);

        if(MainActivity.user == null || MainActivity.user.getUid() == null) {
            keyEditText.setText("잠시 후 다시 시도해주세요.");
        } else {
            keyEditText.setText(MainActivity.user.getUid());
        }

        view.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                dismiss();
            }
        });

        return view;
    }



}
