package com.discoverylab.ripple.android.fragment;


import android.app.Fragment;
import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.Toast;

import com.discoverylab.ripple.android.R;
import com.discoverylab.ripple.android.activity.NfcActivity;
import com.discoverylab.ripple.android.activity.s2013.PatientDetailsActivity;
import com.discoverylab.ripple.android.activity.PrefsActivity;
import com.discoverylab.ripple.android.activity.ScenarioActivity;

/**
 * Fragment contained the {@link com.discoverylab.ripple.android.activity.LaunchActivity}
 * Use the {@link LaunchFragment#newInstance} factory method to
 * create an instance of this fragment.
 */
public class LaunchFragment extends Fragment implements View.OnClickListener {
    private static final String TAG = LaunchFragment.class.getSimpleName();

    /**
     * Use this factory method to create a new instance of
     * this fragment using the provided parameters.
     *
     * @return A new instance of fragment LaunchFragment.
     */
    public static LaunchFragment newInstance() {
        LaunchFragment fragment = new LaunchFragment();
        return fragment;
    }

    public LaunchFragment() {
        // Required empty public constructor
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View v = inflater.inflate(R.layout.fragment_launch, container, false);
        // get views
        Button actionPlanBtn = (Button) v.findViewById(R.id.launch_action_plan);
        Button scenarioBtn = (Button) v.findViewById(R.id.launch_scenario);
        Button extraBtn = (Button) v.findViewById(R.id.launch_extra);
        Button settingsBtn = (Button) v.findViewById(R.id.launch_settings);

        // set buttons' on click
        actionPlanBtn.setOnClickListener(this);
        scenarioBtn.setOnClickListener(this);
        extraBtn.setOnClickListener(this);
        settingsBtn.setOnClickListener(this);


        return v;
    }


    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.launch_action_plan:
                Toast.makeText(getActivity(), "Not implemented yet.", Toast.LENGTH_SHORT).show();
                break;
            case R.id.launch_extra:
                Toast.makeText(getActivity(), "Surprise!", Toast.LENGTH_SHORT).show();
                // Currently launch the old interface
                startActivity(new Intent(getActivity(), PatientDetailsActivity.class));
                break;
            case R.id.launch_scenario:
                startActivity(new Intent(getActivity(), ScenarioActivity.class));
                break;
            case R.id.launch_settings:
                startActivity(new Intent(getActivity(), PrefsActivity.class));
                break;
            default:
                Log.e(TAG, "Unknown view clicked!");
        }
    }
}
