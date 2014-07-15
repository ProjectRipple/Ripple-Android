package com.discoverylab.ripple.android.activity;



import android.os.Bundle;
import android.support.v4.app.FragmentActivity;

import com.discoverylab.ripple.android.R;
import com.discoverylab.ripple.android.fragment.PatientBannerFragment;
import com.discoverylab.ripple.android.fragment.ScenarioPatientFragment;

public class ScenarioActivity extends FragmentActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_scenerio);
        if (savedInstanceState == null) {
            getFragmentManager().beginTransaction()
                    .add(R.id.banner_container, PatientBannerFragment.newInstance())
                    .commit();


            getSupportFragmentManager().beginTransaction()
                    .add(R.id.patient_scenario_container, ScenarioPatientFragment.newInstance())
                    .commit();
        }
    }

}
