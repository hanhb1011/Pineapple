package com.example.hanhb.caretaker_android;

import android.content.Context;
import android.support.annotation.NonNull;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.skt.Tmap.TMapView;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.List;

public class VoiceMessageAdapter extends RecyclerView.Adapter {

    private Context context;
    private List<VoiceMessage> voiceMessageList;
    private SimpleDateFormat fmt = new SimpleDateFormat("yyyy-mm-dd HH:MM");

    public VoiceMessageAdapter(Context context){
        this.context = context;
        voiceMessageList = new ArrayList<>();
        if(MainActivity.user.getMessageToCareTaker() != null)
            voiceMessageList.addAll(MainActivity.user.getMessageToCareTaker().values());

    }


    @NonNull
    @Override
    public RecyclerView.ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view;
        view = LayoutInflater.from(parent.getContext()).inflate(R.layout.item_log, parent, false);
        return new VoiceMessageViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull RecyclerView.ViewHolder holder, int position) {
        VoiceMessage voiceMessage = voiceMessageList.get(position);
        ((VoiceMessageViewHolder)holder).bind(voiceMessage);
    }

    @Override
    public int getItemCount() {
        return voiceMessageList.size();
    }

    class VoiceMessageViewHolder extends RecyclerView.ViewHolder {

        TextView messageTextView;
        TextView timeTextView;

        public VoiceMessageViewHolder(View itemView) {
            super(itemView);
            messageTextView = itemView.findViewById(R.id.voice_message_tv);
            timeTextView = itemView.findViewById(R.id.voice_message_time_tv);
        }

        public void bind(final VoiceMessage voiceMessage) {

            // 파싱: timestamp 에서 String value
            Calendar cal = Calendar.getInstance();
            cal.setTimeInMillis(voiceMessage.getTimestamp());
            timeTextView.setText(fmt.format(cal.getTime()));

            messageTextView.setText(voiceMessage.getMessage());

        }
    }
}
