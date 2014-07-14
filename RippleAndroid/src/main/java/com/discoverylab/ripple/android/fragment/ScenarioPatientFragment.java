package com.discoverylab.ripple.android.fragment;




import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.discoverylab.ripple.android.R;

/**
 * A simple {@link Fragment} subclass.
 * Use the {@link ScenarioPatientFragment#newInstance} factory method to
 * create an instance of this fragment.
 *
 */
public class ScenarioPatientFragment extends Fragment {

    private static final String PATIENT_NOTE_FRAG_TAG = "ScenarioPatientNoteFrag";
    private static final String PATIENT_INFO_FRAG_TAG = "ScenarioPatientInfoFrag";
    private static final String PATIENT_CURRENT_VITALS_FRAG_TAG = "ScenarioPatientCurrentVitalsFrag";


    /**
     * Use this factory method to create a new instance of
     * this fragment using the provided parameters.
     *
     * @return A new instance of fragment ScenarioPatientFragment.
     */
    public static ScenarioPatientFragment newInstance() {
        ScenarioPatientFragment fragment = new ScenarioPatientFragment();
        return fragment;
    }
    public ScenarioPatientFragment() {
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
        View v =  inflater.inflate(R.layout.fragment_scenario_patient, container, false);


        return v;
    }

    @Override
    public void onStart() {
        super.onStart();
        FragmentManager fragmentManager = getChildFragmentManager();

        // Get child fragments

        ScenarioNoteFragment noteFragment = (ScenarioNoteFragment) fragmentManager.findFragmentByTag(PATIENT_NOTE_FRAG_TAG);

        if(noteFragment == null){

            fragmentManager.beginTransaction()
                    .add(R.id.scenario_patient_note_container,
                            ScenarioNoteFragment.newInstance(),
                            PATIENT_NOTE_FRAG_TAG)
                    .commit();
        }

        PatientInfoFragment infoFragment = (PatientInfoFragment) fragmentManager.findFragmentByTag(PATIENT_INFO_FRAG_TAG);

        if(infoFragment == null){

            fragmentManager.beginTransaction()
                    .add(R.id.scenario_patient_info_container,
                            PatientInfoFragment.newInstance(),
                            PATIENT_INFO_FRAG_TAG)
                    .commit();
        }

        PatientCurrentVitalsFragment currentVitalsFragment = (PatientCurrentVitalsFragment) fragmentManager.findFragmentByTag(PATIENT_CURRENT_VITALS_FRAG_TAG);

        if(currentVitalsFragment == null){

            fragmentManager.beginTransaction()
                    .add(R.id.scenario_patient_current_vitals_container,
                            PatientCurrentVitalsFragment.newInstance(),
                            PATIENT_CURRENT_VITALS_FRAG_TAG)
                    .commit();
        }





    }
}
