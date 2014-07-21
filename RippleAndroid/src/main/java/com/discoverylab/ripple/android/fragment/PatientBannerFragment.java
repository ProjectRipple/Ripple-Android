package com.discoverylab.ripple.android.fragment;


import android.app.Fragment;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.LinearLayout;

import com.discoverylab.ripple.android.R;
import com.discoverylab.ripple.android.object.Patient;
import com.discoverylab.ripple.android.object.Patients;
import com.discoverylab.ripple.android.util.RandomPatient;
import com.discoverylab.ripple.android.view.BannerPatientView;

import java.util.Map;
import java.util.Set;

/**
 * Displays a horizontal list of patients.
 * <p/>
 * Use the {@link PatientBannerFragment#newInstance} factory method to
 * create an instance of this fragment.
 */
public class PatientBannerFragment extends Fragment {


    // Log tag
    private static final String TAG = PatientBannerFragment.class.getSimpleName();
    // Reference to the currently selected Patient
    public Patient selectedPatient;
    // Handler for messages to Banner
    private Handler mHandler = new PatientBannerHandler();
    // Reference to layout holding patient views
    private LinearLayout viewLayout;
    // Runnable to refresh all patient views
    private Runnable refreshRunnable = new RefreshBannerRunnable();


    // set to true to populate banner with dummy patients
    private static boolean randomTestPatients = false;

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

        // TODO: handle rotation by recreating all patient views
        Patients patients = Patients.getInstance();
        if (patients.getNumPatients() > 0) {
            Set<Map.Entry<String, Patient>> patientsSet = patients.getPatientEntries();
            for (Map.Entry<String, Patient> entry : patientsSet) {
                this.createPatientView(entry.getValue());
            }
        }

        if (randomTestPatients) {
            // TODO: remove after debugging
            for (int i = 0; i < RandomPatient.MAX_UNIQUE_PATIENTS; i++) {
                Patient p = RandomPatient.getRandomPatient();
                this.createPatientView(p);
                patients.addPatient(p.getPatientId(), p);
            }
            randomTestPatients = false;
        }


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
    public void refreshBanner() {
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

            Patient curPatient;
            Patients patients = Patients.getInstance();

            switch (msg.what) {

                default:
                    Log.d(TAG, "Unknown message to handler.");
            }
        }
    }


}
