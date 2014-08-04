package com.discoverylab.ripple.android.fragment;


import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.text.Editable;
import android.text.TextWatcher;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Spinner;
import android.widget.SpinnerAdapter;

import com.discoverylab.ripple.android.R;
import com.discoverylab.ripple.android.activity.ScenarioActivity;
import com.discoverylab.ripple.android.adapter.ui.ColorSpinnerAdapter;
import com.discoverylab.ripple.android.adapter.ui.NBCSpinnerAdapter;
import com.discoverylab.ripple.android.adapter.ui.PatientStatusSpinnerAdapter;
import com.discoverylab.ripple.android.config.Common;
import com.discoverylab.ripple.android.config.JSONTag;
import com.discoverylab.ripple.android.object.Patient;
import com.discoverylab.ripple.android.util.Util;
import com.google.gson.JsonObject;

import java.text.DateFormat;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Date;
import java.util.List;

/**
 * Fragment to handle patient information entered by the user.
 * Use the {@link ScenarioPatientInfoFragment#newInstance} factory method to
 * create an instance of this fragment.
 */
public class ScenarioPatientInfoFragment extends Fragment implements View.OnClickListener {

    private static final String TAG = ScenarioPatientInfoFragment.class.getSimpleName();
    // Temporary save button (until a better sync method is implemented)
    private Button saveButton;
    private Spinner triageColor;
    private Spinner status;
    private Spinner patientSex;
    private Spinner nbc;
    private EditText patientName;
    private EditText patientAge;
    // Reference to currently selected patient
    private Patient selectedPatient;

    /**
     * Use this factory method to create a new instance of
     * this fragment using the provided parameters.
     *
     * @return A new instance of fragment PatientInfoFragment.
     */
    public static ScenarioPatientInfoFragment newInstance() {
        return new ScenarioPatientInfoFragment();
    }

    public ScenarioPatientInfoFragment() {
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
        this.triageColor = (Spinner) v.findViewById(R.id.patient_triage_color_spinner);
        // get spinner colors
        Common.TRIAGE_COLORS[] colorsArray = Common.TRIAGE_COLORS.values();
        List<Common.TRIAGE_COLORS> colorsList = new ArrayList<Common.TRIAGE_COLORS>(colorsArray.length);
        Collections.addAll(colorsList, colorsArray);

        // set adapter & listener
        triageColor.setAdapter(new ColorSpinnerAdapter(getActivity(), colorsList));
        triageColor.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                onTriageColorChanged((Common.TRIAGE_COLORS) parent.getItemAtPosition(position));
            }

