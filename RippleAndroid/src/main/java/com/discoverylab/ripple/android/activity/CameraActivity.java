package com.discoverylab.ripple.android.activity;

import android.app.Activity;
import android.os.Bundle;

import com.discoverylab.ripple.android.R;
import com.discoverylab.ripple.android.fragment.CameraFragment;

public class CameraActivity extends Activity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_camera);
        setResult(Activity.RESULT_CANCELED, null);
        if (savedInstanceState == null) {
            getFragmentManager().beginTransaction()
                    .add(R.id.container, CameraFragment.newInstance())
                    .commit();
        }
    }



}
