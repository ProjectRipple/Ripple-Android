package com.discoverylab.ripple.android.fragment;


import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.discoverylab.ripple.android.R;
import com.discoverylab.ripple.android.object.Patient;

/**
 * Fragment to display the patient's current vitals in the Scenario view.
 * <p/>
 * Use the {@link ScenarioPatientCurrentVitalsFragment#newInstance} factory method to
 * create an instance of this fragment.
 */
public class ScenarioPatientCurrentVitalsFragment extends Fragment {


    private TextView temperatureText;
    private TextView sp02Text;
    private TextView heartRateText;
    private TextView systolicText;
    private TextView diastolicText;
    private TextView respirationText;
    private TextView painText;


    /**
     * Use this factory method to create a new instance of
     * this fragment using the provided parameters.
     *
     * @return A new instance of fragment PatientCurrentVitalsFragment.
     */
    public static ScenarioPatientCurrentVitalsFragment newInstance() {
        ScenarioPatientCurrentVitalsFragment fragment = new ScenarioPatientCurrentVitalsFragment();
        return fragment;
    }

    public ScenarioPatientCurrentVitalsFragment() {
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
        View v = inflater.inflate(R.layout.fragment_patient_current_vitals, container, false);

        // get views
        this.temperatureText = (TextView) v.findViewById(R.id.current_vitals_temperature_value);
        this.sp02Text = (TextView) v.findViewById(R.id.current_vitals_sp02_value);
        this.heartRateText = (TextView) v.findViewById(R.id.current_vitals_heartrate_value);
        this.systolicText = (TextView) v.findViewById(R.id.current_vitals_systolic_value);
        this.diastolicText = (TextView) v.findViewById(R.id.current_vitals_diastolic_value);
        this.respirationText = (TextView) v.findViewById(R.id.current_vitals_respiration_value);
        this.painText = (TextView) v.findViewById(R.id.current_vitals_pain_value);

        return v;
    }

    @Override
    public void onDestroyView() {
        super.onDestroyView();
        // remove view references
        this.temperatureText = null;
        this.sp02Text = null;
        this.heartRateText = null;
        this.systolicText = null;
        this.diastolicText = null;
        this.respirationText = null;
        this.painText = null;
    }

    public void updateVitals(Patient p) {
        if (getView() == null) {
            // view destroyed so nothing to update
            return;
        }
        if (p == null) {
            // set default values
            this.temperatureText.setText("N/A");
            this.sp02Text.setText("N/A");
            this.heartRateText.setText("N/A");
            this.systolicText.setText("N/A");
            this.diastolicText.setText("N/A");
            this.respirationText.setText("N/A");
            this.painText.setText("N/A");
        } else {
            // get values from object
            this.temperatureText.setText(p.getTemperature() + " F");
            this.sp02Text.setText(p.getO2() + "%");
            this.heartRateText.setText(p.getHeartRate() + " BPM");
            // TODO: add blood pressure values to patient
            this.systolicText.setText("N/A");
            this.diastolicText.setText("N/A");
            this.respirationText.setText(p.getBreathsPerMin() + "");
            // TODO: add pain values to patient
            this.painText.setText("N/A");

        }
    }
}