            @Override
            public void onNothingSelected(AdapterView<?> parent) {
                // do nothing for now
            }
        });

        // get status, sex, and nbc spinners
        this.status = (Spinner) v.findViewById(R.id.patient_status_spinner);
        Common.PATIENT_STATUS[] statusArray = Common.PATIENT_STATUS.values();
        List<Common.PATIENT_STATUS> statusList = new ArrayList<Common.PATIENT_STATUS>(statusArray.length);
        Collections.addAll(statusList, statusArray);

        // set adapter & listener
        status.setAdapter(new PatientStatusSpinnerAdapter(getActivity(), statusList));
        status.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                onPatientStatusChanged((Common.PATIENT_STATUS) parent.getItemAtPosition(position));
            }

            @Override
            public void onNothingSelected(AdapterView<?> parent) {
                // do nothing for now
            }
        });

        this.patientSex = (Spinner) v.findViewById(R.id.patient_sex_spinner);
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

        this.nbc = (Spinner) v.findViewById(R.id.patient_nbc_spinner);

        Common.NBC_CONTAMINATION_OPTIONS[] nbcArray = Common.NBC_CONTAMINATION_OPTIONS.values();
        List<Common.NBC_CONTAMINATION_OPTIONS> nbcList = new ArrayList<Common.NBC_CONTAMINATION_OPTIONS>(nbcArray.length);
        Collections.addAll(nbcList, nbcArray);
        this.nbc.setAdapter(new NBCSpinnerAdapter(getActivity(), nbcList));

        nbc.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                onNbcChanged((Common.NBC_CONTAMINATION_OPTIONS) parent.getItemAtPosition(position));
            }

            @Override
            public void onNothingSelected(AdapterView<?> parent) {
                // do nothing for now
            }
        });

        // get text fields
        this.patientName = (EditText) v.findViewById(R.id.patient_name_text);
        patientName.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {

            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
                if (selectedPatient != null && !selectedPatient.getName().equals(s.toString())) {
                    saveButton.setEnabled(true);
                    Log.d(TAG, "Name changed");
                }
            }

            @Override
            public void afterTextChanged(Editable s) {

            }
        });

        this.patientAge = (EditText) v.findViewById(R.id.patient_age_text);
        patientAge.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {

            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
                String compareString = "";
                if (selectedPatient != null && selectedPatient.getAge() >= 0) {
                    compareString = selectedPatient.getAge() + "";
                }
                if (!compareString.equals(s.toString())) {
                    saveButton.setEnabled(true);
                    Log.d(TAG, "Age changed");
                }
            }

            @Override
            public void afterTextChanged(Editable s) {

            }
        });

        // get temp save changes button
        this.saveButton = (Button) v.findViewById(R.id.patient_info_save);

        this.saveButton.setOnClickListener(this);
        this.saveButton.setEnabled(false);

        this.resetAllFields();
        this.disableAllFields();

        return v;
    }

    @Override
    public void onDestroyView() {
        super.onDestroyView();

        // release views
        this.triageColor = null;
        this.status = null;
        this.patientSex = null;
        this.nbc = null;
        this.patientName = null;
        this.patientAge = null;
        this.saveButton = null;
    }

    public void setPatient(Patient p) {
        this.selectedPatient = p;
        if (p == null) {
            // disable all entry fields
            resetAllFields();
            disableAllFields();
        } else {
            setFieldsFromPatient(p);
            enableAllFields();
        }
    }

    private void setFieldsFromPatient(Patient p) {
        // Make sure selection 0 is always a default
        int selection = 0;
        int count;
        int i;
        SpinnerAdapter adapter;

        // set triage color field
        adapter = this.triageColor.getAdapter();
        count = adapter.getCount();
        for (i = 0; i < count; i++) {
            Common.TRIAGE_COLORS item = (Common.TRIAGE_COLORS) adapter.getItem(i);
            if (item == p.getTriageState()) {
                selection = i;
            }
        }
        this.triageColor.setSelection(selection);

        // Set status field
        selection = 0;
        adapter = this.status.getAdapter();
        count = adapter.getCount();
        for (i = 0; i < count; i++) {
            Common.PATIENT_STATUS item = (Common.PATIENT_STATUS) adapter.getItem(i);
            if (item == p.getStatus()) {
                selection = i;
            }
        }
        this.status.setSelection(selection);

        // Set sex field
        selection = 0;
        adapter = this.patientSex.getAdapter();
        count = adapter.getCount();
        for (i = 1; i < count; i++) {
            String item = (String) adapter.getItem(i);
            if (item.toLowerCase().equals(p.getSex().toLowerCase())) {
                selection = i;
            }
        }
        this.patientSex.setSelection(selection);

        // Set NBC field
        adapter = this.nbc.getAdapter();
        count = adapter.getCount();
        selection = 0;

        for (i = 0; i < count; i++) {
            Common.NBC_CONTAMINATION_OPTIONS option = (Common.NBC_CONTAMINATION_OPTIONS) adapter.getItem(i);
            if (option == p.getNbcContam()) {
                selection = i;
            }
        }

        this.nbc.setSelection(selection);
        this.patientName.setText(p.getName());
        if (p.getAge() >= 0) {
            this.patientAge.setText(p.getAge() + "");
        } else {
            this.patientAge.setText("");
        }
        Log.d(TAG, "fields set to patient values");
    }

    private void saveFieldsToPatient(Patient p) {
        p.setName(this.patientName.getText().toString());
        try {
            // try to read string from field
            if (this.patientAge.getText().length() > 0) {
                p.setAge(Integer.parseInt(this.patientAge.getText().toString()));
            } else {
                // nothing to parse
                p.setAge(-1);
            }
        } catch (NumberFormatException nfe) {
            // if fail to parse, set to -1
            p.setAge(-1);
        }
        p.setNbcContam((Common.NBC_CONTAMINATION_OPTIONS) this.nbc.getSelectedItem());
        p.setSex((String) this.patientSex.getSelectedItem());
        // assuming that array entries match
        p.setTriageState((Common.TRIAGE_COLORS) this.triageColor.getSelectedItem());
        p.setStatus((Common.PATIENT_STATUS) this.status.getSelectedItem());
        Log.d(TAG, "saved fields to patient");
    }

    private void disableAllFields() {
        if (getView() == null) {
            // no view (fragment view destroyed)
            return;
        }
        this.triageColor.setEnabled(false);
        this.status.setEnabled(false);
        this.patientSex.setEnabled(false);
        this.nbc.setEnabled(false);
        this.patientName.setEnabled(false);
        this.patientAge.setEnabled(false);
        this.saveButton.setEnabled(false);
        Log.d(TAG, "fields disabled");
    }

    private void enableAllFields() {
        if (getView() == null) {
            // no view (fragment view destroyed)
            return;
        }
        this.triageColor.setEnabled(true);
        this.status.setEnabled(true);
        this.patientSex.setEnabled(true);
        this.nbc.setEnabled(true);
        this.patientName.setEnabled(true);
        this.patientAge.setEnabled(true);
        this.saveButton.setEnabled(false);
        Log.d(TAG, "fields enabled");
    }

    private void resetAllFields() {
        if (getView() == null) {
            // no view (fragment view destroyed)
            return;
        }
        this.triageColor.setSelection(0);
        this.status.setSelection(0);
        this.patientSex.setSelection(0);
        this.nbc.setSelection(0);
        this.patientName.setText("John Doe");
        this.patientAge.setText("");
        this.saveButton.setEnabled(false);
        Log.d(TAG, "Fields reset");
    }

    private void onTriageColorChanged(Common.TRIAGE_COLORS triageState) {
        if (this.selectedPatient != null && this.selectedPatient.getTriageState() != triageState) {
            this.saveButton.setEnabled(true);
            Log.d(TAG, "Triage color changed");
        }
    }

    private void onPatientStatusChanged(Common.PATIENT_STATUS status) {
        if (this.selectedPatient != null && this.selectedPatient.getStatus() != status) {
            this.saveButton.setEnabled(true);
            Log.d(TAG, "status changed");
        }
    }

    private void onGenderChanged(String gender) {
        if (this.selectedPatient != null && !this.selectedPatient.getSex().equalsIgnoreCase(gender)) {
            this.saveButton.setEnabled(true);
            Log.d(TAG, "gender changed");
        }
    }

    private void onNbcChanged(Common.NBC_CONTAMINATION_OPTIONS nbcStatus) {
        if (this.selectedPatient != null && (nbcStatus != this.selectedPatient.getNbcContam())) {
            this.saveButton.setEnabled(true);
            Log.d(TAG, "nbc changed");
        }
    }

    @Override
    public void onClick(View v) {
        if (v.getId() == R.id.patient_info_save) {
            this.saveButton.setEnabled(false);
            Patient p = ((ScenarioPatientFragment) getParentFragment()).getSelectedPatient();
            if (p != null) {
                saveFieldsToPatient(p);
                Date updateTime = new Date();
                // set last updated time
                p.setLastUpdated(updateTime);

                JsonObject updateMsg = new JsonObject();

                updateMsg.addProperty(JSONTag.RESPONDER_ID, Common.RESPONDER_ID);
                updateMsg.addProperty(JSONTag.PATIENT_ID, p.getPatientId());

                DateFormat df = Util.getISOUTCFormatter();


                updateMsg.addProperty(JSONTag.DATE, df.format(updateTime));


                updateMsg.addProperty(JSONTag.PATIENT_INFO_NAME, p.getName());
                updateMsg.addProperty(JSONTag.PATIENT_INFO_AGE, p.getAge());
                updateMsg.addProperty(JSONTag.PATIENT_INFO_SEX, p.getSex());
                updateMsg.addProperty(JSONTag.PATIENT_INFO_NBC, p.getNbcContam().toString());
                updateMsg.addProperty(JSONTag.PATIENT_INFO_TRIAGE, p.getTriageState().toString());
                updateMsg.addProperty(JSONTag.PATIENT_INFO_STATUS, p.getStatus().toString());

                Log.d(TAG, "Patient info message: " + updateMsg.toString());

                if (getActivity() != null && getActivity() instanceof ScenarioActivity) {
                    ((ScenarioActivity) getActivity()).publishMQTTMessage(
                            Common.MQTT_TOPIC_PATIENT_INFO_UPDATE
                                    .replace(Common.MQTT_TOPIC_PATIENT_ID_STRING, p.getPatientId()),
                            updateMsg.toString());
                }
            }

        }
    }
}
