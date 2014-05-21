package mil.afrl.discoverylab.sate13.rippleandroid.fragment.patient;


import android.app.Activity;
import android.app.Dialog;
import android.app.Fragment;
import android.content.Intent;
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

import mil.afrl.discoverylab.sate13.ripple.data.model.MultiValueVital;
import mil.afrl.discoverylab.sate13.ripple.data.model.SubscriptionResponse;
import mil.afrl.discoverylab.sate13.rippleandroid.Common;
import mil.afrl.discoverylab.sate13.rippleandroid.MainActivity;
import mil.afrl.discoverylab.sate13.rippleandroid.PrefsActivity;
import mil.afrl.discoverylab.sate13.rippleandroid.R;
import mil.afrl.discoverylab.sate13.rippleandroid.adapter.network.UdpClient;
import mil.afrl.discoverylab.sate13.rippleandroid.adapter.ui.GraphHelper;
import mil.afrl.discoverylab.sate13.rippleandroid.config.WSConfig;
import mil.afrl.discoverylab.sate13.rippleandroid.data.requestmanager.RippleRequestFactory;
import mil.afrl.discoverylab.sate13.rippleandroid.data.requestmanager.RippleRequestManager;
import mil.afrl.discoverylab.sate13.rippleandroid.view.FingerPaint;

/**
 * The left patient fragment is used to display the most recent health data and information
 * for the patient
 * <p/>
 * The a chart is added to the bottom left linear layout and is used to display the ecg waveform
 */
public class PatientLeft extends Fragment implements View.OnClickListener, RequestManager.RequestListener {

    private static final String SAVED_STATE_REQUEST_LIST = "savedStateRequestList";
    //private static UdpClient udpc = new UdpClient();
    private int curPatient = -1;
    //private int curVital;
    protected RippleRequestManager mRequestManager;
    protected ArrayList<Request> mRequestList;
    private View view;
    private TextView patientName;
    private TextView temperature;
    private TextView pulse;
    private TextView bloodOx;
    private GraphHelper graphHelper;
    private Handler bannerHandler;
    private Button settingsButton;
    private Button connectButton;

    private Handler handler = new Handler() {
        @Override
        public void handleMessage(final Message msg) {
            super.handleMessage(msg);

            switch (msg.what) {
                case Common.RIPPLE_MSG_VITALS_STREAM: {
                    graphHelper.offerVitals((MultiValueVital[]) msg.obj);
                    break;
                }
                case Common.RIPPLE_MSG_VITALS_TEMPERATURE: {
                    temperature.setText(Integer.toString(msg.arg1));
                    break;
                }
                case Common.RIPPLE_MSG_VITALS_PULSE: {
                    pulse.setText(Integer.toString(msg.arg1));
                    break;
                }
                case Common.RIPPLE_MSG_VITALS_BLOOD_OX: {
                    bloodOx.setText(Integer.toString(msg.arg1));
                    break;
                }
                default:
                    Log.e(Common.LOG_TAG, "Unknown Message type: " + msg.what);
            }

        }
    };

    private synchronized void callSubscriptionWS(int pid, String action) {
        if (pid >= 0) {
            Log.d(Common.LOG_TAG, action + "ing from " + pid);
            Request request = RippleRequestFactory.getSubscriptionRequest(
                    pid,
                    action,
                    WSConfig.UDP_VITALS_STREAM_PORT);
            mRequestManager.execute(request, this);
            mRequestList.add(request);
        }
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        // Need to recreate client since it is disconnected on destroy
        //udpc = new UdpClient();
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
        graphHelper = new GraphHelper(this.getActivity(), handler);

        layout.addView(graphHelper.getChartView(),
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
                        bannerHandler.sendMessage(bannerHandler.obtainMessage(Common.RIPPLE_MSG_BITMAP,
                                curPatient,
                                0,
                                ((FingerPaint) dialog.findViewById(R.id.fingerpaint)).getmBitmap()));
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
        this.temperature = (TextView) view.findViewById(R.id.temp_value_tv);
        this.pulse = (TextView) view.findViewById(R.id.pulse_value_tv);
        this.bloodOx = (TextView) view.findViewById(R.id.o2_value_tv);

        this.settingsButton = (Button) view.findViewById(R.id.setting_button);
        this.settingsButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                startActivity(new Intent(getActivity(), PrefsActivity.class));
            }
        });

        this.connectButton = (Button) view.findViewById(R.id.connect_button);
        this.connectButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                ((MainActivity)getActivity()).startMQTTService();
            }
        });



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
        SubscriptionResponse response = resultData.getParcelable(RippleRequestFactory.BUNDLE_EXTRA_SUBSCRIPTION);
        if (response.success) {
            if (response.action_echo.equals("unsubscribe") && curPatient >= 0) {
                Log.d(Common.LOG_TAG, "Successfully unsubscribed: " + response.toString());
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

    @Override
    public void onDestroy() {
        super.onDestroy();
        callSubscriptionWS(curPatient, "unsubscribe");
        //udpc.removehandler(handler);
        //udpc.disconnect();
        graphHelper.stopPlotter();
        graphHelper.clearGraph();
    }

    public void setPatient(int pid) {

        graphHelper.clearGraph();

        if (curPatient != pid) {

            if (curPatient >= 0) {
                callSubscriptionWS(curPatient, "unsubscribe");
            }

            graphHelper.startPlotter();

            // Subscribe
            callSubscriptionWS(pid, "subscribe");

            // Connect to UdpStream
            /*
            if (!udpc.isListening()) {
                udpc.connect(WSConfig.UDP_VITALS_STREAM_HOST, WSConfig.UDP_VITALS_STREAM_PORT);
            }

            udpc.addHandler(handler);
            */
            curPatient = pid;

            // TODO: may need settext on UI thread
            patientName.setText("Dummy Patient(" + curPatient + ")");
        } else {
            //udpc.removehandler(handler);

            callSubscriptionWS(curPatient, "unsubscribe");

            graphHelper.stopPlotter();

            // reset to default id
            curPatient = -1;
        }
    }

    public void setBannerHandler(Handler bannerHandler) {
        this.bannerHandler = bannerHandler;
    }
}
