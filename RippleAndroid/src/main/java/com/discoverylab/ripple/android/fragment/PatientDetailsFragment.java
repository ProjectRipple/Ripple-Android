package com.discoverylab.ripple.android.fragment;


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
import android.widget.Toast;

import com.discoverylab.ripple.android.R;
import com.discoverylab.ripple.android.activity.MainActivity;
import com.discoverylab.ripple.android.activity.PrefsActivity;
import com.discoverylab.ripple.android.adapter.ui.GraphHelper;
import com.discoverylab.ripple.android.api.ApiClient;
import com.discoverylab.ripple.android.config.Common;
import com.discoverylab.ripple.android.config.JSONTag;
import com.discoverylab.ripple.android.model.EcgRequestData;
import com.discoverylab.ripple.android.mqtt.PublishedMessage;
import com.google.gson.JsonObject;

import retrofit.Callback;
import retrofit.RetrofitError;
import retrofit.client.Response;


/**
 * The patient fragment is used to display the most recent health data and information
 * for the patient
 * <p/>
 * The a chart is added to the bottom left linear layout and is used to display the ecg waveform
 */
public class PatientDetailsFragment extends Fragment {

    // Key for saving current patient src
    private static final String SAVED_STATE_PATIENT_SRC = "savedStatePatientSrc";
    // Current patient src (id)
    private String curPatientSrc = "";
    // Text views
    private TextView patientName;
    private TextView temperature;
    private TextView pulse;
    private TextView bloodOx;
    // Helper for ECG graph
    private GraphHelper graphHelper;
    // Reference to Banner's handler
    private Handler bannerHandler;
    // Buttons
    private Button settingsButton;
    private Button connectButton;
    private Button ecgRequestButton;
    // Is a ECG request in progress
    private boolean ecgRequestInProgress = false;
    // Handler for messages to this fragment
    private Handler handler = new PatientDetailsHandler();


    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
    }

    /**
     * @param inflater           Inflator for view
     * @param container          Container for view
     * @param savedInstanceState previously saved instance
     * @return view
     */
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // inflate view
        final View view = inflater.inflate(R.layout.patient_left, container, false);
        assert view != null;

        // Get layout for chart
        LinearLayout layout = (LinearLayout) view.findViewById(R.id.chart);
        graphHelper = new GraphHelper(this.getActivity());

        // add chart to layout
        layout.addView(graphHelper.getChartView(),
                new LinearLayout.LayoutParams(LinearLayout.LayoutParams.MATCH_PARENT,
                        LinearLayout.LayoutParams.MATCH_PARENT)
        );

        // get image view of patient tag
        ImageView tagview = (ImageView) view.findViewById(R.id.tagview);
        assert tagview != null;

        tagview.setClickable(true);
        tagview.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                // custom dialog
                final Dialog dialog = new Dialog(getActivity());
                dialog.setContentView(R.layout.finger_paint_dialog);
                dialog.setTitle(" Draw Something ");

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

        // Get text views
        this.patientName = (TextView) view.findViewById(R.id.name_value_tv);
        this.temperature = (TextView) view.findViewById(R.id.temp_value_tv);
        this.pulse = (TextView) view.findViewById(R.id.pulse_value_tv);
        this.bloodOx = (TextView) view.findViewById(R.id.o2_value_tv);

        // get settings button
        this.settingsButton = (Button) view.findViewById(R.id.setting_button);
        this.settingsButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                startActivity(new Intent(getActivity(), PrefsActivity.class));
            }
        });

        // get connect button
        this.connectButton = (Button) view.findViewById(R.id.connect_button);
        this.connectButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                // start or stop ECG request & change button text
                if (connectButton.getText().equals(getActivity().getString(R.string.connect))) {
                    ((MainActivity) getActivity()).startMQTTService();
                    connectButton.setText(R.string.disconnect);
                } else if (connectButton.getText().equals(getActivity().getString(R.string.disconnect))) {
                    ((MainActivity) getActivity()).stopMQTTService();
                    connectButton.setText(R.string.connect);
                }
            }
        });

        // get ecg request button
        this.ecgRequestButton = (Button) view.findViewById(R.id.ecg_request_btn);
        this.ecgRequestButton.setOnClickListener(new View.OnClickListener() {

            @Override
            public void onClick(View v) {
                requestEcgStream();
            }
        });

        // Restore patient src from saved state
        if (savedInstanceState != null) {
            this.setPatientSrc(savedInstanceState.getString(SAVED_STATE_PATIENT_SRC));
        }

        return view;
    }

    @Override
    public void onSaveInstanceState(Bundle outState) {
        super.onSaveInstanceState(outState);
        graphHelper.save(outState);
        outState.putString(SAVED_STATE_PATIENT_SRC, this.curPatientSrc);
    }

    @Override
    public void onActivityCreated(Bundle savedState) {
        super.onActivityCreated(savedState);
        if (savedState != null) {
            graphHelper.restore(savedState);
        }

        if (isMQTTConnected()) {
            this.connectButton.setText(R.string.disconnect);
        }
    }

    @Override
    public void onResume() {
        super.onResume();
    }

    @Override
    public void onPause() {
        super.onPause();
    }

    @Override
    public void onAttach(Activity activity) {
        super.onAttach(activity);
    }

    @Override
    public void onDestroyView() {
        super.onDestroyView();
        // Remove view references
        this.patientName = null;
        this.temperature = null;
        this.pulse = null;
        this.bloodOx = null;
        this.settingsButton = null;
        this.connectButton = null;
        this.ecgRequestButton = null;
        // Remove reference to graphhelper (which holds reference to a view that references our activity)
        this.graphHelper.stopPlotter();
        this.graphHelper.clearGraph();
        this.graphHelper = null;
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
    }

    /**
     * Send request for ECG stream to the Broker.
     */
    private void requestEcgStream() {
        if (!ecgRequestInProgress && !curPatientSrc.equals("") && isMQTTConnected()) {
            ecgRequestInProgress = true;
            // clear ecg graph
            graphHelper.clearGraph();
            // set request
            ApiClient.getRippleApiClient().requestEcgStream(curPatientSrc, new Callback<EcgRequestData>() {
                @Override
                public void success(EcgRequestData ecgRequestData, Response response) {
                    ecgRequestInProgress = false;
                    Toast.makeText(getActivity(), ecgRequestData.getMsg(), Toast.LENGTH_SHORT).show();
                }

                @Override
                public void failure(RetrofitError retrofitError) {
                    ecgRequestInProgress = false;
                    Toast.makeText(getActivity(), "Request failed. " + " Message:" + retrofitError.getMessage(), Toast.LENGTH_LONG).show();
                }
            });
        } else {
            Toast.makeText(getActivity(), "Please connect to the Broker and select a patient before requesting ECG.", Toast.LENGTH_LONG).show();
        }
    }

    /**
     * Set the current patient by ID
     *
     * @param patientSrc Patient ID of select patient
     */
    public void setPatientSrc(String patientSrc) {

        graphHelper.clearGraph();

        if (this.curPatientSrc.equals(patientSrc) || patientSrc.equals("")) {
            // unsubscribe
            if (!this.curPatientSrc.equals("") && isMQTTConnected()) {
                String topic = Common.MQTT_TOPIC_ECG_STREAM.replace(Common.MQTT_TOPIC_ID_STRING, this.curPatientSrc);
                ((MainActivity) getActivity()).unsubscribeFromTopic(topic);
            }

            graphHelper.stopPlotter();

            this.curPatientSrc = "";

            patientName.setText("N/A");
            temperature.setText("N/A");
            bloodOx.setText("N/A");
            pulse.setText("N/A");

        } else {

            // unsubscribe from old patient stream (if any)
            if (!this.curPatientSrc.equals("") && isMQTTConnected()) {
                String topic = Common.MQTT_TOPIC_ECG_STREAM.replace(Common.MQTT_TOPIC_ID_STRING, this.curPatientSrc);
                ((MainActivity) getActivity()).unsubscribeFromTopic(topic);
            }
            graphHelper.startPlotter();

            // subscribe to new patient
            this.curPatientSrc = patientSrc;

            if (!this.curPatientSrc.equals("") && isMQTTConnected()) {
                String topic = Common.MQTT_TOPIC_ECG_STREAM.replace(Common.MQTT_TOPIC_ID_STRING, this.curPatientSrc);
                ((MainActivity) getActivity()).subscribeToTopic(topic);
            }

            patientName.setText(curPatientSrc);
        }

        if (this.bannerHandler != null) {
            this.bannerHandler.obtainMessage(Common.RIPPLE_MSG_SELECT_PATIENT, this.curPatientSrc).sendToTarget();
        }
    }

    /**
     *
     * @return true if MQTT service is connected, false otherwise
     */
    private boolean isMQTTConnected() {
        // TODO: make a better connection check as service running does not always mean MQTT is connected
        Activity activity = getActivity();
        if (activity != null && activity instanceof MainActivity) {
            return ((MainActivity) getActivity()).isMQTTServiceRunning();
        } else {
            return false;
        }
    }

    public void setBannerHandler(Handler bannerHandler) {
        this.bannerHandler = bannerHandler;
    }

    /**
     * Get handler for message to this object
     * @return this object's handler
     */
    public Handler getHandler() {
        return this.handler;
    }

    /**
     * Handler for this class
     */
    private class PatientDetailsHandler extends Handler{
        @Override
        public void handleMessage(final Message msg) {
            super.handleMessage(msg);

            switch (msg.what) {
                case Common.RIPPLE_MSG_RECORD:
                    JsonObject recordJson = (JsonObject) msg.obj;
                    String src = recordJson.get(JSONTag.RECORD_SOURCE).getAsString();
                    // update text fieldd
                    if (!curPatientSrc.equals("") && src.equals(curPatientSrc)) {
                        temperature.setText(recordJson.get(JSONTag.RECORD_TEMPERATURE).getAsString());
                        if (recordJson.get(JSONTag.RECORD_HEART_RATE).getAsInt() < 250) {
                            pulse.setText(recordJson.get(JSONTag.RECORD_HEART_RATE).getAsString());
                        } else {
                            pulse.setText("---");
                        }
                        if (recordJson.get(JSONTag.RECORD_BLOOD_OX).getAsInt() < 120) {
                            bloodOx.setText(recordJson.get(JSONTag.RECORD_BLOOD_OX).getAsString());
                        } else {
                            bloodOx.setText("---");
                        }
                    }
                    break;
                case Common.RIPPLE_MSG_ECG_STREAM:
                    PublishedMessage ecgMsg = (PublishedMessage) msg.obj;
                    // set message to graph helper to process
                    if (!curPatientSrc.equals("") && ecgMsg.getTopic().contains(curPatientSrc)) {
                        graphHelper.offerVitals(ecgMsg);
                    } else {
                        Log.d(Common.LOG_TAG, "Stream not for current patient.");
                    }
                    break;
                default:
                    Log.e(Common.LOG_TAG, "Unknown Message type: " + msg.what);
            }

        }
    }
}
