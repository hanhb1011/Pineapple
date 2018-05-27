package org.androidtown.pineapple_android;

import android.app.DialogFragment;
import android.os.Build;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.constraint.ConstraintLayout;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

/**
 * Created by hanhb on 2018-05-17.
 */

public class ChatLogFragment extends DialogFragment {

    private RecyclerView chatRecyclerView;
    private ConstraintLayout constraintLayout;


    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_chat_log,container, false);
        bindView(view);

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
            chatRecyclerView.setLayoutManager(new LinearLayoutManager(getContext()));
            chatRecyclerView.setAdapter(new MessageAdapter(getContext(), this));
        } else {
            chatRecyclerView.setLayoutManager(new LinearLayoutManager(getActivity()));
            chatRecyclerView.setAdapter(new MessageAdapter(getActivity(), this));
        }

        constraintLayout.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                dismiss();
            }
        });

        return view;
    }

    private void bindView(View view) {
        chatRecyclerView = view.findViewById(R.id.chat_recycler_view_in_fragment);
        constraintLayout = view.findViewById(R.id.chat_constraintlayout_in_fragment);
    }


}
