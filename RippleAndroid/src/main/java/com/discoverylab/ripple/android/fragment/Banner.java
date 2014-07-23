package com.discoverylab.ripple.android.fragment;

import android.app.Activity;
import android.app.Fragment;
import android.content.Context;
import android.graphics.Color;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TableRow;

import com.discoverylab.ripple.android.R;
import com.discoverylab.ripple.android.config.Common;
import com.discoverylab.ripple.android.config.JSONTag;
import com.discoverylab.ripple.android.object.Patient;
import com.discoverylab.ripple.android.view.PatientView;
import com.google.gson.JsonObject;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Timer;
import java.util.TimerTask;

/**
 * This displays the banner at the top of the display
 * <p/>
 * Created by harmonbc on 6/19/13.
 */
public class Banner extends Fragment {

    // Time between updates of banner views
    private static final int TIMER_PERIOD_MS = 5000;
    // Strings for saving object states
    private static final String PATIENT_LIST = "patient_list";
    private static final String SAVE_STATE = "save_state";

    // List of patient objects
    private List<Patient> mPatients;
    // Lock for patient list
    private final Object patientLock = new Object();
    // Reference to context (activity)
    private Context mContext;
    // Table row of Banner
    private TableRow tableRow;
    // Reference to the currently selected Patient
    public Patient selectedPatient;
    // Handler for messages to Banner
    private Handler mHandler = new BannerHandler();
    // Timer to control updating Banner views
    private Timer autoUpdateTimer;
    // Bundle to help manage saved state
    private Bundle savedState;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup viewgroup, Bundle savedInstanceState) {
        if (viewgroup == null) {
            // Fragment being re-created from a Bundle & won't be shown
            return null;
        }
        super.onCreateView(inflater, viewgroup, savedInstanceState);
        setRetainInstance(true);
        // inflate banner view
        View view = inflater.inflate(R.layout.banner, viewgroup, false);
        // get table row
        this.tableRow = (TableRow) view.findViewById(R.id.bannerTableRow);

        // set timer for every 5 seconds
        this.autoUpdateTimer = new Timer();
        this.autoUpdateTimer.schedule(new TimerTask() {
            @Override
            public void run() {
                mHandler.post(new Runnable() {
                    @Override
                    public void run() {
                        // Update views
                        for (int i = 0; i < tableRow.getVirtualChildCount(); i++) {
                            PatientView p = (PatientView) tableRow.getVirtualChildAt(i);
                            p.updateViewFields();
                            p.postInvalidate();

                        }
                    }
                });

            }
        }, 0, TIMER_PERIOD_MS);

        /* If the Fragment was destroyed inbetween (screen rotation), we need to recover the savedState first */
        /* However, if it was not, it stays in the instance from the last onDestroyView() and we don't want to overwrite it */
        if (savedInstanceState != null && savedState == null) {
            savedState = savedInstanceState.getBundle(SAVE_STATE);
        }

        if (savedState != null && savedState.containsKey(PATIENT_LIST)) {
            this.mPatients = new ArrayList<Patient>(Arrays.asList((Patient[]) savedState.getParcelableArray(PATIENT_LIST)));
        }
        savedState = null;


        if (this.mPatients == null) {
            // create new list
            mPatients = new ArrayList<Patient>();
        } else {
            // recreate views for patients
            for (Patient p : this.mPatients) {
                this.createPatientView(p);
            }
        }

        return view;

    }

    @Override
    public void onStart() {
        super.onStart();
    }

    @Override
    public void onStop() {
        super.onStop();
    }

    @Override
    public void onSaveInstanceState(Bundle outState) {
        super.onSaveInstanceState(outState);

        outState.putBundle(SAVE_STATE, this.savedState != null ? this.savedState : saveState());
    }

    private Bundle saveState() {
        Bundle state = new Bundle();
        synchronized (this.patientLock) {
            state.putParcelableArray(PATIENT_LIST, this.mPatients.toArray(new Patient[this.mPatients.size()]));
        }
        return state;

    }

    @Override
    public void onDestroyView() {
        super.onDestroyView();
        // Stop timer
        if (this.autoUpdateTimer != null) {
            this.autoUpdateTimer.cancel();
            this.autoUpdateTimer = null;
        }

        // remove reference to view objects
        this.tableRow = null;

        this.savedState = this.saveState();

    }

    @Override
    public void onDestroy() {
        super.onDestroy();
    }

    @Override
    public void onAttach(Activity activity) {
        super.onAttach(activity);
        mContext = activity;
    }

    @Override
    public void onDetach() {
        super.onDetach();
        // remove reference to activity
        mContext = null;
    }

    /**
     * Clears all patients from Banner
     */
    public void clearPatientBanner() {
        this.selectedPatient = null;
        // remove all patient views
        this.tableRow.removeAllViews();
        // remove all patient objects
        synchronized (this.patientLock) {
            this.mPatients.clear();
        }
    }

    // TODO: remove debug method
    public void addPatient(Patient patient) {
        this.mPatients.add(patient);
        this.createPatientView(patient);
    }

    /**
     * Creates a PatientView for the given patient and adds it to the tablerow
     *
     * @param patient Patient to create view for.
     */
    private void createPatientView(Patient patient) {
        PatientView v = new PatientView(this.mContext);
        v.setPatient(patient);
        if (mContext instanceof View.OnClickListener) {
            v.setOnClickListener((View.OnClickListener) this.mContext);
        }
        this.tableRow.addView(v);
        this.tableRow.postInvalidate();
    }

    /**
     * Get handler for banner
     *
     * @return Banner's Handler
     */
    public Handler getHandler() {
        return mHandler;
    }


    /**
     * Handler for messages to Banner
     */
    private class BannerHandler extends Handler {
        @Override
        public void handleMessage(Message msg) {

            boolean patientFound;
            Patient curPatient;

            switch (msg.what) {
                case Common.RIPPLE_MSG_RECORD:
                    if (mPatients == null || msg.obj == null) {
                        // No patients to update or no message
                        return;
                    }

                    JsonObject recordJson = (JsonObject) msg.obj;

                    patientFound = false;
                    curPatient = null;
                    String src = recordJson.get(JSONTag.RECORD_SOURCE).getAsString();
                    int hr = recordJson.get(JSONTag.RECORD_HEART_RATE).getAsInt();
                    int spO2 = recordJson.get(JSONTag.RECORD_BLOOD_OX).getAsInt();
                    int temperature = recordJson.get(JSONTag.RECORD_TEMPERATURE).getAsInt();
                    int resp_pm = recordJson.get(JSONTag.RECORD_RESP_PER_MIN).getAsInt();

                    // find patient
                    synchronized (patientLock) {
                        for (Patient p : mPatients) {
                            if (p.getPatientId().equals(src)) {
                                patientFound = true;
                                curPatient = p;
                                break;
                            }
                        }
                        if (!patientFound) {
                            // Add patient
                            curPatient = new Patient(src);
                            mPatients.add(curPatient);
                            curPatient.setTriageState(Common.TRIAGE_COLORS.UNKNOWN);
                            createPatientView(curPatient);
                        }
                    }
                    // Update patient values
                    curPatient.setO2(spO2);
                    curPatient.setHeartRate(hr);
                    curPatient.setTemperature(temperature);
                    curPatient.setBreathsPerMin(resp_pm);

                    break;

                case Common.RIPPLE_MSG_SELECT_PATIENT:
                    if (mPatients == null || msg.obj == null) {
                        // No patients to update or no message
                        return;
                    }
                    patientFound = false;
                    curPatient = null;

                    String patientId = (String) msg.obj;
                    if (patientId.equals("")) {
                        // no patient selected
                        if (selectedPatient != null) {
                            selectedPatient.setSelected(false);
                            selectedPatient = null;
                        }
                    } else {
                        // find patient
                        synchronized (patientLock) {
                            for (Patient p : mPatients) {
                                if (p.getPatientId().equals(patientId)) {
                                    patientFound = true;
                                    curPatient = p;
                                    break;
                                }
                            }
                        }

                        if (patientFound) {
                            // remove old selected patient
                            if (selectedPatient != null) {
                                selectedPatient.setSelected(false);
                                selectedPatient = null;
                            }
                            selectedPatient = curPatient;
                            selectedPatient.setSelected(true);

                        }
                    }
                    // Update views now
                    for (int i = 0; i < tableRow.getVirtualChildCount(); i++) {
                        PatientView p = (PatientView) tableRow.getVirtualChildAt(i);
                        p.updateViewFields();
                        p.invalidate();

                    }
                    break;
            }
        }
    }
}
