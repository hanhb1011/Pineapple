package com.example.hanhb.caretaker_android;

import android.content.Context;
import android.support.annotation.NonNull;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import java.text.SimpleDateFormat;
import java.util.HashMap;
import java.util.Map;

public class LogAdapter extends RecyclerView.Adapter {

    private Context context;
    private HashMap<String, RouteNavigation> navigationLog = MainActivity.user.getNavigationLog();
    private SimpleDateFormat fmt = new SimpleDateFormat("HH:mm");

    public LogAdapter(Context context) {
        this.context = context;
    }

    @NonNull
    @Override
    public RecyclerView.ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {

        View view;
        view = LayoutInflater.from(parent.getContext()).inflate(R.layout.item_log, parent, false);
        return new LogViewHolder(view);

    }

    @Override
    public void onBindViewHolder(@NonNull RecyclerView.ViewHolder holder, int position) {

    }

    @Override
    public int getItemCount() {
        return navigationLog.size();
    }



    class LogViewHolder extends RecyclerView.ViewHolder {


        public LogViewHolder(View itemView) {
            super(itemView);
        }

        public void bind(RouteNavigation routeNavigation) {



        }

    }
}
