package com.discoverylab.ripple.android.activity;


import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.preference.PreferenceManager;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentActivity;
import android.util.Log;
import android.view.View;
import android.widget.Toast;

import com.discoverylab.ripple.android.R;
import com.discoverylab.ripple.android.config.Common;
import com.discoverylab.ripple.android.config.JSONTag;
import com.discoverylab.ripple.android.config.WSConfig;
import com.discoverylab.ripple.android.fragment.PatientBannerFragment;
import com.discoverylab.ripple.android.fragment.PrefsFragment;
import com.discoverylab.ripple.android.fragment.ScenarioPatientFragment;
import com.discoverylab.ripple.android.mqtt.MQTTClientService;
import com.discoverylab.ripple.android.mqtt.MQTTServiceConstants;
import com.discoverylab.ripple.android.mqtt.MQTTServiceManager;
import com.discoverylab.ripple.android.mqtt.PublishedMessage;
import com.discoverylab.ripple.android.object.Patient;
import com.discoverylab.ripple.android.object.Patients;
import com.discoverylab.ripple.android.view.BannerPatientView;
import com.google.gson.JsonObject;

import java.lang.ref.WeakReference;

public class ScenarioActivity extends FragmentActivity implements View.OnClickListener {

    // Log tag
    private static final String TAG = ScenarioActivity.class.getSimpleName();

    // MQTT
    private MQTTServiceManager mqttServiceManager;

    // References to fragments
    private PatientBannerFragment patientBanner;
    private ScenarioPatientFragment patientFragment;

