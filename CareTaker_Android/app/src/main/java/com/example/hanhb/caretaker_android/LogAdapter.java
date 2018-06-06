package com.example.hanhb.caretaker_android;

import android.content.Context;
import android.os.Message;
import android.support.annotation.NonNull;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.skt.Tmap.TMapData;
import com.skt.Tmap.TMapMarkerItem;
import com.skt.Tmap.TMapPoint;
import com.skt.Tmap.TMapTapi;
import com.skt.Tmap.TMapView;

import org.xml.sax.SAXException;

import java.io.IOException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import javax.xml.parsers.ParserConfigurationException;

/*
    경로 탐색 기록을 RecyclerView로 보여주기 위한 Adapter
 */

public class LogAdapter extends RecyclerView.Adapter {

    private Context context;
    private List<RouteNavigation> logList;
    private SimpleDateFormat fmt = new SimpleDateFormat("yyyy-mm-dd HH:mm");
    private TMapData tMapData;
    private TMapTapi tmaptapi;

    public LogAdapter(Context context) {
        this.context = context;
        logList = new ArrayList<>();
        //사용하기 쉽도록 HashMap에서 List로 변환한다.
        logList.addAll(MainActivity.user.getNavigationLog().values());

        //TODO 시간 순으로 sort
        //logList.sort((a,b)->a.getTimestamp() - b.getTimestamp());


        //Tmap 초기화
        String tmapKey = context.getResources().getString(R.string.TMAP_API_KEY);
        tmaptapi = new TMapTapi(context);
        tmaptapi.setSKTMapAuthentication(tmapKey);
        tMapData = new TMapData();

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
        RouteNavigation log = logList.get(position);
        ((LogViewHolder)holder).bind(log);
    }

    @Override
    public int getItemCount() {
        return logList.size();
    }


    class LogViewHolder extends RecyclerView.ViewHolder {

        TextView nameTextView;
        TextView timeTextView;
        TextView addressTextView;
        TextView distanceTextView;
        LinearLayout tmapLayout;
        boolean updateLayout = true;

        TMapView tMapView;


        public LogViewHolder(View itemView) {
            super(itemView);

            nameTextView = itemView.findViewById(R.id.dst_name_tv);
            timeTextView = itemView.findViewById(R.id.time_tv);
            addressTextView = itemView.findViewById(R.id.dst_addr_tv);
            distanceTextView = itemView.findViewById(R.id.dst_dist_tv);
            tmapLayout = itemView.findViewById(R.id.tmap_layout_in_log);
            tMapView = new TMapView(context);
            tMapView.setSKTMapApiKey(context.getResources().getString(R.string.TMAP_API_KEY));


        }

        public void bind(final RouteNavigation routeNavigation) {

            // 파싱: timestamp 에서 String value
            Calendar cal = Calendar.getInstance();
            cal.setTimeInMillis(routeNavigation.getTimestamp());
            timeTextView.setText(fmt.format(cal.getTime()));

            nameTextView.setText(routeNavigation.getDstName());
            addressTextView.setText(routeNavigation.getDstAddress());

            StringBuilder distance = new StringBuilder(String.valueOf(routeNavigation.getDstDistance())).append("M");
            distanceTextView.setText(distance);

            //뷰 클릭 시 상세경로를 지도로 보여준다.
            itemView.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    tmapLayout.setVisibility(View.VISIBLE);

                    new Thread(){
                        @Override
                        public void run() {
                            super.run();

                            try {
                                tMapView.setCenterPoint(routeNavigation.getSrcLongitude(), routeNavigation.getSrcLatitude());
                                tmapLayout.addView(tMapView);

                            } catch (Exception e) {
                                e.printStackTrace();
                            }

                        }

                    }.start();

                }
            });

        }

    }
}
