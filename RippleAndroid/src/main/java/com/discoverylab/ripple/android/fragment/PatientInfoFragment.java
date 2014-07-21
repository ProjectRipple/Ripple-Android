package com.discoverylab.ripple.android.fragment;


import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.EditText;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;

import com.discoverylab.ripple.android.R;
import com.discoverylab.ripple.android.adapter.ui.ColorSpinnerAdapter;

import java.util.ArrayList;
import java.util.List;

/**
 * Fragment to handle patient information entered by the user.
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

        // get color spinner
        Spinner triageColor = (Spinner) v.findViewById(R.id.patient_triage_color_spinner);
        // get colors for spinner
        int[] colorsArray = getResources().getIntArray(R.array.triage_color_options);
        List<Integer> colors = new ArrayList<Integer>(colorsArray.length);
        for (int c : colorsArray) {
            colors.add(c);
        }
        // set adapter & listener
        triageColor.setAdapter(new ColorSpinnerAdapter(getActivity(), colors));
        triageColor.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                onTriageColorChanged((Integer) parent.getItemAtPosition(position));
            }

            @Override
            public void onNothingSelected(AdapterView<?> parent) {
                // do nothing for now
            }
        });

        // get status, sex, and nbc spinners
        Spinner status = (Spinner) v.findViewById(R.id.patient_status_spinner);
        status.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                onPatientStatusChanged((String) parent.getItemAtPosition(position));
            }

            @Override
            public void onNothingSelected(AdapterView<?> parent) {
                // do nothing for now
            }
        });

        Spinner patientSex = (Spinner) v.findViewById(R.id.patient_sex_spinner);
        patientSex.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                onGenderChanged((String) parent.getItemAtPosition(position));
            }

            @Override
            public void onNothingSelected(AdapterView<?> parent) {
                // do nothing for now
            }
        });

        Spinner nbc = (Spinner) v.findViewById(R.id.patient_nbc_spinner);
        nbc.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                onNbcChanged((String) parent.getItemAtPosition(position));
            }

            @Override
            public void onNothingSelected(AdapterView<?> parent) {
                // do nothing for now
            }
        });

        // get text fields
        EditText patientName = (EditText) v.findViewById(R.id.patient_name_text);

        EditText patientAge = (EditText) v.findViewById(R.id.patient_age_text);


        return v;
    }

    private void onTriageColorChanged(int triageColor) {
        //Toast.makeText(getActivity(), "triage Color: " + Integer.toHexString(triageColor), Toast.LENGTH_SHORT).show();
    }

    private void onPatientStatusChanged(String status) {
        //Toast.makeText(getActivity(), "status: " + status, Toast.LENGTH_SHORT).show();
    }

    private void onGenderChanged(String gender) {
        //Toast.makeText(getActivity(), "Gender: " + gender, Toast.LENGTH_SHORT).show();
    }

    private void onNbcChanged(String nbcStatus) {
        //Toast.makeText(getActivity(), "NBC: " + nbcStatus, Toast.LENGTH_SHORT).show();
    }

}