    // Is mqtt currently connected?
    private boolean isMqttConnected = false;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_scenerio);
        if (savedInstanceState == null) {
            this.patientBanner = PatientBannerFragment.newInstance();
            getSupportFragmentManager().beginTransaction()
                    .add(R.id.banner_container, this.patientBanner)
                    .commit();

            this.patientFragment = ScenarioPatientFragment.newInstance();
            getSupportFragmentManager().beginTransaction()
                    .add(R.id.patient_scenario_container, this.patientFragment)
                    .commit();
        } else {
            // Fragments already exist, so just get from fragment manager
            this.patientBanner = (PatientBannerFragment) getSupportFragmentManager().findFragmentById(R.id.banner_container);
            this.patientFragment = (ScenarioPatientFragment) getSupportFragmentManager().findFragmentById(R.id.patient_scenario_container);
        }



        // get MQTT service manager
        this.mqttServiceManager = new MQTTServiceManager(this, MQTTClientService.class, new MQTTHandler(this));

        if(!this.mqttServiceManager.isServiceRunning()) {
            // Start MQTT connection
            this.startMQTTService();
        }
    }

    @Override
    protected void onStart() {
        super.onStart();
    }

    @Override
    protected void onResume() {
        super.onResume();
    }

    @Override
    protected void onPause() {
        super.onPause();
    }

    @Override
    protected void onStop() {
        super.onStop();
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        // stop MQTT connection
        this.stopMQTTService();
        // ensure no patient is currently selected
        this.patientFragment.setSelectedPatient(null);
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        // pass result to fragments
        for (Fragment fragment : getSupportFragmentManager().getFragments()) {
            fragment.onActivityResult(requestCode, resultCode, data);
        }
    }

    @Override
    public void onClick(View v) {
        if (v instanceof BannerPatientView) {
            BannerPatientView bpv = (BannerPatientView) v;
            Patient p = bpv.getPatient();
            this.patientFragment.setSelectedPatient(p);
            this.patientBanner.refreshBanner();
        }
    }

    /**
     * Start MQTT service with current connection preferences
     */
    public void startMQTTService() {
        if (this.mqttServiceManager != null) {
            if (this.isMQTTServiceRunning()) {
                // stop service
                this.stopMQTTService();
            }
            SharedPreferences prefs = PreferenceManager.getDefaultSharedPreferences(this);
            this.mqttServiceManager.start(prefs.getString(PrefsFragment.IP_FROM_PREFS, WSConfig.DEFAULT_BROKER_IP), prefs.getString(PrefsFragment.PORT_NUM_MQTT_PREFS, WSConfig.DEFAULT_MQTT_PORT));
        }
    }

    /**
     * Stop MQTT service (disconnect from Broker)
     */
    public void stopMQTTService() {
        if (this.mqttServiceManager != null && this.isMQTTServiceRunning()) {
            this.mqttServiceManager.stop();
        }
        this.setMqttConnected(false);
    }

    /**
     * @return true if MQTT service is currently running, false otherwise
     */
    public boolean isMQTTServiceRunning() {
        return this.mqttServiceManager.isServiceRunning();
    }


    /**
     * Subscribe to a MQTT topic if connected to broker
     *
     * @param topicName MQTT topic to subscribe to
     */
    public void subscribeToTopic(String topicName) {
        Message msg = Message.obtain(null, MQTTServiceConstants.MSG_SUBSCRIBE);
        Bundle bundle = new Bundle();
        bundle.putString(MQTTServiceConstants.MQTT_TOPIC, topicName);
        msg.setData(bundle);
        try {
            this.mqttServiceManager.send(msg);
            Log.d(Common.LOG_TAG, "Subscribed for: " + topicName);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    /**
     * Unsubscribe from an MQTT topic if connected to broker
     *
     * @param topicName MQTT topic to unsubscribe from
     */
    public void unsubscribeFromTopic(String topicName) {
        Message msg = Message.obtain(null, MQTTServiceConstants.MSG_UNSUBSCRIBE);
        Bundle bundle = new Bundle();
        bundle.putString(MQTTServiceConstants.MQTT_TOPIC, topicName);
        msg.setData(bundle);
        try {
            this.mqttServiceManager.send(msg);
            Log.d(TAG, "Unsubscribed from: " + topicName);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    /**
     * Publish a message over MQTT
     *
     * @param topic   Topic to publish to.
     * @param message Message to publish
     */
    public void publishMQTTMessage(String topic, String message) {
        Message msg = Message.obtain(null, MQTTServiceConstants.MSG_PUBLISH_TO_TOPIC);
        Bundle data = new Bundle();
        data.putString(MQTTServiceConstants.MQTT_TOPIC, topic);
        data.putString(MQTTServiceConstants.MQTT_MESSAGE, message);
        msg.setData(data);
        try {
            this.mqttServiceManager.send(msg);
            Log.d(TAG, "Published message to topic " + topic);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    private void setMqttConnected(boolean connected) {
        this.isMqttConnected = connected;
    }

    private boolean isMqttConnected() {
        return this.isMqttConnected;
    }

    /**
     * Processes message from MQTT client
     *
     * @param msg MQTT message to process
     */
    private void processPublishedMessage(PublishedMessage msg) {
        String topic = msg.getTopic();
        if (topic.matches(Common.MQTT_TOPIC_MATCH_VITALCAST)) {
            JsonObject recordJson = Common.GSON.fromJson(msg.getPayload(), JsonObject.class);
            this.processPatientUpdate(recordJson);
            //this.banner.getHandler().obtainMessage(Common.RIPPLE_MSG_RECORD, recordJson).sendToTarget();
            //this.patLeft.getHandler().obtainMessage(Common.RIPPLE_MSG_RECORD, recordJson).sendToTarget();
        } else if (topic.matches(Common.MQTT_TOPIC_MATCH_ECG_STREAM)) {
            // TODO: Send to new note fragment when ready
            //this.patLeft.getHandler().obtainMessage(Common.RIPPLE_MSG_ECG_STREAM, msg).sendToTarget();
        } else {
            Log.d(Common.LOG_TAG, "Unknown MQTT topic recieved:" + topic);
        }
    }

    /**
     * Process a message for a patient's vitals
     *
     * @param recordJson JSON message to process
     */
    private void processPatientUpdate(JsonObject recordJson) {
        Patients patients = Patients.getInstance();
        Patient curPatient = null;


        String src = recordJson.get(JSONTag.RECORD_SOURCE).getAsString();
        int hr = recordJson.get(JSONTag.RECORD_HEART_RATE).getAsInt();
        int spO2 = recordJson.get(JSONTag.RECORD_BLOOD_OX).getAsInt();
        int temperature = recordJson.get(JSONTag.RECORD_TEMPERATURE).getAsInt();
        int resp_pm = recordJson.get(JSONTag.RECORD_RESP_PER_MIN).getAsInt();


        // find patient
        curPatient = patients.getPatient(src);
        if (curPatient == null) {
            // Add patient
            curPatient = new Patient(src);
            patients.addPatient(src, curPatient);
            // Inform banner of new patient
            this.patientBanner.addPatient(curPatient);
        }

        // Update patient values
        curPatient.setO2(spO2);
        curPatient.setHeartRate(hr);
        curPatient.setTemperature(temperature);
        curPatient.setBreathsPerMin(resp_pm);

        if(curPatient == this.patientFragment.getSelectedPatient()){
            // update vitals on screen for selected patient
            this.patientFragment.updatePatientVitals();
        }
    }

    /**
     * Class to handle messages from MQTT client
     */
    private static class MQTTHandler extends Handler {
        private WeakReference<ScenarioActivity> activityReference;

        public MQTTHandler(ScenarioActivity activity) {
            this.activityReference = new WeakReference<ScenarioActivity>(activity);
        }

        @Override
        public void handleMessage(Message msg) {
            ScenarioActivity activity = activityReference.get();
            if (activity != null) {
                switch (msg.what) {
                    case MQTTServiceConstants.MSG_CONNECTED:
                        // Set that we are connected
                        activity.setMqttConnected(true);
                        // Subscribe to vitalcast messages
                        activity.subscribeToTopic(Common.MQTT_TOPIC_VITALCAST.replace(Common.MQTT_TOPIC_ID_STRING, Common.MQTT_TOPIC_WILDCARD_SINGLE_LEVEL));
                        Toast.makeText(activity, "Connected", Toast.LENGTH_SHORT).show();
                        // Clear old patient list
                        break;
                    case MQTTServiceConstants.MSG_CANT_CONNECT:
                        activity.setMqttConnected(false);
                        Toast.makeText(activity, "Unable to connect", Toast.LENGTH_SHORT).show();
                        break;
                    case MQTTServiceConstants.MSG_PUBLISHED_MESSAGE:
                        activity.processPublishedMessage((PublishedMessage) msg.obj);
                        break;
                    case MQTTServiceConstants.MSG_DISCONNECTED:
                        activity.setMqttConnected(false);
                        Toast.makeText(activity, "Disconnected from Broker.", Toast.LENGTH_SHORT).show();
                        break;
                    case MQTTServiceConstants.MSG_NO_NETWORK:
                        activity.setMqttConnected(false);
                        Toast.makeText(activity, "No network connection, will attempt to reconnect with broker when network is restored.", Toast.LENGTH_LONG).show();
                        break;
                    case MQTTServiceConstants.MSG_RECONNECTING:
                        activity.setMqttConnected(false);
                        Toast.makeText(activity, "Reconnecting to Broker...", Toast.LENGTH_SHORT).show();
                        break;
                    case MQTTServiceConstants.MSG_CONNECTION_STATUS:
                        activity.setMqttConnected(msg.getData().getBoolean(MQTTServiceConstants.MQTT_CONNECTION_STATUS, activity.isMqttConnected()));
                        break;
                    default:
                        Log.d(Common.LOG_TAG, "Unknown message type :" + msg.what);
                        break;
                }
            }
        }
    }
}
