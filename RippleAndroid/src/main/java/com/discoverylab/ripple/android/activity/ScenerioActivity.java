package com.discoverylab.ripple.android.activity;

import android.app.Activity;
import android.os.Bundle;

import com.discoverylab.ripple.android.R;
import com.discoverylab.ripple.android.fragment.PatientBannerFragment;

public class ScenerioActivity extends Activity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_scenerio);
        if (savedInstanceState == null) {
            getFragmentManager().beginTransaction()
                    .add(R.id.banner_container, PatientBannerFragment.newInstance())
                    .commit();
        }
    }

}
