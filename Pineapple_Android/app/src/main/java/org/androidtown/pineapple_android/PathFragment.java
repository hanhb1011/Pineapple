package org.androidtown.pineapple_android;

import android.app.DialogFragment;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.constraint.ConstraintLayout;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.LinearLayout;

/**
 * Created by MSI on 2018-05-27.
 */

public class PathFragment extends DialogFragment {

    LinearLayout mLinearLayoutTmap;
    private ConstraintLayout constraintLayout;

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_path,container, false);
        bindView(view);

        if(MainActivity.navi.gettMapView().getParent()!=null){
            ((ViewGroup)MainActivity.navi.gettMapView().getParent()).removeView(MainActivity.navi.gettMapView());
        }
        mLinearLayoutTmap.addView(MainActivity.navi.gettMapView());

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
    }

    @Override
    public void onDestroyView() {
        mLinearLayoutTmap.removeAllViews();
        super.onDestroyView();
    }
}
