package com.discoverylab.ripple.android.fragment;


import android.app.Fragment;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.LinearLayout;

import com.discoverylab.ripple.android.R;
import com.discoverylab.ripple.android.config.Common;
import com.discoverylab.ripple.android.config.JSONTag;
import com.discoverylab.ripple.android.object.Patient;
import com.discoverylab.ripple.android.object.PatientList;
import com.discoverylab.ripple.android.util.RandomPatient;
import com.discoverylab.ripple.android.view.BannerPatientView;
import com.google.gson.JsonObject;

/**
 * Displays a horizontal list of patients.
 * <p/>
 * Use the {@link PatientBannerFragment#newInstance} factory method to
 * create an instance of this fragment.
 */
public class PatientBannerFragment extends Fragment {


    // Reference to the currently selected Patient
    public Patient selectedPatient;
    // Handler for messages to Banner
    private Handler mHandler = new PatientBannerHandler();
    // Reference to layout holding patient views
    private LinearLayout viewLayout;
    // Runnable to refresh all patient views
    private Runnable refreshRunnable = new RefreshBannerRunnable();

    /**
     * Use this factory method to create a new instance of
     * this fragment using the provided parameters.
     *
     * @return A new instance of fragment PatientBannerFragment.
     */
    public static PatientBannerFragment newInstance() {
        PatientBannerFragment fragment = new PatientBannerFragment();
        return fragment;
    }

    public PatientBannerFragment() {
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
        View v = inflater.inflate(R.layout.fragment_patient_banner, container, false);

        this.viewLayout = (LinearLayout) v.findViewById(R.id.patient_banner_view_layout);

        // TODO: remove after debugging
        PatientList patientList = PatientList.getInstance();
        for (int i = 0; i < RandomPatient.MAX_UNIQUE_PATIENTS; i++) {
            Patient p = RandomPatient.getRandomPatient();
            this.createPatientView(p);
            patientList.addPatient(p);
        }

        // TODO: handle rotation by recreating all patient views

        return v;
    }

    @Override
    public void onDestroyView() {
        super.onDestroyView();
        // remove view references
        this.viewLayout = null;
    }

    /**
     * Add a new patient to the banner
     *
     * @param p Patien to add
     */
    public void addPatient(Patient p) {
        this.createPatientView(p);
    }


    /**
     * Create a new patient view for patient
     *
     * @param p Patient to create view for
     */
    private void createPatientView(Patient p) {
        if (this.viewLayout != null) {
            BannerPatientView bpv = new BannerPatientView(getActivity());
            bpv.setPatient(p);
            if (getActivity() instanceof View.OnClickListener) {
                bpv.setOnClickListener((View.OnClickListener) getActivity());
            }
            this.viewLayout.addView(bpv);
            this.viewLayout.postInvalidate();
        }
    }

    /**
     * Cause redraw of banner child views.
     */
    private void refreshBanner() {
        this.mHandler.post(refreshRunnable);
    }

    private class RefreshBannerRunnable implements Runnable {

        @Override
        public void run() {
            // TODO: check that this works
            int childViews = viewLayout.getChildCount();
            for (int i = 0; i < childViews; i++) {
                BannerPatientView p = (BannerPatientView) viewLayout.getChildAt(i);
                p.updateViewFields();
                p.postInvalidate();
            }
        }
    }


    public Handler getHandler() {
        return this.mHandler;
    }


    private class PatientBannerHandler extends Handler {
        @Override
        public void handleMessage(Message msg) {

            boolean patientFound;
            Patient curPatient;
            PatientList patientList = PatientList.getInstance();

            switch (msg.what) {
                case Common.RIPPLE_MSG_RECORD:
                    if (msg.obj == null) {
                        // message
                        return;
                    }
                    // new patient update

                    JsonObject recordJson = (JsonObject) msg.obj;

                    patientFound = false;
                    curPatient = null;
                    String src = recordJson.get(JSONTag.RECORD_SOURCE).getAsString();
                    int hr = recordJson.get(JSONTag.RECORD_HEART_RATE).getAsInt();
                    int spO2 = recordJson.get(JSONTag.RECORD_BLOOD_OX).getAsInt();
                    int temperature = recordJson.get(JSONTag.RECORD_TEMPERATURE).getAsInt();
                    int resp_pm = recordJson.get(JSONTag.RECORD_RESP_PER_MIN).getAsInt();

                    // find patient
                    for (Patient p : patientList.getPatientList()) {
                        if (p.getPatientId().equals(src)) {
                            patientFound = true;
                            curPatient = p;
                            break;
                        }
                    }
                    if (!patientFound) {
                        // Add patient
                        curPatient = new Patient();
                        curPatient.setPatientId(src);
                        patientList.addPatient(curPatient);
                        createPatientView(curPatient);
                    }

                    // Update patient values
                    curPatient.setO2(spO2);
                    curPatient.setBpm(hr);
                    curPatient.setTemperature(temperature);
                    curPatient.setRpm(resp_pm);

                    break;

                case Common.RIPPLE_MSG_SELECT_PATIENT:
                    if (msg.obj == null) {
                        // no message
                        return;
                    }

                    patientFound = false;
                    curPatient = null;

                    String patientId = (String) msg.obj;
                    if (patientId.equals("")) {
                        // no patient currently selected
                        if (selectedPatient != null) {
                            selectedPatient.setSelected(false);
                            selectedPatient = null;
                        }
                    } else {
                        // find patient

                        for (Patient p : patientList.getPatientList()) {
                            if (p.getPatientId().equals(patientId)) {
                                patientFound = true;
                                curPatient = p;
                                break;
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
                    refreshBanner();
                    break;
            }
        }
    }


}
