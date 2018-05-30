package org.androidtown.pineapple_android;

import android.app.DialogFragment;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.constraint.ConstraintLayout;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.LinearLayout;

import com.skt.Tmap.TMapMarkerItem;
import com.skt.Tmap.TMapPoint;
import com.skt.Tmap.TMapPolyLine;
import com.skt.Tmap.TMapView;

import org.androidtown.pineapple_android.Util.Navigation;

/**
 * Created by MSI on 2018-05-27.
 */

public class PathFragment extends DialogFragment {

    LinearLayout mLinearLayoutTmap;
    private ConstraintLayout constraintLayout;
    TMapView tMapView;
    private static String mKey = "345ff3b1-839d-47a2-860f-de2d9dc3acd8";
    Navigation navigation;

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_path,container, false);
        bindView(view);
        navigation = MainActivity.navi;
        drawWayInMap();

        constraintLayout.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                dismiss();
            }
        });

        return view;
    }



    private void bindView(View view) {
        constraintLayout = view.findViewById(R.id.path_constraintlayout_in_fragment);
        mLinearLayoutTmap = view.findViewById(R.id.linearLayoutTmap);

        tMapView = new TMapView(view.getContext());
        tMapView.setSKTMapApiKey(mKey);
        tMapView.setCenterPoint(navigation.getCurrentX(),navigation.getCurrentY());
        mLinearLayoutTmap.addView(tMapView);
    }

    public void drawWayInMap() { //수정
        setMarker();
        int i=0;
        if(navigation.gettMapPolyLines()!=null) {
            for (TMapPolyLine l : navigation.gettMapPolyLines()) {
                tMapView.addTMapPolyLine("line" + i, l);
                i++;
            }
        }
        tMapView.refreshMap();
    }

    public void setMarker(){
        TMapMarkerItem startItem = new TMapMarkerItem();
        startItem.setTMapPoint(new TMapPoint(navigation.getCurrentX(),navigation.getCurrentY()));
        startItem.setName("현재위치");
        startItem.setVisible(TMapMarkerItem.VISIBLE);

        TMapMarkerItem endItem = new TMapMarkerItem();
        endItem.setTMapPoint(new TMapPoint(navigation.getEndY(),navigation.getEndX()));
        endItem.setName("도착지");
        endItem.setVisible(TMapMarkerItem.VISIBLE);


        tMapView.addMarkerItem("현재위치",startItem);
        tMapView.addMarkerItem("도착지",endItem);
    }

    @Override
    public void onDestroyView() {
        mLinearLayoutTmap.removeAllViews();
        super.onDestroyView();
    }
}
