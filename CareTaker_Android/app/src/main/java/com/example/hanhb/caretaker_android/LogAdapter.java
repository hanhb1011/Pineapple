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
import com.skt.Tmap.TMapPolyLine;
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
    private SimpleDateFormat fmt = new SimpleDateFormat("yyyy/MM/dd HH:mm");
    private TMapData tMapData;
    private TMapTapi tmaptapi;

    public LogAdapter(Context context) {
        this.context = context;
        logList = new ArrayList<>();
        //사용하기 쉽도록 HashMap에서 List로 변환한다.
        if(MainActivity.user.getNavigationLog() !=null)
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

        public LogViewHolder(View itemView) {
            super(itemView);

            nameTextView = itemView.findViewById(R.id.dst_name_tv);
            timeTextView = itemView.findViewById(R.id.time_tv);
            addressTextView = itemView.findViewById(R.id.dst_addr_tv);
            distanceTextView = itemView.findViewById(R.id.dst_dist_tv);

        }

        public void bind(final RouteNavigation routeNavigation) {

            // 파싱: timestamp 에서 String value
            Calendar cal = Calendar.getInstance();
            cal.setTimeInMillis(routeNavigation.getTimestamp());
            timeTextView.setText(fmt.format(cal.getTime()));

            nameTextView.setText(routeNavigation.getDstName());
            String correctedAddress = routeNavigation.getDstAddress().replace("null", "");
            addressTextView.setText(correctedAddress);

            //거리 표시
            try {
                StringBuilder distance = new StringBuilder(String.format("%.2f",routeNavigation.getDstDistance())).append(" m");
                distanceTextView.setText(distance);
            } catch (Exception e){}


            //클릭할 경우 경로 그리기
            itemView.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    List <Node> nodes = routeNavigation.getNodes();
                    TMapView tMapView = LogActivity.tMapView;
                    tMapView.setCenterPoint(routeNavigation.getSrcLongitude(),routeNavigation.getSrcLatitude(),true); //지도 중심을 출발지로 정한다.

                    if(nodes == null || tMapView ==null || nodes.size() < 1){
                        Toast.makeText(context, "경로 정보가 존재하지 않습니다.", Toast.LENGTH_SHORT).show();
                        return; //경로 정보가 없거나 TMap view가 존재하지 않을 경우 리턴
                    }

                    //경로 그리기 : 노드 List로부터 PolyLine을 생성하고 Tmap에 적용시킨다.
                    tMapView.removeAllTMapPolyLine();
                    TMapPoint prevPoint = new TMapPoint(nodes.get(0).getLatitude(), nodes.get(0).getLongitude());

                    for(int i=1; i<nodes.size(); i++) {
                        TMapPoint currentPoint = new TMapPoint(nodes.get(i).getLatitude(), nodes.get(i).getLongitude());
                        TMapPolyLine tMapPolyLine = new TMapPolyLine();
                        tMapPolyLine.addLinePoint(prevPoint);
                        tMapPolyLine.addLinePoint(currentPoint);
                        tMapView.addTMapPolyLine(tMapPolyLine.toString(), tMapPolyLine);

                        //마지막엔 prev <- current
                        prevPoint = currentPoint;
                    }

                }
            });
        }

    }
}
