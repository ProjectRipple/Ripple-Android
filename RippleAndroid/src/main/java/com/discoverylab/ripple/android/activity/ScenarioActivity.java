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
import com.google.android.gms.maps.model.LatLng;
import com.google.gson.JsonArray;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;

import java.lang.ref.WeakReference;
import java.text.DateFormat;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.TimeZone;
import java.util.Timer;
import java.util.TimerTask;

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

    // Reference to timer for sending periodic messages
    private Timer periodicTimer = new Timer();

    // periodic task period
    private static final long PERIODIC_PERIOD = 10 * 1000;

    // true if location was set by gps
    private boolean gpsLocationSet = false;

    // true if periodic timer was started
    private boolean periodicTimerStarted = false;

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

        if (!this.mqttServiceManager.isServiceRunning()) {
            // Start MQTT connection
            this.startMQTTService();
        } else {
            // just bind service
            this.mqttServiceManager.bind();
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
        // stop timer task
        this.periodicTimer.cancel();
        this.periodicTimer = null;
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

    private void sendResponderUpdate() {
        if (!this.isMqttConnected) {
            // don't bother with message if mqtt is not connected
            return;
        }
        JsonObject updateMsg = new JsonObject();
        // set id
        updateMsg.addProperty(JSONTag.RESPONDER_ID, Common.RESPONDER_ID);
        // set date of message
        DateFormat df = new SimpleDateFormat(Common.ISO_DATETIME_FORMAT);
        df.setTimeZone(TimeZone.getTimeZone("UTC"));
        updateMsg.addProperty(JSONTag.BROKER_PING_DATE, df.format(new Date()));

        // set location
        JsonObject location = new JsonObject();
        location.addProperty(JSONTag.BROKER_PING_LOCATION_LAT, Common.responderLatLng.latitude);
        location.addProperty(JSONTag.BROKER_PING_LOCATION_LNG, Common.responderLatLng.longitude);
        location.addProperty(JSONTag.BROKER_PING_LOCATION_ALT, Common.responderAltitude);

        updateMsg.add(JSONTag.BROKER_PING_LOCATION, location);

        Log.d(TAG, "Sending update: " + updateMsg.toString());

        // publish message
        this.publishMQTTMessage(Common.MQTT_TOPIC_RESPONDER_PING.replace(Common.MQTT_TOPIC_RESPONDER_ID_STRING, Common.RESPONDER_ID), updateMsg.toString());

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
        } else if (topic.matches(Common.MQTT_TOPIC_MATCH_BROKER_PING)) {
            JsonObject pingJson = Common.GSON.fromJson(msg.getPayload(), JsonObject.class);
            this.processBrokerPing(pingJson);
        } else if (topic.matches(Common.MQTT_TOPIC_MATCH_ECG_STREAM)) {
            // TODO: Send to new note fragment when ready
            //this.patLeft.getHandler().obtainMessage(Common.RIPPLE_MSG_ECG_STREAM, msg).sendToTarget();
        } else {
            Log.d(Common.LOG_TAG, "Unknown MQTT topic recieved:" + topic);
        }
    }

    /**
     * Process a ping message from the broker.
     *
     * @param pingJson JSON message to process
     */
    private void processBrokerPing(JsonObject pingJson) {

        // TODO: filter by broker id
        // parse message
        String brokerId = pingJson.get(JSONTag.BROKER_PING_ID).getAsString();
        String date = pingJson.get(JSONTag.BROKER_PING_DATE).getAsString();

        // Get location
        JsonObject location = pingJson.get(JSONTag.BROKER_PING_LOCATION).getAsJsonObject();
        double lat = location.get(JSONTag.BROKER_PING_LOCATION_LAT).getAsDouble();
        double lng = location.get(JSONTag.BROKER_PING_LOCATION_LNG).getAsDouble();
        double alt = location.get(JSONTag.BROKER_PING_LOCATION_ALT).getAsDouble();

        Common.brokerLatLng = new LatLng(lat, lng);
        Common.brokerAltitude = alt;

        if (!this.gpsLocationSet) {
            // set responder location to broker's only if gps has not set the location
            Common.responderLatLng = Common.brokerLatLng;
            Common.responderAltitude = Common.brokerAltitude;
        }

        if (!this.periodicTimerStarted) {
            // start timer task now that we know we have a location
            this.periodicTimer.scheduleAtFixedRate(new TimerTask() {
                @Override
                public void run() {
                    sendResponderUpdate();
                }
            }, PERIODIC_PERIOD, PERIODIC_PERIOD);
            this.periodicTimerStarted = true;
        }

        // get patient list from broker
        JsonArray patientsArray = pingJson.get(JSONTag.BROKER_PING_PATIENTS).getAsJsonArray();
        Patients patients = Patients.getInstance();
        // check that we have all patients in our cache
        for (JsonElement p : patientsArray) {
            JsonObject patient = p.getAsJsonObject();
            String id = patient.get(JSONTag.BROKER_PING_PATIENTS_ID).getAsString();

            String lastSeenDateString = patient.get(JSONTag.BROKER_PING_PATIENTS_LAST_SEEN).getAsString();
            Date lastSeenDate = null;
            try {
                // TODO: check on other systems than 4.4
                DateFormat df = new SimpleDateFormat(Common.ISO_DATETIME_FORMAT);
                df.setTimeZone(TimeZone.getTimeZone("UTC"));
                lastSeenDate = df.parse(lastSeenDateString);
                Log.d(TAG, "Original String: " + lastSeenDateString + ", derived string: " + df.format(lastSeenDate));
            } catch (ParseException e) {
                e.printStackTrace();
            }

            // add patient to list if patient does not exist
            if (!patients.patientExists(id)) {
                Log.d(TAG, "New patient " + id + " from ping message");
                // get full IP address
                String ip = patient.get(JSONTag.BROKER_PING_PATIENTS_IP).getAsString();

                // create new patient
                Patient newPatient = new Patient(id);
                newPatient.setIpaddr(ip);
                newPatient.setLastSeenDate(lastSeenDate);
                patients.addPatient(id, newPatient);
                // also add to banner
                this.patientBanner.addPatient(newPatient);

            } else {
                // just update last seen
                patients.getPatient(id).setLastSeenDate(lastSeenDate);
            }
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

        if (curPatient == this.patientFragment.getSelectedPatient()) {
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
                        activity.subscribeToTopic(Common.MQTT_TOPIC_VITALCAST.replace(Common.MQTT_TOPIC_PATIENT_ID_STRING, Common.MQTT_TOPIC_WILDCARD_SINGLE_LEVEL));
                        // Subscribe to ping messages from broker
                        activity.subscribeToTopic(Common.MQTT_TOPIC_BROKER_PING.replace(Common.MQTT_TOPIC_BROKER_ID_STRING, Common.MQTT_TOPIC_WILDCARD_SINGLE_LEVEL));
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
