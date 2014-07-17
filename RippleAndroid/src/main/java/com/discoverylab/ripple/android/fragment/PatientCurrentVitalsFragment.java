package com.discoverylab.ripple.android.fragment;


import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.discoverylab.ripple.android.R;

/**
 * Fragment to display the patient's current vitals in the Scenario view.
 * <p/>
 * Use the {@link PatientCurrentVitalsFragment#newInstance} factory method to
 * create an instance of this fragment.
 */
public class PatientCurrentVitalsFragment extends Fragment {

    /**
     * Use this factory method to create a new instance of
     * this fragment using the provided parameters.
     *
     * @return A new instance of fragment PatientCurrentVitalsFragment.
     */
    public static PatientCurrentVitalsFragment newInstance() {
        PatientCurrentVitalsFragment fragment = new PatientCurrentVitalsFragment();
        return fragment;
    }

    public PatientCurrentVitalsFragment() {
        // Required empty public constructor
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        return inflater.inflate(R.layout.fragment_patient_current_vitals, container, false);
    }


}
