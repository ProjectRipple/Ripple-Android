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
import android.widget.Toast;

import com.google.gson.JsonObject;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import mil.afrl.discoverylab.sate13.rippleandroid.Common;
import mil.afrl.discoverylab.sate13.rippleandroid.MainActivity;
import mil.afrl.discoverylab.sate13.rippleandroid.PrefsActivity;
import mil.afrl.discoverylab.sate13.rippleandroid.R;
import mil.afrl.discoverylab.sate13.rippleandroid.Util;
import mil.afrl.discoverylab.sate13.rippleandroid.adapter.ui.GraphHelper;
import mil.afrl.discoverylab.sate13.rippleandroid.api.ApiClient;
import mil.afrl.discoverylab.sate13.rippleandroid.model.EcgRequestData;
import mil.afrl.discoverylab.sate13.rippleandroid.mqtt.PublishedMessage;
import mil.afrl.discoverylab.sate13.rippleandroid.view.FingerPaint;
import retrofit.Callback;
import retrofit.RetrofitError;
import retrofit.client.Response;


/**
 * The left patient fragment is used to display the most recent health data and information
 * for the patient
 * <p/>
 * The a chart is added to the bottom left linear layout and is used to display the ecg waveform
 */
public class PatientLeft extends Fragment {

    private static final String SAVED_STATE_PATIENT_SRC = "savedStatePatientSrc";
    //private static UdpClient udpc = new UdpClient();
    private int curPatient = -1;
    private String curPatientSrc = "";
    //private int curVital;
    private View view;
    private TextView patientName;
    private TextView temperature;
    private TextView pulse;
    private TextView bloodOx;
    private GraphHelper graphHelper;
    private Handler bannerHandler;
    private Button settingsButton;
    private Button connectButton;
    private Button ecgRequestButton;

    private boolean ecgRequestInProgress = false;
    private Handler handler = new Handler() {
        @Override
        public void handleMessage(final Message msg) {
            super.handleMessage(msg);

            switch (msg.what) {
                case Common.RIPPLE_MSG_RECORD:
                    JsonObject recordJson = (JsonObject) msg.obj;
                    String src = recordJson.get(Common.RECORD_SOURCE).getAsString();
                    if(!curPatientSrc.equals("") && src.equals(curPatientSrc)){
                        temperature.setText(recordJson.get(Common.RECORD_TEMPERATURE).getAsString());
                        pulse.setText(recordJson.get(Common.RECORD_HEART_RATE).getAsString());
                        bloodOx.setText(recordJson.get(Common.RECORD_BLOOD_OX).getAsString());
                    }
                    break;
                case Common.RIPPLE_MSG_ECG_STREAM:
                    PublishedMessage ecgMsg = (PublishedMessage) msg.obj;
                    if(!curPatientSrc.equals("") && ecgMsg.getTopic().contains(curPatientSrc)){
                        graphHelper.offerVitals(ecgMsg);
                    } else {
                        Log.d(Common.LOG_TAG, "Stream not for current patient.");
                    }
                    break;
                default:
                    Log.e(Common.LOG_TAG, "Unknown Message type: " + msg.what);
            }

        }
    };


    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
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
        graphHelper = new GraphHelper(this.getActivity());

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
                if(connectButton.getText().equals(getActivity().getString(R.string.connect))) {
                    ((MainActivity) getActivity()).startMQTTService();
                    connectButton.setText(R.string.disconnect);
                } else if (connectButton.getText().equals(getActivity().getString(R.string.disconnect))){
                    ((MainActivity) getActivity()).stopMQTTService();
                    connectButton.setText(R.string.connect);
                }
            }
        });

        this.ecgRequestButton = (Button) view.findViewById(R.id.ecg_request_btn);
        this.ecgRequestButton.setOnClickListener(new View.OnClickListener(){

            @Override
            public void onClick(View v) {
                requestEcgStream();
            }
        });


        if(savedInstanceState != null)
        {
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

        if(isMQTTConnected()){
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
    public void onDestroy() {
        super.onDestroy();
        graphHelper.stopPlotter();
        graphHelper.clearGraph();
    }

    private void requestEcgStream(){


        if(!ecgRequestInProgress && !curPatientSrc.equals("") && isMQTTConnected()){
            ecgRequestInProgress = true;
            graphHelper.clearGraph();

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

    public void setPatientSrc(String patientSrc){

        graphHelper.clearGraph();

        if(this.curPatientSrc.equals(patientSrc) || patientSrc.equals("")){
            // unsubscribe
            if(!this.curPatientSrc.equals("") && isMQTTConnected()){
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
            if(!this.curPatientSrc.equals("") && isMQTTConnected()){
                String topic = Common.MQTT_TOPIC_ECG_STREAM.replace(Common.MQTT_TOPIC_ID_STRING, this.curPatientSrc);
                ((MainActivity) getActivity()).unsubscribeFromTopic(topic);
            }
            graphHelper.startPlotter();

            // subscribe to new patient
            this.curPatientSrc = patientSrc;

            if(!this.curPatientSrc.equals("") && isMQTTConnected()){
                String topic = Common.MQTT_TOPIC_ECG_STREAM.replace(Common.MQTT_TOPIC_ID_STRING, this.curPatientSrc);
                ((MainActivity) getActivity()).subscribeToTopic(topic);
            }

            patientName.setText(curPatientSrc);
        }
    }

    private boolean isMQTTConnected(){
        // TODO: make a better connection check as service running does not always mean MQTT is connected
        Activity activity = getActivity();
        if(activity != null && activity instanceof MainActivity) {
            return ((MainActivity) getActivity()).isMQTTServiceRunning();
        } else {
            return false;
        }
    }

    public void setBannerHandler(Handler bannerHandler) {
        this.bannerHandler = bannerHandler;
    }

    public Handler getHandler(){ return this.handler;}
}
