package com.discoverylab.ripple.android.activity;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;

import com.discoverylab.ripple.android.R;
import com.discoverylab.ripple.android.fragment.CameraFragment;

public class CameraActivity extends Activity {

    // Key for sending a string in the intent extras
    public static final String ID_EXTRA = "ID";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_camera);
        setResult(Activity.RESULT_CANCELED, null);

        Bundle extras = getIntent().getExtras();
        String id = null;
        if(extras != null) {
            id = extras.getString(ID_EXTRA);
        }

        if (savedInstanceState == null) {
            getFragmentManager().beginTransaction()
                    .add(R.id.container, CameraFragment.newInstance(id))
                    .commit();
        }
    }



}
