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
import java.util.List;

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

    // TODO: save patient list after rotation
    List<Patient> mPatients;
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
                    Log.d(Common.LOG_TAG, "Banner Handler" + msg.obj);
                    // TODO: parse message text
                    // TODO: request patient info if ID not found(in background of course)

                    JsonObject json = gson.fromJson(msg.obj.toString(), JsonObject.class);
                    int patientId = json.getAsJsonPrimitive("pid").getAsInt();
                    JsonArray vitals = json.getAsJsonArray("vitals");


                    boolean patientFound = false;
                    Patient curPatient = null;
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
                    // TODO: limit redraws?
                    // get view to redraw
                    for(int i = 0; i < tableRow.getVirtualChildCount(); i++){
                        PatientView p = (PatientView) tableRow.getVirtualChildAt(i);
                        if(p.getPid() == patientId)
                        {
                            p.postInvalidate();
                        }
                    }

                    break;
            }
        }
    };

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup viewgroup, Bundle bundle) {
        super.onCreate(bundle);
        setRetainInstance(true);

        View view = inflater.inflate(R.layout.banner, viewgroup, false);
//        TableLayout tableLayout = (TableLayout) view.findViewById(R.id.bannerTableLayout);
        this.tableRow = (TableRow) view.findViewById(R.id.bannerTableRow);

        if(this.mPatients == null){
            Log.d(Common.LOG_TAG, "Creating new patient list");
            mPatients = new ArrayList<Patient>();
        } else {
            for(Patient p : this.mPatients){
                this.createPatientView(p);
            }
        }

//        TableRow tableRow = (TableRow) view.findViewById(R.id.bannerTableRow);
//        //This is only here for debugging purposes till we start generating patients.
//        for (int i = 0; i < 20; i++) {
//            mPatients.add(RandomPatient.getRandomPatient());
//            mPatients.get(mPatients.size() - 1).setPid(i);
//            //Implements a custom view, the custom view is passed the patient object
//            PatientView v = new PatientView(mContext, mPatients.get(i), i);
//            v.setMinimumHeight(100);
//            v.setMinimumWidth(200);
//            tableRow.addView(v);
//            if (mContext instanceof View.OnClickListener) {
//                v.setOnClickListener((View.OnClickListener) this.mContext);
//            }
//        }
//        tableLayout.addView(tableRow, new TableLayout.LayoutParams());

        if (this.multicastClient == null) {
            this.multicastClient = new MulticastClient();
        }
        this.multicastClient.addHandler(this.mHandler);
        try {
            this.multicastClient.joinGroup(Inet6Address.getByName(Common.MCAST_GROUP), Common.MCAST_PORT);
        } catch (UnknownHostException e) {
            Log.e(Common.LOG_TAG, "Unknown Host " + Common.MCAST_GROUP, e);
        }

        return view;
    }

    @Override
    public void onDestroyView() {
        super.onDestroyView();
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
