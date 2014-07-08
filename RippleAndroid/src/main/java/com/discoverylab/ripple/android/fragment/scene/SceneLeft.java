package com.discoverylab.ripple.android.fragment.scene;

import android.app.Activity;
import android.app.Fragment;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;

import com.discoverylab.ripple.android.ActivityClickInterface;
import com.discoverylab.ripple.android.R;

/**
 * Created by Brandon on 6/17/13.
 */
public class SceneLeft extends Fragment implements View.OnClickListener {

    ActivityClickInterface aci;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.scene_left, container, false);
//        Button button = (Button) view.findViewById(R.id.button1);
//        button.setOnClickListener(this);
        return view;
    }

    @Override
    public void onAttach(Activity activity) {
        super.onAttach(activity);
        aci = (ActivityClickInterface) activity;
    }

    @Override
    public void onClick(View view) {
        aci.onClickListener(view.getId());
    }
}
