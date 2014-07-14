package com.discoverylab.ripple.android.activity;

import android.app.Activity;
import android.os.Bundle;

import com.discoverylab.ripple.android.R;
import com.discoverylab.ripple.android.fragment.LaunchFragment;

/**
 * LaunchActivity contains the first screen that the user will see when launching the application.
 */
public class LaunchActivity extends Activity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_launch);
        if (savedInstanceState == null) {
            getFragmentManager().beginTransaction()
                    .add(R.id.banner_container, LaunchFragment.newInstance())
                    .commit();
        }
    }
}
