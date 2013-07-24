package mil.afrl.discoverylab.sate13.rippleandroid.fragment;

import android.app.Activity;
import android.app.Fragment;
import android.content.Context;
import android.graphics.Color;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TableLayout;
import android.widget.TableRow;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.google.gson.JsonArray;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;

import java.net.Inet6Address;
import java.net.UnknownHostException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Timer;
import java.util.TimerTask;

import mil.afrl.discoverylab.sate13.rippleandroid.Common;
import mil.afrl.discoverylab.sate13.rippleandroid.PatientView;
import mil.afrl.discoverylab.sate13.rippleandroid.R;
import mil.afrl.discoverylab.sate13.rippleandroid.RandomPatient;
import mil.afrl.discoverylab.sate13.rippleandroid.adapter.network.MulticastClient;
import mil.afrl.discoverylab.sate13.rippleandroid.data.model.Vital;
import mil.afrl.discoverylab.sate13.rippleandroid.object.Patient;

import static mil.afrl.discoverylab.sate13.rippleandroid.Common.VITAL_TYPES.VITAL_BLOOD_OX;

/**
 * This displays the banner at the top of the display
 * <p/>
 * Created by harmonbc on 6/19/13.
 */
public class Banner extends Fragment {

    private static final int TIMER_PERIOD_MS = 5000;
    private static final String PATIENT_LIST = "patient_list";
    private static final String SAVE_STATE = "save_state";

    // TODO: save patient list after rotation
    private List<Patient> mPatients;
    private Context mContext;
    private MulticastClient multicastClient;

