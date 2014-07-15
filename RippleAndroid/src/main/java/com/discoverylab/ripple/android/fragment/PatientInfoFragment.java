package com.discoverylab.ripple.android.fragment;


import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.discoverylab.ripple.android.R;

/**
 * A simple {@link Fragment} subclass.
 * Use the {@link PatientInfoFragment#newInstance} factory method to
 * create an instance of this fragment.
 */
public class PatientInfoFragment extends Fragment {

    /**
     * Use this factory method to create a new instance of
     * this fragment using the provided parameters.
     *
     * @return A new instance of fragment PatientInfoFragment.
     */
    public static PatientInfoFragment newInstance() {
        PatientInfoFragment fragment = new PatientInfoFragment();

        return fragment;
    }

    public PatientInfoFragment() {
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
        View v = inflater.inflate(R.layout.fragment_patient_info, container, false);

        return v;
    }


}
