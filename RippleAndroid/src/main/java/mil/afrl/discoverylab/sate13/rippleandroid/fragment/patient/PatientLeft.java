package mil.afrl.discoverylab.sate13.rippleandroid.fragment.patient;


import android.app.Activity;
import android.app.Dialog;
import android.app.Fragment;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.foxykeep.datadroid.requestmanager.Request;
import com.foxykeep.datadroid.requestmanager.RequestManager;

import java.util.ArrayList;

import mil.afrl.discoverylab.sate13.rippleandroid.Common;
import mil.afrl.discoverylab.sate13.rippleandroid.R;
import mil.afrl.discoverylab.sate13.rippleandroid.adapter.network.UdpClient;
import mil.afrl.discoverylab.sate13.rippleandroid.adapter.ui.GraphHelper;
import mil.afrl.discoverylab.sate13.rippleandroid.config.WSConfig;
import mil.afrl.discoverylab.sate13.rippleandroid.data.model.SubscriptionResponse;
import mil.afrl.discoverylab.sate13.rippleandroid.data.model.Vital;
import mil.afrl.discoverylab.sate13.rippleandroid.data.requestmanager.RippleRequestFactory;
import mil.afrl.discoverylab.sate13.rippleandroid.data.requestmanager.RippleRequestManager;

/**
 * The left patient fragment is used to display the most recent health data and information
 * for the patient
 * <p/>
 * The a chart is added to the bottom left linear layout and is used to display the ecg waveform
 */
public class PatientLeft extends Fragment implements View.OnClickListener, RequestManager.RequestListener {

    private static final String SAVED_STATE_REQUEST_LIST = "savedStateRequestList";
    private static UdpClient udpc = new UdpClient(WSConfig.UDP_VITALS_STREAM_HOST, WSConfig.UDP_VITALS_STREAM_PORT);
    private int curPatient;
    private int curVital;
    protected RippleRequestManager mRequestManager;
    protected ArrayList<Request> mRequestList;
    private View view;
    private TextView patientName;
    private GraphHelper graphHelper;

    private Handler handler = new Handler() {
        @Override
        public void handleMessage(Message msg) {
            super.handleMessage(msg);

            switch (msg.what) {
                case Common.RIPPLE_MSG_VITALS_STREAM: {

                    Vital v = (Vital) msg.obj;
                    if (v.sensor_type == 1 && v.pid == curPatient) {
                        graphHelper.addPoint((double) v.sensor_timestamp, (double) v.value);
                    }
                    break;
                }
                default:
                    Log.e(Common.LOG_TAG, "Unknown Message type: " + msg.what);
            }
        }
    };

    private synchronized void callSubscriptionWS(int pid, String action) {
        Request request = RippleRequestFactory.getSubscriptionRequest(
                pid,
                action,
                WSConfig.UDP_VITALS_STREAM_PORT);
        mRequestManager.execute(request, this);
        mRequestList.add(request);
    }

    /**
     * TODO: Query the content provider for a list of available patients,
     * otherwise wait for a notification from the Banner
     * <p/>
     * TODO: on addition of a new patient query the content provider for existing vitals
     * and update the curVital
     *
     * @param inflater
     * @param container
     * @param savedInstanceState
     * @return
     */
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        view = inflater.inflate(R.layout.patient_left, container, false);
        assert view != null;

        LinearLayout layout = (LinearLayout) view.findViewById(R.id.chart);
        graphHelper = new GraphHelper(this.getActivity(), layout);
        layout.addView(graphHelper.chartView,
                new LinearLayout.LayoutParams(LinearLayout.LayoutParams.MATCH_PARENT,
                        LinearLayout.LayoutParams.MATCH_PARENT));

        ImageView tagview = (ImageView) view.findViewById(R.id.tagview);
        assert tagview != null;
        tagview.setClickable(true);
        tagview.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                // custom dialog
                final Dialog dialog = new Dialog(getActivity());
                dialog.setContentView(R.layout.finger_paint_dialog);
                dialog.setTitle(" Draw Something Friend ");