    private Gson gson = new GsonBuilder().setDateFormat(Common.DATE_TIME_FORMAT).create();
    private TableRow tableRow;
    private Handler mHandler = new Handler() {

        @Override
        public void handleMessage(Message msg) {
            super.handleMessage(msg);

            switch (msg.what) {
                case Common.RIPPLE_MSG_MCAST:
                    if (mPatients == null) {
                        // No patients to update
                        return;
                    }
//                    Log.d(Common.LOG_TAG, "Banner Handler" + msg.obj);


                    JsonObject json = gson.fromJson(msg.obj.toString(), JsonObject.class);
                    int patientId = json.getAsJsonPrimitive("pid").getAsInt();
                    JsonArray vitals = json.getAsJsonArray("vitals");


                    boolean patientFound = false;
                    Patient curPatient = null;
                    synchronized (mPatients) {
                        for (Patient p : mPatients) {
                            if (p.getPid() == patientId) {
                                patientFound = true;
                                curPatient = p;
                                break;
                            }
                        }
                        if (!patientFound) {
                            // Add patient
                            curPatient = new Patient();
                            curPatient.setPid(patientId);
                            mPatients.add(curPatient);
                            curPatient.setColor(Color.CYAN);
                            createPatientView(curPatient);
                        }
                    }
                    if (curPatient != null) {
                        for (JsonElement j : vitals) {

                            Vital v = gson.fromJson(j, Vital.class);

                            if (v.value_type == VITAL_BLOOD_OX.getValue()) {
                                curPatient.setO2(v.value);
                            } else if (v.value_type == Common.VITAL_TYPES.VITAL_PULSE.getValue()) {
                                curPatient.setBpm(v.value);
                            } else if (v.value_type == Common.VITAL_TYPES.VITAL_TEMPERATURE.getValue()) {
                                curPatient.setTemperature(v.value);
                            } else {
                                Log.e(Common.LOG_TAG, "Unknown Vital type: " + v.value_type);
                            }
                        }
                    }


                    break;
            }
        }
    };
    private Timer autoUpdateTimer;
    private Bundle savedState;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup viewgroup, Bundle savedInstanceState) {
        if(viewgroup == null)
        {
            // Fragment being re-created from a Bundle & won't be shown
            return null;
        }
        super.onCreateView(inflater, viewgroup, savedInstanceState);
        setRetainInstance(true);

        View view = inflater.inflate(R.layout.banner, viewgroup, false);

        this.tableRow = (TableRow) view.findViewById(R.id.bannerTableRow);

        // set timer for every 5 seconds
        this.autoUpdateTimer = new Timer();
        this.autoUpdateTimer.schedule(new TimerTask() {
            @Override
            public void run() {
                // Update views
                for (int i = 0; i < tableRow.getVirtualChildCount(); i++) {
                    PatientView p = (PatientView) tableRow.getVirtualChildAt(i);
                    p.postInvalidate();

                }
            }
        }, 0, TIMER_PERIOD_MS);

        /* If the Fragment was destroyed inbetween (screen rotation), we need to recover the savedState first */
        /* However, if it was not, it stays in the instance from the last onDestroyView() and we don't want to overwrite it */
        if (savedInstanceState != null && savedState == null) {
//            Log.d(Common.LOG_TAG, "Banner: restoring state from saved instance");
            savedState = savedInstanceState.getBundle(SAVE_STATE);
        }

        if (savedState != null && savedState.containsKey(PATIENT_LIST)) {
//            Log.d(Common.LOG_TAG, "Banner: restoring from saved state");

            this.mPatients = new ArrayList<Patient>(Arrays.asList((Patient[]) savedState.getParcelableArray(PATIENT_LIST)));

        }
        savedState = null;


        if (this.mPatients == null) {
//            Log.d(Common.LOG_TAG, "Creating new patient list");
            mPatients = new ArrayList<Patient>();
        } else {
            // recreate views for patients
            for (Patient p : this.mPatients) {
                this.createPatientView(p);
            }
        }
        Log.d(Common.LOG_TAG, this.mPatients.size()+"");

        return view;

    }

    @Override
    public void onStart() {
        super.onStart();
        if (this.multicastClient == null) {
            this.multicastClient = new MulticastClient();
        }
        this.multicastClient.addHandler(this.mHandler);
        try {
            this.multicastClient.joinGroup(Inet6Address.getByName(Common.MCAST_GROUP), Common.MCAST_PORT);
        } catch (UnknownHostException e) {
            Log.e(Common.LOG_TAG, "Unknown Host " + Common.MCAST_GROUP, e);
        }
    }

    @Override
    public void onStop() {
        super.onStop();
        if (this.multicastClient != null) {
            this.multicastClient.removeHandler(this.mHandler);
            try {
                this.multicastClient.leaveGroup(Inet6Address.getByName(Common.MCAST_GROUP), Common.MCAST_PORT);
            } catch (UnknownHostException e) {
                Log.e(Common.LOG_TAG, "Unknown Host " + Common.MCAST_GROUP, e);
            }
        }

    }

    @Override
    public void onSaveInstanceState(Bundle outState) {
        super.onSaveInstanceState(outState);
//        Log.d(Common.LOG_TAG, "Banner: saving instance");

        outState.putBundle(SAVE_STATE, this.savedState != null ? this.savedState : saveState());
    }

    private Bundle saveState() {
//        Log.d(Common.LOG_TAG, "Banner: saving state");
        Bundle state = new Bundle();
        synchronized (this.mPatients) {
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

        this.savedState = this.saveState();

    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        // Stop client
        if(this.multicastClient != null)
        {
            this.multicastClient.removeHandler(this.mHandler);
            this.multicastClient.disconnect();
            this.multicastClient = null;
        }
    }

    @Override
    public void onAttach(Activity activity) {
        super.onAttach(activity);
        mContext = activity;
    }

    private void createPatientView(Patient patient) {
        PatientView v = new PatientView(this.mContext, patient, patient.getPid());
        v.setMinimumHeight(100);
        v.setMinimumWidth(200);
        if (mContext instanceof View.OnClickListener) {
            v.setOnClickListener((View.OnClickListener) this.mContext);
        }
        this.tableRow.addView(v);
        this.tableRow.postInvalidate();
    }

}
