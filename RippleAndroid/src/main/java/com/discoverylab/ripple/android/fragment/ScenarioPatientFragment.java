package com.discoverylab.ripple.android.fragment;


import android.content.Intent;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.discoverylab.ripple.android.R;
import com.discoverylab.ripple.android.object.Patient;

/**
 * Fragment holding lower part of the scenario view.
 * <p/>
 * Use the {@link ScenarioPatientFragment#newInstance} factory method to
 * create an instance of this fragment.
 */
public class ScenarioPatientFragment extends Fragment {

    private static final String PATIENT_NOTE_FRAG_TAG = "ScenarioPatientNoteFrag";
    private static final String PATIENT_INFO_FRAG_TAG = "ScenarioPatientInfoFrag";
    private static final String PATIENT_CURRENT_VITALS_FRAG_TAG = "ScenarioPatientCurrentVitalsFrag";
    // Reference to currently selected patient
    private Patient selectedPatient = null;
    // Fragment references
    private ScenarioNoteFragment noteFragment;
    private PatientInfoFragment infoFragment;
    private PatientCurrentVitalsFragment currentVitalsFragment;


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
        View v = inflater.inflate(R.layout.fragment_scenario_patient, container, false);


        return v;
    }

    @Override
    public void onStart() {
        super.onStart();
        FragmentManager fragmentManager = getChildFragmentManager();

        // Get child fragments

        this.noteFragment = (ScenarioNoteFragment) fragmentManager.findFragmentByTag(PATIENT_NOTE_FRAG_TAG);

        if (this.noteFragment == null) {

            this.noteFragment = ScenarioNoteFragment.newInstance();
            fragmentManager.beginTransaction()
                    .add(R.id.scenario_patient_note_container,
                            this.noteFragment,
                            PATIENT_NOTE_FRAG_TAG)
                    .commit();
        }

        this.infoFragment = (PatientInfoFragment) fragmentManager.findFragmentByTag(PATIENT_INFO_FRAG_TAG);

        if (this.infoFragment == null) {

            this.infoFragment = PatientInfoFragment.newInstance();
            fragmentManager.beginTransaction()
                    .add(R.id.scenario_patient_info_container,
                            this.infoFragment,
                            PATIENT_INFO_FRAG_TAG)
                    .commit();
        }

        this.currentVitalsFragment = (PatientCurrentVitalsFragment) fragmentManager.findFragmentByTag(PATIENT_CURRENT_VITALS_FRAG_TAG);

        if (this.currentVitalsFragment == null) {

            this.currentVitalsFragment = PatientCurrentVitalsFragment.newInstance();
            fragmentManager.beginTransaction()
                    .add(R.id.scenario_patient_current_vitals_container,
                            this.currentVitalsFragment,
                            PATIENT_CURRENT_VITALS_FRAG_TAG)
                    .commit();
        }
    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        // pass result to fragments
        for (Fragment fragment : getChildFragmentManager().getFragments()) {
            fragment.onActivityResult(requestCode, resultCode, data);
        }
    }

    public void setSelectedPatient(Patient p) {


        if (this.selectedPatient != null) {
            this.selectedPatient.setSelected(false);
            if (p != null && this.selectedPatient.getPatientId().equals(p.getPatientId())) {
                p.setSelected(false);
                // deselecting patient
                p = null;
            }
        }

        if (p != null) {
            // ensure p is selected
            p.setSelected(true);
        }

        this.selectedPatient = p;
        this.currentVitalsFragment.updateVitals(this.selectedPatient);
        this.infoFragment.setPatient(this.selectedPatient);

    }

    public Patient getSelectedPatient() {
        return this.selectedPatient;
    }

    public void updatePatientVitals() {
        this.currentVitalsFragment.updateVitals(this.selectedPatient);
    }

    public void updatePatientInfo() {
        // TODO: do this in a way that merges with local edit if possible.
        this.infoFragment.setPatient(this.selectedPatient);
    }
}
