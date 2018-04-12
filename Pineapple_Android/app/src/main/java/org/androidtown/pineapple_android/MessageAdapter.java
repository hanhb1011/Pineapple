package org.androidtown.pineapple_android;

import android.content.Context;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import java.text.SimpleDateFormat;
import java.util.ArrayList;

/**
 * Created by hanhb on 2018-04-12.
 */


public class MessageAdapter extends RecyclerView.Adapter {


    private Context context;
    private ArrayList<Message> messageList;
    private SimpleDateFormat fmt = new SimpleDateFormat("HH:mm");

    MessageAdapter(Context context) {
        this.context = context;
    }

    MessageAdapter(Context context, ArrayList<Message> messageList) {
        this.context = context;
        this.messageList = messageList;
    }

    @Override
    public int getItemViewType(int position) {
        Message message = messageList.get(position);
        return message.getMessageType();
    }

    @Override
    public RecyclerView.ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View view;

        switch (viewType) {

            case GroupConstants.MY_MESSAGE:
                view = LayoutInflater.from(parent.getContext()).inflate(R.layout.item_my_message, parent, false);
                return new MyMessageHolder(view);

            case GroupConstants.BOT_MESSAGE:
                view = LayoutInflater.from(parent.getContext()).inflate(R.layout.item_bot_message, parent, false);
                return new BotMessageHolder(view);

        }

        return null;
    }

    @Override
    public void onBindViewHolder(RecyclerView.ViewHolder holder, int position) {
        Message message = messageList.get(position);

        switch (holder.getItemViewType()) {

            case GroupConstants.MY_MESSAGE :
                ((MyMessageHolder)holder).bind(message);
                break;

            case GroupConstants.BOT_MESSAGE :
                ((BotMessageHolder)holder).bind(message);
                break;
        }

    }

    @Override
    public int getItemCount() {
        return messageList.size();
    }

    public void setMessageList(ArrayList<Message> messageList) {
        this.messageList = messageList;
    }

    public void addMessage(Message message) {
        this.messageList.add(message);
    }


    private class MyMessageHolder extends RecyclerView.ViewHolder {

        public MyMessageHolder(View itemView) {
            super(itemView);


        }

        public void bind(Message message) {





        }
    }

    private class BotMessageHolder extends RecyclerView.ViewHolder {

        public BotMessageHolder(View itemView) {
            super(itemView);


        }

        public void bind(Message message) {




        }
    }
}
