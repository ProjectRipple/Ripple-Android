package mil.afrl.discoverylab.sate13.rippleandroid.fragment;

import android.app.Activity;
import android.app.Fragment;
import android.content.Context;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TableLayout;
import android.widget.TableRow;

import java.net.Inet6Address;
import java.net.UnknownHostException;
import java.util.ArrayList;

import mil.afrl.discoverylab.sate13.rippleandroid.Common;
import mil.afrl.discoverylab.sate13.rippleandroid.PatientView;
import mil.afrl.discoverylab.sate13.rippleandroid.R;
import mil.afrl.discoverylab.sate13.rippleandroid.RandomPatient;
import mil.afrl.discoverylab.sate13.rippleandroid.adapter.network.MulticastClient;
import mil.afrl.discoverylab.sate13.rippleandroid.object.Patient;

/**
 * This displays the banner at the top of the display
 * <p/>
 * Created by harmonbc on 6/19/13.
 */
public class Banner extends Fragment {

    ArrayList<Patient> mPatients;
    private Context mContext;
    private MulticastClient multicastClient;

    private Handler mHandler = new Handler() {

        @Override
        public void handleMessage(Message msg) {
            super.handleMessage(msg);

            switch (msg.what) {
                case Common.RIPPLE_MSG_MCAST:
                    if(mPatients == null){
                        // No patients to update
                        return;
                    }
                    Log.d(Common.LOG_TAG, "Banner Handler" + (String)msg.obj);
                    // TODO: parse message text
                    // TODO: request patient info if ID not found(in background of course)
                    for(Patient p : mPatients){
                        //if(p.getPid() == )
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
        TableLayout tableLayout = (TableLayout) view.findViewById(R.id.bannerTableLayout);
        mPatients = new ArrayList<Patient>();

        TableRow tableRow = (TableRow) view.findViewById(R.id.bannerTableRow);
        //This is only here for debugging purposes till we start generating patients.
        for (int i = 0; i < 20; i++) {
            mPatients.add(RandomPatient.getRandomPatient());
            mPatients.get(mPatients.size() - 1).setPid(i);
            //Implements a custom view, the custom view is passed the patient object
            PatientView v = new PatientView(mContext, mPatients.get(i), i);
            v.setMinimumHeight(100);
            v.setMinimumWidth(200);
            tableRow.addView(v);
            if (mContext instanceof View.OnClickListener) {
                v.setOnClickListener((View.OnClickListener) this.mContext);
            }
        }
//        tableLayout.addView(tableRow, new TableLayout.LayoutParams());

        if(this.multicastClient == null)
        {
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
        if(this.multicastClient != null)
        {
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
}