                // set the custom dialog components - text, image and button
                Button dialogButton = (Button) dialog.findViewById(R.id.fingerbuttonok);
                // if button is clicked, close the custom dialog
                dialogButton.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        dialog.dismiss();
                    }
                });
                dialog.show();
            }
        });

        mRequestManager = RippleRequestManager.from(getActivity());
        if (savedInstanceState != null) {
            mRequestList = savedInstanceState.getParcelableArrayList(SAVED_STATE_REQUEST_LIST);
        } else {
            mRequestList = new ArrayList<Request>();
        }

        this.patientName = (TextView) view.findViewById(R.id.name_value_tv);
        return view;
    }

    @Override
    public void onSaveInstanceState(Bundle outState) {
        super.onSaveInstanceState(outState);
        graphHelper.save(outState);
        outState.putParcelableArrayList(SAVED_STATE_REQUEST_LIST, mRequestList);
    }

    @Override
    public void onActivityCreated(Bundle savedState) {
        super.onActivityCreated(savedState);
        if (savedState != null) {
            graphHelper.restore(savedState);
            mRequestManager = RippleRequestManager.from(getActivity());
            mRequestList = savedState.getParcelableArrayList(SAVED_STATE_REQUEST_LIST);

        } else {
            mRequestList = new ArrayList<Request>();
        }
    }

    @Override
    public void onResume() {
        super.onResume();
        for (int i = 0; i < mRequestList.size(); i++) {
            Request request = mRequestList.get(i);
            if (mRequestManager.isRequestInProgress(request)) {
                mRequestManager.addRequestListener(this, request);
            } else {
                mRequestManager.callListenerWithCachedData(this, request);
                i--;
                mRequestList.remove(request);
            }
        }
    }

    @Override
    public void onPause() {
        super.onPause();
        if (!mRequestList.isEmpty()) {
            mRequestManager.removeRequestListener(this);
        }
    }

    @Override
    public void onRequestFinished(final Request request, final Bundle resultData) {
        SubscriptionResponse response = resultData.getParcelable(RippleRequestFactory.BUNDLE_EXTRA_VITAL_LIST);
        if (response.success) {
            if (response.action_echo.equals("unsubscribe") && curPatient >= 0) {
                Log.d(Common.LOG_TAG, "Successfully unsubscribed: " + response.toString());
                callSubscriptionWS(curPatient, "subscribe");
            } else if (response.action_echo.equals("subscribe") && response.pid_echo == curPatient) {
                Log.d(Common.LOG_TAG, "Successfully subscribed: " + response.toString());
            }
        } else {
            Log.e(Common.LOG_TAG, "Negative Subscription Response: " + response.toString());
        }
    }

    @Override
    public void onRequestConnectionError(Request request, int statusCode) {
        if (mRequestList.contains(request)) {
            mRequestList.remove(request);
        }
    }

    @Override
    public void onRequestDataError(Request request) {
        if (mRequestList.contains(request)) {
            mRequestList.remove(request);
        }
    }

    @Override
    public void onRequestCustomError(Request request, Bundle resultData) {
        // Never called.
    }

    @Override
    public void onAttach(Activity activity) {
        super.onAttach(activity);
    }

    @Override
    public void onClick(View view) {
    }

    public void setPatient(int pid) {
        if (curPatient != pid) {
            if (curPatient >= 0) {
                // Unsubscribe
                callSubscriptionWS(curPatient, "unsubscribe");
                // Clear the Graph
                graphHelper.clearGraph();
                // Disconnect the UdpStream
                udpc.removehandler(handler);
            } else {
                // Subscribe
                callSubscriptionWS(curPatient, "subscribe");
                // Connect to UdpStream
                udpc.addHandler(handler);
            }
            // TODO: may need settext on UI thread
            curPatient = pid;
            patientName.setText("Dummy Patient(" + curPatient + ")");
        } else {
            // Do Nothing
        }
    }

}
