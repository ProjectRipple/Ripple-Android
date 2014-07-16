package com.discoverylab.ripple.android.fragment;


import android.os.Bundle;
import android.support.v4.app.DialogFragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.discoverylab.ripple.android.R;

/**
 * Use the {@link PatientNoteFragment#newInstance} factory method to
 * create an instance of this fragment.
 */
public class PatientNoteFragment extends DialogFragment {

    /**
     * Use this factory method to create a new instance of
     * this fragment using the provided parameters.
     *
     * @return A new instance of fragment PatientNoteFragment.
     */
    public static PatientNoteFragment newInstance() {
        PatientNoteFragment fragment = new PatientNoteFragment();
        return fragment;
    }

    public PatientNoteFragment() {
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
        return inflater.inflate(R.layout.fragment_patient_note, container, false);
    }


}
