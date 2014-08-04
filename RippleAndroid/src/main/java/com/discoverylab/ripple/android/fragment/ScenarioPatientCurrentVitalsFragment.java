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

    // text to display when a vital is not available
    private static String VITAL_NOT_AVAILABLE = "N/A";

    // View references
    private TextView temperatureText;
    private TextView spO2Text;
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
        return new ScenarioPatientCurrentVitalsFragment();
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
        this.spO2Text = (TextView) v.findViewById(R.id.current_vitals_sp02_value);
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
        this.spO2Text = null;
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
            this.temperatureText.setText(VITAL_NOT_AVAILABLE);
            this.spO2Text.setText(VITAL_NOT_AVAILABLE);
            this.heartRateText.setText(VITAL_NOT_AVAILABLE);
            this.systolicText.setText(VITAL_NOT_AVAILABLE);
            this.diastolicText.setText(VITAL_NOT_AVAILABLE);
            this.respirationText.setText(VITAL_NOT_AVAILABLE);
            this.painText.setText(VITAL_NOT_AVAILABLE);
        } else {
            // get values from object
            int temperature = p.getTemperature();
            if (temperature < 0 || temperature > 150) {
                this.temperatureText.setText(VITAL_NOT_AVAILABLE);
            } else {
                this.temperatureText.setText(temperature + " F");
            }

            int o2 = p.getO2();
            if (o2 < 0 || o2 > 100) {
                this.spO2Text.setText(VITAL_NOT_AVAILABLE);
            } else {
                this.spO2Text.setText(o2 + "%");
            }

            int heartRate = p.getHeartRate();
            if (heartRate < 0 || heartRate > 250) {
                this.heartRateText.setText(VITAL_NOT_AVAILABLE);
            } else {
                this.heartRateText.setText(heartRate + " BPM");
            }
            // TODO: add blood pressure values to patient
            this.systolicText.setText(VITAL_NOT_AVAILABLE);
            this.diastolicText.setText(VITAL_NOT_AVAILABLE);

            int respiration = p.getBreathsPerMin();
            if (respiration < 0 || respiration > 70) {
                this.respirationText.setText(VITAL_NOT_AVAILABLE);
            } else {
                this.respirationText.setText(p.getBreathsPerMin() + "");
            }
            // TODO: add pain values to patient
            this.painText.setText(VITAL_NOT_AVAILABLE);

        }
    }
}
